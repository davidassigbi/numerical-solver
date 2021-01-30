package utils.nonLinearEquations

import abstractClasses.AbstractNonLinearEquationsResolutionMethod
import classes.ParametersSource
import classes.ResolutionMethod
import objects.Functions
import objects.GUIOutputModelForNewtonRaphsonMethod
import objects.InputData
import objects.NLEOutputData
import utils.*
import kotlin.math.abs

object NewtonRaphsonMethod : AbstractNonLinearEquationsResolutionMethod<Double>() {

    private fun df(x : Double) = Functions.df(x)

    private fun fi(xn : Double) = Functions.fi(xn)

    override val methodID = ResolutionMethod.NEWTON_RAPHSON

    fun solve(paramXn : Double, source : ParametersSource = ParametersSource.INPUT_DATA): NLEOutputData<Double> {
        var xn = paramXn
        var xnPlus1 = xn
        var fx = f(xn)
        var dfxnPlus1 = df(xn)
        var index = 0
        var currentGap = InputData.precisionOrTolerance + 1

        val output = NLEOutputData<Double>()

        println("index $THREE_TABS xn $THREE_TABS xn+1 $THREE_TABS |xn-xn+1| $THREE_TABS df(xn+1) ")
        while (index < InputData.maxNumberOfIterations
                && currentGap > InputData.precisionOrTolerance
                && dfxnPlus1 != .0 && dfxnPlus1.isFinite()
                && fx.isFinite()) {

            index++

            xnPlus1 = fi(xn)
            currentGap = abs(xnPlus1 - xn) / abs(xnPlus1)

            dfxnPlus1 = df(xnPlus1)
            fx = f(xnPlus1)

            output.newtonRaphsonMethodTracks.add(GUIOutputModelForNewtonRaphsonMethod(index,xn,xnPlus1,abs(xn-xnPlus1),dfxnPlus1))
            // println("$index $THREE_TABS $xn $THREE_TABS $xnPlus1 $THREE_TABS ${abs(xn-xnPlus1)} $THREE_TABS $dfxnPlus1 ")

            xn = xnPlus1
        }
        if (currentGap <= InputData.precisionOrTolerance
                && dfxnPlus1 != .0 && dfxnPlus1.isFinite()
                && fx.isFinite()
                && index < InputData.maxNumberOfIterations){
            addMethodSolution(xnPlus1,source,paramXn)
            output.solutions.add(xnPlus1)
        }
        else {
            if (dfxnPlus1 == .0) reportMethodError("df(xn) = 0 pour xn = $xn .")
            if (dfxnPlus1.isInfinite()) reportMethodError("df(xn) semble non défini pour xn = $xn .")
            if (index >= InputData.maxNumberOfIterations) reportMethodError("La méthode ne converge pas pour x0 = ${InputData.startPoint} après ${InputData.maxNumberOfIterations} itérations.")
            else {
                reportMethodError("Cette méthode ne converge pas pour les paramètres de départ choisis ")
            }
        }
        return output
    }

    operator fun invoke(useScannedIntervals : Boolean = false,startPoint : Double = InputData.startPoint) {
        if(!useScannedIntervals) {
            solve(startPoint)
        } else {
            for (lowerBound in InputData.intervalsWithOneSolution) {
                solve(lowerBound)
            }
        }
    }
}
