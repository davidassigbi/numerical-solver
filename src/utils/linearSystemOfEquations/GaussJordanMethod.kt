package utils.linearSystemOfEquations

import abstractClasses.PivotStrategy
import classes.LinearSystemOfEquations
import classes.ResolutionMethod

object GaussJordanMethod : PivotStrategy<Double>() {

    const val ERROR_ALTERNATIVE = "Veuillez choisir une autre méthode de résolution."

    override val methodID: ResolutionMethod = ResolutionMethod.GAUSS_JORDAN

    fun getSchemesAsStrings(): List<String> {
        return SCHEMES.values().map { it.toString() }
    }

    enum class SCHEMES(val schemeName: String = "",val id: Int = 0){
        NO_PIVOT_STRATEGY("Pas de stratégie de pivot", id = 0),
        PARTIAL_PIVOT_STRATEGY("Stratégie du pivot partiel", id = 1),
        TOTAL_PIVOT_STRATEGY("Stratégie de pivot total", id = 2)
        ;
        override fun toString() = this.schemeName
    }


    var pivotStrategy: Int = 2
    override fun bestPivotNonNull(system: LinearSystemOfEquations<Double>, currentRow: Int): Boolean {
        return when(pivotStrategy) {
            0 -> GaussWithoutPivotStrategy.bestPivotNonNull(system,currentRow)
            1 -> GaussWithPartialPivotStrategy.bestPivotNonNull(system,currentRow)
            else -> GaussWithTotalPivotStrategy.bestPivotNonNull(system,currentRow)
        }
    }
    override fun findError(system: LinearSystemOfEquations<Double>, currentRow: Int): String {
        return when(pivotStrategy) {
            0 -> GaussWithoutPivotStrategy.findError(system, currentRow)
            1 -> GaussWithPartialPivotStrategy.findError(system, currentRow)
            else -> GaussWithTotalPivotStrategy.findError(system, currentRow)
        }
    }
    override fun rowsToEliminate(system: LinearSystemOfEquations<Double>, currentRow: Int) = (0 until system.dimension).toMutableList().apply { remove(currentRow) }
//        return (currentRow+1 until linearSystem.dimension).toMutableList().remove(currentRow+1)


    override fun doSomethingOnPivotRow(system: LinearSystemOfEquations<Double>, i: Int, pivot: Double) {
        system.divRow(i,pivot)
    }

    override fun otherRowFactor(system: LinearSystemOfEquations<Double>, i: Int, j: Int, pivot: Double): Double {
        return -system.nativeMatrix[j][i]
        // return super.otherRowFactor(linearSystem, i, j, pivot)
    }
}
/*
        return if(currentRow != linearSystem.nativeMatrix.dimension - 1) "La sous matrice de A ayant pour premier élément A[${currentRow+1}][${currentRow+1}] est nulle après les permutations. La matrice est semble t'il singulière."+ ERROR_ALTERNATIVE
        else "La dernière ligne de la matrice A est nulle après les différentes permutations, la matrice est donc singulière"+ ERROR_ALTERNATIVE
*/

/*
    override fun solve(linearSystem: MatrixV3<Double>,source: ParametersSource, transparent: Boolean) {
        fun reportMethodError(error: String) {
            if(!transparent) this.reportMethodError(error)
        }
        fun addMethodSolution() {
            if(!transparent) this.addMethodSolution(linearSystem, source)
        }
        fun printSolution() = printSolution(linearSystem,transparent)

        for(i in 0 until linearSystem.dimension) {
            val bestPivotNotNull = linearSystem.maximizePivotFromRestOfMatrix(i)
            if(bestPivotNotNull) {
                val pivot = linearSystem.nativeMatrix[i][i]
                linearSystem.divRow(i,pivot)
                for(j in i+1 until linearSystem.dimension) {
                    linearSystem.addRow(destRowIndex = j,
                            otherRowIndex = i,
                            otherRowFactor = - linearSystem.nativeMatrix[j][i])
                    linearSystem.nativeMatrix[j][i] = linearSystem.nullValue
                }
            } else {
                reportMethodError(
                        if(i != linearSystem.nativeMatrix.dimension - 1) "La sous matrice de A ayant pour premier élément A[${i+1}][${i+1}] est nulle après les permutations. La matrice est semble t'il singulière."+ ERROR_ALTERNATIVE
                        else "La dernière ligne de la matrice A est nulle après les différentes permutations, la matrice est donc singulière"+ ERROR_ALTERNATIVE)
                return
            }
        }
        // après ce qui précède si tout s'est passé comme voulu, le système devrait être à matrice triangualire supérieure donc on procède à une résolution par la méthode de retour en arrière
        RollbackMethod(linearSystem = linearSystem,transparent = true)
        addMethodSolution()
        printSolution()
    }
*/


/*
    override val bestPivotNonNull = when(pivotStrategy) {
        0 -> GaussWithoutPivotStrategy::bestPivotNonNull
        1 -> GaussWithPartialPivotStrategy::bestPivotNonNull
        else -> GaussWithTotalPivotStrategy::bestPivotNonNull
    }
*/
