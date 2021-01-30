package utils.digitalIntegration

import abstractClasses.AbstractDigitalIntegrationResolutionMethod
import classes.Func
import classes.ResolutionMethod
import objects.DIOutputData
import ui.others.SchemeInterface

object SimplistIntegrationMethod: AbstractDigitalIntegrationResolutionMethod() {

    override val methodID: ResolutionMethod = ResolutionMethod.SIMPLIST_INTEGRATION_METHOD

    override val outputData: DIOutputData<Double> = DIOutputData()

    override fun getSchemesAsStrings(): List<String> {
        return SCHEMES.values().map { it.toString() }
    }

    override fun getSchemesAsListOfSchemeInterface(): List<SchemeInterface> {
        return SCHEMES.values().asList()
    }

    enum class SCHEMES(override val schemeName: String = "",override val id: Int = 0): SchemeInterface {
        LEFT_RECTANGLES("Méthode des rectangles à gauche (ordre 0)",id = 1),
        RIGHT_RECTANGLES("Méthode des rectangles à droite (ordre 0)", id = 2),
        MIDDLE_POINT("Méthode du point milieu (ordre 1)", id = 3),
        LINEAR_INTERPOLATION("Méthode d'interpolation linéaire", id = 4),
        ALL_SCHEMES(ui.others.ALL_SCHEMES)
        ;
        constructor(other: SchemeInterface): this(other.schemeName, other.id)
        override fun toString(): String = this.schemeName
        companion object {
            fun get(str: String) = values().first { println("str: $str, it: $it");it.schemeName == str } // ?: LINEAR_INTERPOLATION
        }
    }

    const val LEFT_RECTANGLES = 1
    const val RIGHT_RECTANGLES = 2
    const val MIDDLE_POINT = 3
    const val LINEAR_INTERPOLATION = 4


    var lastUsedScheme = 0

    operator fun invoke(a: Double, b: Double, scheme: Int = SCHEMES.MIDDLE_POINT.id): DIOutputData<Double> {
        lastUsedScheme = scheme
        val output = DIOutputData<Double>()
        output.a = a
        output.b = b
        output.f = Func(::f)
        output.result = .0

        val fxi = when(scheme) {
            SCHEMES.LEFT_RECTANGLES.id -> f(a)
            SCHEMES.RIGHT_RECTANGLES.id -> f(b)
            SCHEMES.MIDDLE_POINT.id -> f(x = (a + b)/2)
            SCHEMES.LINEAR_INTERPOLATION.id -> (f(a) + f(b)) / 2
            else -> f(x = (a + b) / 2)
        }

        output.result = (b - a) * fxi

        return output
    }
}