package utils.nonLinearEquations

import abstractClasses.AbstractNonLinearEquationsResolutionMethod
import classes.ParametersSource
import classes.ResolutionMethod
import objects.Functions
import objects.GUIOutputModelForSecantMethod
import objects.InputData
import objects.NLEOutputData
import utils.*

import kotlin.math.abs

object SecantMethod : AbstractNonLinearEquationsResolutionMethod<Double>() {

    private fun fi(xn : Double , xnMoins1 : Double) = Functions.fiApproximative(xn,xnMoins1)

    private fun fiDenominator(xn : Double , xnMoins1 : Double) = Functions.fiApproximativeDenominator(xn,xnMoins1)

    override val methodID = ResolutionMethod.SECANT

    fun solve(paramXnMinus1: Double, paramXn: Double, source: ParametersSource = ParametersSource.INPUT_DATA): NLEOutputData<Double> {
        var xnMoins1 = paramXnMinus1
        var xn = paramXn
        var xnPlus1 = fi(xn,xnMoins1)
        var fx = f(xnMoins1)
        var index = 0

        val output = NLEOutputData<Double>()

        var currentPrecision = InputData.precisionOrTolerance + 1

        println("index $THREE_TABS xn-1 $THREE_TABS xn $THREE_TABS xn+1 $THREE_TABS |xn-xn+1| $THREE_TABS  ")

        while (index < InputData.maxNumberOfIterations
                && currentPrecision > InputData.precisionOrTolerance
                && fiDenominator(xn,xnMoins1) != .0
                && fx.isFinite() ) {
            index++

            xnPlus1 = fi(xn,xnMoins1)
            currentPrecision = abs(xnPlus1 - xn) / abs(xnPlus1)

            fx = f(xnPlus1)

            output.secantMethodTracks.add(GUIOutputModelForSecantMethod(index,xnMoins1,xn,xnPlus1,abs(xn-xnPlus1)))
//        println("$index $THREE_TABS $xnMoins1 $THREE_TABS $xn $THREE_TABS $xnPlus1 $THREE_TABS ${abs(xn-xnPlus1)} ")

            xnMoins1 = xn
            xn = xnPlus1
        }
        if(currentPrecision <= InputData.precisionOrTolerance
                && fx.isFinite()
                && index < InputData.maxNumberOfIterations){
            addMethodSolution(xnPlus1,source,paramXnMinus1)
            output.solutions.add(xnPlus1)
        }
        else {
            if(fiDenominator(xn,xnMoins1) != .0) reportMethodError("Le denominateur : f(xn) - f(xn-1) = 0 avec : xn = $xn  et xn-1 = $xnMoins1")
            if(fx.isInfinite()) reportMethodError("f(x) est infini après $index itérations")
            if(fx.isNaN()) reportMethodError("f(x) = NaN après $index itérations")
            if(index >= InputData.maxNumberOfIterations) reportMethodError("La méthode ne converge pas pour x0 = ${InputData.startPoint} après ${InputData.maxNumberOfIterations} itérations.")
        }

        return output
    }

    operator fun invoke(useScannedIntervals : Boolean = false, xnMoins1 : Double = InputData.aPoint, xn : Double = InputData.anotherPoint){
        if(!useScannedIntervals) {
            solve(xnMoins1, xn)
        } else {
            for (lowerBound in InputData.intervalsWithOneSolution) {
                solve(lowerBound , lowerBound + InputData.intervalScanStep,ParametersSource.SCANNED_INTERVAL)
            }
        }
    }
}