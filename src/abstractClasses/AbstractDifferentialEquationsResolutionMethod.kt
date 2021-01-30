package abstractClasses

import extensions.isNotOneOf
import objects.DEOutputData
import objects.Functions
import objects.buildMethodTracksAndSolutions
import ui.others.SchemeInterface

abstract class AbstractDifferentialEquationsResolutionMethod: AbstractResolutionMethod() {

    abstract fun getSchemesAsStrings(): List<String>

    abstract fun getSchemesAsListOfSchemeInterface(): List<SchemeInterface>

    open val ynPlusOneVariants: MutableMap<Int, (yn: Double,tn: Double, step: Double) -> Double> = mutableMapOf()

    open fun f(tn: Double, yn: Double): Double = Functions.yPrime(tn, yn)

    open var lastUsedMethodOrScheme = 0

    open fun solve(
            lowerBound: Double,
            upperBound: Double,
            numberOFPointsToPutIn: Int? = null,
            scanStep: Double = .0,
            y0: Double = .0,
            methodOrSchemeToUse: Int = 1): DEOutputData<Double> {
        lastUsedMethodOrScheme = methodOrSchemeToUse

        var yn = y0
        var ynPlus1: Double
        val h = if(numberOFPointsToPutIn.isNotOneOf(null,0)) { (upperBound - lowerBound)/numberOFPointsToPutIn!! } else scanStep
        var tn: Double = lowerBound

        val calcYnPlusOne = ynPlusOneVariants[methodOrSchemeToUse]!! // (yn,tn,step)
        val output = DEOutputData<Double>()

        while(tn <= upperBound) {
            ynPlus1 = calcYnPlusOne(yn,tn,h)

            output.tnValues.add(tn)
            output.ynValues.add(yn)

            yn = ynPlus1
            tn += h
        }

        output.buildMethodTracksAndSolutions()
        return output
    }
}