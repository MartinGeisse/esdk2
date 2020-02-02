
# Type System

Types are associated with constants, signals, registers, module ports and instance ports. Each type belongs to one
of the type families: bit, vector, matrix, integer, text and clock.

The following combinations are possible:
* a constant can have any type except clock.
* signals, module ports and instance ports can have bit, vector or clock type.
* registers can have bit, vector or matrix type.

The bit and vector types are what actual hardware deals with all the time.

Integers cannot exist at run-time since they are unbounded. However, *constant* integers are used to specify the
behvaiour of some expressions; constant *folding* makes compile-time operations on integers possible, and
*assignment conversion* allows to use constant integers as a helper notation for vector literals.

Clocks exist *only* at run-time. No operations are possible on clock-typed sigals. They can only be assigned,
unchanged, to route a clock signal to sub-modules.

Matrix types can be used by constants (i.e. look-up tables) and registers (synchronous memory). The only possible
operation is index selection, both when reading and writing from/to the matrix. Such index selection follows the
same rules with respect to the index expressions as for vector-typed containers.

The text type exists as a compile-time helper type. The only operations possible for the text type is conversion of
any type to text, as well as concatenation.

## Type Rules for Expressions

### Literals

The type and value of a literal are explicit. MaHDL supports vector, integer and text literals.

The ``bit`` function can be used instead of bit literals.

### References

A reference to a constant, signal, register, module port or instance port has the same type and value as the referenced
entity.

### Unary Operators

The *plus* and *minus* operators expect an operand of vector or integer type and yield the same type.
* The *plus* operator yields its operand.
* The *minus* operator yields the two's complement negation for vectors and the integer negation for integers.

The *not* operator expects and operand of bit, vector or integer type and yields the same type.
* For bit and vector, it inverts all bits.
* For integer, the result is the inversion of all bits in the two's complement representation.

### Binary Operators

The following "type-symmetric" operators can be applied to two values of the same type and yield the same type:
* plus, minus, times, divide by, remainder (applicable to vector and integer).
  * For integers, these operators have integer meaning.
  * For vectors, these operators derive their meaning from the interpretation as an unsigned integer, with the result
  truncated to the vector size.
* and, or, xor (applicable to bit and vector)
  * For vectors, these operators are applied bit-wise.

The following comparison operators can be applied to two values of the same type, which must be a vector or integer
type, and yield type bit: equal, not equal, less than, less than or equal, greater than, greater than or equal.
  * For integers, these operators have integer meaning.
  * For vectors, these operators derive their meaning from the interpretation as an unsigned integer

Concatenation can be used in two different meanings:
* Concatenation of two values of bit or vector type yields a vector whose size is the sum of the operand sizes,
counting bit type as size 1.
* Concatenation of two values, one of which is of type text, yields type text.
  * Conversion to text is explained below.

For shift operators, the following cases are supported:
* both operands are integers. The right operand must be non-negative. The shift has integer meaning.
* The left operand is a vector and the right operand is an integer. The right operand must be non-negative. Since it
  is an integer, it must be constant. The shift has bit meaning, with zeroes shifted in.
* Both operands are vectors. The shift has bit meaning, with zeroes shifted in.

### Conditional Operator

The condition must be of type bit.

The then and else branches must have the same type, which must be a bit, vector or integer type.

### Switch Expression

The selector must be of vector type. All selector values listed in cases must be of the same type.

All branches must have the same type, which must be a bit, vector or integer type.

### Index Selection

The container must be a vector or matrix expression. For a matrix, the first size is relevant here.

The index must be a vector or integer expression.

If the index is a vector of size *width*, then the container size must be at least 2^*width*.

If the index is an integer, then it must be constant, and its value must be non-negative and less than the container
size.

### Range Selection

The container must be a vector expression. Both range ends must be of integer type and constant. The lower range
end must be non-negative and less than or equal to the upper range end; the upper range end must be less than the
container size.

### Function Call

The type rules for function calls depend on the function being called.

## Type Rules for Statements and Initializers

The condition of an if-statement must be of type bit.

The type of the selector of a switch expression follows the same rules as for a switch statement.

For both assignment statements and initializers, the right-hand side must be of the same type as the left-hand side,
or must support *assignment conversion* to the type of the left-hand side. These conversions do *not* occur in
other contexts than assignemnts and initializers; specifically, they do not occur in a sub-expression of a larger
expression.

# Assignment Conversion

Supported assignment conversions for some value to some target type are:
* An integer value can be converted to bit type if its value is 0 or 1.
* An integer value can be converted to vector type as long as the integer meaning of the resulting vector is the
original value.

## Miscellaneous

### Conversion to Text

Any type can be converted to text implicitly. This happens in the initializer of a text-type constant, for text-typed
function arguments and for text concatenation. Implicit conversion to text is usually used for simple debugging
output and does not produce a well-defined output format.

Clocks cannot be converted to text. This does not contradict the above statement that *any* type can be
converted to text because text only exists at compile-time, while clocks only exist at run-time.

Text cannot be converted to any other type. To achieve effects such as integer parsing, functions must be used.
