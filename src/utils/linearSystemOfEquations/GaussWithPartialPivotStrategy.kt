package utils.linearSystemOfEquations

import abstractClasses.PivotStrategy
import classes.LinearSystemOfEquations
import classes.ResolutionMethod

object GaussWithPartialPivotStrategy : PivotStrategy<Double>() {

    const val ERROR_ALTERNATIVE = "Veuillez choisir une autre méthode de résolution."

    override val methodID: ResolutionMethod = ResolutionMethod.GAUSS_WITH_PARTIAL_PIVOT_STRATEGY

    override fun bestPivotNonNull(system: LinearSystemOfEquations<Double>, currentRow: Int): Boolean {
        return system.maximizePivotFromRestOfColumn(currentRow)
    }
    override fun findError(system: LinearSystemOfEquations<Double>, currentRow: Int): String {
        return if(currentRow != system.nativeMatrix.dimension - 1) "Le pivot maximal trouvé sur la colonne ${currentRow+1} est nul. La matrice est semble t'il singulière."+ ERROR_ALTERNATIVE
        else "La dernière ligne de la matrice A est nulle après les différentes permutations, la matrice est donc singulière"+ ERROR_ALTERNATIVE
    }
    override fun rowsToEliminate(system: LinearSystemOfEquations<Double>, currentRow: Int) = (currentRow+1 until system.dimension).toList()

}
/*
    open override fun solve(linearSystem: MatrixV3<Double>,source: ParametersSource, transparent: Boolean) {
        fun reportMethodError(error: String) {
            if(!transparent) this.reportMethodError(error)
        }
        fun addMethodSolution() {
            if(!transparent) this.addMethodSolution(linearSystem, source)
        }
        fun printSolution() = printSolution(linearSystem,transparent)

        for(i in 0 until linearSystem.dimension) {
            val bestPivotNotNull = linearSystem.maximizePivotFromRestOfColumn(i)
            if(bestPivotNotNull) {
                val pivot = linearSystem.nativeMatrix[i][i]
                linearSystem.divRow(i,pivot)
                for(j in i+1 until linearSystem.dimension) {
                    linearSystem.addRow(destRowIndex = j,
                            otherRowIndex = i,
                            otherRowFactor = -linearSystem.nativeMatrix[j][i])
                    linearSystem.nativeMatrix[j][i] = linearSystem.nullValue
                }
            } else {
                reportMethodError(
                        if(i != linearSystem.nativeMatrix.dimension - 1) "Le pivot maximal trouvé sur la colonne ${i+1} est nul. La matrice est semble t'il singulière."+ ERROR_ALTERNATIVE
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