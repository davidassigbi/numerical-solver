package utils.nonLinearEquations

import abstractClasses.AbstractNonLinearEquationsResolutionMethod
import classes.ParametersSource
import classes.ResolutionMethod
import objects.Functions
import objects.GUIOutputModelForFixedPointMethod

import objects.InputData
import objects.NLEOutputData
import kotlin.math.abs
/* Liste des variables que cette fonction utilise dans l'objet InputData
*   -> InputData.startPoint
*   -> InputData.maxNumberOfIterations
* */
object FixedPointMethod : AbstractNonLinearEquationsResolutionMethod<Double>() {

    override val methodID = ResolutionMethod.FIXED_POINT

    fun g(x: Double) = Functions.g(x)

    fun solve(paramX : Double, source : ParametersSource = ParametersSource.INPUT_DATA): NLEOutputData<Double> {
        val FIXED_POINT_METHOD_ITERATION_OVERFLOW  = "La methode de substition ne converge pas " +
                "pour X0 = ${InputData.startPoint} " +
                "après ${InputData.maxNumberOfIterations} iterations. " +
                "\n Veuillez augmenter le nombre maximal d'iterations ou changer la valeur X0 de départ " +
                "et recommencer."

        var index = 0
        var x = paramX

        val output = NLEOutputData<Double>()

        if(f(x) == .0) {
            output.fixedPointMethodTracks.add(GUIOutputModelForFixedPointMethod(index,x,f(x),f(x)-x))
        } else {
            while (index < InputData.maxNumberOfIterations
                    && f(x).isFinite()
                    && abs(f(x) - x) > InputData.precisionOrTolerance) {
                println("currentIterationIndex = $index ; x = $x ; f(x) = ${f(x)} ;|f(x) - x| = ${abs(f(x) - x)}")

                output.fixedPointMethodTracks.add(GUIOutputModelForFixedPointMethod(index,x,f(x),abs(f(x)-x)))

                x = f(x)
                index++
            }
        }

        /* on verifie si la precision obtenue a la fin est assez petite */
        when {
            index >= InputData.maxNumberOfIterations -> {
                println(FIXED_POINT_METHOD_ITERATION_OVERFLOW)
                reportMethodError(FIXED_POINT_METHOD_ITERATION_OVERFLOW)
            }
            f(x).isInfinite() -> reportMethodError("f(x) semble être infini pour x = $x")
            abs(f(x) - x ) <= InputData.precisionOrTolerance -> {
                addMethodSolution(x, source, paramX)
                output.solutions.add(x)
            }
        }
        return output
    }

    operator fun invoke(useScannedIntervals : Boolean = false, x : Double = InputData.aPoint){
        if(!useScannedIntervals) {
            solve(x)
        } else {
            for (lowerBound in InputData.intervalsWithOneSolution) {
                solve(lowerBound , ParametersSource.SCANNED_INTERVAL)
            }
        }
    }
}