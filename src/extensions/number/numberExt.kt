
package extensions.number

import classes.MatrixNxM
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.sign

@Suppress("IMPLICIT_CAST_TO_ANY")
inline fun <reified T : Number> Number.toTypedValue() : T = when(T::class) {
    Short::class -> toShort()
    Int::class -> toInt()
    Long::class -> toLong()
    Float::class -> toFloat()
    Double::class -> toDouble()
    else -> toShort()
} as T

infix fun Number.by(other: Number) = Pair(this,other)

infix fun Double.end(other: Double) = Pair(this,other)
infix fun Double.end(other: Int) = Pair(this,other.toDouble())
infix fun Int.end(other: Int) = Pair(this.toDouble(),other.toDouble())
infix fun Int.end(other: Double) = Pair(this.toDouble(),other)

fun Number.boundedBy(lowerBound: Number, upperBound: Number, boundsIncluded: Boolean = true) : Boolean {
    return if(boundsIncluded)
        this.toDouble() >= lowerBound.toDouble() && (this.toDouble() <= upperBound.toDouble())
    else
        this.toDouble() > lowerBound.toDouble() && this.toDouble() < upperBound.toDouble()
}

fun Number.getSign() = when(this.toDouble().sign) {
    -1.0 -> "-"
    else -> "+"
}

fun String.eliminateExponent(): String {
    return  if(this.contains('E'))
                split("E").joinToString("*(10^(")+"))"
            else this
}

fun Double.signedString(signedIfPositive: Boolean = true, spaced: Boolean = false): String =
        if(sign != 1.0)
            "-${if(spaced) " " else ""}${absoluteValue.toString().eliminateExponent()}"
        else
            "${if(signedIfPositive)"+" else ""}${if(spaced) " " else ""}${this.toString().eliminateExponent()}"

fun Number.isEven() = this.toInt() % 2 == 0

fun Number.isOdd() = !isEven()

fun Number.toBoolean(): Boolean = this.toInt() > 0

operator fun <T: Number> T.times(other: MatrixNxM<T>): MatrixNxM<T> = other * this

fun createInferiorMethod(strict: Boolean): Number.(Number) -> Boolean {
    return if (strict)
        fun Number.(other: Number): Boolean = this.toDouble() <= other.toDouble() // Int::compStrict // as KFunction2<Int, Int, Boolean>
    else
        fun Number.(other: Number): Boolean = this.toDouble() < other.toDouble() // Int::compNotStrict //

}
fun createSuperiorMethod(strict: Boolean): Number.(Number) -> Boolean {
    return if (strict)
        fun Number.(other: Number): Boolean = this.toDouble() > other.toDouble() // Int::compStrict // as KFunction2<Int, Int, Boolean>
    else
        fun Number.(other: Number): Boolean = this.toDouble() >= other.toDouble() // Int::compNotStrict //
}

fun createInverseInferiorMethod(strict: Boolean): Number.(Number) -> Boolean = createInferiorMethod(!strict)

fun createInverseSuperiorMethod(strict: Boolean): Number.(Number) -> Boolean = createSuperiorMethod(!strict)
