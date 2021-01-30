package utils.differentialEquations

import abstractClasses.AbstractDifferentialEquationsResolutionMethod
import classes.ResolutionMethod
import objects.DEOutputData
import ui.others.ALL_SCHEMES
import ui.others.SchemeInterface


object EulerMethod: AbstractDifferentialEquationsResolutionMethod() {

    override val methodID: ResolutionMethod = ResolutionMethod.EULER

    override val outputData: DEOutputData<Double> = DEOutputData()

    override fun getSchemesAsStrings(): List<String> {
        return SCHEMES.values().map { it.toString() }
    }

    override fun getSchemesAsListOfSchemeInterface(): List<SchemeInterface> {
        return SCHEMES.values().asList()
    }

    const val PROGRESSIVE_EULER_SCHEME  = 1
    const val RETROGRADE_EULER_SCHEME = 2

    enum class SCHEMES(override val schemeName: String = "", override val id: Int = 0): SchemeInterface {
        PROGRESSIVE_EULER_SCHEME("Schéma progressif",1),
        RETROGRADE_EULER_SCHEME("Schéma rétrograde",2),
        ALL_SCHEMES(ui.others.ALL_SCHEMES)
        ;
        constructor(other: SchemeInterface): this(other.schemeName, other.id)
        override fun toString() = this.schemeName
        companion object {
            fun get(str: String) = values().first { it.schemeName == str }
        }
    }

    init {
        lastUsedMethodOrScheme = SCHEMES.PROGRESSIVE_EULER_SCHEME.id
        ynPlusOneVariants.apply {
            this.clear()
            this[SCHEMES.PROGRESSIVE_EULER_SCHEME.id] = { yn: Double, tn: Double, step: Double ->
                (step * f(tn, yn) + yn)
            }
            this[SCHEMES.RETROGRADE_EULER_SCHEME.id] =  { yn: Double, tn: Double, step: Double ->
                (step * f(tn + step, yn + step * f(tn, yn)) + yn)
            }
        }
    }

    operator fun invoke(
            lowerBound: Double,
            upperBound: Double,
            numberOFPointsToPutIn: Int? = null,
            scanStep: Double = .0,
            y0: Double = .0,
            methodOrSchemeToUse: Int = EulerMethod.SCHEMES.PROGRESSIVE_EULER_SCHEME.id): DEOutputData<Double> {
        return solve(lowerBound, upperBound, numberOFPointsToPutIn, scanStep, y0, methodOrSchemeToUse)
    }
}

/*
        lastUsedMethodOrScheme = methodOrSchemeToUse
        var yn = y0
        var ynPlus1 = yn
        val h: Double
        var tn: Double = a
        var numberOFPointsToPutIn = numberOFPointsToPutIn ?: 0

        val calcYnPlusOne = ynPlusOneVariants[methodOrSchemeToUse]!! // (yn,tn,step)

        val resultList = mutableListOf<Pair<Double,Double>>()
        val result = DEOutputData<Double>()

        if(numberOFPointsToPutIn != 0) {
            h = (b - a)/numberOFPointsToPutIn
        } else {
            numberOFPointsToPutIn = ((b - a) / scanStep).toInt()
            h = scanStep
        }

        while(tn < b) {
            ynPlus1 = calcYnPlusOne(yn,tn,h)

            resultList.add(Pair(tn,yn))
            result.solutions.add(Pair(tn,yn))

            yn = ynPlus1
            tn += h
        }
* */