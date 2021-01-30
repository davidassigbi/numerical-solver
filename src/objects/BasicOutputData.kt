package objects

import classes.Func
import classes.LinearSystemOfEquations
import classes.ResolutionMethod
import classes.SolutionVector
import extensions.pair.TnYn
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import tornadofx.*
import kotlin.math.abs

open class BasicOutputData(var solvedIt: Boolean = false, var errorMessage: String = "", var rank: Int = 0) {
    init {
        //alls.add(this)
        if(rank == 0)
            this.rank = alls.size
    }
    companion object OutputData {
        var alls = mutableListOf<BasicOutputData>()

        var fixedPointMethodErrorMessage = ""
        var fixedPointMethodSolutions = mutableSetOf<Double>()

        var dichotomyMethodSolutions = mutableSetOf<Double>()

        var newtonRaphsonMethodErrorMessage = ""
        var newtonRaphsonMethodSolutions = mutableSetOf<Double>()

        var secantMethodErrorMessage = ""
        var secantMethodSolutions = mutableSetOf<Double>()

        var linearInterpolationMethodErrorMessage = ""
        var linearInterpolationMethodSolutions = mutableSetOf<Double>()

        var rollbackMethodSolvedIt = false
        var rollbackMethodSolutionVector = SolutionVector<Double>()
        var rollbackMethodSolutionsForMemorized = mutableSetOf<LinearSystemOfEquations<Double>>()

        operator fun get(index: Int): BasicOutputData = alls.find { it.rank == index }!!

        operator fun get(mtd: ResolutionMethod) = when(mtd.chapter) {
            2 -> get(mtd.rank) as NLEOutputData<Double>
            3 -> get(mtd.rank) as LSEOutputData<Double>
            else -> get(mtd.rank) as LSEOutputData<Double>
        }
    }
}

// équations non-linéaires
open class NLEOutputData<T: Number>: BasicOutputData() {
    var solutions: MutableSet<T> = mutableSetOf()
    var solutionsForScannedInterval: MutableSet<Pair<Double,T>> = mutableSetOf()

    val dichotomyMethodTracks: MutableList<GUIOutputModelForDichotomyMethod<T>> = mutableListOf()
    val linearInterpolationMethodTracks: MutableList<GUIOutputModelForDichotomyMethod<T>> = mutableListOf()
    val fixedPointMethodTracks: MutableList<GUIOutputModelForFixedPointMethod<T>> = mutableListOf()
    val newtonRaphsonMethodTracks: MutableList<GUIOutputModelForNewtonRaphsonMethod<T>> = mutableListOf()
    val secantMethodTracks: MutableList<GUIOutputModelForSecantMethod<T>> = mutableListOf()
}
class GUIOutputModelForDichotomyMethod<T: Number>(val index: Int, val a: T, val fA: T, val b: T, val fB: T, val middle: T, val fMiddle: T, val absoluteAMoinsB: T = abs(a.toDouble() - b.toDouble()) as T )
class GUIOutputModelForFixedPointMethod<T: Number>(val index: Int, val x: T, val fx: T, val absoluteFxMoinsX: T)
class GUIOutputModelForNewtonRaphsonMethod<T: Number>(val index: Int, val xn: T, val xnPlus1: T, val absoluteXnMoinsXnPlus1: T, val dfXnPlus1: T)
class GUIOutputModelForSecantMethod<T: Number>(val index: Int, val xnMoins1: T, val xn: T, val xnPlus1: T, val absoluteXnMoinsXnPlus1: T)

// système d'équations linéaires
open class LSEOutputData<T: Number>: BasicOutputData() {
    var solutionVector: SolutionVector<T> = SolutionVector()
    var solutionsForMemorizedSystems: MutableSet<Pair<LinearSystemOfEquations<T>, SolutionVector<T>> > = mutableSetOf()

    var lastIterationIndex = 0
}


// équations différentielles
open class DEOutputData<T: Number>: BasicOutputData(){
    var solutions: MutableList<TnYn<T,T>> = mutableListOf()
    var tnValues: MutableList<T> = mutableListOf()
    var ynValues: MutableList<T> = mutableListOf()
    val methodTracks:  MutableList<GUIOutputModelForDifferentialEquations<T>> = mutableListOf()
}
class GUIOutputModelForDifferentialEquations<T: Number>(val index: Int, val tn: T, val yn: T)
fun <T: Number> DEOutputData<T>.buildMethodTracksAndSolutions() {
    methodTracks.clear()
    solutions.clear()
    for(i in 0 until tnValues.size) {
        solutions.add(Pair(tnValues[i],ynValues[i]))
        methodTracks.add(GUIOutputModelForDifferentialEquations(i,tnValues[i],ynValues[i]))
    }
}



