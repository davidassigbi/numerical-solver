package utils.linearSystemOfEquations

import abstractClasses.AbstractLinearSystemOfEquationsResolutionMethod
import classes.LinearSystemOfEquations
import classes.ParametersSource
import classes.ResolutionMethod
import classes.SolutionVector
import extensions.ranges.except
import objects.InputData
import kotlin.math.pow


object GaussSeidelMethod : AbstractLinearSystemOfEquationsResolutionMethod<Double>() {

    const val ERROR_ALTERNATIVE = "Veuillez choisir une autre méthode de résolution."

    override val methodID: ResolutionMethod = ResolutionMethod.GAUSS_SEIDEL

    override fun solve(system: LinearSystemOfEquations<Double>, source: ParametersSource, transparent: Boolean) {
        fun reportMethodError(error: String) {
            if(!transparent) this.reportMethodError(error)
        }
        fun addMethodSolution() {
            if(!transparent) this.addMethodSolution(system, source)
        }
        fun printSolution() = printSolution(system,transparent)

/*
        val D = linearSystem.nativeMatrix.getDiagonalMatrix()
        val E = linearSystem.nativeMatrix.getLowerTriangularMatrix(strict = true)
        val F = linearSystem.nativeMatrix.getUpperTriangularMatrix(strict = true)
        var xK = linearSystem.solutionVector.copy().apply { forEach { it.value = linearSystem.nullValue } } //SolutionVector<>(size = linearSystem.dimension)
        var xKPlusUn = linearSystem.solutionVector.copy()
*/
        var tmpSum: Double
        var tmpSol: Double
        var currentDiff = .0
        var iter = 0

        if(system.nativeMatrix.hasDiagonalNonNull()) {
            do {
                iter++
                for(i in 0 until system.dimension) {
                    tmpSum = system.nullValue
                    tmpSol = system.X[i].value

                    println("iter = $iter; Xk = ${system.solutionVector.toStringWithLineBreaks(unknownsOrValuesOrBoth = SolutionVector.VALUES_ONLY, lineBreak = ",")} ")

                    for(j in 0 until system.dimension except i) {
                        tmpSum += system.A[i][j] * system.X[j].value
                    }
                    if(system.A[i][i] != system.nullValue) {
                        system.X[i].value = (system.B[i] - tmpSum) / system.A[i][i]
                        currentDiff += (system.X[i].value - tmpSol).pow(2)
                    }
                }
                currentDiff = currentDiff.pow(0.5)
                println("in GaussSeidel: iter = $iter; currentDiff = $currentDiff")
            } while (currentDiff > InputData.precisionOrTolerance && iter < InputData.maxNumberOfIterations)

            if(iter <= InputData.maxNumberOfIterations) {
                addMethodSolution()
                printSolution()
            } else {
                if(system.nativeMatrix.isDominantDiagonalMatrix())
                    reportMethodError("Aucune solution trouvée après $iter iterations, veuillez augmenter le nombre maximal d'iterations ou choisir un meilleur vecteur solution de départ")
                else
                    reportMethodError("La matrice A n'est pas à diagonale dominante. L'algorithme de Gauss-Seidel ne garantit donc pas la convergence.")
            }
        } else {
            val firstNullInndex = system.nativeMatrix.indexOfFirstNullOnDiagonal() + 1
            reportMethodError("A[$firstNullInndex][$firstNullInndex] est nul donc le système ne peut être résolu par l'algorithme de Gauss-Seidel."+ ERROR_ALTERNATIVE)
        }
    }
}