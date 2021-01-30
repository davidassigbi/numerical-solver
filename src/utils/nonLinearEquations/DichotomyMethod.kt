package utils.nonLinearEquations

import classes.ParametersSource
import abstractClasses.AbstractNonLinearEquationsResolutionMethod
import classes.ResolutionMethod
import objects.GUIOutputModelForDichotomyMethod
import objects.InputData
import objects.NLEOutputData
import kotlin.math.abs



object DichotomyMethod : AbstractNonLinearEquationsResolutionMethod<Double>() {

    override val methodID = ResolutionMethod.DICHOTOMY

    fun solve(paramLowerBound: Double, paramUpperBound: Double, source : ParametersSource = ParametersSource.INPUT_DATA): NLEOutputData<Double> {
        var lowerBound = paramLowerBound
        var upperBound = paramUpperBound
        var middleBound = (lowerBound + upperBound ) / 2

        val output = NLEOutputData<Double>()

        if( f(lowerBound) * f(upperBound) == .0 ) {
            if(f(lowerBound) == .0) addMethodSolution(lowerBound); output.solutions.add(lowerBound)
            if(f(upperBound) == .0) addMethodSolution(upperBound); output.solutions.add(upperBound)
        } else if(f(lowerBound) * f(upperBound) < .0) {
            var index = 0
            println("middleBound = $middleBound")
            while (abs(upperBound - lowerBound) > InputData.precisionOrTolerance && index < InputData.maxNumberOfIterations ) {

                output.dichotomyMethodTracks.add(GUIOutputModelForDichotomyMethod(index,lowerBound,f(lowerBound),upperBound,f(upperBound),middleBound,f(middleBound)))

                when {
                    f(lowerBound) * f(middleBound) < 0 -> upperBound = middleBound
                    f(middleBound) * f(upperBound) < 0 -> lowerBound = middleBound
                    f(middleBound) == .0 -> { lowerBound = middleBound ; upperBound = middleBound }
                }
                middleBound = ( upperBound + lowerBound ) / 2
                println("middleBound = $middleBound")
                index++
            }
//        println("middleBound = $middleBound")
            if(index < InputData.maxNumberOfIterations) {
                addMethodSolution(middleBound,source,paramLowerBound)
                output.solutions.add(middleBound)
            }
            else reportMethodError("Veuillez réduire la précision du calcul ou augmenter le nombre maximal d'itérations.")
        } else {
            reportMethodError("Cet intervalle : [${InputData.lowerBound} : ${InputData.upperBound}] ne semble contenir aucune solution car f(${InputData.lowerBound})*f(${InputData.upperBound}) > 0.")
        }

        return output
    }

    operator fun invoke(useScannedIntervals : Boolean = false, lowerBound: Double = InputData.lowerBound, upperBound : Double = InputData.upperBound) {
        if(!useScannedIntervals) {
            solve(lowerBound , upperBound)
        } else {
            for (lBound in InputData.intervalsWithOneSolution) {
                solve(lBound , lBound + InputData.intervalScanStep,ParametersSource.SCANNED_INTERVAL)
            }
        }
    }
}
