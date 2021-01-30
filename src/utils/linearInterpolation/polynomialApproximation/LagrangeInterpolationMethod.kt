package utils.linearInterpolation.polynomialApproximation

import abstractClasses.AbstractResolutionMethod
import classes.ResolutionMethod
import extensions.mutablesList.emptyMutableListOf
import extensions.number.signedString
import extensions.ranges.except
import objects.InputData
import objects.LagrangeOutputData

object LagrangeInterpolationMethod: AbstractResolutionMethod() {

    override val methodID: ResolutionMethod = ResolutionMethod.LAGRANGE

    override val outputData: LagrangeOutputData<Double> = LagrangeOutputData()

    fun solve(xiValues: List<Double> = InputData.xiValues, yiValues: List<Double> = InputData.yiValues): LagrangeOutputData<Double> {
        val numberOfKnownValues = xiValues.size
        val fiPolynomials = emptyMutableListOf<String>()
        var fi = ""
        var denominator = .0

        val output = LagrangeOutputData<Double>()

        for(i in 0 until numberOfKnownValues) {
            denominator = 1.0
            output.coefficientsInNumerator.add(mutableListOf())
            fi = "${yiValues[i].signedString()}*("
            for(j in 0 until numberOfKnownValues except i) {
                output.coefficientsInNumerator[i].add(-xiValues[j])
                fi += "(x${(-1*xiValues[j]).signedString()})"
                denominator *= (xiValues[i] - xiValues[j])
            }
            fi += ")/(${denominator.signedString()})"
            output.denominators.add(denominator)
            output.fiPolynomials.add(fi)
            fiPolynomials.add(fi)
        }
        output.finalPolynomial = output.fiPolynomials.joinToString("").split(")(").joinToString(")*(")

        InputData.lagrangeInterpolationPolynomial = fiPolynomials.joinToString("").split(")(").joinToString(")*(")
        InputData.fiPolynomialsOfLagrange = fiPolynomials

        println("InputData.lagrangeInterpolationPolynomial = ${InputData.lagrangeInterpolationPolynomial}")
        println("InputData.fiPolynomialsOfLagrange = ${InputData.fiPolynomialsOfLagrange}")

        return output
        // return polynomials //.joinToString("")
    }

    operator fun invoke(xiValues: List<Double> = InputData.xiValues, yiValues: List<Double> = InputData.yiValues): LagrangeOutputData<Double> {
        return solve(xiValues, yiValues)
    }
}