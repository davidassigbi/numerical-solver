package utils.nonLinearEquations

import objects.Functions
import objects.InputData
import tornadofx.*

fun f (x: Double) : Double = Functions.f(x)

fun scanInterval(
        lowerBound: Double = InputData.lowerBound,
        upperBound: Double = InputData.upperBound,
        scanStep: Double = InputData.intervalScanStep,
        exactSolutions: MutableList<Double> = InputData.exactSolutions,
        intervalsWithOneSolution: MutableList<Double> = InputData.intervalsWithOneSolution) {

    exactSolutions.clear()
    intervalsWithOneSolution.clear()

    var i = lowerBound + scanStep

    //
    checkContinuousFunction()

    checkEqualAndAddToList(lowerBound)
    checkEqualAndAddToList(upperBound)

    while (i < upperBound) {
        val times = f(i) * f(i + scanStep)
        if(times < .0){
            intervalsWithOneSolution.add(i)
        }
        else if( times == .0){
            exactSolutions.add(i)
            i += scanStep
        }
        i += scanStep
    }
    println("Exacts solutions => $exactSolutions")
    println("Intervals containing solutions => $intervalsWithOneSolution")
    return
}

fun checkEqualAndAddToList(value : Double, destList: MutableList<Double> = InputData.exactSolutions) {
    if(f(value) == .0 ) destList.add(value)
}

fun checkContinuousFunction() {
    val result = mutableListOf<Double>()
    var lb = InputData.lowerBound
    var ub = InputData.upperBound

    while (lb < ub) {
        result.add(Functions.f(lb))
        lb += InputData.intervalScanStep
    }
    var fr = result[0]
    if(result.all { it == fr }){
        information(header = "Message d'information", content = "La fonction donnée est continue sur l'ensemble de définition")
    }
}
