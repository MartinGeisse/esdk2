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

.globl scroll
.set scroll, 72

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// term
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

.globl termInitialize
.set termInitialize, 76

.globl termPrintString
.set termPrintString, 80

.globl termPrintChar
.set termPrintChar, 84

.globl termPrintInt
.set termPrintInt, 88

.globl termPrintUnsignedInt
.set termPrintUnsignedInt, 92

.globl termPrintHexInt
.set termPrintHexInt, 96

.globl termPrintUnsignedHexInt
.set termPrintUnsignedHexInt, 100

.globl termPrintln
.set termPrintln, 104

.globl termPrintlnString
.set termPrintlnString, 108

.globl termPrintlnChar
.set termPrintlnChar, 112

.globl termPrintlnInt
.set termPrintlnInt, 116

.globl termPrintlnUnsignedInt
.set termPrintlnUnsignedInt, 120

.globl termPrintlnHexInt
.set termPrintlnHexInt, 124

.globl termPrintlnUnsignedHexInt
.set termPrintlnUnsignedHexInt, 128

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// profiling
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

.globl profReset
.set profReset, 132

.globl profLog
.set profLog, 136

.globl profDisplay
.set profDisplay, 140