// intégration numérique
open class DIOutputData<T: Number>: BasicOutputData() {
    lateinit var a: T
    lateinit var b: T
    lateinit var result: T
    lateinit var f: Func

    val newtonCotesMethodTracks: MutableList<GUIOutputModelForNewtonCotesMethod<T>> = mutableListOf()
}
open class NewtonCotesOutputData<T: Number>: DIOutputData<T>() {
    lateinit var interpolationResult: LagrangeOutputData<T>
    lateinit var ksiValues: MutableList<T>
    lateinit var tiValues: MutableList<T>
    lateinit var wiValues: MutableList<T>
}
class GUIOutputModelForNewtonCotesMethod<T: Number>(val n: Int, val ti: T, val ksi: T, val wi: T)
fun <T: Number> NewtonCotesOutputData<T>.buildGUIOutputModel(): ObservableList<GUIOutputModelForNewtonCotesMethod<T>> {
    val result: MutableList<GUIOutputModelForNewtonCotesMethod<T>> = mutableListOf()
    for(i in 0 until ksiValues.size)
        result.add(GUIOutputModelForNewtonCotesMethod(i,tiValues[i],ksiValues[i],wiValues[i]))
    return result.observable()
}
fun <T: Number> NewtonCotesOutputData<T>.buildGUIOutputModel(dest: ObservableList<GUIOutputModelForNewtonCotesMethod<T>>): ObservableList<GUIOutputModelForNewtonCotesMethod<T>> {
    dest.remove(0, dest.size)
    for(i in 0 until ksiValues.size)
        dest.add(GUIOutputModelForNewtonCotesMethod(i,tiValues[i],ksiValues[i],wiValues[i]))
    return dest
}


// interpolation linéaire
open class LagrangeOutputData<T: Number>(): BasicOutputData() {
    constructor(
            coefficientsInNumerator: MutableList<MutableList<T>>,
            denominators: List<T>,
            fiPolynomials: List<String>,
            finalPolynomial: String = ""): this() {
        this.coefficientsInNumerator = coefficientsInNumerator //.toMutableList()
        this.denominators = denominators.toMutableList()
        this.fiPolynomials = fiPolynomials.toMutableList()
        this.finalPolynomial = finalPolynomial
    }
    var coefficientsInNumerator: MutableList<MutableList<T>> = mutableListOf()
    var denominators: MutableList<T> = mutableListOf()
    var fiPolynomials: MutableList<String> = mutableListOf()
    var finalPolynomial: String = ""
}
open class NewtonInterpolationOutputData<T: Number>(): BasicOutputData() {
    constructor(
            coefficients: MutableList<MutableList<T>>,
            polynomials: List<String>,
            alphas: List<T>,
            fullPolynomial: String = "",
            reducedPolynomial: String = ""): this() {
        this.coefficients = coefficients //.toMutableList()
        this.alphas = alphas.toMutableList()
        this.polynomials = polynomials.toMutableList()
        this.fullPolynomial = fullPolynomial
        this.reducedPolynomial = reducedPolynomial
    }
    var coefficients: MutableList<MutableList<T>> = mutableListOf()
    var alphas: MutableList<T> = mutableListOf()
    var polynomials: MutableList<String> = mutableListOf()
    var fullPolynomial: String = ""
    var reducedPolynomial: String = ""
}
open class LeastSquaresOutputData<T: Number>(): BasicOutputData() {
    constructor(
            coefficients: List<T>,
            buildLinearSystem: LinearSystemOfEquations<T>,
            fullPolynomial: String = ""): this() {
        this.coefficients = coefficients.toMutableList()
        this.builtLinearSystem = buildLinearSystem
        this.polynomial = fullPolynomial
    }
    var coefficients: MutableList<T> = mutableListOf()
    lateinit var builtLinearSystem: LinearSystemOfEquations<T>
    var polynomial: String = ""
}