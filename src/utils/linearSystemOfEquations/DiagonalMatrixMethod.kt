package utils.linearSystemOfEquations

import abstractClasses.AbstractLinearSystemOfEquationsResolutionMethod
import classes.LinearSystemOfEquations
import classes.ParametersSource
import classes.ResolutionMethod

object DiagonalMatrixMethod : AbstractLinearSystemOfEquationsResolutionMethod<Double>() {

    const val ERROR_ALTERNATIVE = "Veuillez choisir une autre méthode de résolution."
    const val MATRIX_NOT_DIAGONAL = "Impossible de résoudre ce système avec la méthode de résolution des systèmes d'équations à matrice diagonale car le système n'est pas à matrice diagonale. " + ERROR_ALTERNATIVE
    const val ELEMENT_NULL_ON_DIAGONAL = "Impossible de résoudre ce système avec cette méthode car un des éléments de la digonale est nul." + ERROR_ALTERNATIVE

    override val methodID: ResolutionMethod = ResolutionMethod.DIAGONAL_MATRIX_SYSTEM_SOLVER

    override fun solve(system: LinearSystemOfEquations<Double>, source: ParametersSource, transparent: Boolean) {
        fun reportMethodError(error: String) {
            if(!transparent) this.reportMethodError(error)
        }
        fun addMethodSolution() {
            if(!transparent) this.addMethodSolution(system, source)
        }
        fun printSolution() = printSolution(system,transparent)

        if(!system.nativeMatrix.isDiagonalMatrix())
            reportMethodError(MATRIX_NOT_DIAGONAL)
        else {
            if(!system.nativeMatrix.hasDiagonalNonNull())
                reportMethodError(ELEMENT_NULL_ON_DIAGONAL)
            else {
                try {
                    for (line in system.dimension - 1 downTo 0)
                        system.solutionVector[line].value = system.bVector[line] / system.nativeMatrix[line][line]

                    // affichage de la solution
                    addMethodSolution()
                    printSolution()
                }catch (e: Exception) {
                    println(e)
                    reportMethodError(e.message!!)
                }
            }
        }
    }
}