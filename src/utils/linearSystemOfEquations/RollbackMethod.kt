package utils.linearSystemOfEquations

import abstractClasses.AbstractLinearSystemOfEquationsResolutionMethod
import classes.LinearSystemOfEquations
import classes.ParametersSource
import classes.ResolutionMethod

object
RollbackMethod : AbstractLinearSystemOfEquationsResolutionMethod<Double>() {

    const val ERROR_ALTERNATIVE = "Veuillez choisir une autre méthode de résolution."
    const val MATRIX_NOT_UPPER_TRIANGULAR_ERROR = "Impossible de résoudre ce système avec la méthode de retour en arrière car le système n'est pas à matrice triangulaire supéieure. " + ERROR_ALTERNATIVE
    const val ELEMENT_NULL_ON_DIAGONAL = "Impossible de résoudre ce système avec la méthode de retour en arrière car un des éléments de la digonale est nul." + ERROR_ALTERNATIVE


    override val methodID: ResolutionMethod = ResolutionMethod.ROLLBACK

    override fun solve(system: LinearSystemOfEquations<Double>, source: ParametersSource, transparent: Boolean) {
        fun reportMethodError(error: String) {
            if(!transparent) this.reportMethodError(error)
        }
        fun addMethodSolution() {
            if(!transparent) this.addMethodSolution(system, source)
        }
        fun printSolution() = printSolution(system,transparent)

        if(!system.nativeMatrix.isUpperTriangularMatrix())
            reportMethodError(MATRIX_NOT_UPPER_TRIANGULAR_ERROR)
        else {
            if(!system.nativeMatrix.hasDiagonalNonNull())
                reportMethodError(ELEMENT_NULL_ON_DIAGONAL)
            else {
                var tmpSum = .0
                try {
                    for (line in (0 until system.nativeMatrix.size ).reversed()) {
                        print("line = $line [")
                        tmpSum = .0
                        for (col in (line+1 until system.nativeMatrix.size)) {
                            print("col = $col, ")
                            tmpSum += system[line, col] * system.solutionVector[col].value
                        }
                        println("]")
                        system.solutionVector[line].value = (1 / system[line][line]) * (system.bVector[line] - tmpSum)
                        println("system[line][line] = ${system[line][line]}, system.bVector[line] = ${system.bVector[line]}")
                        println("solutionVector = ${system.solutionVector[line].value}")
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