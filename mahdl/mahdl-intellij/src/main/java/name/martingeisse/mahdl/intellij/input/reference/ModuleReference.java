/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.intellij.input.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import name.martingeisse.mahdl.common.ReferenceResolutionException;
import name.martingeisse.mahdl.input.cm.CmUtil;
import name.martingeisse.mahdl.input.cm.impl.ModuleImpl;
import name.martingeisse.mahdl.input.cm.impl.QualifiedModuleNameImpl;
import name.martingeisse.mahdl.intellij.input.PsiUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class ModuleReference implements PsiReference {

	private final QualifiedModuleNameImpl moduleName;

	public ModuleReference(@NotNull QualifiedModuleNameImpl moduleName) {
		this.moduleName = moduleName;
	}

	@Override
	@NotNull
	public PsiElement getElement() {
		return moduleName;
	}

	@Override
	@NotNull
	public TextRange getRangeInElement() {
		return new TextRange(0, getCanonicalText().length());
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		try {
			return PsiUtil.resolveModuleName(moduleName);
		} catch (ReferenceResolutionException e) {
			return null;
		}
	}

	@NotNull
	@Override
	public String getCanonicalText() {
		// this removes whitespace and comments from the module name
		String[] segments = CmUtil.parseQualifiedModuleName(moduleName);
		return StringUtils.join(segments, '.');
	}

	@Override
	@NotNull
	public PsiElement handleElementRename(@Nullable String newName) throws IncorrectOperationException {
		// TODO check PSI afterwards
		// return (PsiElement) moduleName.replaceWithText(newName);
		// moduleName.getNode().addLeaf(Symbols.qualifiedModuleName, "foo.bar", null);
		throw new IncorrectOperationException("not yet supported");
	}

	@Override
	@NotNull
	public PsiElement bindToElement(@NotNull PsiElement psiElement) throws IncorrectOperationException {
		if (psiElement instanceof ModuleImpl) {
			throw new IncorrectOperationException("cannot bind this reference to a non-module PSI node");
		}
		throw new IncorrectOperationException("not yet supported"); // TODO I have no idea how to manipulate the PSI that way
	}

	@Override
	public boolean isReferenceTo(@Nullable PsiElement psiElement) {
		if (psiElement instanceof ModuleImpl) {
			String canonicalReferenceModuleName = CmUtil.canonicalizeQualifiedModuleName(moduleName);
			String canonicalCandidateModuleName = CmUtil.canonicalizeQualifiedModuleName(((ModuleImpl) psiElement).getModuleName());
			if (canonicalCandidateModuleName.equals(canonicalReferenceModuleName)) {
				PsiElement resolved = resolve();
				return (resolved != null && resolved.equals(psiElement));
			}
		}
		return false;
	}

	@NotNull
	@Override
	public Object[] getVariants() {

		// note: if this returns PSI elements, they must be PsiNamedElement or contain the name in meta-data

		// A simple file-based index doesn't work here since it doesn't delete obsolete keys, and we can't
		// distinguish those from real ones. If performance becomes a problem, use stub trees (stub-backed PSI
		// instead of AST-backed PSI) and stub indexes.
		Set<String> variants = new HashSet<>();
		VirtualFile[] sourceRoots = PsiUtil.getSourceRoots(moduleName);
		if (sourceRoots != null) {
			for (VirtualFile sourceRoot : sourceRoots) {
				VfsUtilCore.visitChildrenRecursively(sourceRoot, new VirtualFileVisitor<Object>(VirtualFileVisitor.SKIP_ROOT) {
					@Override
					public boolean visitFile(@NotNull VirtualFile file) {
						String name = file.getName();
						String prefix = (String) getCurrentValue();
						if (!file.isDirectory() && name.endsWith(".mahdl")) {
							String simpleModuleName = name.substring(0, name.length() - ".mahdl".length());
							String fullModuleName = (prefix == null ? simpleModuleName : (prefix + '.' + simpleModuleName));
							variants.add(fullModuleName);
						}
						setValueForChildren(prefix == null ? name : (prefix + '.' + name));
						return (name.indexOf('.') < 0);
					}
				});
			}
		}
		return variants.toArray();

	}

	@Override
	public boolean isSoft() {
		return false;
	}

}
