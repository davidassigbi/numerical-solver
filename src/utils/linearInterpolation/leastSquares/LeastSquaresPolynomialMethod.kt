package utils.linearInterpolation.leastSquares

import abstractClasses.AbstractResolutionMethod
import classes.LinearSystemOfEquations
import classes.ResolutionMethod
import extensions.number.signedString
import objects.InputData
import objects.LeastSquaresOutputData
import utils.linearSystemOfEquations.GaussWithTotalPivotStrategy
import kotlin.math.pow

object LeastSquaresPolynomialMethod: AbstractResolutionMethod() {

    override val methodID: ResolutionMethod = ResolutionMethod.LEAST_SQUARES

    override val outputData: LeastSquaresOutputData<Double> = LeastSquaresOutputData()

    fun solve(xiValues: List<Double> = InputData.xiValues, yiValues: List<Double> = InputData.yiValues, degree: Int): LeastSquaresOutputData<Double> {
        val n = xiValues.size - 1
        val P: LinearSystemOfEquations<Double>

        val p = degree
        var polynomial = ""
        val values: List<Double>

        val output = LeastSquaresOutputData<Double>()

        if(p < n) {
            P = LinearSystemOfEquations(p+1)

            for(j in 0 until p+1) {
                for(k in j until p+1) {
                    P[j][k] = .0;
                    for(i in 0 until xiValues.size) {
                        P[j][k] += xiValues[i].pow(2*p-j-k)
                    }
                    P[k][j] = P[j][k];
                }
                for(i in 0 until xiValues.size) {
                    P.B[j] += xiValues[i].pow(p-j) * yiValues[i]
                }
            }

            //
            P.backupSystemOriginalState()
            output.builtLinearSystem = P

            // output.builtLinearSystem.backupSystemOriginalState()

            println("\n\n${P.toLinearSystemOfEquations()}\n")

            GaussWithTotalPivotStrategy(system = P)

            values = P.solutionVector.values()

            for(i in 0..p)
                polynomial += "${values[i].signedString()}*x^${p-i}"

            //
            output.polynomial = polynomial

            // remplissaage dess paramètres de résultat
            InputData.leastSquaresPolynomial = polynomial
            InputData.leastSquaresPolynomialFactors = values.toMutableList()
            InputData.leastSquaresPolynomialDegree = p
        } else {
            values = emptyList()
        }
        output.coefficients = values.toMutableList()

        return output
    }

    operator fun invoke(xiValues: List<Double> = InputData.xiValues, yiValues: List<Double> = InputData.yiValues, degree: Int): LeastSquaresOutputData<Double> {
        return solve(xiValues, yiValues, degree)
    }
}