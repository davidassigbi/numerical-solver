package classes

import abstractClasses.AbstractResolutionMethod
import utils.AllMethods
import utils.differentialEquations.EulerMethod
import utils.differentialEquations.RungeKuntaMethod
import utils.digitalIntegration.NewtonCotesMethod
import utils.digitalIntegration.SimplistIntegrationMethod
import utils.linearInterpolation.leastSquares.LeastSquaresPolynomialMethod
import utils.linearInterpolation.polynomialApproximation.LagrangeInterpolationMethod
import utils.linearInterpolation.polynomialApproximation.NewtonInterpolationMethod
import utils.linearSystemOfEquations.*
import utils.nonLinearEquations.*
import kotlin.reflect.full.isSubclassOf

sealed class Chapter {
    companion object {
        private val chapters = mutableListOf<Chapter>()
        init {
            Chapter::class.nestedClasses.forEach {
                if(it.isFinal && it.isSubclassOf(Chapter::class))
                    chapters.add(it.objectInstance as Chapter)
            }
            chapters.sortBy{ it.rank }
            chapters.forEach { it.methodsList.add(AllMethods) }
        }
        fun getAll() = chapters

        operator fun get(title: String) : Chapter = chapters.first { c -> c.title == title}

        operator fun get(chapterRank: Int) : Chapter = chapters.first { c -> c.rank == chapterRank }

        fun forEach(action : (Chapter) -> Unit){
            chapters.forEach(action)
        }
    }

    var methodsList = mutableListOf<AbstractResolutionMethod>()

    operator fun get(methodName: String): AbstractResolutionMethod? = methodsList.find { s -> s.methodID.toString() == methodName }

    operator fun get(methodID: ResolutionMethod): AbstractResolutionMethod? = get(methodID.toString())

    override fun toString(): String = title

    var rank = 0

    var title = ""

    fun buildDetailedChapter(): String {
        chapterWithDetails = ""
        chapterDetails = ""

        chapterWithDetails += "Chapitre N°$rank) $title \n"

        methodsList.forEachIndexed { index, method ->
            chapterDetails += "\t$rank.${index + 1}) ${method.methodID} \n"
        }
        chapterWithDetails += chapterDetails

        return chapterWithDetails
    }

    var chapterWithDetails: String = ""

    var chapterDetails = ""

    object SolvingNonLinearEquations : Chapter() {
        init {
            title = "Résolution des équations non linéaires"
            rank = 2
            methodsList.apply {
                add(DichotomyMethod)
                add(NewtonRaphsonMethod)
                add(SecantMethod)
                add(LinearInterpolationMethod)
                add(FixedPointMethod)
            }
            chapterWithDetails = buildDetailedChapter()
        }
    }
    object SolvingSystemOfLinearEquations : Chapter() {
        init {
            title = "Résolution des systèmes d'équations linéaires"
            rank = 3
            methodsList.apply {
                add(AscentMethod)
                add(RollbackMethod)
                add(DiagonalMatrixMethod)

                add(CroutMethod)
                add(CholeskyMethod)

                add(JacobiMethod)
                add(GaussSeidelMethod)

                add(GaussWithoutPivotStrategy)
                add(GaussWithPartialPivotStrategy)
                add(GaussWithTotalPivotStrategy)
                add(GaussJordanMethod)
            }
            chapterWithDetails = buildDetailedChapter()
        }
    }
    object LinearInterpolation : Chapter() {
        init {
            title = "Interpolation linéaire"
            rank = 4
            methodsList.apply {
                add(LagrangeInterpolationMethod)
                add(NewtonInterpolationMethod)
                add(LeastSquaresPolynomialMethod)
            }
            chapterWithDetails = buildDetailedChapter()
        }
    }
    object DifferentialEquations : Chapter() {
        init {
            title = "Equations différentielles"
            rank = 5
            methodsList.apply {
                add(EulerMethod)
                add(RungeKuntaMethod)
            }
            chapterWithDetails = buildDetailedChapter()
        }
    }
    object DigitalIntegration : Chapter() {
        init {
            title = "Inégration numérique"
            rank = 6
            methodsList.apply {
                add(SimplistIntegrationMethod)
                add(NewtonCotesMethod)
            }
            chapterWithDetails = buildDetailedChapter()
        }
    }
}
/*

object NotAChapter : Chapter() {
    init {
        rank = 99
    }
}

*/

/*

    constructor (rank : Int, title : String) {
//        this.CHAPTER_TITLE = CHAPTER_TITLE
        this.title = title
        this.rank = rank
    }

    enum class Chapter(val rank: Int) {
        SOLVING_NON_LINEAR_EQUATIONS(rank = 2) {
            override fun toString() = "Résolution des équations non linéaires"
        },
        SOLVING_SYSTEM_OF_LINEAR_EQUATIONS(rank = 3) {
            override fun toString() = "Résolution des systèmes d'équations linéaires"
        },
        LINEAR_INTERPOLATION(rank = 4) {
            override fun toString(): String = "Interpolation linéaire"
        },
        DIFFERENTIAL_EQUATIONS(rank = 5) {
            override fun toString(): String = "Equations différentielles"
        },
        DIGITAL_INTEGRATION(rank = 6) {
            override fun toString(): String = "Inégration numérique"
        }
    }

    lateinit var CHAPTER_TITLE: Chapter
*/
