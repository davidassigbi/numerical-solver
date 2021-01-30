package utils.linearSystemOfEquations

import abstractClasses.AbstractLinearSystemOfEquationsResolutionMethod
import classes.LinearSystemOfEquations
import classes.ParametersSource
import classes.ResolutionMethod
import classes.SolutionVector
import extensions.ranges.except
import objects.InputData

object JacobiMethod : AbstractLinearSystemOfEquationsResolutionMethod<Double>() {

    const val ERROR_ALTERNATIVE = "Veuillez choisir une autre méthode de résolution."

    override val methodID: ResolutionMethod = ResolutionMethod.JACOBI

    override fun solve(system: LinearSystemOfEquations<Double>, source: ParametersSource, transparent: Boolean) {
        fun reportMethodError(error: String) {
            // if(!transparent)
                this.reportMethodError(error)
        }
        fun addMethodSolution() {
            if(!transparent) this.addMethodSolution(system, source)
        }
        fun printSolution() = printSolution(system,transparent)

//*
        val D = system.nativeMatrix.getDiagonalMatrix()
        val E = -system.nativeMatrix.getLowerTriangularMatrix(strict = true)
        val F = -system.nativeMatrix.getUpperTriangularMatrix(strict = true)
        val xK = InputData.initialSolutionVector.copy() //SolutionVector<>(size = linearSystem.dimension)
        val xKPlusUn = system.solutionVector.copy()
// */
        var tmpSum: Double
        var currentDiff: Double
        var iter = 0

        if(system.nativeMatrix.hasDiagonalNonNull()) {
            println("La matrice D: \n${D.toStringWithLineBreaks()}")
            println("La matrice E: \n${E.toStringWithLineBreaks()}")
            println("La matrice F: \n${F.toStringWithLineBreaks()}")
            do {
                iter++
                for(i in 0 until system.dimension) {
                    tmpSum = system.nullValue
                    println("iter = $iter; Xk = ${xK.toStringWithLineBreaks(unknownsOrValuesOrBoth = SolutionVector.VALUES_ONLY, lineBreak = ",")}, Xk+1 = ${xKPlusUn.toStringWithLineBreaks(unknownsOrValuesOrBoth = SolutionVector.VALUES_ONLY, lineBreak = ", ")}")
                    for(j in 0 until system.dimension except i) {
                        tmpSum += system.A[i][j] * xK[j].value
                    }
                    if(system.A[i][i] != system.nullValue) {
                        xKPlusUn[i].value = (system.B[i] - tmpSum) / system.A[i][i]


                    } else {
                        reportMethodError("A[$i][$i] = 0, impossible d'utiliser la méthode de Jacobi pour résoudre ce système.")
                    }
                }
                currentDiff = xKPlusUn.distanceTo(xK)
                println("in JacobiMethod: iter = $iter; currentDiff = $currentDiff")
                xK.swapValuesWith(xKPlusUn)
            } while (currentDiff > InputData.precisionOrTolerance && iter < InputData.maxNumberOfIterations)

            if(iter <= InputData.maxNumberOfIterations) {
                system.solutionVector = xK
                addMethodSolution()
                printSolution()
                //
                outputData.lastIterationIndex = iter
            } else {
                if(system.nativeMatrix.isDominantDiagonalMatrix())
                    reportMethodError("Aucune solution trouvée après $iter iterations, veuillez augmenter le nombre maximal d'iterations ou choisir un meilleur vecteur solution de départ")
                else
                    reportMethodError("La matrice A n'est pas à diagonale dominante. L'algorithme de Gauss-Seidel ne garantit donc pas la convergence de la solution")
            }
        } else {
            val firstNullInndex = system.nativeMatrix.indexOfFirstNullOnDiagonal()
            reportMethodError("A[$firstNullInndex][$firstNullInndex] est nul donc le système ne peut être résolu par la méthode de Jacobi."+ ERROR_ALTERNATIVE)
        }
    }
}