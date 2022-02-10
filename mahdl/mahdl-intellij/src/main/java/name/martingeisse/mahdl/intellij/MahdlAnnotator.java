/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.intellij;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import name.martingeisse.mahdl.common.processor.ErrorHandler;
import name.martingeisse.mahdl.common.processor.ModuleProcessor;
import name.martingeisse.mahdl.input.cm.CmLinked;
import name.martingeisse.mahdl.input.cm.CmNode;
import name.martingeisse.mahdl.input.cm.impl.InternalPsiUtil;
import name.martingeisse.mahdl.input.cm.impl.ModuleImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public class MahdlAnnotator implements Annotator {

	/**
	 * This method gets called on ALL PsiElements, post-order.
	 */
	@Override
	public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
		IntellijEnvironment.initialize();
		if (psiElement instanceof ModuleImpl) {
			annotate((ModuleImpl) psiElement, annotationHolder);
		}
	}

	private void annotate(@NotNull ModuleImpl module, @NotNull AnnotationHolder annotationHolder) {
		ErrorHandler errorHandler = new ErrorHandler() {

			@Override
			public void onError(@NotNull CmLinked errorSource, @NotNull String message, @Nullable Throwable t) {
				CmNode node = errorSource.getCmNode();
				if (node == null) {
					node = module;
				}
				annotationHolder.newAnnotation(HighlightSeverity.ERROR, message)
					.range(InternalPsiUtil.getPsiFromCm(node))
					.create();
			}

			@Override
			public void onDiagnostic(@NotNull CmLinked errorSource, @NotNull String message, @Nullable Throwable t) {
				// ignore
			}

		};
		new ModuleProcessor(module, errorHandler).process();
	}

}
