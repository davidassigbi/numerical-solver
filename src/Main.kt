import abstractClasses.AbstractResolutionMethod
import classes.Chapter
import classes.LinearSystemOfEquations
import classes.ResolutionMethod
import extensions.number.end
import extensions.ranges.step
import navigation.chooseChapter
import navigation.chooseMethod
import objects.*
import org.mariuszgromada.math.mxparser.Argument
import org.mariuszgromada.math.mxparser.Expression
import org.mariuszgromada.math.mxparser.Function
import org.mariuszgromada.math.mxparser.mXparser
import tornadofx.*
import ui.AlgoNumApp
import utils.AllMethods
import utils.THREE_TABS
import utils.digitalIntegration.NewtonCotesMethod
import utils.getSingleBoundedNumberInput
import utils.getSingleNumberInput
import utils.linearInterpolation.leastSquares.LeastSquaresPolynomialMethod
import utils.linearInterpolation.polynomialApproximation.LagrangeInterpolationMethod
import utils.linearInterpolation.polynomialApproximation.NewtonInterpolationMethod
import utils.linearSystemOfEquations.GaussJordanMethod
import utils.nonLinearEquations.*

fun waitForInput() {
    getSingleNumberInput<Int>("","")
}

/* fun testScriptEngineManager() {
    val engine = KotlinJsr223JvmLocalScriptEngineFactory().getScriptEngine()
    // KotlinJsr223JvmScriptEngineFactory
    //val engine = ScriptEngineManager().getEngineByExtension("kts")!! // as KotlinJsr223JvmLocalScriptEngine
    // val doubleResult: Double = (engine.eval("1+1") as Int).toDouble()
    println("result = $doubleResult")
} */


fun calculateFunctionValuesTo(lowerBound: Double, step: Double, upperBound: Double , strFunction: String = "", dest: MutableList<Double>? = null): MutableList<Double> {
    val function = Function("f",strFunction,"x")
    val result: MutableList<Double> = dest ?: mutableListOf()
    result.clear()
    var x = lowerBound

    while(x < upperBound) {
        result.add(function.calculate(x))
        x += step
    }

    return result
}

fun calculateFunctionValuesTo(xValues: List<Number>, strFunction: String = "", dest: MutableList<Double>? = null): MutableList<Double> {
    val function = Function("f",strFunction,"x")
    val result: MutableList<Double> = dest ?: mutableListOf()
    result.clear()

    for(x in xValues)
        result.add(function.calculate(x.toDouble()))

    return result
}

fun calculateFunctionValues(xValues: List<Number>, strFunction: String = ""): MutableList<Double> = calculateFunctionValuesTo(xValues, strFunction, null)

fun calculateFunctionValuesTo(lowerBound: Number, step: Number, upperBound: Number, strFunction: String = "", dest: MutableList<Double>? = null): MutableList<Double> =
        calculateFunctionValuesTo(lowerBound.toDouble(), step.toDouble(), upperBound.toDouble(),strFunction, dest)

fun calculateFunctionValues(lowerBound: Number, step: Number, upperBound: Number, strFunction: String = ""): MutableList<Double> =
        calculateFunctionValuesTo(lowerBound, step, upperBound, strFunction)

