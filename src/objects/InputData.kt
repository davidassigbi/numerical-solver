package objects

import classes.LinearSystemOfEquations
import classes.SolutionVector
import extensions.mutablesList.emptyMutableListOf

object InputData {
    // l'intervalle
    var lowerBound = -100.0
    var upperBound = 100.0

    // deux points quelconques qui sont en fait les points qu'utilisent les méthodes itératives qui nécessitent deux points au départ pour faire les calculs
    var aPoint : Double = -10.0
    var anotherPoint : Double = 13.0
    // point de depart pour les méthodes itératives nécessitant un seul point de départ
    var startPoint : Double = 0.0

    // etat du balayage
    var scanningDone = false

    // nombre maximal d'iterations
    var maxNumberOfIterations = 1000

    // le pas de balayage et la precision utilisée par différentes methodes de calcul
    var precisionOrTolerance = 0.001
    var intervalScanStep = .005

    // liste des solutions exactes trouvées après balayage
    var exactSolutions = mutableListOf<Double>()
    // liste des intervalles contenant(pretendument) des solutions apres balayage
    var intervalsWithOneSolution = mutableListOf<Double>()

    var numberOfPointsToPutInInterval = 25

    // matrix d'entrée
    var matrixDimension = 3
    var linearSystem = LinearSystemOfEquations(matrixDimension, .0).apply { nativeMatrix.setUtils(.0,1.0,.0) }
    var memorizedSystems = mutableSetOf<LinearSystemOfEquations<Double>>()
    var initialSolutionList = mutableListOf(.0,.0,.0,.0,.0)
    var initialSolutionVector = SolutionVector(values = initialSolutionList)

    var numberOfKnownValuesPlusOne = 4
    var xiValues = emptyMutableListOf<Double>().apply { addAll(arrayOf(2.0,3.0,-1.0,4.0)) }
    var yiValues = emptyMutableListOf<Double>().apply { addAll(arrayOf(1.0,-1.0,2.0,3.0)) }

    var lagrangeInterpolationPolynomial = ""
    var fiPolynomialsOfLagrange = mutableListOf<String>()

    var newtonPolynomials = mutableListOf<String>()
    var listOfDividedDifferences = mutableListOf<Double>()
    var fullNewtonInterpolationPolynomial = ""
    var reducedNewtonInterpolationPolynomial = ""

    var leastSquaresPolynomialDegree = xiValues.size - 2
    var leastSquaresPolynomial = ""
    var leastSquaresPolynomialFactors = mutableListOf<Double>()

    var stringVersionOfSchemeToUse: String = ""
}