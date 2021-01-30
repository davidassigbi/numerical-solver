package abstractClasses

import objects.Functions
import ui.others.SchemeInterface

abstract class AbstractDigitalIntegrationResolutionMethod: AbstractResolutionMethod() {
    abstract fun getSchemesAsStrings(): List<String>

    abstract fun getSchemesAsListOfSchemeInterface(): List<SchemeInterface>

    open fun f(x: Double): Double = Functions.integrationFunction(x)
}