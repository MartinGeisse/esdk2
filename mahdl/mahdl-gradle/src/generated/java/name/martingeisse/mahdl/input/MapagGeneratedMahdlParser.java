

package name.martingeisse.mahdl.input;

import name.martingeisse.mahdl.input.cm.CmNode;
import name.martingeisse.mahdl.input.cm.impl.CmTokenImpl;
import name.martingeisse.mahdl.input.cm.impl.IElementType;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapagGeneratedMahdlParser {

	// ------------------------------------------------------------------------------------------------
	// --- generated stuff
	// ------------------------------------------------------------------------------------------------

	// symbols (tokens and nonterminals)
	private static final int EOF_SYMBOL_CODE = 0;
	private static final int ERROR_SYMBOL_CODE = 1;
	private static final IElementType[] SYMBOL_CODE_TO_ELEMENT_TYPE = {
		null, // %eof -- doesn't have an IElementType
		null, // %error -- doesn't have an IElementType
		IElementType.BAD_CHARACTER, // %badchar
		Symbols.BLOCK_COMMENT,
		Symbols.CLOSING_CURLY_BRACE,
		Symbols.CLOSING_PARENTHESIS,
		Symbols.CLOSING_SQUARE_BRACKET,
		Symbols.COLON,
		Symbols.COMMA,
		Symbols.DOT,
		Symbols.EQUALS,
		Symbols.IDENTIFIER,
		Symbols.INTEGER_LITERAL,
		Symbols.KW_BIT,
		Symbols.KW_CASE,
		Symbols.KW_CLOCK,
		Symbols.KW_CONSTANT,
		Symbols.KW_DEFAULT,
		Symbols.KW_DO,
		Symbols.KW_ELSE,
		Symbols.KW_IF,
		Symbols.KW_IN,
		Symbols.KW_INTEGER,
		Symbols.KW_INTERFACE,
		Symbols.KW_MATRIX,
		Symbols.KW_MODULE,
		Symbols.KW_NATIVE,
		Symbols.KW_OUT,
		Symbols.KW_REGISTER,
		Symbols.KW_SIGNAL,
		Symbols.KW_SWITCH,
		Symbols.KW_TEXT,
		Symbols.KW_VECTOR,
		Symbols.LINE_COMMENT,
		Symbols.OPENING_CURLY_BRACE,
		Symbols.OPENING_PARENTHESIS,
		Symbols.OPENING_SQUARE_BRACKET,
		Symbols.OP_AND,
		Symbols.OP_CONCAT,
		Symbols.OP_CONDITIONAL,
		Symbols.OP_DIVIDED_BY,
		Symbols.OP_EQUAL,
		Symbols.OP_GREATER_THAN,
		Symbols.OP_GREATER_THAN_OR_EQUAL,
		Symbols.OP_LESS_THAN,
		Symbols.OP_LESS_THAN_OR_EQUAL,
		Symbols.OP_MINUS,
		Symbols.OP_NOT,
		Symbols.OP_NOT_EQUAL,
		Symbols.OP_OR,
		Symbols.OP_PLUS,
		Symbols.OP_REMAINDER,
		Symbols.OP_SHIFT_LEFT,
		Symbols.OP_SHIFT_RIGHT,
		Symbols.OP_TIMES,
		Symbols.OP_XOR,
		Symbols.SEMICOLON,
		Symbols.TEXT_LITERAL,
		Symbols.VECTOR_LITERAL,
	};

	// state machine (general)
	private static final int START_STATE = 0;

	// state machine (action table)
	private static final int ACTION_TABLE_WIDTH = 91;
	private static final int[] ACTION_TABLE;

	static {
		try (InputStream inputStream = MapagGeneratedMahdlParser.class.getResourceAsStream("/MapagGeneratedMahdlParser.actions")) {
			try (DataInputStream dataInputStream = new DataInputStream(inputStream)) {
				ACTION_TABLE = new int[929 * ACTION_TABLE_WIDTH];
				for (int i = 0; i < ACTION_TABLE.length; i++) {
					ACTION_TABLE[i] = dataInputStream.readInt();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// state machine (alternatives / reduction table)
	private static final int[] ALTERNATIVE_INDEX_TO_RIGHT_HAND_SIDE_LENGTH = {
		0,
		2,
		0,
		1,
		1,
		2,
		1,
		1,
		1,
		4,
		3,
		5,
		1,
		1,
		1,
		2,
		1,
		3,
		2,
		4,
		5,
		7,
		7,
		3,
		2,
		1,
		1,
		1,
		3,
		4,
		3,
		0,
		2,
		1,
		3,
		1,
		1,
		1,
		3,
		4,
		6,
		3,
		4,
		2,
		2,
		2,
		3,
		3,
		3,
		3,
		3,
		3,
		3,
		3,
		3,
		3,
		3,
		3,
		3,
		3,
		3,
		3,
		3,
		5,
		1,
		3,
		1,
		1,
		1,
		1,
		1,
		9,
		4,
		2,
		1,
		1,
		4,
		7,
		1,
		1,
		1,
		0,
		2,
		1,
		2,
		1,
		1,
		1,
		1,
		1,
		4,
		3,
		1,
		1,
		1,
		3,
		1,
		7,
		1,
		3,
	};
	private static final Object[] ALTERNATIVE_INDEX_TO_PARSE_NODE_HEAD = {
		new ListNodeGenerationWrapper(Symbols.synthetic_List_Statement),
		new ListNodeGenerationWrapper(Symbols.synthetic_List_Statement),
		Symbols.synthetic_Optional_KWNATIVE,
		Symbols.synthetic_Optional_KWNATIVE,
		new ListNodeGenerationWrapper(Symbols.synthetic_List_ExpressionCaseItem_Nonempty),
		new ListNodeGenerationWrapper(Symbols.synthetic_List_ExpressionCaseItem_Nonempty),
		Symbols.literal_Integer,
		Symbols.literal_Vector,
		Symbols.literal_Text,
		Symbols.implementationItem_SignalLikeDefinitionGroup,
		Symbols.implementationItem_ModuleInstanceDefinitionGroup,
		Symbols.implementationItem_DoBlock,
		Symbols.implementationItem_Error,
		Symbols.qualifiedModuleName,
		new ListNodeGenerationWrapper(Symbols.synthetic_List_Statement_Nonempty),
		new ListNodeGenerationWrapper(Symbols.synthetic_List_Statement_Nonempty),
		Symbols.signalLikeDefinition_WithoutInitializer,
		Symbols.signalLikeDefinition_WithInitializer,
		Symbols.signalLikeDefinition_Error,
		Symbols.statement_Assignment,
		Symbols.statement_IfThen,
		Symbols.statement_IfThenElse,
		Symbols.statement_Switch,
		Symbols.statement_Block,
		Symbols.statement_Error1,
		Symbols.statement_Error2,
		Symbols.portDefinition,
		new ListNodeGenerationWrapper(Symbols.synthetic_SeparatedList_IDENTIFIER_DOT_Nonempty),
		new ListNodeGenerationWrapper(Symbols.synthetic_SeparatedList_IDENTIFIER_DOT_Nonempty),
		Symbols.expressionCaseItem_Value,
		Symbols.expressionCaseItem_Default,
		new ListNodeGenerationWrapper(Symbols.synthetic_List_ImplementationItem),
		new ListNodeGenerationWrapper(Symbols.synthetic_List_ImplementationItem),
		new ListNodeGenerationWrapper(Symbols.synthetic_SeparatedList_ModuleInstanceDefinition_COMMA_Nonempty),
		new ListNodeGenerationWrapper(Symbols.synthetic_SeparatedList_ModuleInstanceDefinition_COMMA_Nonempty),
		Symbols.moduleInstanceDefinition,
		Symbols.expression_Literal,
		Symbols.expression_Identifier,
		Symbols.expression_InstancePort,
		Symbols.expression_IndexSelection,
		Symbols.expression_RangeSelection,
		Symbols.expression_Parenthesized,
		Symbols.expression_FunctionCall,
		Symbols.expression_UnaryNot,
		Symbols.expression_UnaryPlus,
		Symbols.expression_UnaryMinus,
		Symbols.expression_BinaryPlus,
		Symbols.expression_BinaryMinus,
		Symbols.expression_BinaryTimes,
		Symbols.expression_BinaryDividedBy,
		Symbols.expression_BinaryRemainder,
		Symbols.expression_BinaryEqual,
		Symbols.expression_BinaryNotEqual,
		Symbols.expression_BinaryGreaterThan,
		Symbols.expression_BinaryGreaterThanOrEqual,
		Symbols.expression_BinaryLessThan,
		Symbols.expression_BinaryLessThanOrEqual,
		Symbols.expression_BinaryAnd,
		Symbols.expression_BinaryOr,
		Symbols.expression_BinaryXor,
		Symbols.expression_BinaryShiftLeft,
		Symbols.expression_BinaryShiftRight,
		Symbols.expression_BinaryConcat,
		Symbols.expression_Conditional,
		new ListNodeGenerationWrapper(Symbols.synthetic_SeparatedList_Expression_COMMA_Nonempty),
		new ListNodeGenerationWrapper(Symbols.synthetic_SeparatedList_Expression_COMMA_Nonempty),
		Symbols.doBlockTrigger_Combinatorial,
		Symbols.doBlockTrigger_Clocked,
		Symbols.doBlockTrigger_Error,
		Symbols.functionName_Identifier,
		Symbols.functionName_Bit,
		Symbols.module,
		Symbols.portDefinitionGroup_Valid,
		Symbols.portDefinitionGroup_Error1,
		Symbols.portDefinitionGroup_Error2,
		Symbols.dataType_Bit,
		Symbols.dataType_Vector,
		Symbols.dataType_Matrix,
		Symbols.dataType_Integer,
		Symbols.dataType_Text,
		Symbols.dataType_Clock,
		new ListNodeGenerationWrapper(Symbols.synthetic_List_PortDefinitionGroup),
		new ListNodeGenerationWrapper(Symbols.synthetic_List_PortDefinitionGroup),
		new ListNodeGenerationWrapper(Symbols.synthetic_List_StatementCaseItem_Nonempty),
		new ListNodeGenerationWrapper(Symbols.synthetic_List_StatementCaseItem_Nonempty),
		Symbols.signalLikeKind_Constant,
		Symbols.signalLikeKind_Signal,
		Symbols.signalLikeKind_Register,
		Symbols.portDirection_In,
		Symbols.portDirection_Out,
		Symbols.statementCaseItem_Value,
		Symbols.statementCaseItem_Default,
		Symbols.instanceReferenceName,
		Symbols.instancePortName,
		new ListNodeGenerationWrapper(Symbols.synthetic_SeparatedList_PortDefinition_COMMA_Nonempty),
		new ListNodeGenerationWrapper(Symbols.synthetic_SeparatedList_PortDefinition_COMMA_Nonempty),
		Symbols.extendedExpression_Normal,
		Symbols.extendedExpression_Switch,
		new ListNodeGenerationWrapper(Symbols.synthetic_SeparatedList_SignalLikeDefinition_COMMA_Nonempty),
		new ListNodeGenerationWrapper(Symbols.synthetic_SeparatedList_SignalLikeDefinition_COMMA_Nonempty),
	};
	private static final int[] ALTERNATIVE_INDEX_TO_NONTERMINAL_SYMBOL_CODE = {
		82,
		82,
		85,
		85,
		79,
		79,
		68,
		68,
		68,
		65,
		65,
		65,
		65,
		74,
		83,
		83,
		75,
		75,
		75,
		77,
		77,
		77,
		77,
		77,
		77,
		77,
		71,
		86,
		86,
		62,
		62,
		80,
		80,
		88,
		88,
		70,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		61,
		87,
		87,
		60,
		60,
		60,
		64,
		64,
		69,
		72,
		72,
		72,
		59,
		59,
		59,
		59,
		59,
		59,
		81,
		81,
		84,
		84,
		76,
		76,
		76,
		73,
		73,
		78,
		78,
		67,
		66,
		89,
		89,
		63,
		63,
		90,
		90,
	};

	// state machine (error messages)
	private static final String[] STATE_INPUT_EXPECTATION = {
		"module native",
		"",
		"bit clock integer matrix text vector",
		"bit clock integer matrix text vector",
		"(identifier)",
		"(identifier)",
		"(identifier)",
		"[",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"[",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"(identifier)",
		"(identifier)",
		"[",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"(identifier)",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) * + - bit ~",
		")",
		")",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit switch ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit switch ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit switch ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit case default if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit case default if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit switch ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit switch ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit switch ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit switch ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit switch ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit switch ~",
		"!= % & ( * + - . / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & ( ) * + , - . / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ( ) * + - . / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ( * + - . / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & ( * + - . / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & ( * + , - . / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ( * + - . / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ( * + , - . / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ( * + - . / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ( * + - . / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"(",
		"(",
		"(",
		"(",
		"(",
		"(",
		"(",
		"(",
		"(",
		"(",
		") ,",
		") ,",
		") ,",
		") ,",
		") ,",
		") ,",
		") ,",
		") ,",
		") ,",
		") ,",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		".",
		".",
		".",
		".",
		".",
		".",
		".",
		".",
		".",
		".",
		"(identifier)",
		"(identifier)",
		"(identifier)",
		"(identifier)",
		"(identifier)",
		"(identifier)",
		"(identifier)",
		"(identifier)",
		"(identifier)",
		"(identifier)",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"case default",
		"case default",
		"case default",
		"case default }",
		"case default }",
		"case default }",
		", :",
		"case default }",
		":",
		"case default }",
		"(",
		"(",
		"(",
		"{",
		"{",
		"{",
		"case default }",
		", ;",
		";",
		"(",
		"(identifier) constant do register signal",
		"(identifier) constant do register signal",
		"(",
		")",
		"(identifier) constant do register signal",
		"(identifier)",
		", ;",
		"(identifier) constant do register signal",
		"(identifier)",
		", ;",
		"(identifier) constant do register signal",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ case default | }",
		"!= % & ) * + , - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & ) * + - / < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + - / < << <= == > >= >> OP_CONDITIONAL [ ] ^ _ |",
		"!= % & * + , - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / : < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + , - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / < << <= = == > >= >> OP_CONDITIONAL [ ^ _ |",
		"!= % & * + - / ; < << <= == > >= >> OP_CONDITIONAL [ ^ _ |",
		"module",
		"(identifier)",
		";",
		"interface",
		"{",
		"in out }",
		"in out }",
		"(identifier) constant do register signal",
		"(identifier)",
		", ;",
		"(identifier)",
		"(identifier)",
		", ;",
		"; in out }",
		"in out }",
		", ;",
		"in out }",
		"bit clock integer matrix text vector",
		"bit clock integer matrix text vector",
		"(identifier) .",
		". ;",
		"(identifier)",
		", ; =",
		", ;",
		", ;",
		"bit clock integer matrix text vector",
		"bit clock integer matrix text vector",
		"bit clock integer matrix text vector",
		"(identifier) ; constant do else register signal",
		"(identifier) ; constant do register signal",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - ; bit case default else if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - ; bit case default if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - ; bit else if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - ; bit if switch { } ~",
		"(identifier) constant do else register signal",
		"(identifier) constant do register signal",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit case default else if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit case default if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit else if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { } ~",
		"(",
		"(",
		"(",
		"(",
		"(",
		"(",
		"(identifier) constant do else register signal",
		"(identifier) constant do else register signal",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit case default else if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit case default else if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit else if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit else if switch { } ~",
		"(identifier) constant do else register signal",
		"(identifier) constant do register signal",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit case default else if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit case default if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit else if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { } ~",
		"(",
		"(",
		"(",
		"(",
		"(",
		"(",
		"{",
		"{",
		"{",
		"{",
		"{",
		"{",
		"case default",
		"case default",
		"case default",
		"case default",
		"case default",
		"case default",
		"case default }",
		"case default }",
		"case default }",
		"case default }",
		"case default }",
		"case default }",
		"(identifier) constant do else register signal",
		"(identifier) constant do register signal",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit case default else if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit case default if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit else if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { } ~",
		"(identifier) constant do else register signal",
		"(identifier) constant do register signal",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit case default else if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit case default if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit else if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { } ~",
		";",
		";",
		";",
		";",
		";",
		";",
		"(identifier) constant do else register signal",
		"(identifier) constant do register signal",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit case default else if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit case default if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit else if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { } ~",
		", :",
		":",
		"case default }",
		"case default }",
		"(identifier) constant do register signal",
		"in out }",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit case default if switch { } ~",
		"( (identifier) (integer-literal) (text-literal) (vector-literal) + - bit case default if switch { } ~",
		"case default }",
		"case default }",
		"module",
		"(identifier) .",
		". ;",
		"(identifier)",
		"(identifier)",
		"(identifier) .",
		". ;",
		", ;",
		", ;",
		", ;",
		", ;",
		", ;",
		", ;",
	};

	// other
	private static final int RECOVERY_SYNC_LENGTH = 3;

	// target-specific declarations

	private void beginSpeculativeTokenConsumption() {
		tokenMarker = tokenPointer;
	}

	private void endSpeculativeTokenConsumption() {
		tokenPointer = tokenMarker;
	}

	private CmTokenImpl getToken() {
		return tokenPointer >= tokens.length ? null : tokens[tokenPointer];
	}

	private CmTokenImpl getLocationToken() {
		return tokenPointer >= tokens.length ? tokens[tokens.length - 1] : tokens[tokenPointer];
	}

	private IElementType getTokenType() {
		return tokenPointer >= tokens.length ? null : tokens[tokenPointer].getElementType();
	}

	private boolean isAtEof() {
		return tokenPointer >= tokens.length;
	}

	private void nextToken() {
		if (tokenPointer < tokens.length) {
			tokenPointer++;
		}
	}

	private void reportErrorForCurrentToken(String errorMessage) {
		reportError(getLocationToken(), errorMessage);
	}

	protected void reportError(CmTokenImpl locationToken, String message) {
		System.err.println("syntax error in row " + locationToken.getRow() + ", column " + locationToken.getColumn() + ": " + message);
	}

	private CmNode buildCm(Object what) {
		if (what == null) {
			CmTokenImpl node = getToken();
			nextToken();
			return node;
		} else if (what instanceof Object[]) {
			CmTokenImpl locationToken = getLocationToken();
			Object[] reduction = (Object[]) what;
			IElementType elementType;
			List<CmNode> childNodes = new ArrayList<>();
			if (reduction[0] instanceof ListNodeGenerationWrapper) {
				ListNodeGenerationWrapper listNodeGenerationWrapper = (ListNodeGenerationWrapper) reduction[0];
				elementType = listNodeGenerationWrapper.elementType;
				collectListChildNodes(reduction, elementType, childNodes);
			} else {
				elementType = (IElementType) reduction[0];
				for (int i = 1; i < reduction.length; i++) {
					childNodes.add(buildCm(reduction[i]));
				}
			}
			return elementType.getCmNodeFactory().createCmNode(locationToken.getRow(), locationToken.getColumn(), childNodes.toArray(new CmNode[0]));
		} else if (what instanceof List<?>) {
			// an object list has the same meaning as an object array (needed for error symbols)
			return buildCm(((List<?>) what).toArray());
		} else if (what instanceof ErrorLocationIndicator) {
			ErrorLocationIndicator errorLocationIndicator = (ErrorLocationIndicator) what;
			reportErrorForCurrentToken("expected one of: " + STATE_INPUT_EXPECTATION[errorLocationIndicator.state]);
			return null;
		} else if (what instanceof UnrecoverableSyntaxException) {
			reportErrorForCurrentToken(((UnrecoverableSyntaxException) what).getMessage());
			tokenPointer = tokens.length;
			return null;
		} else {
			throw new RuntimeException("unknown object in raw parse tree: " + what);
		}
	}

	// elementType is the IElementType of the list itself, not of the list's elements
	private void collectListChildNodes(Object[] reduction, IElementType elementType, List<CmNode> childNodeDestination) {
		for (int i = 1; i < reduction.length; i++) {

			// We have to include valid children here, but exclude separators. To avoid looking for (possibly multiple)
			// IElementTypes of valid children and separators, we make use of the fact that the only way a separator can
			// appear is a (ListNodeGenerationWrapper // remainingList, separator, child) reduction, which is also the
			// only possible reduction with three child nodes (4 array elements including the ListNodeGenerationWrapper).
			if (i == 2 && reduction.length == 4) {
				// run buildCm so the tokens get consumed, but discard the result
				buildCm(reduction[i]);
				continue;
			}

			// check if the reduction item is a list-type reduction of the same IElementTyp, and if so, flatten the lists
			Object reductionItem = reduction[i];
			if (reductionItem instanceof Object[]) {
				Object[] subReduction = (Object[]) reductionItem;
				if (subReduction[0] instanceof ListNodeGenerationWrapper) {
					ListNodeGenerationWrapper subWrapper = (ListNodeGenerationWrapper) subReduction[0];
					if (subWrapper.elementType == elementType) {
						collectListChildNodes(subReduction, elementType, childNodeDestination);
						continue;
					}
				}
			}

			// default case: just convert the reduction item to a CM node recursively
			childNodeDestination.add(buildCm(reductionItem));

		}
	}

	// ------------------------------------------------------------------------------------------------
	// --- non-generated stuff (initialization and static stuff)
	// ------------------------------------------------------------------------------------------------

	// static table, but has to be initialized at startup since element type indices aren't compile-time constants
	private static int[] elementTypeIndexToSymbolCode;

	/**
	 * This method initializes static tables on the first parse run -- we need element type
	 * indices to be initialized before doing this.
	 */
	private static void initializeStatic() {
		if (elementTypeIndexToSymbolCode != null) {
			return;
		}
		int maxElementTypeIndex = 0;
		for (IElementType token : SYMBOL_CODE_TO_ELEMENT_TYPE) {
			if (token != null) {
				if (maxElementTypeIndex < token.getIndex()) {
					maxElementTypeIndex = token.getIndex();
				}
			}
		}
		elementTypeIndexToSymbolCode = new int[maxElementTypeIndex + 1];
		Arrays.fill(elementTypeIndexToSymbolCode, -1);
		for (int symbolCode = 0; symbolCode < SYMBOL_CODE_TO_ELEMENT_TYPE.length; symbolCode++) {
			IElementType token = SYMBOL_CODE_TO_ELEMENT_TYPE[symbolCode];
			if (token != null) {
				elementTypeIndexToSymbolCode[token.getIndex()] = symbolCode;
			}
		}
	}

	private static int getSymbolCodeForElementType(IElementType elementType) {
		int index = elementType.getIndex();
		if (index >= 0 && index < elementTypeIndexToSymbolCode.length) {
			int symbolCode = elementTypeIndexToSymbolCode[index];
			if (symbolCode >= 0) {
				return symbolCode;
			}
		}
		throw new RuntimeException("unknown token: " + elementType);
	}

	// ------------------------------------------------------------------------------------------------
	// --- The main parser code. Code generation here mostly deals with IntelliJ vs. standalone.
	// ------------------------------------------------------------------------------------------------

	private CmTokenImpl[] tokens;
	private int tokenPointer, tokenMarker;
	private CmNode cmRootNode;

	private boolean used = false;
	private int[] stateStack = new int[256];
	private Object[] parseTreeStack = new Object[256];
	private int stackSize = 0;
	private int state = START_STATE;

	public CmNode parse(CmTokenImpl[] tokens) {
		this.tokens = tokens;
		parse();
		return cmRootNode;
	}

	private void parse() {

		// prevent re-use of this object since the internal state gets changed during parsing
		if (used) {
			throw new IllegalStateException("cannot re-use this parser object");
		}
		used = true;

		// initialize static parser information
		initializeStatic();

		// handle unrecoverable syntax errors
		tokenPointer = tokenMarker = 0;
		try {

			// Parse the input using the generated machine to build a parse tree. The state machine cannot execute the
			// accept action here since the input cannot contain EOF.
			while (!isAtEof()) {
				if (consumeSymbol(getSymbolCodeForElementType(getTokenType()), null)) {
					nextToken();
				} else {
					recoverFromError();
				}
			}

			// Consume the EOF token. This should (possibly after some reductions) accept the input. If not, this causes
			// a syntax error (unexpected EOF), since the parser generator wouldn't emit a "shift EOF" action.
			{
				int originalState = state;
				if (!consumeSymbol(EOF_SYMBOL_CODE, null)) {
					recoverFromError();
					if (!consumeSymbol(EOF_SYMBOL_CODE, null)) {
						throw new UnrecoverableSyntaxException(originalState);
					}
				}
			}

		} catch (UnrecoverableSyntaxException e) {

			// Build a "code fragment" node that contains the parsed and partially reduced part (i.e. the parse tree
			// stack), then the exception. This will report the error properly and also consume the remaining tokens.
			List<Object> nodeBuilder = new ArrayList<>();
			nodeBuilder.add(Symbols.__PARSED_FRAGMENT);
			for (int i = 0; i < stackSize; i++) {
				nodeBuilder.add(parseTreeStack[i]);
			}
			nodeBuilder.add(e);
			parseTreeStack[0] = nodeBuilder.toArray();
			stackSize = 1;

		}

		// At this point, the state stack should contain single element (the start state) and the associated parse
		// tree stack contains the root node as its single element. If anything in the input tried to prevent that,
		// consuming the EOF token would have failed.
		if (stackSize != 1) {
			// either the Lexer returned an explicit EOF (which it shouldn't) or this is a bug in the parser generator
			throw new RuntimeException("unexpected stack size after accepting start symbol");
		}

		tokenPointer = tokenMarker = 0;
		cmRootNode = buildCm(parseTreeStack[0]);

	}

	/**
	 * Consumes a symbol (token, nonterminal or EOF). This performs one or several actions until the symbol gets shifted
	 * (or, in the case of EOF, accepted).
	 * <p>
	 * Returns true on success, false on syntax error. This method does not handle syntax errors itself.
	 */
	private boolean consumeSymbol(int symbolCode, Object symbolData) throws UnrecoverableSyntaxException {
		while (true) { // looped on reduce
			int action = ACTION_TABLE[state * ACTION_TABLE_WIDTH + symbolCode];
			if (action == Integer.MIN_VALUE) { // accept
				return true;
			} else if (action > 0) { // shift
				shift(symbolData, action - 1);
				return true;
			} else if (action < 0) { // reduce, then continue with the original symbol
				reduce(-action - 1);
			} else { // syntax error
				return false;
			}
		}
	}

	private void shift(Object data, int newState) {
		if (stackSize == stateStack.length) {
			stackSize = stackSize * 2;
			stateStack = Arrays.copyOf(stateStack, stackSize);
			parseTreeStack = Arrays.copyOf(parseTreeStack, stackSize);
		}
		stateStack[stackSize] = state;
		parseTreeStack[stackSize] = data;
		stackSize++;
		state = newState;
	}

	private void reduce(int alternativeIndex) throws UnrecoverableSyntaxException {

		// determine the reduction (nonterminal + alternative) to reduce
		int rightHandSideLength = ALTERNATIVE_INDEX_TO_RIGHT_HAND_SIDE_LENGTH[alternativeIndex];
		Object parseNodeHead = ALTERNATIVE_INDEX_TO_PARSE_NODE_HEAD[alternativeIndex];
		int nonterminalSymbolCode = ALTERNATIVE_INDEX_TO_NONTERMINAL_SYMBOL_CODE[alternativeIndex];

		// pop (rightHandSideLength) states off the state stack
		if (rightHandSideLength > 0) {
			stackSize -= rightHandSideLength;
			state = stateStack[stackSize];
		}

		// build a parse tree node from the nonterminal element type and the subtrees in the parse tree stack
		Object[] reduction = new Object[rightHandSideLength + 1];
		reduction[0] = parseNodeHead;
		System.arraycopy(parseTreeStack, stackSize, reduction, 1, rightHandSideLength);

		// shift the nonterminal (errors cannot occur here in LR(1) parsing)
		if (!consumeSymbol(nonterminalSymbolCode, reduction)) {
			throw new RuntimeException("syntax error while shifting a nonterminal... WTF?");
		}

	}

	private void recoverFromError() throws UnrecoverableSyntaxException {

		int originalState = state; // used for error messages

		// First, attempt implicit reduce-on-error to reduce a nonterminal that was actually completed directly before
		// the error, but not yet reduced since LR(1) would demand the right lookahead token to reduce. We want
		// to reduce that nonterminal to give other IDE parts a better partial PSI to work with. However, we must
		// make sure that we don't reduce a state away from the stack that could handle the error symbol, because
		// we would prevent subsequent error recovery. Also, we obviously cannot reduce "the" alternative if there
		// are multiple ones. Note that specifying %reduceOnError overrides any of these concerns, but doing so places
		// the corresponding reductions into the parse table, so once we're here, we can ignore that modifier.
		// Note that we might be able to reduce multiple times before we have to start error recovery, hence the loop.
		outerLoop:
		while (true) {

			// look for a unique reduce action code in the action table
			int reduceActionCode = 0;
			for (int i = 0; i < ACTION_TABLE_WIDTH; i++) {
				int action = ACTION_TABLE[state * ACTION_TABLE_WIDTH + i];
				if (action < 0 && action != Integer.MIN_VALUE) {
					if (reduceActionCode == 0) {
						reduceActionCode = action; // found a reduce action
					} else if (reduceActionCode != action) {
						break outerLoop; // found a different reduce action, so we cannot reduce
					}
				}
			}
			if (reduceActionCode == 0) {
				break;
			}

			// Make sure we don't remove a state from the stack that could consume %error. At depth
			// (rightHandSideLength) on the state stack lies the state to reveal by reducing (and before shifting
			// the nonterminal), so that state is NOT checked here -- even if it can handle %error, reducing won't
			// prevent that. Only the states above it on the stack are checked. As an implicit special case, we can
			// always reduce an alternative with a zero-length right hand side, no matter the current state and stack
			// contents.
			int alternativeIndex = -reduceActionCode - 1;
			int rightHandSideLength = ALTERNATIVE_INDEX_TO_RIGHT_HAND_SIDE_LENGTH[alternativeIndex];
			int probeState = state, depth = 0;
			while (depth < rightHandSideLength) {
				if (ACTION_TABLE[probeState * ACTION_TABLE_WIDTH + ERROR_SYMBOL_CODE] != 0) {
					break outerLoop; // found error-handling state
				}
				depth++;
				probeState = stateStack[stackSize - depth];
			}

			// no state found that could recover, so let's reduce
			reduce(alternativeIndex);

		}

		// Attempt error recovery. For now, this parser uses the same logic as Java CUP: find the first state from the
		// stack that can shift an error symbol, then throw away input terminals until parsing succeeds for
		// RECOVERY_SYNC_LENGTH terminals. This is okay-ish but it will never find recovery-capable states deeper in
		// the stack. For example, in a C-like language, if we allow a statement to consist of an error symbol, but
		// also allow a function to consist of an error symbol, then a syntax error in a statement will never try to
		// reduce the whole broken function to %error -- it will always insist on reducing only the broken statement
		// to %error.
		//
		// Note the edge cases: Both the current state (at the current stack size) and the start state (with an empty
		// stack) could be able to consume the error symbol.

		// dig up a recovery-capable state from the stack
		int originalStackSize = stackSize;
		while (ACTION_TABLE[state * ACTION_TABLE_WIDTH + ERROR_SYMBOL_CODE] == 0) {
			stackSize--;
			if (stackSize < 0) {
				// we didn't even find a recovery-capable state
				stackSize = originalStackSize;
				throw new UnrecoverableSyntaxException(originalState);
			}
			state = stateStack[stackSize];
		}

		// all symbols (terminals and nonterminals) we removed make up the first part of the erroneous content
		List<Object> errorNodeBuilder = new ArrayList<>();
		errorNodeBuilder.add(Symbols.__PARSED_FRAGMENT); // not an error-indicating element type, see next paragraph
		for (int i = stackSize; i < originalStackSize; i++) {
			errorNodeBuilder.add(parseTreeStack[i]);
		}

		// This special object is used to signal the exact location of the error to the PsiBuilder. We do not mark
		// the whole error node as an error because IntelliJ would then underline that whole node, making it harder
		// for the user to locate the error. For example, if the grammar allows to reduce the content for a whole
		// broken statement to %error, IntelliJ would underline the whole broken statement as an error, not just the
		// location where the error occurred.
		ErrorLocationIndicator errorLocationIndicator = new ErrorLocationIndicator();
		errorLocationIndicator.state = originalState;
		errorNodeBuilder.add(errorLocationIndicator);

		// shift the error symbol. The parse tree is the node builder, so we can add further discarded tokens below.
		if (!consumeSymbol(ERROR_SYMBOL_CODE, errorNodeBuilder)) {
			throw new RuntimeException("failed to push error symbol in state that should consume it");
		}

		// throw away further erroneous content until parsing works again for RECOVERY_SYNC_LENGTH steps
		while (true) {

			// make a backup of the state and stack
			int backupState = state;
			int backupStackSize = stackSize;
			int[] backupStateStack = Arrays.copyOf(stateStack, stackSize);
			Object[] backupParseTreeStack = Arrays.copyOf(parseTreeStack, stackSize);

			// Attempt to parse for RECOVERY_SYNC_LENGTH steps (stop early if we hit EOF). If we reach EOF, then we
			// must be able to consume that too
			beginSpeculativeTokenConsumption();
			boolean success = true;
			for (int i = 0; i < RECOVERY_SYNC_LENGTH && !isAtEof(); i++) {
				if (consumeSymbol(getSymbolCodeForElementType(getTokenType()), null)) {
					nextToken();
				} else {
					success = false;
					break;
				}
			}
			if (success && isAtEof()) {
				success = consumeSymbol(EOF_SYMBOL_CODE, null);
			}
			endSpeculativeTokenConsumption();

			// restore state and stack backup
			System.arraycopy(backupStateStack, 0, stateStack, 0, backupStackSize);
			System.arraycopy(backupParseTreeStack, 0, parseTreeStack, 0, backupStackSize);
			stackSize = backupStackSize;
			state = backupState;

			// Check if successful. If so, resume normal parsing. If not, discard a token.
			if (success) {
				return;
			}
			if (isAtEof()) {
				// Error recovery failed, so we'll signal a "giving up" syntax error and wrap the remainder of the
				// input in a dummy AST node. We don't bother restoring the original parser state since it's
				// irrelevant now. The PSI builder need not be reset here -- that happens automatically after the
				// catch block.
				stackSize = originalStackSize;
				throw new UnrecoverableSyntaxException(originalState);
			}
			errorNodeBuilder.add(null);
			nextToken();

		}

	}

	private static class UnrecoverableSyntaxException extends Exception {

		UnrecoverableSyntaxException(int state) {
			super("expected one of: " + STATE_INPUT_EXPECTATION[state]);
		}

	}

	private static class ErrorLocationIndicator {
		int state;
	}

	private static class ListNodeGenerationWrapper {

		// note: this is the IElementType of the list itself, not of the list's elements. The word "element" has
		// two meanings here, unfortunately.
		IElementType elementType;

		ListNodeGenerationWrapper(IElementType elementType) {
			this.elementType = elementType;
		}

		public String toString() {
			return "LIST(" + elementType + ")";
		}

	}

}

