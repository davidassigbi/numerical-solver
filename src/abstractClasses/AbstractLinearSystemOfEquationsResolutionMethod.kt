package abstractClasses

import classes.LinearSystemOfEquations
import classes.ParametersSource
import objects.InputData
import objects.LSEOutputData

abstract class AbstractLinearSystemOfEquationsResolutionMethod <T: Number> : AbstractResolutionMethod() {

    override val outputData: LSEOutputData<T> = LSEOutputData()

    open fun addMethodSolution(system: LinearSystemOfEquations<T>, source: ParametersSource = ParametersSource.INPUT_DATA) {
        outputData.solvedIt = true
        outputData.errorMessage = ""
        system.methodsWithSolutions.add(methodID to system.solutionVector.copy())
        when(source) {
            ParametersSource.INPUT_DATA -> {outputData.solutionVector = system.solutionVector.copy()}
            ParametersSource.MEMORIZED_SYSTEMS -> {outputData.solutionsForMemorizedSystems.add(system to system.solutionVector.copy())}
        }
    }

    abstract fun solve(system: LinearSystemOfEquations<T>, source: ParametersSource = ParametersSource.INPUT_DATA, transparent: Boolean = false)

    open fun printSolution(system: LinearSystemOfEquations<T>, transparent: Boolean = false) {
        if(!transparent) {
            println("Après l'application de la méthode : \"${methodID.methodName}\" le système devient :")
            print(system.toLinearSystemOfEquations())
            println("Le vecteur solution est donc:")
            print(system.detailedSolutionVector)
        }
    }


    open operator fun invoke(useMemorizedSystem: Boolean = false, system: LinearSystemOfEquations<Double>? = null, transparent: Boolean = false) {
        when {
            !useMemorizedSystem && system == null -> this.solve(InputData.linearSystem as LinearSystemOfEquations<T>, ParametersSource.INPUT_DATA, transparent)
            !useMemorizedSystem && system != null -> solve(system as LinearSystemOfEquations<T>, ParametersSource.USER_INPUT, transparent)
            else -> {
                InputData.memorizedSystems.forEach { s: LinearSystemOfEquations<Double> ->
                    solve(s as LinearSystemOfEquations<T>, ParametersSource.MEMORIZED_SYSTEMS, transparent)
                }
            }
        }
    }
}