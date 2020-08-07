package name.martingeisse.mahdl.gradle.codegen;

import name.martingeisse.mahdl.common.processor.ErrorHandler;
import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.definition.*;
import name.martingeisse.mahdl.common.processor.expression.InstancePortReference;
import name.martingeisse.mahdl.common.processor.expression.ProcessedExpression;
import name.martingeisse.mahdl.common.processor.expression.SignalLikeReference;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.compiler.CompilerContext;
import name.martingeisse.mahdl.compiler.model.ClockedDoBlockInfo;
import name.martingeisse.mahdl.compiler.model.ContinuousDoBlockInfo;
import name.martingeisse.mahdl.compiler.model.GenerationModel;
import name.martingeisse.mahdl.compiler.model.ModuleInstanceInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Predicate;

import static name.martingeisse.mahdl.gradle.codegen.Util.signalTypeToString;
import static name.martingeisse.mahdl.gradle.codegen.Util.valueTypeToString;

/**
 *
 */
public final class CodeGenerator {

	private final GenerationModel model;
	private final DataFileFactory dataFileFactory;
	private final ProcessingSidekick sidekick;
	private final StringBuilder builder;
	private final ValueGenerator valueGenerator;
	private final ExpressionGenerator expressionGenerator;
	private final ContinuousStatementExpressionGenerator continuousStatementExpressionGenerator;
	private final ClockedStatementGenerator clockedStatementGenerator;

	public CodeGenerator(GenerationModel model, DataFileFactory dataFileFactory, ErrorHandler errorHandler) {
		this.model = model;
		this.dataFileFactory = dataFileFactory;
		this.sidekick = new ProcessingSidekick(errorHandler);
		this.builder = new StringBuilder();
		this.valueGenerator = new ValueGenerator(model, builder, dataFileFactory);
		this.expressionGenerator = new ExpressionGenerator(model, builder, valueGenerator, sidekick);
		this.continuousStatementExpressionGenerator = new ContinuousStatementExpressionGenerator(model, builder);
		this.clockedStatementGenerator = new ClockedStatementGenerator(model, builder, valueGenerator, expressionGenerator, sidekick);
	}

	public void run() {

		// package declaration
		if (!model.getPackageName().isEmpty()) {
			builder.append("package ").append(model.getPackageName()).append(";\n");
			builder.append("\n");
		}

		// imports
		builder.append("import com.google.common.collect.ImmutableList;\n");
		builder.append("import name.martingeisse.esdk.core.rtl.*;\n");
		builder.append("import name.martingeisse.esdk.core.rtl.block.*;\n");
		builder.append("import name.martingeisse.esdk.core.rtl.block.statement.*;\n");
		builder.append("import name.martingeisse.esdk.core.rtl.block.statement.target.*;\n");
		builder.append("import name.martingeisse.esdk.core.rtl.memory.*;\n");
		builder.append("import name.martingeisse.esdk.core.rtl.signal.*;\n");
		builder.append("import name.martingeisse.esdk.core.rtl.signal.connector.*;\n");
		builder.append("import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;\n");
		builder.append("import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.*;\n");
		builder.append("import name.martingeisse.esdk.core.util.*;\n");
		builder.append("import name.martingeisse.esdk.core.util.vector.VectorValue;\n");
		builder.append("\n");

		// interface intro
		builder.append("public interface ").append(model.getLocalName()).append(" extends RtlItemOwned {\n");
		for (ModulePort port : model.getDataPorts()) {
			if (port.getDirection() == PortDirection.IN) {
				builder.append("	void set").append(StringUtils.capitalize(port.getName())).append("(")
					.append(signalTypeToString(port.getProcessedDataType())).append(' ').append(port.getName())
					.append(");\n");
			} else {
				builder.append("	").append(signalTypeToString(port.getProcessedDataType())).append(" get")
					.append(StringUtils.capitalize(port.getName())).append("();\n");
			}
		}
		builder.append("\n");

		// real implementation and connector implementation classes
		if (!model.getModuleDefinition().isNative()) {
			writeImplementationClass(false);
		}
		writeImplementationClass(true);

		// outro
		builder.append("}\n");
		builder.append("\n");

	}

