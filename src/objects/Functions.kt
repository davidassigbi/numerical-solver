package objects

import classes.Func
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.tan

object Functions {
    /*
    x.pow(3) + 3* x.pow(2) + 3*x+1
    2 * tan(x) - 1
    */
    val f = Func({ x, _ -> atan((x + 1) / 2) - x }, "x * x - 2 * x + 1")

    val g = Func({ x, _ -> f(x) - x }, "$f - x")

    val fx = Func({ x, _ -> x - f(x) }, "x - $f")

    /*
    * 2 * x - 2
    * */

    val df = Func({ x, _ -> - 1 + (0.5) / ( 1 + ((x + 1)/2).pow(2) ) }, "2 * x - 2")

    val fi = Func({ x, _ -> x - f(x) / df(x) }, "x - f(x) / df(x)")

    val dfApproximative = Func({ xn, xnMoins1 -> (f(xn) - f(xnMoins1)) / (xn - xnMoins1) }, "(f(x1) - f(x0)) / (x1 - x0)")

    val fiApproximativeBiss = Func({ xn, xnMoins1 -> xn - f(xn) / dfApproximative(xn, xnMoins1) }, "x1 - f(x1) / df(x1 , x0)")

    val fiApproximativeDenominator = Func({ xn, xnMoins1 -> f(xn) - f(xnMoins1) }, "f(xn) - f(xn-1)")

    val fiApproximative = Func({ xn, xnMoins1 -> (f(xn) * xnMoins1 - f(xnMoins1) * xn) / (fiApproximativeDenominator(xn, xnMoins1)) }, "( f(xn)*xn-1 -  f(xn-1)*xn ) / ( df(xn,xn-1))")

    val yPrime = Func({t: Double, y: Double -> t * cos(y) },"t * cos(y)")

    val integrationFunction = Func({ x, _ -> x * x * x},"x^3")

    fun computeFrom(x: Double, coefList: List<Double>, reversed: Boolean = false): Double {
        var result = .0
        val coefs = if(reversed) coefList.reversed() else coefList

        for(i in 0 until coefs.size)
            result += coefs[i].pow(i)

        return result
    }

    fun computeFractionalFunction(x: Double, numeratorCoefList: List<Double>,denominatorCoefList: List<Double>): Double {
        var result = .0
        var numeratorValue = 1.0
        var denominatorValue = 1.0

        for(coef in numeratorCoefList)
            numeratorValue *= (x + coef)
        for(coef in denominatorCoefList)
            denominatorValue *= (x + coef)

        if(denominatorValue != .0)
            result = numeratorValue / denominatorValue

        return result
    }
}
