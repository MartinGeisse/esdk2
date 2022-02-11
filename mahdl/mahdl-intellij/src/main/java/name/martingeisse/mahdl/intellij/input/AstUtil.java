package name.martingeisse.mahdl.intellij.input;

import com.intellij.lang.ASTNode;
import com.intellij.psi.impl.source.tree.LeafElement;

public final class AstUtil {

    private AstUtil() {
    }

    public static ASTNode getFirstToken(ASTNode node) {
        if (node instanceof LeafElement) {
            return node;
        }
        for (ASTNode child : node.getChildren(null)) {
            ASTNode firstToken = getFirstToken(child);
            if (firstToken != null) {
                return firstToken;
            }
        }
        return null;
    }

    public static ASTNode getLastToken(ASTNode node) {
        if (node instanceof LeafElement) {
            return node;
        }
        ASTNode[] children = node.getChildren(null);
        for (int i = children.length - 1; i >= 0; i--) {
            ASTNode child = children[i];
            ASTNode lastToken = getLastToken(child);
            if (lastToken != null) {
                return lastToken;
            }
        }
        return null;
    }

}
