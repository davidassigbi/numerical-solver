package abstractClasses

import classes.ParametersSource
import objects.Functions
import objects.NLEOutputData

abstract class AbstractNonLinearEquationsResolutionMethod<T: Number> : AbstractResolutionMethod() {
    override val outputData: NLEOutputData<T> = NLEOutputData<T>()

    open fun f(x: Double) = Functions.f(x)

    open fun addMethodSolution(solution : T, source : ParametersSource = ParametersSource.INPUT_DATA, lowerBound : Double = .0) {
        outputData.solvedIt = true
        outputData.errorMessage = ""
        when (source) {
            ParametersSource.INPUT_DATA -> outputData.solutions.add(solution)
            ParametersSource.SCANNED_INTERVAL -> outputData.solutionsForScannedInterval.add(lowerBound to solution)
        }
    }
}