	private void writeImplementationClass(boolean connector) {

		// class intro
		builder.append("	class " + (connector ? "Connector" : "Implementation") + " extends RtlItem implements ").append(model.getLocalName()).append(" {\n");

		// constants
		if (!connector) {
			for (Constant constant : model.getConstants()) {
				if (constant.getProcessedDataType().getFamily() != ProcessedDataType.Family.MATRIX) {
					builder.append("\n");
					builder.append("		public static final ").append(valueTypeToString(constant.getProcessedDataType()))
							.append(" _").append(constant.getName()).append(" = ")
							.append(valueGenerator.buildValue(constant.getValue())).append(";\n");
				}
			}
		}

		// fields part: clocks
		for (ModulePort clock : model.getClocks()) {
			builder.append("\n");
			builder.append("		public final RtlClockNetwork _").append(clock.getName()).append(";\n");
		}

		// fields part: signal connectors for data ports and local signals (not including registers)
		for (SignalLike signalLike : model.getSignalConnectors()) {
			if (!connector || signalLike instanceof ModulePort) {
				builder.append("\n");
				if (signalLike.getProcessedDataType().getFamily() == ProcessedDataType.Family.BIT) {
					builder.append("		public final RtlBitSignalConnector _").append(signalLike.getName()).append(";\n");
				} else {
					builder.append("		public final RtlVectorSignalConnector _").append(signalLike.getName()).append(";\n");
				}
			}
		}

		// fields part: register variables
		if (!connector) {
			for (ClockedDoBlockInfo doBlockInfo : model.getClockedDoBlockInfos()) {
				for (Register register : doBlockInfo.getRegisters()) {
					builder.append("\n");
					switch (register.getProcessedDataType().getFamily()) {

						case BIT:
							builder.append("		public final RtlProceduralBitRegister _").append(register.getName()).append(";\n");
							break;

						case VECTOR:
							builder.append("		public final RtlProceduralVectorRegister _").append(register.getName()).append(";\n");
							break;

						case MATRIX:
							builder.append("		public final RtlProceduralMemory _").append(register.getName()).append(";\n");
							break;

					}
				}
			}
		}

		// fields part: module instances
		if (!connector) {
			for (ModuleInstance moduleInstance : model.getModuleInstances()) {
				builder.append("\n");
				builder.append("		public final ").append(moduleInstance.getModuleApi().getModuleIdentifier())
						.append(" _").append(moduleInstance.getName()).append(";\n");
			}
		}

		// constructor intro
		builder.append("\n");
		builder.append("		public " + (connector ? "Connector" : "Implementation") + "(RtlRealm realm");
		for (ModulePort port : model.getClocks()) {
			builder.append(", RtlClockNetwork ").append(port.getName());
		}
		builder.append(") {\n");
		builder.append("			super(realm);\n");

		// assign clock networks (final variables)
		for (ModulePort port : model.getClocks()) {
			builder.append("			this._").append(port.getName()).append(" = ").append(port.getName()).append(";\n");
		}

		// definition part: create module instances
		if (!connector) {
			for (ModuleInstanceInfo info : model.getModuleInstanceInfos()) {
				builder.append("			this._").append(info.getModuleInstance().getName()).append(" = create")
						.append(StringUtils.capitalize(info.getModuleInstance().getName()))
						.append("(getRealm()");
				for (String localClock : info.getLocalClocksToPass()) {
					builder.append(", _").append(localClock);
				}
				builder.append(");\n");
				builder.append("			this._").append(info.getModuleInstance().getName()).append(".getRtlItem().setHierarchyParent(this);\n");
				builder.append("			this._").append(info.getModuleInstance().getName()).append(".getRtlItem().setName(")
						.append(Util.buildJavaStringLiteral(info.getModuleInstance().getName())).append(");\n");
			}
		}

		// definition part: create signal connectors
		for (SignalLike signalLike : model.getSignalConnectors()) {
			if (!connector || signalLike instanceof ModulePort) {
				builder.append("			this._").append(signalLike.getName()).append(" = new ");
				if (signalLike.getProcessedDataType().getFamily() == ProcessedDataType.Family.BIT) {
					builder.append("RtlBitSignalConnector(realm);\n");
				} else {
					int width = ((ProcessedDataType.Vector) signalLike.getProcessedDataType()).getSize();
					builder.append("RtlVectorSignalConnector(realm, ").append(width).append(");\n");
				}
				builder.append("			this._").append(signalLike.getName()).append(".setHierarchyParent(this);\n");
				builder.append("			this._").append(signalLike.getName()).append(".setName(")
						.append(Util.buildJavaStringLiteral(signalLike.getName())).append(");\n");
			}
		}

		// definition part: create clocked do-blocks and registers
		if (!connector) {
			for (ClockedDoBlockInfo doBlockInfo : model.getClockedDoBlockInfos()) {
				ProcessedExpression clock = doBlockInfo.getDoBlock().getClock();
				if (!(clock instanceof SignalLikeReference)) {
					CompilerContext.get().reportError(clock, "this compiler currently only supports clocks that are input ports");
					continue;
				}
				String clockName = ((SignalLikeReference) clock).getDefinition().getName();

				builder.append("			RtlClockedBlock ").append(doBlockInfo.getName()).append(" = new RtlClockedBlock(_");
				builder.append(clockName);
				builder.append(");\n");
				for (Register register : doBlockInfo.getRegisters()) {
					String initializerValue = (register.getInitializerValue() == null ? null :
							valueGenerator.buildValue(register.getInitializerValue()));
					builder.append("			_").append(register.getName()).append(" = ").append(doBlockInfo.getName());
					switch (register.getProcessedDataType().getFamily()) {

						case BIT: {
							builder.append(".createBit(");
							break;
						}

						case VECTOR: {
							ProcessedDataType.Vector vectorType = (ProcessedDataType.Vector) register.getProcessedDataType();
							builder.append(".createVector(").append(vectorType.getSize());
							if (initializerValue != null) {
								builder.append(", ");
							}
							break;
						}

						case MATRIX: {
							builder.append(".createMemory(");
							if (initializerValue == null) {
								ProcessedDataType.Matrix matrixType = (ProcessedDataType.Matrix) register.getProcessedDataType();
								builder.append(matrixType.getFirstSize()).append(", ").append(matrixType.getSecondSize());
							}
						}

					}
					if (initializerValue != null) {
						builder.append(initializerValue);
					}
					builder.append(");\n");
					builder.append("			_").append(register.getName()).append(".setHierarchyParent(this);\n");
					builder.append("			_").append(register.getName()).append(".setName(").
							append(Util.buildJavaStringLiteral(register.getName())).append(");\n");
				}
			}
		}

		// implementation part: generate signal connector inputs from signal initializers
		if (!connector) {
			for (Signal signal : model.getSignals()) {
				ProcessedExpression initializer = signal.getProcessedInitializer();
				if (initializer != null) {
					String expression = expressionGenerator.buildExpression(initializer);
					builder.append("			_").append(signal.getName()).append(".setConnected(").append(expression).append(");\n");
				}
			}
		}

		// implementation part: generate signal connector inputs from continuous do-blocks
		if (!connector) {
			for (ContinuousDoBlockInfo doBlockInfo : model.getContinuousDoBlockInfos()) {
				for (SignalLike target : doBlockInfo.getSignalLikes()) {
					Predicate<ProcessedExpression> leftHandSideMatcher = expression -> {
						if (expression instanceof InstancePortReference) {
							return false;
						}
						if (!(expression instanceof SignalLikeReference)) {
							CompilerContext.get().reportError(expression,
									"only assignment to whole registers are currently supported");
							return false;
						}
						return (((SignalLikeReference) expression).getDefinition() == target);
					};
					ProcessedExpression equivalentExpression = continuousStatementExpressionGenerator.buildEquivalentExpression(
							doBlockInfo, target.getProcessedDataType(), leftHandSideMatcher);
					String expressionText = expressionGenerator.buildExpression(equivalentExpression);
					builder.append("			_").append(target.getName()).append(".setConnected(").append(expressionText).append(");\n");
				}
				for (InstancePortReference target : doBlockInfo.getInstancePortReferences()) {
					if (target.getPort().getDataType().getFamily() == ProcessedDataType.Family.CLOCK) {
						// clocks get passed as constructor arguments in module instantiation
						continue;
					}
					Predicate<ProcessedExpression> leftHandSideMatcher = expression -> {
						if (expression instanceof InstancePortReference) {
							InstancePortReference reference = (InstancePortReference) expression;
							return reference.isSameAs(target);
						}
						return false;
					};
					ProcessedExpression equivalentExpression = continuousStatementExpressionGenerator.buildEquivalentExpression(
							doBlockInfo, target.getDataType(), leftHandSideMatcher);
					String expressionText = expressionGenerator.buildExpression(equivalentExpression);
					builder.append("			_").append(target.getModuleInstance().getName()).append(".set")
							.append(StringUtils.capitalize(target.getPort().getName())).append("(")
							.append(expressionText).append(");\n");
				}

			}
		}

		// implementation part: generate clocked do-block statements
		if (!connector) {
			for (ClockedDoBlockInfo doBlockInfo : model.getClockedDoBlockInfos()) {
				clockedStatementGenerator.generateStatements(doBlockInfo.getName() + ".getStatements()", doBlockInfo.getDoBlock().getBody());
			}
		}

		// end of constructor
		builder.append("		}\n");
		builder.append("\n");

		// accessors part: generate port accessors
		for (ModulePort port : model.getDataPorts()) {
			if (port.getDirection() == PortDirection.IN) {

				// setter for the caller
				builder.append("		public void set").append(StringUtils.capitalize(port.getName())).append("(")
						.append(signalTypeToString(port.getProcessedDataType())).append(' ').append(port.getName())
						.append(") {\n");
				builder.append("			this._").append(port.getName()).append(".setConnected(").append(port.getName()).append(");\n");
				builder.append("		}\n");
				builder.append("\n");

				// getter for the connector socket (returns the signal connector to be independent from setter calls)
				if (connector) {
					builder.append("		public ").append(signalTypeToString(port.getProcessedDataType())).append(" get")
							.append(StringUtils.capitalize(port.getName())).append("Socket() {\n");
					builder.append("			return _").append(port.getName()).append(";\n");
					builder.append("		}\n");
					builder.append("\n");
				}

			} else {

				// getter for the caller (returns the signal connector to be independent from setter calls)
				builder.append("		public ").append(signalTypeToString(port.getProcessedDataType())).append(" get")
						.append(StringUtils.capitalize(port.getName())).append("() {\n");
				builder.append("			return _").append(port.getName()).append(";\n");
				builder.append("		}\n");
				builder.append("\n");

				// setter for the connector socket
				if (connector) {
					builder.append("		public void set").append(StringUtils.capitalize(port.getName())).append("Socket(")
							.append(signalTypeToString(port.getProcessedDataType())).append(' ').append(port.getName())
							.append(") {\n");
					builder.append("			this._").append(port.getName()).append(".setConnected(").append(port.getName()).append(");\n");
					builder.append("		}\n");
					builder.append("\n");
				}

			}
		}

		// factory methods for creating module instances (can be overridden for dependency injection)
        // TODO when using native modules, we should probably make the factory method abstract -- but this ripples
        // through the module hierarchy up to the top module. OTOH this just reflects that the caller must indeed
        // subclass all those modules -- inconvenient but actually correct. However, for now we take the easy route and
        // throw an exception instead of making the method abstract.
		if (!connector) {
			for (ModuleInstanceInfo info : model.getModuleInstanceInfos()) {
				builder.append("		protected ").append(info.getCanonicalModuleName())
						.append(" create").append(StringUtils.capitalize(info.getModuleInstance().getName()))
						.append("(RtlRealm realm");
				for (String clockToPass : info.getLocalClocksToPass()) {
					builder.append(", RtlClockNetwork ").append(clockToPass);
				}
				builder.append(") {\n");
				if (info.getModuleInstance().getModuleApi().isNative()) {
					builder.append("			throw new UnsupportedOperationException(\"submodule is native\");\n");
				} else {
					builder.append("			return new ").append(info.getCanonicalModuleName()).append(".Implementation(realm");
					for (String clockToPass : info.getLocalClocksToPass()) {
						builder.append(", ").append(clockToPass);
					}
					builder.append(");\n");
				}
				builder.append("		}\n");
				builder.append("\n");
			}
		}

		// custom verilog contribution (not needed since the RTL classes used contribute everything needed)
		builder.append("		@Override\n");
		builder.append("		public VerilogContribution getVerilogContribution() {\n");
		builder.append("			return new EmptyVerilogContribution();\n");
		builder.append("		}\n");
		builder.append("\n");
        builder.append("	}\n");
        builder.append("\n");

	}

	public String getCode() {
		return builder.toString();
	}

	public interface DataFileFactory {
		String getAnchorClassName();
		void createDataFile(String filename, byte[] data);
	}

}
