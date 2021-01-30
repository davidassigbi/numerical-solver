package utils.linearSystemOfEquations

import abstractClasses.AbstractLinearSystemOfEquationsResolutionMethod
import classes.LinearSystemOfEquations
import classes.ParametersSource
import classes.ResolutionMethod

object AscentMethod : AbstractLinearSystemOfEquationsResolutionMethod<Double>() {

    const val ERROR_ALTERNATIVE = "Veuillez choisir une autre méthode de résolution."
    const val MATRIX_NOT_LOWER_TRIANGULAR_ERROR = "Impossible de résoudre ce système avec la méthode de remontée car le système n'est pas à matrice triangulaire inférieure. " + ERROR_ALTERNATIVE
    const val ELEMENT_NULL_ON_DIAGONAL = "Impossible de résoudre ce système avec la méthode de remontée car un des éléments de la digonale est nul." + ERROR_ALTERNATIVE


    override val methodID: ResolutionMethod = ResolutionMethod.ASCENT

    override fun solve(system: LinearSystemOfEquations<Double>, source: ParametersSource, transparent: Boolean) {
        fun reportMethodError(error: String) {
            if(!transparent) this.reportMethodError(error)
        }
        fun addMethodSolution() {
            if(!transparent) this.addMethodSolution(system, source)
        }
        fun printSolution() = printSolution(system,transparent)

        if(!system.nativeMatrix.isLowerTriangularMatrix())
            reportMethodError(MATRIX_NOT_LOWER_TRIANGULAR_ERROR)
        else {
            if(!system.nativeMatrix.hasDiagonalNonNull())
                reportMethodError(ELEMENT_NULL_ON_DIAGONAL)
            else {
                system.nativeMatrix.changeMinusNullByNull()
                var tmpSum = .0
                try {
                    for (line in 0 until system.nativeMatrix.size) {
                        for (col in 0 until line) {
                            tmpSum += system[line, col] * system.solutionVector[line].value
                        }
                        system.solutionVector[line].value = (system.bVector[line] - tmpSum) / system[line, line]
                    }

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