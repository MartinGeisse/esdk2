package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import name.martingeisse.mahdl.input.Symbols;
import name.martingeisse.mahdl.input.cm.*;

public class PsiFactory {

	public static PsiElement createPsiElement(ASTNode node) {
		IElementType type = node.getElementType();

		if (type == Symbols.synthetic_List_Statement) {
			return new CmListImpl<Statement, StatementImpl>(node, createTokenSet(Symbols.statement_Assignment, Symbols.statement_IfThen, Symbols.statement_IfThenElse, Symbols.statement_Switch, Symbols.statement_Block, Symbols.statement_Error1, Symbols.statement_Error2), Statement.class, StatementImpl.class);
		}
		if (type == Symbols.synthetic_Optional_KWNATIVE) {
			return new CmOptionalImpl<CmToken, LeafPsiElement>(node);
		}
		if (type == Symbols.synthetic_List_ExpressionCaseItem_Nonempty) {
			return new CmListImpl<ExpressionCaseItem, ExpressionCaseItemImpl>(node, createTokenSet(Symbols.expressionCaseItem_Value, Symbols.expressionCaseItem_Default), ExpressionCaseItem.class, ExpressionCaseItemImpl.class);
		}
		if (type == Symbols.literal_Integer) {
			return new Literal_IntegerImpl(node);
		}
		if (type == Symbols.literal_Vector) {
			return new Literal_VectorImpl(node);
		}
		if (type == Symbols.literal_Text) {
			return new Literal_TextImpl(node);
		}
		if (type == Symbols.implementationItem_SignalLikeDefinitionGroup) {
			return new ImplementationItem_SignalLikeDefinitionGroupImpl(node);
		}
		if (type == Symbols.implementationItem_ModuleInstanceDefinitionGroup) {
			return new ImplementationItem_ModuleInstanceDefinitionGroupImpl(node);
		}
		if (type == Symbols.implementationItem_DoBlock) {
			return new ImplementationItem_DoBlockImpl(node);
		}
		if (type == Symbols.implementationItem_Error) {
			return new ImplementationItem_ErrorImpl(node);
		}
		if (type == Symbols.qualifiedModuleName) {
			return new QualifiedModuleNameImpl(node);
		}
		if (type == Symbols.synthetic_List_Statement_Nonempty) {
			return new CmListImpl<Statement, StatementImpl>(node, createTokenSet(Symbols.statement_Assignment, Symbols.statement_IfThen, Symbols.statement_IfThenElse, Symbols.statement_Switch, Symbols.statement_Block, Symbols.statement_Error1, Symbols.statement_Error2), Statement.class, StatementImpl.class);
		}
		if (type == Symbols.signalLikeDefinition_WithoutInitializer) {
			return new SignalLikeDefinition_WithoutInitializerImpl(node);
		}
		if (type == Symbols.signalLikeDefinition_WithInitializer) {
			return new SignalLikeDefinition_WithInitializerImpl(node);
		}
		if (type == Symbols.signalLikeDefinition_Error) {
			return new SignalLikeDefinition_ErrorImpl(node);
		}
		if (type == Symbols.statement_Assignment) {
			return new Statement_AssignmentImpl(node);
		}
		if (type == Symbols.statement_IfThen) {
			return new Statement_IfThenImpl(node);
		}
		if (type == Symbols.statement_IfThenElse) {
			return new Statement_IfThenElseImpl(node);
		}
		if (type == Symbols.statement_Switch) {
			return new Statement_SwitchImpl(node);
		}
		if (type == Symbols.statement_Block) {
			return new Statement_BlockImpl(node);
		}
		if (type == Symbols.statement_Error1) {
			return new Statement_Error1Impl(node);
		}
		if (type == Symbols.statement_Error2) {
			return new Statement_Error2Impl(node);
		}
		if (type == Symbols.portDefinition) {
			return new PortDefinitionImpl(node);
		}
		if (type == Symbols.synthetic_SeparatedList_IDENTIFIER_DOT_Nonempty) {
			return new CmListImpl<CmToken, LeafPsiElement>(node, createTokenSet(Symbols.IDENTIFIER), CmToken.class, LeafPsiElement.class);
		}
		if (type == Symbols.expressionCaseItem_Value) {
			return new ExpressionCaseItem_ValueImpl(node);
		}
		if (type == Symbols.expressionCaseItem_Default) {
			return new ExpressionCaseItem_DefaultImpl(node);
		}
		if (type == Symbols.synthetic_List_ImplementationItem) {
			return new CmListImpl<ImplementationItem, ImplementationItemImpl>(node, createTokenSet(Symbols.implementationItem_SignalLikeDefinitionGroup, Symbols.implementationItem_ModuleInstanceDefinitionGroup, Symbols.implementationItem_DoBlock, Symbols.implementationItem_Error), ImplementationItem.class, ImplementationItemImpl.class);
		}
		if (type == Symbols.synthetic_SeparatedList_ModuleInstanceDefinition_COMMA_Nonempty) {
			return new CmListImpl<ModuleInstanceDefinition, ModuleInstanceDefinitionImpl>(node, createTokenSet(Symbols.moduleInstanceDefinition), ModuleInstanceDefinition.class, ModuleInstanceDefinitionImpl.class);
		}
		if (type == Symbols.moduleInstanceDefinition) {
			return new ModuleInstanceDefinitionImpl(node);
		}
		if (type == Symbols.expression_Literal) {
			return new Expression_LiteralImpl(node);
		}
		if (type == Symbols.expression_Identifier) {
			return new Expression_IdentifierImpl(node);
		}
		if (type == Symbols.expression_InstancePort) {
			return new Expression_InstancePortImpl(node);
		}
		if (type == Symbols.expression_IndexSelection) {
			return new Expression_IndexSelectionImpl(node);
		}
		if (type == Symbols.expression_RangeSelection) {
			return new Expression_RangeSelectionImpl(node);
		}
		if (type == Symbols.expression_Parenthesized) {
			return new Expression_ParenthesizedImpl(node);
		}
		if (type == Symbols.expression_FunctionCall) {
			return new Expression_FunctionCallImpl(node);
		}
		if (type == Symbols.expression_UnaryNot) {
			return new Expression_UnaryNotImpl(node);
		}
		if (type == Symbols.expression_UnaryPlus) {
			return new Expression_UnaryPlusImpl(node);
		}
		if (type == Symbols.expression_UnaryMinus) {
			return new Expression_UnaryMinusImpl(node);
		}
		if (type == Symbols.expression_BinaryPlus) {
			return new Expression_BinaryPlusImpl(node);
		}
		if (type == Symbols.expression_BinaryMinus) {
			return new Expression_BinaryMinusImpl(node);
		}
		if (type == Symbols.expression_BinaryTimes) {
			return new Expression_BinaryTimesImpl(node);
		}
		if (type == Symbols.expression_BinaryDividedBy) {
			return new Expression_BinaryDividedByImpl(node);
		}
		if (type == Symbols.expression_BinaryRemainder) {
			return new Expression_BinaryRemainderImpl(node);
		}
		if (type == Symbols.expression_BinaryEqual) {
			return new Expression_BinaryEqualImpl(node);
		}
		if (type == Symbols.expression_BinaryNotEqual) {
			return new Expression_BinaryNotEqualImpl(node);
		}
		if (type == Symbols.expression_BinaryGreaterThan) {
			return new Expression_BinaryGreaterThanImpl(node);
		}
		if (type == Symbols.expression_BinaryGreaterThanOrEqual) {
			return new Expression_BinaryGreaterThanOrEqualImpl(node);
		}
		if (type == Symbols.expression_BinaryLessThan) {
			return new Expression_BinaryLessThanImpl(node);
		}
		if (type == Symbols.expression_BinaryLessThanOrEqual) {
			return new Expression_BinaryLessThanOrEqualImpl(node);
		}
		if (type == Symbols.expression_BinaryAnd) {
			return new Expression_BinaryAndImpl(node);
		}
		if (type == Symbols.expression_BinaryOr) {
			return new Expression_BinaryOrImpl(node);
		}
		if (type == Symbols.expression_BinaryXor) {
			return new Expression_BinaryXorImpl(node);
		}
		if (type == Symbols.expression_BinaryShiftLeft) {
			return new Expression_BinaryShiftLeftImpl(node);
		}
		if (type == Symbols.expression_BinaryShiftRight) {
			return new Expression_BinaryShiftRightImpl(node);
		}
		if (type == Symbols.expression_BinaryConcat) {
			return new Expression_BinaryConcatImpl(node);
		}
		if (type == Symbols.expression_Conditional) {
			return new Expression_ConditionalImpl(node);
		}
		if (type == Symbols.synthetic_SeparatedList_Expression_COMMA_Nonempty) {
			return new CmListImpl<Expression, ExpressionImpl>(node, createTokenSet(Symbols.expression_Literal, Symbols.expression_Identifier, Symbols.expression_InstancePort, Symbols.expression_IndexSelection, Symbols.expression_RangeSelection, Symbols.expression_Parenthesized, Symbols.expression_FunctionCall, Symbols.expression_UnaryNot, Symbols.expression_UnaryPlus, Symbols.expression_UnaryMinus, Symbols.expression_BinaryPlus, Symbols.expression_BinaryMinus, Symbols.expression_BinaryTimes, Symbols.expression_BinaryDividedBy, Symbols.expression_BinaryRemainder, Symbols.expression_BinaryEqual, Symbols.expression_BinaryNotEqual, Symbols.expression_BinaryGreaterThan, Symbols.expression_BinaryGreaterThanOrEqual, Symbols.expression_BinaryLessThan, Symbols.expression_BinaryLessThanOrEqual, Symbols.expression_BinaryAnd, Symbols.expression_BinaryOr, Symbols.expression_BinaryXor, Symbols.expression_BinaryShiftLeft, Symbols.expression_BinaryShiftRight, Symbols.expression_BinaryConcat, Symbols.expression_Conditional), Expression.class, ExpressionImpl.class);
		}
		if (type == Symbols.doBlockTrigger_Combinatorial) {
			return new DoBlockTrigger_CombinatorialImpl(node);
		}
		if (type == Symbols.doBlockTrigger_Clocked) {
			return new DoBlockTrigger_ClockedImpl(node);
		}
		if (type == Symbols.doBlockTrigger_Error) {
			return new DoBlockTrigger_ErrorImpl(node);
		}
		if (type == Symbols.functionName_Identifier) {
			return new FunctionName_IdentifierImpl(node);
		}
		if (type == Symbols.functionName_Bit) {
			return new FunctionName_BitImpl(node);
		}
		if (type == Symbols.module) {
			return new ModuleImpl(node);
		}
		if (type == Symbols.portDefinitionGroup_Valid) {
			return new PortDefinitionGroup_ValidImpl(node);
		}
		if (type == Symbols.portDefinitionGroup_Error1) {
			return new PortDefinitionGroup_Error1Impl(node);
		}
		if (type == Symbols.portDefinitionGroup_Error2) {
			return new PortDefinitionGroup_Error2Impl(node);
		}
		if (type == Symbols.dataType_Bit) {
			return new DataType_BitImpl(node);
		}
		if (type == Symbols.dataType_Vector) {
			return new DataType_VectorImpl(node);
		}
		if (type == Symbols.dataType_Matrix) {
			return new DataType_MatrixImpl(node);
		}
		if (type == Symbols.dataType_Integer) {
			return new DataType_IntegerImpl(node);
		}
		if (type == Symbols.dataType_Text) {
			return new DataType_TextImpl(node);
		}
		if (type == Symbols.dataType_Clock) {
			return new DataType_ClockImpl(node);
		}
		if (type == Symbols.synthetic_List_PortDefinitionGroup) {
			return new CmListImpl<PortDefinitionGroup, PortDefinitionGroupImpl>(node, createTokenSet(Symbols.portDefinitionGroup_Valid, Symbols.portDefinitionGroup_Error1, Symbols.portDefinitionGroup_Error2), PortDefinitionGroup.class, PortDefinitionGroupImpl.class);
		}
		if (type == Symbols.synthetic_List_StatementCaseItem_Nonempty) {
			return new CmListImpl<StatementCaseItem, StatementCaseItemImpl>(node, createTokenSet(Symbols.statementCaseItem_Value, Symbols.statementCaseItem_Default), StatementCaseItem.class, StatementCaseItemImpl.class);
		}
		if (type == Symbols.signalLikeKind_Constant) {
			return new SignalLikeKind_ConstantImpl(node);
		}
		if (type == Symbols.signalLikeKind_Signal) {
			return new SignalLikeKind_SignalImpl(node);
		}
		if (type == Symbols.signalLikeKind_Register) {
			return new SignalLikeKind_RegisterImpl(node);
		}
		if (type == Symbols.portDirection_In) {
			return new PortDirection_InImpl(node);
		}
		if (type == Symbols.portDirection_Out) {
			return new PortDirection_OutImpl(node);
		}
		if (type == Symbols.statementCaseItem_Value) {
			return new StatementCaseItem_ValueImpl(node);
		}
		if (type == Symbols.statementCaseItem_Default) {
			return new StatementCaseItem_DefaultImpl(node);
		}
		if (type == Symbols.instanceReferenceName) {
			return new InstanceReferenceNameImpl(node);
		}
		if (type == Symbols.instancePortName) {
			return new InstancePortNameImpl(node);
		}
		if (type == Symbols.synthetic_SeparatedList_PortDefinition_COMMA_Nonempty) {
			return new CmListImpl<PortDefinition, PortDefinitionImpl>(node, createTokenSet(Symbols.portDefinition), PortDefinition.class, PortDefinitionImpl.class);
		}
		if (type == Symbols.extendedExpression_Normal) {
			return new ExtendedExpression_NormalImpl(node);
		}
		if (type == Symbols.extendedExpression_Switch) {
			return new ExtendedExpression_SwitchImpl(node);
		}
		if (type == Symbols.synthetic_SeparatedList_SignalLikeDefinition_COMMA_Nonempty) {
			return new CmListImpl<SignalLikeDefinition, SignalLikeDefinitionImpl>(node, createTokenSet(Symbols.signalLikeDefinition_WithoutInitializer, Symbols.signalLikeDefinition_WithInitializer, Symbols.signalLikeDefinition_Error), SignalLikeDefinition.class, SignalLikeDefinitionImpl.class);
		}
		if (type == Symbols.__PARSED_FRAGMENT) {
			return new ASTWrapperPsiElement(node);
		}

		throw new RuntimeException("cannot create PSI element for AST node due to unknown element type: " + type);
	}

	private static TokenSet createTokenSet(IElementType... types) {
		return TokenSet.create(types);
	}

}
