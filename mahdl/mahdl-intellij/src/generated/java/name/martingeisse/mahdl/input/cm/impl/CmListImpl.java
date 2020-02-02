package name.martingeisse.mahdl.input.cm.impl;

import com.google.common.collect.ImmutableList;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import name.martingeisse.mahdl.input.cm.CmList;
import name.martingeisse.mahdl.input.cm.CmNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class CmListImpl<T extends CmNode, PSI extends PsiElement> extends ASTWrapperPsiElement implements PsiCm, CmList<T> {

	private final TokenSet elementTypes;
	private final Class<T> elementClass;
	private final Class<PSI> elementClassPsi;

	public CmListImpl(@NotNull ASTNode node, TokenSet elementTypes, Class<T> elementClass, Class<PSI> elementClassPsi) {
		super(node);
		this.elementTypes = elementTypes;
		this.elementClass = elementClass;
		this.elementClassPsi = elementClassPsi;
	}

	@Override
	public <S extends CmNode> CmList<S> cast(Class<S> subclass) {
		if (!elementClass.isAssignableFrom(subclass)) {
			throw new ClassCastException(subclass.getName() + " is not a subclass of " + elementClass.getName());
		}
		return (CmList) this;
	}

	public <X extends PsiElement> CmListImpl<T, X> castPsi(Class<X> subclass) {
		if (!elementClassPsi.isAssignableFrom(subclass)) {
			throw new ClassCastException(subclass.getName() + " is not a subclass of " + elementClassPsi.getName());
		}
		return (CmListImpl) this;
	}

	@Override
	public final List<T> getAll() {
		List<T> list = new ArrayList<>();
		addAllTo(list);
		return list;
	}

	public final List<PSI> getAllPsi() {
		List<PSI> list = new ArrayList<>();
		addAllToPsi(list);
		return list;
	}

	@Override
	public final void addAllTo(List<T> list) {
		foreach(list::add);
	}

	public final void addAllToPsi(List<PSI> list) {
		foreachPsi(list::add);
	}

	@Override
	public final void addAllTo(ImmutableList.Builder<T> builder) {
		foreach(builder::add);
	}

	public final void addAllToPsi(ImmutableList.Builder<PSI> builder) {
		foreachPsi(builder::add);
	}

	@Override
	public final void foreach(Consumer<T> consumer) {
		foreachPsi((PSI childPsi) -> {
			consumer.accept(elementClass.cast(InternalPsiUtil.getCmFromPsi(childPsi)));
		});
	}

	public final void foreachPsi(Consumer<PSI> consumer) {
		InternalPsiUtil.foreachChild(this, child -> {
			if (elementTypes.contains(child.getNode().getElementType())) {
				consumer.accept(elementClassPsi.cast(child));
				return;
			}
			if (child instanceof CmListImpl<?, ?> && child.getNode().getElementType() == getNode().getElementType()) {
				CmListImpl<?, ?> typedChild = (CmListImpl<?, ?>) child;
				typedChild.castPsi(elementClassPsi).foreachPsi(consumer);
			}
		});
	}

}
