package utils.linearInterpolation.polynomialApproximation

import abstractClasses.AbstractResolutionMethod
import classes.ResolutionMethod
import extensions.number.signedString
import objects.BasicOutputData
import objects.InputData
import objects.NewtonInterpolationOutputData
import kotlin.math.absoluteValue

object NewtonInterpolationMethod: AbstractResolutionMethod() {

    override val methodID: ResolutionMethod = ResolutionMethod.NEWTON_IN_LINEAR_INTERPOLATION

    override val outputData: NewtonInterpolationOutputData<Double> = NewtonInterpolationOutputData()

    fun solve(xiValues: List<Double> = InputData.xiValues, yiValues: List<Double> = InputData.yiValues): NewtonInterpolationOutputData<Double> {
        // declarations de fonctions locales
        fun alpha(i: Int) = f(0, i, xiValues, yiValues)

        // declarations de variables locales
        val numberOfKnownValuesPlusOne = xiValues.size
        // No(x) = "" on remettra la valeur de No à 1 à la fin de l'opération
        val newtonPolynomials = mutableListOf<String>()
        var currentPolynomial = ""
        val alphas = mutableListOf<Double>()//alpha(0)) // listOfDividedDifferences
        var fullNewtonInterpolationPolynomial = "" // "${alphas[0]}"
        var reducedNewtonInterpolationPolynomial = "" //"${alphas[0]}+(x${(-1*xiValues[0]).signedString()})("
        var endingParenthesis = ")"

        val output = NewtonInterpolationOutputData<Double>()

        //val n = numberOfKnownValuesPlusOne - 1
        for(i in 0..numberOfKnownValuesPlusOne-1)
            alphas.add(alpha(i))

        newtonPolynomials.add("")
        for(i in 1..numberOfKnownValuesPlusOne-1) {
            currentPolynomial = newtonPolynomials.last()+"(x${(-1*xiValues[i-1]).signedString()})"
            newtonPolynomials.add(currentPolynomial)
        }; newtonPolynomials[0] = "1"

        for(i in 0..numberOfKnownValuesPlusOne-1)
            fullNewtonInterpolationPolynomial += "${alphas[i].signedString()}*${newtonPolynomials[i]}"
        fullNewtonInterpolationPolynomial = fullNewtonInterpolationPolynomial.split(")(").joinToString(")*(")

        reducedNewtonInterpolationPolynomial += ""
        for(i in 0..numberOfKnownValuesPlusOne-2){
            reducedNewtonInterpolationPolynomial += "${alphas[i]}+(x${(-1*xiValues[i]).signedString()})*("// fact(i)
            endingParenthesis += ")"
        }; reducedNewtonInterpolationPolynomial += "${alphas.last()}"+endingParenthesis.dropLast(1)

        InputData.newtonPolynomials = newtonPolynomials
        InputData.listOfDividedDifferences = alphas
        InputData.fullNewtonInterpolationPolynomial = fullNewtonInterpolationPolynomial
        InputData.reducedNewtonInterpolationPolynomial = reducedNewtonInterpolationPolynomial


        output.alphas = alphas
        output.fullPolynomial = fullNewtonInterpolationPolynomial
        output.reducedPolynomial = reducedNewtonInterpolationPolynomial
        output.polynomials = newtonPolynomials
        output.apply {
            for(i in 0 until xiValues.size) {
                coefficients.add(xiValues.subList(0,i).toMutableList())
            }
        }

        println("InputData.newtonPolynomials = ${InputData.newtonPolynomials.joinToString("\n")}")
        println("InputData.listOfDividedDifferences = ${InputData.listOfDividedDifferences}")
        println("InputData.fullNewtonInterpolationPolynomial = ${InputData.fullNewtonInterpolationPolynomial}")
        println("InputData.reducedNewtonInterpolationPolynomial = ${InputData.reducedNewtonInterpolationPolynomial}")

        return output //newtonPolynomials
    }

    operator fun invoke(xiValues: List<Double> = InputData.xiValues, yiValues: List<Double> = InputData.yiValues): NewtonInterpolationOutputData<Double> {
        return solve(xiValues, yiValues)
    }

    fun f(a: Int = 0, b: Int = a, xiValues: List<Double>, yiValues: List<Double>): Double {
        return when((b - a).absoluteValue) {
            // f[x0] || on cherche en fait à calculer la différence divisée d'un seul point
            0 -> yiValues[a] // or yiValues[b]
            // f[x0, x1]
            1 -> (yiValues[a] - yiValues[b]) / (xiValues[a] - xiValues[b])
            // f[x0,x1,x2] || (b - a).absoluteValue > 1
            else -> (1.toDouble()/(xiValues[a] - xiValues[b]))*(f(a, b - 1, xiValues, yiValues) - f(a + 1, b, xiValues, yiValues))
        }
    }
}