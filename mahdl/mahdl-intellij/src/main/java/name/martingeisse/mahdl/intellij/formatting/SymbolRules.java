package name.martingeisse.mahdl.intellij.formatting;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import name.martingeisse.mahdl.input.Symbols;
import name.martingeisse.mahdl.input.cm.ImplementationItem_ModuleInstanceDefinitionGroup;
import name.martingeisse.mahdl.input.cm.Statement;
import name.martingeisse.mahdl.intellij.input.AstUtil;

import java.util.function.Predicate;

@SuppressWarnings("RedundantIfStatement")
final class SymbolRules {

    private SymbolRules() {
    }

    static boolean isLeftAttached(ASTNode tokenNode) {
        IElementType type = tokenNode.getElementType();
        if (type == Symbols.CLOSING_PARENTHESIS ||
                type == Symbols.CLOSING_SQUARE_BRACKET ||
                type == Symbols.COMMA ||
                type == Symbols.SEMICOLON ||
                type == Symbols.COLON ||
                type == Symbols.DOT) {
            return true;
        }

        // OP_TIMES may be a combinatorial do-block trigger, but the parantheses already attach to it
        return false;
    }

    static boolean isRightAttached(ASTNode tokenNode) {
        IElementType type = tokenNode.getElementType();
        if (type == Symbols.OPENING_PARENTHESIS ||
                type == Symbols.OPENING_SQUARE_BRACKET ||
                type == Symbols.DOT ||
                type == Symbols.OP_NOT) {
            return true;
        }
        if (type == Symbols.OP_PLUS || type == Symbols.OP_MINUS) {
            IElementType parentType = tokenNode.getTreeParent().getElementType();
            return parentType == Symbols.expression_UnaryPlus || parentType == Symbols.expression_UnaryMinus;
        }
        if (type == Symbols.COLON) {
            IElementType parentType = tokenNode.getTreeParent().getElementType();
            return parentType == Symbols.expression_RangeSelection;
        }

        // OP_TIMES may be a combinatorial do-block trigger, but the parantheses already attach to it
        return false;
    }

    static boolean isLineStarter(ASTNode tokenNode) {
        IElementType type = tokenNode.getElementType();

        // start of interface block, as well as lines in it
        if (type == Symbols.KW_INTERFACE || type == Symbols.KW_IN || type == Symbols.KW_OUT) {
            return true;
        }

        // keywords that start an implementation item
        if (type == Symbols.KW_CONSTANT ||
                type == Symbols.KW_SIGNAL ||
                type == Symbols.KW_REGISTER ||
                type == Symbols.KW_DO) {
            return true;
        }

        // an identifier can start an implementation item (as part of a qualified module name)
        if (type == Symbols.IDENTIFIER) {
            if (isFirstTokenOf(tokenNode, node -> node.getPsi() instanceof ImplementationItem_ModuleInstanceDefinitionGroup)) {
                return true;
            }
        }

        // Closing curly brace goes in the next line and starts it.
        if (type == Symbols.CLOSING_CURLY_BRACE) {
            return true;
        }

        // Statements can unfortunately start with a lot of tokens, so we check for start of statement.
        // Special rule: Opening curly brace goes in the same line, even at start of statement (block).
        if (type != Symbols.OPENING_CURLY_BRACE && isFirstTokenOf(tokenNode, node -> node.getPsi() instanceof Statement)) {
            return true;
        }

        // value cases and default cases start on a new line
        if (type == Symbols.KW_CASE || type == Symbols.KW_DEFAULT) {
            return true;
        }

        return false;
    }

    static boolean isLineEnder(ASTNode tokenNode) {
        IElementType type = tokenNode.getElementType();

        // Opening curly brace goes in the same line and ends it.
        if (type == Symbols.OPENING_CURLY_BRACE) {
            return true;
        }

        // Semicolon ends a lot of things (implementation items, statements, ...)
        if (type == Symbols.SEMICOLON) {
            return true;
        }

        // Closing curly brace sometimes ends statements.
        if (type == Symbols.CLOSING_CURLY_BRACE) {
            // the closing curly brace of the interface block is handled by the fact that it can only be followed by
            // implementation items, which are already line starters
            return isLastTokenOf(tokenNode, node -> node.getPsi() instanceof Statement);
        }

        if (type == Symbols.COLON) {
            IElementType parentType = tokenNode.getTreeParent().getElementType();
            return parentType == Symbols.statementCaseItem_Value || parentType == Symbols.statementCaseItem_Default;
        }

        return false;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // helpers
    // ----------------------------------------------------------------------------------------------------------------

    private static boolean isFirstTokenOf(ASTNode tokenNode, Predicate<ASTNode> predicate) {
        // this may seem inefficient, but keep in mind that composite AST nodes can contain zero tokens
        for (ASTNode node = tokenNode; node != null; node = node.getTreeParent()) {
            if (predicate.test(node) && AstUtil.getFirstToken(node) == tokenNode) {
                return true;
            }
        }
        return false;
    }

    private static boolean isLastTokenOf(ASTNode tokenNode, Predicate<ASTNode> predicate) {
        // this may seem inefficient, but keep in mind that composite AST nodes can contain zero tokens
        for (ASTNode node = tokenNode; node != null; node = node.getTreeParent()) {
            if (predicate.test(node) && AstUtil.getLastToken(node) == tokenNode) {
                return true;
            }
        }
        return false;
    }

}
