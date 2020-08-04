package name.martingeisse.mahdl.input;

import name.martingeisse.mahdl.compiler.MahdlElementType;
import name.martingeisse.mahdl.input.cm.*;
import name.martingeisse.mahdl.input.cm.impl.*;

public class Symbols {

	//
	// terminals
	//

	public static final MahdlElementType BLOCK_COMMENT = new MahdlElementType("BLOCK_COMMENT", null);
	public static final MahdlElementType CLOSING_CURLY_BRACE = new MahdlElementType("CLOSING_CURLY_BRACE", null);
	public static final MahdlElementType CLOSING_PARENTHESIS = new MahdlElementType("CLOSING_PARENTHESIS", null);
	public static final MahdlElementType CLOSING_SQUARE_BRACKET = new MahdlElementType("CLOSING_SQUARE_BRACKET", null);
	public static final MahdlElementType COLON = new MahdlElementType("COLON", null);
	public static final MahdlElementType COMMA = new MahdlElementType("COMMA", null);
	public static final MahdlElementType DOT = new MahdlElementType("DOT", null);
	public static final MahdlElementType EQUALS = new MahdlElementType("EQUALS", null);
	public static final MahdlElementType IDENTIFIER = new MahdlElementType("IDENTIFIER", null);
	public static final MahdlElementType INTEGER_LITERAL = new MahdlElementType("INTEGER_LITERAL", null);
	public static final MahdlElementType KW_BIT = new MahdlElementType("KW_BIT", null);
	public static final MahdlElementType KW_CASE = new MahdlElementType("KW_CASE", null);
	public static final MahdlElementType KW_CLOCK = new MahdlElementType("KW_CLOCK", null);
	public static final MahdlElementType KW_CONSTANT = new MahdlElementType("KW_CONSTANT", null);
	public static final MahdlElementType KW_DEFAULT = new MahdlElementType("KW_DEFAULT", null);
	public static final MahdlElementType KW_DO = new MahdlElementType("KW_DO", null);
	public static final MahdlElementType KW_ELSE = new MahdlElementType("KW_ELSE", null);
	public static final MahdlElementType KW_IF = new MahdlElementType("KW_IF", null);
	public static final MahdlElementType KW_IN = new MahdlElementType("KW_IN", null);
	public static final MahdlElementType KW_INTEGER = new MahdlElementType("KW_INTEGER", null);
	public static final MahdlElementType KW_INTERFACE = new MahdlElementType("KW_INTERFACE", null);
	public static final MahdlElementType KW_MATRIX = new MahdlElementType("KW_MATRIX", null);
	public static final MahdlElementType KW_MODULE = new MahdlElementType("KW_MODULE", null);
	public static final MahdlElementType KW_NATIVE = new MahdlElementType("KW_NATIVE", null);
	public static final MahdlElementType KW_OUT = new MahdlElementType("KW_OUT", null);
	public static final MahdlElementType KW_REGISTER = new MahdlElementType("KW_REGISTER", null);
	public static final MahdlElementType KW_SIGNAL = new MahdlElementType("KW_SIGNAL", null);
	public static final MahdlElementType KW_SWITCH = new MahdlElementType("KW_SWITCH", null);
	public static final MahdlElementType KW_TEXT = new MahdlElementType("KW_TEXT", null);
	public static final MahdlElementType KW_VECTOR = new MahdlElementType("KW_VECTOR", null);
	public static final MahdlElementType LINE_COMMENT = new MahdlElementType("LINE_COMMENT", null);
	public static final MahdlElementType OPENING_CURLY_BRACE = new MahdlElementType("OPENING_CURLY_BRACE", null);
	public static final MahdlElementType OPENING_PARENTHESIS = new MahdlElementType("OPENING_PARENTHESIS", null);
	public static final MahdlElementType OPENING_SQUARE_BRACKET = new MahdlElementType("OPENING_SQUARE_BRACKET", null);
	public static final MahdlElementType OP_AND = new MahdlElementType("OP_AND", null);
	public static final MahdlElementType OP_CONCAT = new MahdlElementType("OP_CONCAT", null);
	public static final MahdlElementType OP_CONDITIONAL = new MahdlElementType("OP_CONDITIONAL", null);
	public static final MahdlElementType OP_DIVIDED_BY = new MahdlElementType("OP_DIVIDED_BY", null);
	public static final MahdlElementType OP_EQUAL = new MahdlElementType("OP_EQUAL", null);
	public static final MahdlElementType OP_GREATER_THAN = new MahdlElementType("OP_GREATER_THAN", null);
	public static final MahdlElementType OP_GREATER_THAN_OR_EQUAL = new MahdlElementType("OP_GREATER_THAN_OR_EQUAL", null);
	public static final MahdlElementType OP_LESS_THAN = new MahdlElementType("OP_LESS_THAN", null);
	public static final MahdlElementType OP_LESS_THAN_OR_EQUAL = new MahdlElementType("OP_LESS_THAN_OR_EQUAL", null);
	public static final MahdlElementType OP_MINUS = new MahdlElementType("OP_MINUS", null);
	public static final MahdlElementType OP_NOT = new MahdlElementType("OP_NOT", null);
	public static final MahdlElementType OP_NOT_EQUAL = new MahdlElementType("OP_NOT_EQUAL", null);
	public static final MahdlElementType OP_OR = new MahdlElementType("OP_OR", null);
	public static final MahdlElementType OP_PLUS = new MahdlElementType("OP_PLUS", null);
	public static final MahdlElementType OP_REMAINDER = new MahdlElementType("OP_REMAINDER", null);
	public static final MahdlElementType OP_SHIFT_LEFT = new MahdlElementType("OP_SHIFT_LEFT", null);
	public static final MahdlElementType OP_SHIFT_RIGHT = new MahdlElementType("OP_SHIFT_RIGHT", null);
	public static final MahdlElementType OP_TIMES = new MahdlElementType("OP_TIMES", null);
	public static final MahdlElementType OP_XOR = new MahdlElementType("OP_XOR", null);
	public static final MahdlElementType SEMICOLON = new MahdlElementType("SEMICOLON", null);
	public static final MahdlElementType TEXT_LITERAL = new MahdlElementType("TEXT_LITERAL", null);
	public static final MahdlElementType VECTOR_LITERAL = new MahdlElementType("VECTOR_LITERAL", null);

