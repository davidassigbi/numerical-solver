package utils.differentialEquations

import abstractClasses.AbstractDifferentialEquationsResolutionMethod
import classes.ResolutionMethod
import objects.DEOutputData
import ui.others.SchemeInterface
import ui.others.WillApplyMethodAndUpdateUI

object RungeKuntaMethod: AbstractDifferentialEquationsResolutionMethod() {

    override val methodID: ResolutionMethod = ResolutionMethod.RUNGE_KUNTA

    override val outputData: DEOutputData<Double> = DEOutputData()

    override fun getSchemesAsStrings(): List<String> {
        return SCHEMES.values().map { it.toString() }
    }

    override fun getSchemesAsListOfSchemeInterface(): List<SchemeInterface> {
        return SCHEMES.values().asList()
    }

    const val CRANK_NICOLSON = 1
    const val HEUN = 2
    const val EULER_MODDED = 3
    const val RK2_MIDDLE_POINT = 4
    const val RK4_SIMPSON = 5

    enum class SCHEMES(override val schemeName: String = "", override val id: Int = 0): SchemeInterface {
        CRANK_NICOLSON("Schéma implicite ou schéma de Crank-Nicolson",1),
        HEUN("Schéma explicite ou schéma de Heun",2),
        EULER_MODDED("Méthode d'Euler modifiée",3),
        RK2_MIDDLE_POINT("Méthode du point du milieu",4),
        RK4_SIMPSON("Méthode de Runge-Kunta d'ordre 4 ou méthode d'intégration de Simpson",5),
        ALL_SCHEMES(ui.others.ALL_SCHEMES)
        ;
        constructor(other: SchemeInterface): this(other.schemeName, other.id)
        override fun toString() = this.schemeName
        companion object {
            fun get(str: String) = values().first { it.schemeName == str }
        }
    }

    init {
        lastUsedMethodOrScheme = SCHEMES.RK2_MIDDLE_POINT.id
        ynPlusOneVariants.apply {
            this.clear()
            this[SCHEMES.CRANK_NICOLSON.id] = { yn: Double, tn: Double, step: Double ->
                ((step/2) * (f(tn, yn) + f(tn+step,yn+step*f(tn,yn))) + yn)
            }
            this[SCHEMES.HEUN.id] = { yn: Double, tn: Double, step: Double ->
                ((step/2) * (f(tn, yn) + f(tn+step,yn+step*f(tn,yn))) + yn)
            }
            this[SCHEMES.EULER_MODDED.id] = { yn: Double, tn: Double, step: Double ->
                (step * f(tn+step/2,yn + 0.5 * f(tn,yn)) + yn)
            }
            this[SCHEMES.RK2_MIDDLE_POINT.id] = { yn: Double, tn: Double, step: Double ->
                ((step/2) * (f(tn, yn) + f(tn+step,yn+step*f(tn,yn))) + yn)
            }
            this[SCHEMES.RK4_SIMPSON.id] = { yn: Double, tn: Double, step: Double ->
                val K1 = f(tn,yn)
                val K2 = f(tn+step/2,yn+(step/2)*K1)
                val K3 = f(tn+step/2, yn+(step/2)*K2)
                val K4 = f(tn + step, yn + step * K3)
                yn + (step/6) * (K1 + 2 * K2 + 2 * K3 + K4)
            }
        }
    }


    operator fun invoke(
            lowerBound: Double,
            upperBound: Double,
            numberOFPointsToPutIn: Int? = null,
            scanStep: Double = .0,
            y0: Double = .0,
            methodOrSchemeToUse: Int = SCHEMES.RK2_MIDDLE_POINT.id): DEOutputData<Double> {
        return solve(lowerBound, upperBound, numberOFPointsToPutIn, scanStep, y0, methodOrSchemeToUse)
    }
}
/*
        var yn = y0
        var ynPlus1 = yn
        val h: Double
        var tn: Double = a
        var numberOFPointsToPutIn = numberOFPointsToPutIn ?: 0
        val resultList = mutableListOf<Pair<Double,Double>>()
        val calcYnPlusOne = ynPlusOneVariants[lastUsedMethodOrScheme]!! //.invoke(yn,tn,step)


        if(numberOFPointsToPutIn != 0) {
            h = (b - a)/numberOFPointsToPutIn
        } else {
            numberOFPointsToPutIn = ((b - a) / scanStep).toInt()
            h = scanStep
        }

        while(tn < b) {
            ynPlus1 = calcYnPlusOne(yn,tn,h)

            resultList.add(Pair(tn,yn))

            yn = ynPlus1
            tn += h
        }
 */