.option norvc

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// divrem
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

.globl udivrem
.set udivrem, 8

.globl udiv
.set udiv, 12

.globl urem
.set urem, 16

.globl div
.set div, 20

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// draw
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

.globl selectDrawPlane
.set selectDrawPlane, 24

.globl selectDisplayPlane
.set selectDisplayPlane, 28

.globl clearScreen
.set clearScreen, 32

.globl setPixel
.set setPixel, 36

.globl setDrawColor
.set setDrawColor, 40

.globl drawPixel
.set drawPixel, 44

.globl drawAxisAlignedRectangle
.set drawAxisAlignedRectangle, 48

.globl drawLine
.set drawLine, 52

.globl drawTriangle
.set drawTriangle, 56

.globl drawCharacter
.set drawCharacter, 60

.globl drawText
.set drawText, 64

.globl setFont
.set setFont, 68

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// terminal
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

.globl termInitialize
.set termInitialize, 72

.globl termPrintString
.set termPrintString, 76

.globl termPrintChar
.set termPrintChar, 80

.globl termPrintInt
.set termPrintInt, 84

.globl termPrintUnsignedInt
.set termPrintUnsignedInt, 88

.globl termPrintHexInt
.set termPrintHexInt, 92

.globl termPrintUnsignedHexInt
.set termPrintUnsignedHexInt, 96

.globl termPrintln
.set termPrintln, 100

.globl termPrintlnString
.set termPrintlnString, 104

.globl termPrintlnChar
.set termPrintlnChar, 108

.globl termPrintlnInt
.set termPrintlnInt, 112

.globl termPrintlnUnsignedInt
.set termPrintlnUnsignedInt, 116

.globl termPrintlnHexInt
.set termPrintlnHexInt, 120

.globl termPrintlnUnsignedHexInt
.set termPrintlnUnsignedHexInt, 124

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// profiling
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

.globl profReset
.set profReset, 128

.globl profLog
.set profLog, 132

.globl profDisplay
.set profDisplay, 136