fun main(args: Array<String>) {
    Chapter.getAll().forEach { it.methodsList.add(AllMethods) }

    // var exp = Expression("1*655+sin(5)")
    // var function = Function()
//    mXparser.
    // println(exp)

    launch<AlgoNumApp>()

    waitForInput()
    // testScriptEngineManager()

    println("Hello World!")

    NewtonCotesMethod(-2.0,5.0,NewtonCotesMethod.numberOfPoints(NewtonCotesMethod.SCHEMES.WEDDLE_HARDY_RULE.id))
    println(system.fromString("linearSystem[2,-1,1,5 end -1,-1,-1,-2 end 3,3,1,2]").toLinearSystemOfEquations())

    // lancement du GUI de l'application
    //launch<AlgoNumApp>(args)

    ResolutionMethod.fillRanks()

    NewtonInterpolationMethod()
    LagrangeInterpolationMethod()
    LeastSquaresPolynomialMethod(degree = 2)
    getSingleBoundedNumberInput<Int>()

    val s =
            system[2, -1 , 1 , 5 end
            -1,-1,-1,-2 end
            3, 3, 1, 2]
    s.nativeMatrix.setUtils(.0,1.0,.0)
    GaussJordanMethod(system = s)

    getSingleBoundedNumberInput<Int>()


    var matrix =
            matrix[1,2,3.0 end
                    5,4,5 end
                    45.0,3,6]

    matrix.toString()
    s.toLinearSystemOfEquations()

    Charsets.UTF_8

    for(i in 168..219)
        println("const val SPECIAL_CHAR$i = ${i.toChar().toString()}")
    var f = Char.MAX_HIGH_SURROGATE

    getSingleBoundedNumberInput<Double>()

    var mtx = LinearSystemOfEquations(5, 45.0)

    mtx[1 ,1] = -12
//    mtx[1][1] = 12

println(mtx)
println("\n"+mtx.toLinearSystemOfEquations())


    //mtx[1] = arrayOf(0) as Array<Double>

    readLine()?.getSingleBoundedNumberInput<Int>()
    println("${THREE_TABS + THREE_TABS + THREE_TABS}ALGORITHME NUMERIQUE").also { println() }

    var chosenChapterRank = chooseChapter()

    chosenChapterRank ?:

    println("Vous avez choisi la methode : ${chooseMethod(chosenChapterRank!!)}")

    while (false){
        println("Valeur saisie = ${getSingleNumberInput<Int>()}")
    }

    getSingleNumberInput<Int>()

    scanInterval()

    NewtonRaphsonMethod()
    println("Newton-Raphson solution to this : ${Functions.f} = 0 is = ${NewtonRaphsonMethod.outputData.solutions}")
    println("Newton raphson method errors : " + NewtonRaphsonMethod.outputData.errorMessage)

    SecantMethod()
    println("Secant method solution to this : ${Functions.f} = 0 is = ${(BasicOutputData[ResolutionMethod.SECANT] as NLEOutputData<Double>).solutions}")
    println("Secant method errors : " + BasicOutputData[ResolutionMethod.SECANT].errorMessage)

    LinearInterpolationMethod()
    println("Linear interpolation method solution to this : ${Functions.f} = 0 is = ${(BasicOutputData[ResolutionMethod.LINEAR_INTERPOLATION_IN_NON_LINEAR_EQUATIONS] as NLEOutputData<Double>).solutions}")
    println("Linear innterpolation method errors : " + BasicOutputData[ResolutionMethod.LINEAR_INTERPOLATION_IN_NON_LINEAR_EQUATIONS].errorMessage)

    FixedPointMethod()
    println("Fixed point method solution to this : ${Functions.f} = 0 is = ${(BasicOutputData[ResolutionMethod.FIXED_POINT] as NLEOutputData<Double>).solutions}")
//    println("Fixed point method errors : " + SimpleOutputDataResolutionMethod.[fixedPointMethod.ErrorMessage])

//    InputData.startPoint = InputData.exactSolutions[0] + InputData.intervalScanStep
//    getSingleNumberInput()


//    println("Hello Kotlin !!!")
//    var numberOfArgs = 0
//    print("Enter the number of values you want to enter further : ")
//    numberOfArgs = getSingleIntegerInput()
//    println(readDouble(numberOfArgs).toList())
//    InputData.startPoint = 0.0
//    InputData.maxNumberOfIterations = 100

    DichotomyMethod()
    println("Dichotomy solution to this : ${Functions.f} = 0 is = ${(BasicOutputData[ResolutionMethod.DICHOTOMY] as NLEOutputData<Double>).solutions}")
    println("valeur saisie  = ${getSingleNumberInput<Int>()}")
    getSingleNumberInput<Int>()

    println("value is = ${FixedPointMethod()}")

    getSingleNumberInput<Int>()
    scanInterval()
    val errorMessage = "Votre saisie contient des lettres : veuillez refaire la saisie de vos nombres"
    val displayMessage = "Entrez des nombres : "
    while (true){
        val numberInput = getSingleNumberInput<Int>(displayMessage,errorMessage)
        println("Valeur lue : ${numberInput}")
    }

    /*
    println(readLine()!!.toDouble())
    while (true){
        println("Is input correct : ${readLine()!!.isNumberInputValid()}")
    }

    */

}

