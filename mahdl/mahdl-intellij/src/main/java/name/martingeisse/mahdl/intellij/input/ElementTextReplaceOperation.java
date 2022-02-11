package name.martingeisse.mahdl.intellij.input;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafElement;

public final class ElementTextReplaceOperation {

    private LeafElement firstLeafElement = null;

    private ElementTextReplaceOperation() {
    }

    private void clearingPhase(ASTNode node) {
        if (node instanceof LeafElement) {
            LeafElement leafElement = (LeafElement)node;
            if (firstLeafElement == null) {
                firstLeafElement = leafElement;
            } else {
                leafElement.replaceWithText("");
            }
        } else {
            for (ASTNode child : node.getChildren(null)) {
                clearingPhase(child);
            }
        }
    }

    public static PsiElement run(PsiElement element, String newText) {
        ElementTextReplaceOperation operation = new ElementTextReplaceOperation();
        operation.clearingPhase(element.getNode());
        if (operation.firstLeafElement != null) {
            return (PsiElement) operation.firstLeafElement.replaceWithText(newText);
        } else {
            // if we have no first leaf element, nothing has been replaced / cleared either, so the
            // argument element is definitely still valid
            return element;
        }
    }

}
