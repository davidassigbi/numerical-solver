package abstractClasses

import classes.LinearSystemOfEquations
import classes.ParametersSource
import utils.linearSystemOfEquations.RollbackMethod

abstract class PivotStrategy<T: Number> : AbstractLinearSystemOfEquationsResolutionMethod<T>() {
    abstract fun bestPivotNonNull(system: LinearSystemOfEquations<T>, currentRow: Int): Boolean

    abstract fun findError(system: LinearSystemOfEquations<T>, currentRow: Int): String

    abstract fun rowsToEliminate(system: LinearSystemOfEquations<T>, currentRow: Int): List<Int>

    // abstract
    open fun doSomethingOnPivotRow(system: LinearSystemOfEquations<T>, i: Int, pivot: T) {
        // linearSystem.divRow(i,pivot)
    }

    open fun otherRowFactor(system: LinearSystemOfEquations<T>, i: Int, j: Int, pivot: T): T { // todoParam: MatrixV3<T>.(i: Int, j: Int, pivot: T) -> T
        return -(system.nativeMatrix[j][i].toDouble()/pivot.toDouble()) as T
    }

    override fun solve(system: LinearSystemOfEquations<T>, source: ParametersSource, transparent: Boolean) {
        fun reportMethodError(error: String) {
            if(!transparent) this.reportMethodError(error)
        }
        fun addMethodSolution() {
            if(!transparent) this.addMethodSolution(system, source)
        }
        fun printSolution() = printSolution(system,transparent)

        for(i in 0 until system.dimension) {
//            val bestPivotNotNull = linearSystem.maximizePivotFromRestOfMatrix(i)
            if(bestPivotNonNull(system,i)) {
                val pivot = system.nativeMatrix[i][i]
                doSomethingOnPivotRow(system, i , pivot)
                // linearSystem.divRow(i,pivot)
                for(j in rowsToEliminate(system,i)) {
                    system.addRow(
                            destRowIndex = j,
                            otherRowIndex = i,
                            otherRowFactor = otherRowFactor(system,i = i, j = j, pivot  = pivot)) // -(linearSystem.nativeMatrix[j][i].toDouble()/pivot.toDouble()) as T)
                    system.nativeMatrix[j][i] = system.nullValue
                }
            } else {
                reportMethodError(findError(system,i))
                return
            }
        }
        // après ce qui précède si tout s'est passé comme voulu, le système devrait être à matrice triangualire supérieure donc on procède à une résolution par la méthode de retour en arrière
        RollbackMethod(system = system as LinearSystemOfEquations<Double>, transparent = true)
        addMethodSolution()
        printSolution()
    }

}