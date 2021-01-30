package abstractClasses

import classes.LinearSystemOfEquations
import classes.ParametersSource
import utils.linearSystemOfEquations.AscentMethod
import utils.linearSystemOfEquations.RollbackMethod

abstract class DecompositionStrategy<T: Number> : AbstractLinearSystemOfEquationsResolutionMethod<T>() {

    abstract fun getDualSystem(system: LinearSystemOfEquations<T>): Pair<LinearSystemOfEquations<T>, LinearSystemOfEquations<T>>?

    override fun solve(system: LinearSystemOfEquations<T>, source: ParametersSource, transparent: Boolean) {
        fun reportMethodError(error: String) {
            // if(!transparent)
                this.reportMethodError(error)
        }
        fun addMethodSolution() {
            if(!transparent) this.addMethodSolution(system, source)
        }
        fun printSolution() = printSolution(system,transparent)

        val dualSystem : Pair<LinearSystemOfEquations<T>, LinearSystemOfEquations<T>>?

        try {
            dualSystem = getDualSystem(system)
            if(dualSystem != null) {
                val firstSystem = dualSystem.first
                val secondSystem = dualSystem.second

                AscentMethod(system = (firstSystem as LinearSystemOfEquations<Double>), transparent = true)
                secondSystem.B = (firstSystem as LinearSystemOfEquations<T>).X.tobVector()

                RollbackMethod(system = secondSystem as LinearSystemOfEquations<Double>, transparent = true)
                system.X = (secondSystem as LinearSystemOfEquations<T>).X
                addMethodSolution()
                printSolution()
            }
        } catch (e: Exception) {
            reportMethodError(e.message!!)
        }
    }
}