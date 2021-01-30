package classes

import java.math.BigInteger

enum class NumberType(val initializer : Number,val typeName : String) {
    INTEGER(initializer = 0.toInt(),typeName = "entier"),
    LONG(initializer = 0.toLong(),typeName = "entier"),
    BIGINTEGER(initializer = BigInteger.ZERO,typeName = "entier"),
    FLOAT(initializer = 0.toFloat(),typeName = "réel"),
    DOUBLE(initializer = 0.toDouble(),typeName = "réel")
}
