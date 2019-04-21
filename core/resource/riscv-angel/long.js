goog.math.Long (64-Bit-Integer-Klasse)

Konstanten:
    TWO_PWR_x_DBL_ = 2^x als JS-Number
    ZERO, ONE, NEG_ONE, MIN_VALUE, MAX_VALUE als Long
    TWO_PWR_24_ = 2^24 als Long

offensichtliche Methoden:
    static fromString(s, radix)
    toString(radix)


goog.math.Long = function(a, b) {
    this.low_ = a | 0;
    this.high_ = b | 0
};
goog.math.Long.IntCache_ = {};
goog.math.Long.fromInt = function(a) {
    if (-128 <= a && 128 > a) {
        var b = goog.math.Long.IntCache_[a];
        if (b) return b
    }
    b = new goog.math.Long(a | 0, 0 > a ? -1 : 0); - 128 <= a && 128 > a && (goog.math.Long.IntCache_[a] = b);
    return b
};
goog.math.Long.fromNumber = function(a) {
    return isNaN(a) || !isFinite(a) ? goog.math.Long.ZERO : a <= -goog.math.Long.TWO_PWR_63_DBL_ ? goog.math.Long.MIN_VALUE : a + 1 >= goog.math.Long.TWO_PWR_63_DBL_ ? goog.math.Long.MAX_VALUE : 0 > a ? goog.math.Long.fromNumber(-a).negate() : new goog.math.Long(a % goog.math.Long.TWO_PWR_32_DBL_ | 0, a / goog.math.Long.TWO_PWR_32_DBL_ | 0)
};

goog.math.Long.fromNumber2 = function(a) {
    return new goog.math.Long(a|0, a >> 31);
};

