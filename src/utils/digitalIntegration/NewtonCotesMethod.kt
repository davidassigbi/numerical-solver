package utils.digitalIntegration

import abstractClasses.AbstractDigitalIntegrationResolutionMethod
import classes.Func
import classes.ResolutionMethod
import extensions.isOneOf
import extensions.number.isEven
import extensions.number.isOdd
import extensions.test
import objects.*
import ui.others.SchemeInterface
import utils.linearInterpolation.polynomialApproximation.LagrangeInterpolationMethod

object NewtonCotesMethod: AbstractDigitalIntegrationResolutionMethod() {

    override val methodID: ResolutionMethod = ResolutionMethod.NEWTON_COTES

    override val outputData: DIOutputData<Double> = DIOutputData()

    override fun getSchemesAsStrings(): List<String> {
        return SCHEMES.values().map { it.toString() }
    }

    override fun getSchemesAsListOfSchemeInterface(): List<SchemeInterface> {
        return SCHEMES.values().asList()
    }

    enum class SCHEMES(override val schemeName: String = "", override val id: Int = 0): SchemeInterface {
        TRAPEZOID_RULE("Méthode des trapèzes (ordre 1) {2 points}",1),
        SIMPSON_1_3_RULE("Méthode de Simpson (ordre 2) [Pas dans le cours] {3 points}",2),
        SIMPSON_3_8_RULE("Méthode de Simpson (ordre 3) {4 points}",3),
        BOOLE_VILLARCEAU_RULE("Méthode de Boole-Villarceau (ordre 5) {5 points}",4),
        WEDDLE_HARDY_RULE("Méthode de Weddle-Hardy (ordre 7) {7 points}",6),
        ALL_SCHEMES(ui.others.ALL_SCHEMES)
        ;
        constructor(other: SchemeInterface): this(other.schemeName, other.id)
        override fun toString(): String = this.schemeName
        companion object {
            fun get(str: String) = values().first { it.schemeName == str }
        }
    }
    const val TRAPEZOID_RULE = 1
    const val SIMPSON_1_3_RULE = 2
    const val SIMPSON_3_8_RULE = 3
    const val BOOLE_VILLARCEAU_RULE = 4
    const val WEDDLE_HARDY_RULE = 6


    val wi: MutableMap<Int,List<Double>> = mutableMapOf(
            SCHEMES.TRAPEZOID_RULE.id to listOf(1.0/2, 1.0/2),
            SCHEMES.SIMPSON_1_3_RULE.id to listOf(1.0/6, 2.0/3, 1.0/6),
            SCHEMES.SIMPSON_3_8_RULE.id to listOf(1.0/8, 3.0/8, 3.0/8, 1.0/8),
            SCHEMES.BOOLE_VILLARCEAU_RULE.id to listOf(7.0/90, 16.0/45, 2.0/15, 16.0/45, 7.0/90),
            SCHEMES.WEDDLE_HARDY_RULE.id to listOf(41.0/840, 9.0/35, 9.0/280, 34.0/105, 9.0/280, 9.0/35, 41.0/840)
    )

    fun getOrder(rank: Int): Int {
        return when {
            rank.isOneOf(1,3) -> rank + 1
            rank.test { isEven() && this <= 6 } -> rank + 1
            else -> 1
        }
    }

    fun ruleFor(numberOfPoints: Int): Int {
        return when {
            numberOfPoints.isOneOf(2,4) -> numberOfPoints
            numberOfPoints.test { isOdd() && this <= 7 } -> numberOfPoints - 1
            else -> 1
        }
    }

    fun numberOfPoints(ruleID: Int): Int {
        return when {
            ruleID.test { isOneOf(1, 3) || (isEven() && this <= 6) } -> ruleID + 1
            else -> 2
        }
    }

    operator fun invoke(
            a: Double,
            b: Double,
            schemeID: Int = SCHEMES.BOOLE_VILLARCEAU_RULE.id): NewtonCotesOutputData<Double> {

        var result = .0
        val ksiValues = mutableListOf<Double>()
        val tiValues = mutableListOf<Double>()
        val ftiValues = mutableListOf<Double>()

        // on choisit n+1 points equidistants donc n = wi[schemeID]!!.size - 1
        val numberOfPoints = wi[schemeID]!!.size-1
        val step = (b-a) / numberOfPoints
        val output = NewtonCotesOutputData<Double>()
        output.result = .0

        for(i in 0 .. numberOfPoints) {
            ksiValues.add( a + i * step ) //( a + i * (b - a) / n )
            tiValues.add(-1.0 + i * 2.0 / numberOfPoints)
            ftiValues.add(f(tiValues.last()))
        }

        println("n = $numberOfPoints; wi.keys() = ${wi.keys}, wi[$schemeID] = ${wi[schemeID]!!}")
        for(i in 0 .. numberOfPoints) {
            result += wi[schemeID]!![i] * f(a + i * (b-a) / numberOfPoints)// f(ksiValues[i])
            println("iter = $i result = $result")
        }; result *= (b-a)
        output.a = a
        output.b = b
        output.f = Func(::f)
        output.result = result
        output.ksiValues = ksiValues
        output.tiValues = tiValues
        output.wiValues = wi[numberOfPoints]!!.toMutableList()
        output.newtonCotesMethodTracks.addAll(output.buildGUIOutputModel())

        println("result = $result, output.result = ${output.result}")

        val lagrangeOutputData: LagrangeOutputData<Double>

        lagrangeOutputData = LagrangeInterpolationMethod(tiValues,ftiValues)
        output.interpolationResult = lagrangeOutputData

        println("lagrangeOutputData.coefficientsInNumerator = ${lagrangeOutputData.coefficientsInNumerator}")
        println("lagrangeOutputData.denominators = ${lagrangeOutputData.denominators}")
        println("lagrangeOutputData.polynomials = ${lagrangeOutputData.fiPolynomials}")
        println("lagrangeOutputData.fullPolynomial = ${lagrangeOutputData.finalPolynomial}")

        println("ksiValues = $ksiValues, ksiValues.size = ${ksiValues.size}")
        println("tiValues = $tiValues, tiValues.size = ${tiValues.size}")
        println("ftiValues = $ftiValues, ftiValues.size = ${ftiValues.size}")
        println("result = $result")

        return output
    }
}
