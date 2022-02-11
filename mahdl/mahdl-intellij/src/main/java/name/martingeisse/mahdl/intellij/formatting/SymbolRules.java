package name.martingeisse.mahdl.intellij.formatting;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import name.martingeisse.mahdl.input.Symbols;
import name.martingeisse.mahdl.input.cm.Statement;
import name.martingeisse.mahdl.intellij.input.AstUtil;

import java.util.function.Predicate;

final class SymbolRules {

    private SymbolRules() {
    }

/*
TODO:

	switch is line starter if it is a switch statement, but not if it is a switch expression
		generally, the first token if a statement is a line-starter. This covers switch statements.
	generally, the first token of an implementation item is a line starter
	generally, the last token of an implementation item or statement is a line ender
	OP_TIMES, identifier are used as do-block triggers

	public static final MahdlElementType KW_CONSTANT = new MahdlElementType("KW_CONSTANT");
	public static final MahdlElementType KW_DEFAULT = new MahdlElementType("KW_DEFAULT");
	public static final MahdlElementType KW_DO = new MahdlElementType("KW_DO");
	public static final MahdlElementType KW_ELSE = new MahdlElementType("KW_ELSE");
	public static final MahdlElementType KW_IF = new MahdlElementType("KW_IF");
	public static final MahdlElementType KW_IN = new MahdlElementType("KW_IN");
	public static final MahdlElementType KW_INTEGER = new MahdlElementType("KW_INTEGER");
	public static final MahdlElementType KW_INTERFACE = new MahdlElementType("KW_INTERFACE");
	public static final MahdlElementType KW_MATRIX = new MahdlElementType("KW_MATRIX");
	public static final MahdlElementType KW_MODULE = new MahdlElementType("KW_MODULE");
	public static final MahdlElementType KW_NATIVE = new MahdlElementType("KW_NATIVE");
	public static final MahdlElementType KW_OUT = new MahdlElementType("KW_OUT");
	public static final MahdlElementType KW_REGISTER = new MahdlElementType("KW_REGISTER");
	public static final MahdlElementType KW_SIGNAL = new MahdlElementType("KW_SIGNAL");
	public static final MahdlElementType KW_SWITCH = new MahdlElementType("KW_SWITCH");

 */

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
        return false;
    }

    static boolean isLineStarter(ASTNode tokenNode) {
        IElementType type = tokenNode.getElementType();

        // Closing curly brace goes in the next line and starts it.
        if (type == Symbols.CLOSING_CURLY_BRACE) {
            return true;
        }

        // Statements can unfortunately start with a lot of tokens, so we check for start of statement.
        // Special rule: Opening curly brace goes in the same line, even at start of statement (block).
        if (type != Symbols.OPENING_CURLY_BRACE && isFirstTokenOf(tokenNode, node -> node.getPsi() instanceof Statement)) {
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
            // TODO handle the case that parent is module (end of interface clause)
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
