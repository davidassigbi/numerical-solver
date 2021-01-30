package utils.linearSystemOfEquations

import abstractClasses.DecompositionStrategy
import classes.LinearSystemOfEquations
import classes.ResolutionMethod

object CroutMethod : DecompositionStrategy<Double>() {
    override val methodID: ResolutionMethod = ResolutionMethod.CROUT

    override fun getDualSystem(system: LinearSystemOfEquations<Double>): Pair<LinearSystemOfEquations<Double>,LinearSystemOfEquations<Double>>? = system.getCroutLRDecomposition()
}
/*
    override fun solve(linearSystem: MatrixV3<Double>,source: ParametersSource, transparent: Boolean) {
        fun reportMethodError(error: String) {
            if(!transparent) this.reportMethodError(error)
        }
        fun addMethodSolution() {
            if(!transparent) this.addMethodSolution(linearSystem, source)
        }
        fun printSolution() = printSolution(linearSystem,transparent)

        val LR : Pair<MatrixV3<Double>,MatrixV3<Double>>?

        try {
            LR = linearSystem.getCroutLRDecomposition()
            if(LR != null) {
                val L = LR.first
                val R = LR.second

                AscentMethod(linearSystem = L,transparent = true)
                R.B = L.X.tobVector()

                RollbackMethod(linearSystem = R,transparent = true)
                linearSystem.X = R.X
                addMethodSolution()
                printSolution()
            }
        } catch (e: Exception) {
            reportMethodError(e.message!!)
        }
    }
*/
//    const val ERROR_ALTERNATIVE = "Veuillez choisir une autre méthode de résolution."
//    const val MATRIX_NOT_UPPER_TRIANGULAR_ERROR = "Impossible de résoudre ce système avec la méthode de retour en arrière car le système n'est pas à matrice triangulaire supéieure. " + ERROR_ALTERNATIVE
//    const val ELEMENT_NULL_ON_DIAGONAL = "Impossible de résoudre ce système avec la méthode de retour en arrière car un des éléments de la digonale est nul." + ERROR_ALTERNATIVE
