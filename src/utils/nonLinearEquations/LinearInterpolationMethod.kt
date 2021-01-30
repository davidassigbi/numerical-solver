package utils.nonLinearEquations

import abstractClasses.AbstractNonLinearEquationsResolutionMethod
import classes.ParametersSource
import classes.ResolutionMethod
import objects.Functions
import objects.GUIOutputModelForDichotomyMethod
import objects.InputData
import objects.NLEOutputData
import kotlin.math.abs

object LinearInterpolationMethod : AbstractNonLinearEquationsResolutionMethod<Double>() {

    private fun fi(xn: Double, xnMoins1: Double) = Functions.fiApproximative(xn, xnMoins1)

    private fun fiDenominator(xn: Double, xnMoins1: Double) = Functions.fiApproximativeDenominator(xn, xnMoins1)

    override val methodID = ResolutionMethod.LINEAR_INTERPOLATION_IN_NON_LINEAR_EQUATIONS

    fun solve(paramA: Double , paramB: Double, source: ParametersSource = ParametersSource.INPUT_DATA): NLEOutputData<Double> {
        var a = paramA
        var b = paramB
        var index = 0
        var middle = fi(b , a)

        val output = NLEOutputData<Double>()

        if (f(a) * f(b) == .0) {
            if (f(a) == .0) addMethodSolution(a); output.solutions.add(a)
            if (f(b) == .0) addMethodSolution(b); output.solutions.add(b)
        }
        else { //if(f(a) * f(b) > .0)
//            println("middle = $middle")
            while (abs(b - a) > InputData.precisionOrTolerance
                    && fiDenominator(b, a) != .0
                    && index < InputData.maxNumberOfIterations) {

                output.linearInterpolationMethodTracks.add(GUIOutputModelForDichotomyMethod(index,a,f(a),b,f(b),middle,f(middle)))

                val times = f(a) * f(middle)
                when {
                    times < 0 -> {
                        b = middle; middle = fi(b, a)
                    }
                    times > 0 -> {
                        a = middle; middle = fi(b, a)
                    }
                    times == .0 -> {
                        a = middle; b = middle
                    }
                }
                index++
            }
//                println("middle = $middle")
//            println("middleBound = $middle")
            if (fiDenominator(b, a) != .0) {
                addMethodSolution(middle,source,paramA)
                output.solutions.add(middle)
            }
            else reportMethodError("Le denominateur a l'itération $index est null. Impossible de continuer d'utiliser la méthode.")
        }
        return output
    }

    operator fun invoke(useScannedIntervals: Boolean = false, a: Double = InputData.aPoint, b: Double = InputData.anotherPoint) {
        if(!useScannedIntervals) {
            solve(a , b)
        } else {
            for (lowerBound in InputData.intervalsWithOneSolution) {
                solve(lowerBound , lowerBound + InputData.intervalScanStep)
            }
        }
    }
}
