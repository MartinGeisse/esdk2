/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import name.martingeisse.mahdl.common.Environment;
import name.martingeisse.mahdl.common.processor.definition.*;
import name.martingeisse.mahdl.common.processor.expression.ExpressionProcessor;
import name.martingeisse.mahdl.common.processor.expression.ExpressionProcessorImpl;
import name.martingeisse.mahdl.common.processor.statement.ProcessedDoBlock;
import name.martingeisse.mahdl.common.processor.statement.StatementProcessor;
import name.martingeisse.mahdl.common.processor.type.DataTypeProcessor;
import name.martingeisse.mahdl.common.processor.type.DataTypeProcessorImpl;
import name.martingeisse.mahdl.input.cm.*;
import name.martingeisse.mahdl.input.cm.Module;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * This class handles the common logic between error annotations, code generation etc., and provides a unified framework
 * for the individual steps such as constant evaluation, type checking, name resolution, and so on.
 * <p>
 * It emerged from the observation that even a simple task such as annotating the code with error markers is similar to
 * compiling it, in that information about the code must be collected in multiple interdependent steps. Without a
 * central framework for these steps, a lot of code gets duplicated between them.
 * <p>
 * For example, it is not possible to check type correctness without evaluating constants, becuase constants are used to
 * specify array sizes. Type correctness is needed to evaluate constants though. Both of these steps can run into the
 * same errors in various sub-steps, so they would take an annotation holder to report these errors -- but there would
 * need to be an agreement which step reports which errors. And so on.
 */
public final class ModuleProcessor {

	private final Module module;
	private final String canonicalModuleName;
	private final ProcessingSidekick sidekick;

	private DataTypeProcessor dataTypeProcessor;
	private ExpressionProcessor expressionProcessor;
	private DefinitionProcessor definitionProcessor;

	private AssignmentValidator assignmentValidator;
	private StatementProcessor statementProcessor;
	private List<ProcessedDoBlock> processedDoBlocks;

	public ModuleProcessor(@NotNull Module module, @NotNull ErrorHandler errorHandler) {
		this(module, new ProcessingSidekick(errorHandler));
	}

	public ModuleProcessor(@NotNull Module module, @NotNull ProcessingSidekick sidekick) {
		this.module = module;
		this.canonicalModuleName = CmUtil.canonicalizeQualifiedModuleName(module.getModuleName());
		this.sidekick = sidekick;
	}

	@NotNull
	private Map<String, Named> getDefinitions() {
		return definitionProcessor.getDefinitions();
	}

	public ModuleDefinition process() {

		// make sure the module name matches the file name and sits in the right folder
		try {
			Environment.Holder.INSTANCE.validateModuleNameAgainstFilePath(module, module.getModuleName());
		} catch (IOException e) {
			sidekick.onError(module.getModuleName(), "module name does not match file path -- " + e.getMessage());
		}

		// validate nativeness (but still continue even if violated, since the keyword may be misplaced)
		boolean isNative = module.getNativeness().getIt() != null;
		if (isNative) {
			List<ImplementationItem> implementationItems = module.getImplementationItems().getAll();
			if (!implementationItems.isEmpty()) {
				sidekick.onError(implementationItems.get(0), "native module cannot contain implementation items");
			}
		}

		// Create helper objects. These objects work together, especially during constant definition analysis, due to
		// a mutual dependency between the type system, constant evaluation and expression processing. Note the
		// LocalDefinitionResolver parameter to the ExpressionProcessorImpl calling getDefinitions() on the fly,
		// not in advance, to break the dependency cycle.
		expressionProcessor = new ExpressionProcessorImpl(sidekick, name -> getDefinitions().get(name));
		dataTypeProcessor = new DataTypeProcessorImpl(sidekick, expressionProcessor);
		definitionProcessor = new DefinitionProcessor(sidekick, dataTypeProcessor, expressionProcessor);

		// process module definitions
		definitionProcessor.processPorts(module.getPortDefinitionGroups());
		if (isNative) {
			return new ModuleDefinition(isNative, canonicalModuleName, ImmutableMap.copyOf(getDefinitions()), ImmutableList.of());
		}
		for (ImplementationItem implementationItem : module.getImplementationItems().getAll()) {
			if (isConstant(implementationItem)) {
				definitionProcessor.process(implementationItem);
			}
		}
		for (ImplementationItem implementationItem : module.getImplementationItems().getAll()) {
			if (!isConstant(implementationItem)) {
				definitionProcessor.process(implementationItem);
			}
		}
		for (Named definition : getDefinitions().values()) {
			if (!(definition instanceof Constant)) {
				definition.processExpressions(expressionProcessor);
			}
		}

		// Process do-blocks and check for missing / duplicate assignments. Do so in the original file's order so when
		// an error message could in principle appear in one of multiple places, it appears in the topmost place.
		assignmentValidator = new AssignmentValidator(sidekick);
		List<Pair<Runnable, CmNode>> runnables = new ArrayList<>();
		for (Named item : getDefinitions().values()) {
			// Inconsistencies regarding signal-likes in the initializer vs. other assignments:
			// - ports cannot have an initializer
			// - constants cannot be assigned to other than the initializer (the assignment validator ensures that
			//   already while checking expressions)
			// - signals must be checked here
			// - for registers, the initializer does not conflict with other assignments
			if (item instanceof Signal) {
				Signal signal = (Signal) item;
				if (signal.getInitializer() != null) {
					runnables.add(Pair.of(() -> {
						assignmentValidator.considerAssignedTo(signal, signal.getNameElement());
						assignmentValidator.finishSection();
					}, signal.getNameElement()));
				}
			}
		}
		processedDoBlocks = new ArrayList<>();
		statementProcessor = new StatementProcessor(sidekick, expressionProcessor, assignmentValidator);
		for (ImplementationItem implementationItem : module.getImplementationItems().getAll()) {
			runnables.add(Pair.of(() -> {
				// We collect all newly assigned signals in a separate set and add them at the end of the current do-block
				// because assigning to a signal multiple times within the same do-block is allowed. Note that the
				// per-assignment call to the AssignmentValidator is done by the StatementProcessor, so we don't have
				// to call it here.
				if (implementationItem instanceof ImplementationItem_DoBlock) {
					processedDoBlocks.add(statementProcessor.process((ImplementationItem_DoBlock) implementationItem));
				}
				assignmentValidator.finishSection();
			}, implementationItem));
		}
		runnables.sort(Comparator.comparing(Pair::getRight, CmNode::compareStartOffset));
		for (Pair<Runnable, CmNode> pair : runnables) {
			pair.getLeft().run();
		}

		// now check that all ports and signals without initializer have been assigned to
		assignmentValidator.checkMissingAssignments(getDefinitions().values());

		return new ModuleDefinition(isNative, canonicalModuleName, ImmutableMap.copyOf(getDefinitions()), ImmutableList.copyOf(processedDoBlocks));
	}

	private boolean isConstant(ImplementationItem item) {
		if (item instanceof ImplementationItem_SignalLikeDefinitionGroup) {
			SignalLikeKind kind = ((ImplementationItem_SignalLikeDefinitionGroup) item).getKind();
			return kind instanceof SignalLikeKind_Constant;
		} else {
			return false;
		}
	}

}
