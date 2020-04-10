/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.intellij.input;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.Consumer;
import com.intellij.util.FileContentUtil;
import com.intellij.util.IncorrectOperationException;
import name.martingeisse.mahdl.common.ReferenceResolutionException;
import name.martingeisse.mahdl.input.cm.CmUtil;
import name.martingeisse.mahdl.input.cm.QualifiedModuleName;
import name.martingeisse.mahdl.input.cm.impl.*;
import name.martingeisse.mahdl.intellij.MahdlSourceFile;
import name.martingeisse.mahdl.intellij.input.reference.LocalReference;
import name.martingeisse.mahdl.intellij.input.reference.ModuleInstancePortReference;
import name.martingeisse.mahdl.intellij.input.reference.ModuleReference;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public final class PsiUtil {

	// prevent instantiation
	private PsiUtil() {
	}

	//
	// general
	//

	@NotNull
	public static PsiElement setText(@NotNull LeafPsiElement element, @NotNull String newText) {
		return (PsiElement) element.replaceWithText(newText);
	}

	@Nullable
	public static <T> T getAncestor(@NotNull PsiElement element, @NotNull Class<T> nodeClass) {
		while (true) {
			if (nodeClass.isInstance(element)) {
				return nodeClass.cast(element);
			}
			if (element == null || element instanceof PsiFile) {
				return null;
			}
			element = element.getParent();
		}
	}

	public static void foreachPsiNode(@NotNull PsiElement root, @NotNull Consumer<PsiElement> consumer) {
		if (root instanceof ASTWrapperPsiElement) {
			InternalPsiUtil.foreachChild((ASTWrapperPsiElement) root, child -> {
				consumer.consume(child);
				foreachPsiNode(child, consumer);
			});
		}
	}

	@Nullable
	public static VirtualFile getVirtualFile(@NotNull PsiElement psiElement) {
		PsiFile originPsiFile = psiElement.getContainingFile();
		return (originPsiFile == null ? null : originPsiFile.getOriginalFile().getVirtualFile());
	}

	@Nullable
	public static VirtualFile[] getSourceRoots(@NotNull PsiElement psiElement) {
		PsiFile psiFile = psiElement.getContainingFile();
		if (psiFile == null) {
			return null;
		}
		VirtualFile virtualFile = psiFile.getOriginalFile().getVirtualFile();
		if (virtualFile == null) {
			return null;
		}
		com.intellij.openapi.module.Module projectModule =
			ProjectRootManager.getInstance(psiFile.getProject()).getFileIndex().getModuleForFile(virtualFile);
		if (projectModule == null) {
			return null;
		}
		List<VirtualFile> sourceRoots = new ArrayList<>();
		for (VirtualFile moduleRoot : ModuleRootManager.getInstance(projectModule).getContentRoots()) {
			VirtualFile srcFolder = moduleRoot.findChild("src");
			if (srcFolder == null) {
				continue;
			}
			VirtualFile mahdlFolder = srcFolder.findChild("mahdl");
			if (mahdlFolder == null) {
				continue;
			}
			sourceRoots.add(mahdlFolder);
		}
		return sourceRoots.toArray(new VirtualFile[0]);
	}

	@NotNull
	public static ModuleImpl resolveModuleName(QualifiedModuleNameImpl moduleName) throws ReferenceResolutionException {
		String[] segments = CmUtil.parseQualifiedModuleName(moduleName);
		VirtualFile[] sourceRoots = getSourceRoots(moduleName);
		if (sourceRoots == null) {
			throw new ReferenceResolutionException("could not determine source roots");
		}
		ReferenceResolutionException exception = null;
		boolean multipleExceptions = false;
		sourceRootLoop: for (VirtualFile sourceRoot : sourceRoots) {
			VirtualFile targetVirtualFile = sourceRoot;
			for (int i = 0; i < segments.length - 1; i++) {
				targetVirtualFile = targetVirtualFile.findChild(segments[i]);
				if (targetVirtualFile == null) {
					continue sourceRootLoop;
				}
			}
			targetVirtualFile = targetVirtualFile.findChild(segments[segments.length - 1] + ".mahdl");
			if (targetVirtualFile == null) {
				continue sourceRootLoop;
			}
			PsiFile targetPsiFile = PsiManager.getInstance(moduleName.getProject()).findFile(targetVirtualFile);
			if (!(targetPsiFile instanceof MahdlSourceFile)) {
				multipleExceptions |= (exception != null);
				exception = new ReferenceResolutionException(targetVirtualFile.getPath() + " is not a MaHDL source file");
				continue sourceRootLoop;
			}
			ModuleImpl module = ((MahdlSourceFile) targetPsiFile).getModule();
			if (module == null) {
				multipleExceptions |= (exception != null);
				exception = new ReferenceResolutionException("target file does not contain a module");
				continue sourceRootLoop;
			}
			return module;
		}
		if (multipleExceptions || exception == null) {
			exception = new ReferenceResolutionException("could not resolve module " + CmUtil.canonicalizeQualifiedModuleName(moduleName));
		}
		throw exception;
	}

	//
	// naming support
	//

	@Nullable
	public static QualifiedModuleNameImpl getNameIdentifier(@NotNull ModuleImpl node) {
		return node.getModuleNamePsi();
	}

	@Nullable
	public static String getName(@NotNull ModuleImpl node) {
		QualifiedModuleName name = node.getModuleName();
		return name == null ? null : CmUtil.canonicalizeQualifiedModuleName(name);
	}

	public static PsiElement setName(@NotNull ModuleImpl node, @NotNull String newName) {
		throw new IncorrectOperationException("renaming module not yet implemented");
	}

	@Nullable
	public static LeafPsiElement getNameIdentifier(@NotNull PortDefinitionImpl node) {
		return node.getIdentifierPsi();
	}

	@Nullable
	public static LeafPsiElement getNameIdentifier(@NotNull SignalLikeDefinitionImpl node) {
		if (node instanceof SignalLikeDefinition_WithoutInitializerImpl) {
			return ((SignalLikeDefinition_WithoutInitializerImpl) node).getIdentifierPsi();
		} else if (node instanceof SignalLikeDefinition_WithInitializerImpl) {
			return ((SignalLikeDefinition_WithInitializerImpl) node).getIdentifierPsi();
		} else {
			return null;
		}
	}

	@Nullable
	public static LeafPsiElement getNameIdentifier(@NotNull ModuleInstanceDefinitionImpl node) {
		return node.getIdentifierPsi();
	}

	//
	// reference support
	//

	@NotNull
	public static PsiReference getReference(@NotNull QualifiedModuleNameImpl node) {
		return new ModuleReference(node);
	}

	@NotNull
	public static PsiReference getReference(@NotNull InstancePortNameImpl node) {
		return new ModuleInstancePortReference(node);
	}

	@NotNull
	public static PsiReference getReference(@NotNull Expression_IdentifierImpl node) {
		return new LocalReference(node.getIdentifierPsi());
	}

	@NotNull
	public static PsiReference getReference(@NotNull InstanceReferenceNameImpl node) {
		return new LocalReference(node.getIdentifierPsi());
	}

	//
	// safe delete
	//

	public static void delete(@NotNull ModuleImpl node) throws IncorrectOperationException {
		delete(node, node::superclassDelete);
	}

	public static void delete(@NotNull PortDefinitionImpl node) throws IncorrectOperationException {
		delete(node, node::superclassDelete);
	}

	public static void delete(@NotNull SignalLikeDefinitionImpl node) throws IncorrectOperationException {
		delete(node, node::superclassDelete);
	}

	public static void delete(@NotNull ModuleInstanceDefinitionImpl node) throws IncorrectOperationException {
		delete(node, node::superclassDelete);
	}

	public static void delete(@NotNull ASTWrapperPsiElement node, @NotNull Runnable actualDeleteCallback) throws IncorrectOperationException {
		PsiFile psiFile = node.getContainingFile();
		if (psiFile != null) {
			VirtualFile virtualFile = psiFile.getOriginalFile().getVirtualFile();
			if (virtualFile != null) {
				actualDeleteCallback.run();
				FileContentUtil.reparseFiles(node.getProject(), Collections.singletonList(virtualFile), false);
				return;
			}
		}
		throw new IncorrectOperationException("could not determine containing virtual file to reparse after safe delete");
	}

}