	//
	// nonterminals
	//

	public static final MahdlElementType dataType_Bit = new MahdlElementType("dataType_Bit", DataType_BitImpl::new);
	public static final MahdlElementType dataType_Clock = new MahdlElementType("dataType_Clock", DataType_ClockImpl::new);
	public static final MahdlElementType dataType_Integer = new MahdlElementType("dataType_Integer", DataType_IntegerImpl::new);
	public static final MahdlElementType dataType_Matrix = new MahdlElementType("dataType_Matrix", DataType_MatrixImpl::new);
	public static final MahdlElementType dataType_Text = new MahdlElementType("dataType_Text", DataType_TextImpl::new);
	public static final MahdlElementType dataType_Vector = new MahdlElementType("dataType_Vector", DataType_VectorImpl::new);
	public static final MahdlElementType doBlockTrigger_Clocked = new MahdlElementType("doBlockTrigger_Clocked", DoBlockTrigger_ClockedImpl::new);
	public static final MahdlElementType doBlockTrigger_Combinatorial = new MahdlElementType("doBlockTrigger_Combinatorial", DoBlockTrigger_CombinatorialImpl::new);
	public static final MahdlElementType doBlockTrigger_Error = new MahdlElementType("doBlockTrigger_Error", DoBlockTrigger_ErrorImpl::new);
	public static final MahdlElementType expressionCaseItem_Default = new MahdlElementType("expressionCaseItem_Default", ExpressionCaseItem_DefaultImpl::new);
	public static final MahdlElementType expressionCaseItem_Value = new MahdlElementType("expressionCaseItem_Value", ExpressionCaseItem_ValueImpl::new);
	public static final MahdlElementType expression_BinaryAnd = new MahdlElementType("expression_BinaryAnd", Expression_BinaryAndImpl::new);
	public static final MahdlElementType expression_BinaryConcat = new MahdlElementType("expression_BinaryConcat", Expression_BinaryConcatImpl::new);
	public static final MahdlElementType expression_BinaryDividedBy = new MahdlElementType("expression_BinaryDividedBy", Expression_BinaryDividedByImpl::new);
	public static final MahdlElementType expression_BinaryEqual = new MahdlElementType("expression_BinaryEqual", Expression_BinaryEqualImpl::new);
	public static final MahdlElementType expression_BinaryGreaterThan = new MahdlElementType("expression_BinaryGreaterThan", Expression_BinaryGreaterThanImpl::new);
	public static final MahdlElementType expression_BinaryGreaterThanOrEqual = new MahdlElementType("expression_BinaryGreaterThanOrEqual", Expression_BinaryGreaterThanOrEqualImpl::new);
	public static final MahdlElementType expression_BinaryLessThan = new MahdlElementType("expression_BinaryLessThan", Expression_BinaryLessThanImpl::new);
	public static final MahdlElementType expression_BinaryLessThanOrEqual = new MahdlElementType("expression_BinaryLessThanOrEqual", Expression_BinaryLessThanOrEqualImpl::new);
	public static final MahdlElementType expression_BinaryMinus = new MahdlElementType("expression_BinaryMinus", Expression_BinaryMinusImpl::new);
	public static final MahdlElementType expression_BinaryNotEqual = new MahdlElementType("expression_BinaryNotEqual", Expression_BinaryNotEqualImpl::new);
	public static final MahdlElementType expression_BinaryOr = new MahdlElementType("expression_BinaryOr", Expression_BinaryOrImpl::new);
	public static final MahdlElementType expression_BinaryPlus = new MahdlElementType("expression_BinaryPlus", Expression_BinaryPlusImpl::new);
	public static final MahdlElementType expression_BinaryRemainder = new MahdlElementType("expression_BinaryRemainder", Expression_BinaryRemainderImpl::new);
	public static final MahdlElementType expression_BinaryShiftLeft = new MahdlElementType("expression_BinaryShiftLeft", Expression_BinaryShiftLeftImpl::new);
	public static final MahdlElementType expression_BinaryShiftRight = new MahdlElementType("expression_BinaryShiftRight", Expression_BinaryShiftRightImpl::new);
	public static final MahdlElementType expression_BinaryTimes = new MahdlElementType("expression_BinaryTimes", Expression_BinaryTimesImpl::new);
	public static final MahdlElementType expression_BinaryXor = new MahdlElementType("expression_BinaryXor", Expression_BinaryXorImpl::new);
	public static final MahdlElementType expression_Conditional = new MahdlElementType("expression_Conditional", Expression_ConditionalImpl::new);
	public static final MahdlElementType expression_FunctionCall = new MahdlElementType("expression_FunctionCall", Expression_FunctionCallImpl::new);
	public static final MahdlElementType expression_Identifier = new MahdlElementType("expression_Identifier", Expression_IdentifierImpl::new);
	public static final MahdlElementType expression_IndexSelection = new MahdlElementType("expression_IndexSelection", Expression_IndexSelectionImpl::new);
	public static final MahdlElementType expression_InstancePort = new MahdlElementType("expression_InstancePort", Expression_InstancePortImpl::new);
	public static final MahdlElementType expression_Literal = new MahdlElementType("expression_Literal", Expression_LiteralImpl::new);
	public static final MahdlElementType expression_Parenthesized = new MahdlElementType("expression_Parenthesized", Expression_ParenthesizedImpl::new);
	public static final MahdlElementType expression_RangeSelection = new MahdlElementType("expression_RangeSelection", Expression_RangeSelectionImpl::new);
	public static final MahdlElementType expression_UnaryMinus = new MahdlElementType("expression_UnaryMinus", Expression_UnaryMinusImpl::new);
	public static final MahdlElementType expression_UnaryNot = new MahdlElementType("expression_UnaryNot", Expression_UnaryNotImpl::new);
	public static final MahdlElementType expression_UnaryPlus = new MahdlElementType("expression_UnaryPlus", Expression_UnaryPlusImpl::new);
	public static final MahdlElementType extendedExpression_Normal = new MahdlElementType("extendedExpression_Normal", ExtendedExpression_NormalImpl::new);
	public static final MahdlElementType extendedExpression_Switch = new MahdlElementType("extendedExpression_Switch", ExtendedExpression_SwitchImpl::new);
	public static final MahdlElementType functionName_Bit = new MahdlElementType("functionName_Bit", FunctionName_BitImpl::new);
	public static final MahdlElementType functionName_Identifier = new MahdlElementType("functionName_Identifier", FunctionName_IdentifierImpl::new);
	public static final MahdlElementType implementationItem_DoBlock = new MahdlElementType("implementationItem_DoBlock", ImplementationItem_DoBlockImpl::new);
	public static final MahdlElementType implementationItem_Error = new MahdlElementType("implementationItem_Error", ImplementationItem_ErrorImpl::new);
	public static final MahdlElementType implementationItem_ModuleInstanceDefinitionGroup = new MahdlElementType("implementationItem_ModuleInstanceDefinitionGroup", ImplementationItem_ModuleInstanceDefinitionGroupImpl::new);
	public static final MahdlElementType implementationItem_SignalLikeDefinitionGroup = new MahdlElementType("implementationItem_SignalLikeDefinitionGroup", ImplementationItem_SignalLikeDefinitionGroupImpl::new);
	public static final MahdlElementType instancePortName = new MahdlElementType("instancePortName", InstancePortNameImpl::new);
	public static final MahdlElementType instanceReferenceName = new MahdlElementType("instanceReferenceName", InstanceReferenceNameImpl::new);
	public static final MahdlElementType literal_Integer = new MahdlElementType("literal_Integer", Literal_IntegerImpl::new);
	public static final MahdlElementType literal_Text = new MahdlElementType("literal_Text", Literal_TextImpl::new);
	public static final MahdlElementType literal_Vector = new MahdlElementType("literal_Vector", Literal_VectorImpl::new);
	public static final MahdlElementType module = new MahdlElementType("module", ModuleImpl::new);
	public static final MahdlElementType moduleInstanceDefinition = new MahdlElementType("moduleInstanceDefinition", ModuleInstanceDefinitionImpl::new);
	public static final MahdlElementType portDefinition = new MahdlElementType("portDefinition", PortDefinitionImpl::new);
	public static final MahdlElementType portDefinitionGroup_Error1 = new MahdlElementType("portDefinitionGroup_Error1", PortDefinitionGroup_Error1Impl::new);
	public static final MahdlElementType portDefinitionGroup_Error2 = new MahdlElementType("portDefinitionGroup_Error2", PortDefinitionGroup_Error2Impl::new);
	public static final MahdlElementType portDefinitionGroup_Valid = new MahdlElementType("portDefinitionGroup_Valid", PortDefinitionGroup_ValidImpl::new);
	public static final MahdlElementType portDirection_In = new MahdlElementType("portDirection_In", PortDirection_InImpl::new);
	public static final MahdlElementType portDirection_Out = new MahdlElementType("portDirection_Out", PortDirection_OutImpl::new);
	public static final MahdlElementType qualifiedModuleName = new MahdlElementType("qualifiedModuleName", QualifiedModuleNameImpl::new);
	public static final MahdlElementType signalLikeDefinition_Error = new MahdlElementType("signalLikeDefinition_Error", SignalLikeDefinition_ErrorImpl::new);
	public static final MahdlElementType signalLikeDefinition_WithInitializer = new MahdlElementType("signalLikeDefinition_WithInitializer", SignalLikeDefinition_WithInitializerImpl::new);
	public static final MahdlElementType signalLikeDefinition_WithoutInitializer = new MahdlElementType("signalLikeDefinition_WithoutInitializer", SignalLikeDefinition_WithoutInitializerImpl::new);
	public static final MahdlElementType signalLikeKind_Constant = new MahdlElementType("signalLikeKind_Constant", SignalLikeKind_ConstantImpl::new);
	public static final MahdlElementType signalLikeKind_Register = new MahdlElementType("signalLikeKind_Register", SignalLikeKind_RegisterImpl::new);
	public static final MahdlElementType signalLikeKind_Signal = new MahdlElementType("signalLikeKind_Signal", SignalLikeKind_SignalImpl::new);
	public static final MahdlElementType statementCaseItem_Default = new MahdlElementType("statementCaseItem_Default", StatementCaseItem_DefaultImpl::new);
	public static final MahdlElementType statementCaseItem_Value = new MahdlElementType("statementCaseItem_Value", StatementCaseItem_ValueImpl::new);
	public static final MahdlElementType statement_Assignment = new MahdlElementType("statement_Assignment", Statement_AssignmentImpl::new);
	public static final MahdlElementType statement_Block = new MahdlElementType("statement_Block", Statement_BlockImpl::new);
	public static final MahdlElementType statement_Error1 = new MahdlElementType("statement_Error1", Statement_Error1Impl::new);
	public static final MahdlElementType statement_Error2 = new MahdlElementType("statement_Error2", Statement_Error2Impl::new);
	public static final MahdlElementType statement_IfThen = new MahdlElementType("statement_IfThen", Statement_IfThenImpl::new);
	public static final MahdlElementType statement_IfThenElse = new MahdlElementType("statement_IfThenElse", Statement_IfThenElseImpl::new);
	public static final MahdlElementType statement_Switch = new MahdlElementType("statement_Switch", Statement_SwitchImpl::new);
	public static final MahdlElementType synthetic_List_ExpressionCaseItem_Nonempty = new MahdlElementType("synthetic_List_ExpressionCaseItem_Nonempty", (row, column, childNodes) -> new CmListImpl<>(row, column, ExpressionCaseItem.class, childNodes));
	public static final MahdlElementType synthetic_List_ImplementationItem = new MahdlElementType("synthetic_List_ImplementationItem", (row, column, childNodes) -> new CmListImpl<>(row, column, ImplementationItem.class, childNodes));
	public static final MahdlElementType synthetic_List_PortDefinitionGroup = new MahdlElementType("synthetic_List_PortDefinitionGroup", (row, column, childNodes) -> new CmListImpl<>(row, column, PortDefinitionGroup.class, childNodes));
	public static final MahdlElementType synthetic_List_Statement = new MahdlElementType("synthetic_List_Statement", (row, column, childNodes) -> new CmListImpl<>(row, column, Statement.class, childNodes));
	public static final MahdlElementType synthetic_List_StatementCaseItem_Nonempty = new MahdlElementType("synthetic_List_StatementCaseItem_Nonempty", (row, column, childNodes) -> new CmListImpl<>(row, column, StatementCaseItem.class, childNodes));
	public static final MahdlElementType synthetic_List_Statement_Nonempty = new MahdlElementType("synthetic_List_Statement_Nonempty", (row, column, childNodes) -> new CmListImpl<>(row, column, Statement.class, childNodes));
	public static final MahdlElementType synthetic_Optional_KWNATIVE = new MahdlElementType("synthetic_Optional_KWNATIVE", CmOptionalImpl::new);
	public static final MahdlElementType synthetic_SeparatedList_Expression_COMMA_Nonempty = new MahdlElementType("synthetic_SeparatedList_Expression_COMMA_Nonempty", (row, column, childNodes) -> new CmListImpl<>(row, column, Expression.class, childNodes));
	public static final MahdlElementType synthetic_SeparatedList_IDENTIFIER_DOT_Nonempty = new MahdlElementType("synthetic_SeparatedList_IDENTIFIER_DOT_Nonempty", (row, column, childNodes) -> new CmListImpl<>(row, column, CmToken.class, childNodes));
	public static final MahdlElementType synthetic_SeparatedList_ModuleInstanceDefinition_COMMA_Nonempty = new MahdlElementType("synthetic_SeparatedList_ModuleInstanceDefinition_COMMA_Nonempty", (row, column, childNodes) -> new CmListImpl<>(row, column, ModuleInstanceDefinition.class, childNodes));
	public static final MahdlElementType synthetic_SeparatedList_PortDefinition_COMMA_Nonempty = new MahdlElementType("synthetic_SeparatedList_PortDefinition_COMMA_Nonempty", (row, column, childNodes) -> new CmListImpl<>(row, column, PortDefinition.class, childNodes));
	public static final MahdlElementType synthetic_SeparatedList_SignalLikeDefinition_COMMA_Nonempty = new MahdlElementType("synthetic_SeparatedList_SignalLikeDefinition_COMMA_Nonempty", (row, column, childNodes) -> new CmListImpl<>(row, column, SignalLikeDefinition.class, childNodes));

	//
	// special
	//

	// partially parsed input in case of an error
	public static final MahdlElementType __PARSED_FRAGMENT = new MahdlElementType("__PARSED_FRAGMENT", null);

}