goog.math.Long.fromBits = function(a, b) {
    return new goog.math.Long(a, b)
};
goog.math.Long.prototype.toInt = function() {
    return this.low_
};
goog.math.Long.prototype.toNumber = function() {
    return this.high_ * goog.math.Long.TWO_PWR_32_DBL_ + this.getLowBitsUnsigned()
};
goog.math.Long.prototype.getHighBits = function() {
    return this.high_
};
goog.math.Long.prototype.getLowBits = function() {
    return this.low_
};
goog.math.Long.prototype.getLowBitsUnsigned = function() {
    return 0 <= this.low_ ? this.low_ : goog.math.Long.TWO_PWR_32_DBL_ + this.low_
};
goog.math.Long.prototype.getNumBitsAbs = function() {
    if (this.isNegative()) return this.equals(goog.math.Long.MIN_VALUE) ? 64 : this.negate().getNumBitsAbs();
    for (var a = 0 != this.high_ ? this.high_ : this.low_, b = 31; 0 < b && 0 == (a & 1 << b); b--);
    return 0 != this.high_ ? b + 33 : b + 1
};
goog.math.Long.prototype.isZero = function() {
    return 0 == this.high_ && 0 == this.low_
};
goog.math.Long.prototype.isNegative = function() {
    return 0 > this.high_
};
goog.math.Long.prototype.isOdd = function() {
    return 1 == (this.low_ & 1)
};
goog.math.Long.prototype.equals = function(a) {
    return this.high_ == a.high_ && this.low_ == a.low_
};
goog.math.Long.prototype.notEquals = function(a) {
    return this.high_ != a.high_ || this.low_ != a.low_
};
goog.math.Long.prototype.lessThan = function(a) {
    return 0 > this.compare(a)
};
goog.math.Long.prototype.lessThanOrEqual = function(a) {
    return 0 >= this.compare(a)
};
goog.math.Long.prototype.greaterThan = function(a) {
    return 0 < this.compare(a)
};
goog.math.Long.prototype.greaterThanOrEqual = function(a) {
    return 0 <= this.compare(a)
};
goog.math.Long.prototype.compare = function(a) {
    if (this.equals(a)) return 0;
    var b = this.isNegative(),
        c = a.isNegative();
    return b && !c ? -1 : !b && c ? 1 : this.subtract(a).isNegative() ? -1 : 1
};
goog.math.Long.prototype.negate = function() {
    return this.equals(goog.math.Long.MIN_VALUE) ? goog.math.Long.MIN_VALUE : this.not().add(goog.math.Long.ONE)
};
goog.math.Long.prototype.add = function(a) {
    var b = this.high_ >>> 16,
        c = this.high_ & 65535,
        d = this.low_ >>> 16,
        e = a.high_ >>> 16,
        f = a.high_ & 65535,
        g = a.low_ >>> 16,
        k;
    k = 0 + ((this.low_ & 65535) + (a.low_ & 65535));
    a = 0 + (k >>> 16);
    a += d + g;
    d = 0 + (a >>> 16);
    d += c + f;
    c = 0 + (d >>> 16);
    c = c + (b + e) & 65535;
    return new goog.math.Long((a & 65535) << 16 | k & 65535, c << 16 | d & 65535);
};
goog.math.Long.prototype.subtract = function(a) {
    return this.add(a.negate())
};
goog.math.Long.prototype.multiply = function(a) {
    if (this.isZero() || a.isZero()) return goog.math.Long.ZERO;
    if (this.equals(goog.math.Long.MIN_VALUE)) return a.isOdd() ? goog.math.Long.MIN_VALUE : goog.math.Long.ZERO;
    if (a.equals(goog.math.Long.MIN_VALUE)) return this.isOdd() ? goog.math.Long.MIN_VALUE : goog.math.Long.ZERO;
    if (this.isNegative()) return a.isNegative() ? this.negate().multiply(a.negate()) : this.negate().multiply(a).negate();
    if (a.isNegative()) return this.multiply(a.negate()).negate();
    if (this.lessThan(goog.math.Long.TWO_PWR_24_) &&
        a.lessThan(goog.math.Long.TWO_PWR_24_)) return goog.math.Long.fromNumber(this.toNumber() * a.toNumber());
    var b = this.high_ >>> 16,
        c = this.high_ & 65535,
        d = this.low_ >>> 16,
        e = this.low_ & 65535,
        f = a.high_ >>> 16,
        g = a.high_ & 65535,
        k = a.low_ >>> 16;
    a = a.low_ & 65535;
    var m, h, l, n;
    n = 0 + e * a;
    l = 0 + (n >>> 16);
    l += d * a;
    h = 0 + (l >>> 16);
    l = (l & 65535) + e * k;
    h += l >>> 16;
    l &= 65535;
    h += c * a;
    m = 0 + (h >>> 16);
    h = (h & 65535) + d * k;
    m += h >>> 16;
    h &= 65535;
    h += e * g;
    m += h >>> 16;
    h &= 65535;
    m = m + (b * a + c * k + d * g + e * f) & 65535;
    return goog.math.Long.fromBits(l << 16 | n & 65535, m << 16 | h)
};
goog.math.Long.prototype.div = function(a) {
    if (a.isZero()) throw Error("division by zero");
    if (this.isZero()) return goog.math.Long.ZERO;
    if (this.equals(goog.math.Long.MIN_VALUE)) {
        if (a.equals(goog.math.Long.ONE) || a.equals(goog.math.Long.NEG_ONE)) return goog.math.Long.MIN_VALUE;
        if (a.equals(goog.math.Long.MIN_VALUE)) return goog.math.Long.ONE;
        var b = this.shiftRight(1).div(a).shiftLeft(1);
        if (b.equals(goog.math.Long.ZERO)) return a.isNegative() ? goog.math.Long.ONE : goog.math.Long.NEG_ONE;
        var c = this.subtract(a.multiply(b));
        return b.add(c.div(a))
    }
    if (a.equals(goog.math.Long.MIN_VALUE)) return goog.math.Long.ZERO;
    if (this.isNegative()) return a.isNegative() ? this.negate().div(a.negate()) : this.negate().div(a).negate();
    if (a.isNegative()) return this.div(a.negate()).negate();
    for (var d = goog.math.Long.ZERO, c = this; c.greaterThanOrEqual(a);) {
        for (var b = Math.max(1, Math.floor(c.toNumber() / a.toNumber())), e = Math.ceil(Math.log(b) / Math.LN2), e = 48 >= e ? 1 : Math.pow(2, e - 48), f = goog.math.Long.fromNumber(b), g = f.multiply(a); g.isNegative() || g.greaterThan(c);) b -=
            e, f = goog.math.Long.fromNumber(b), g = f.multiply(a);
        f.isZero() && (f = goog.math.Long.ONE);
        d = d.add(f);
        c = c.subtract(g)
    }
    return d
};
goog.math.Long.prototype.modulo = function(a) {
    return this.subtract(this.div(a).multiply(a));
};
goog.math.Long.prototype.not = function() {
    return new goog.math.Long(~this.low_, ~this.high_);
};
goog.math.Long.prototype.and = function(a) {
    return new goog.math.Long(this.low_ & a.low_, this.high_ & a.high_);
};
goog.math.Long.prototype.or = function(a) {
    return new goog.math.Long(this.low_ | a.low_, this.high_ | a.high_);
};
goog.math.Long.prototype.xor = function(a) {
    return new goog.math.Long(this.low_ ^ a.low_, this.high_ ^ a.high_);
};
goog.math.Long.prototype.shiftLeft = function(a) {
    a &= 63;
    if (0 == a) return this;
    var b = this.low_;
    return 32 > a ? goog.math.Long.fromBits(b << a, this.high_ << a | b >>> 32 - a) : goog.math.Long.fromBits(0, b << a - 32)
};
goog.math.Long.prototype.shiftRight = function(a) {
    a &= 63;
    if (0 == a) return this;
    var b = this.high_;
    return 32 > a ? goog.math.Long.fromBits(this.low_ >>> a | b << 32 - a, b >> a) : goog.math.Long.fromBits(b >> a - 32, 0 <= b ? 0 : -1)
};
goog.math.Long.prototype.shiftRightUnsigned = function(a) {
    a &= 63;
    if (0 == a) return this;
    var b = this.high_;
    return 32 > a ? goog.math.Long.fromBits(this.low_ >>> a | b << 32 - a, b >>> a) : 32 == a ? goog.math.Long.fromBits(b, 0) : goog.math.Long.fromBits(b >>> a - 32, 0)
};
