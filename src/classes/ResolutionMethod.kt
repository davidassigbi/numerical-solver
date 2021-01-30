package classes

import abstractClasses.AbstractResolutionMethod

enum class ResolutionMethod(var methodName: String = "",var chapter: Int = 0,var rank: Int = 0) {

    // méthodes pour la resolution des équations non linéaires
    DICHOTOMY (methodName = "Dichotomie",chapter = 2),
    NEWTON_RAPHSON (methodName = "Méthode de Newton-Raphson",chapter = 2),
    SECANT (methodName = "Méthode de la corde ou de la sécante",chapter = 2),
    LINEAR_INTERPOLATION_IN_NON_LINEAR_EQUATIONS (methodName = "Méthode d'interpolation linéaire",chapter = 2),
    FIXED_POINT (methodName = "Méthode du point fixe ou substitution",chapter = 2),

    // méthodes pour la résolution des systèmes d'équations linéaires
    ROLLBACK (methodName = "Méthode de retour en arrière",chapter = 3),
    ASCENT(methodName = "Méthode de remontée",chapter = 3),
    DIAGONAL_MATRIX_SYSTEM_SOLVER(methodName = "Méthode de résolution de systèmes à matrice diagonale",chapter = 3),
    GAUSS_WITHOUT_PIVOT_STRATEGY (methodName = "Méthode de Gauss sans stratégie de pivot",chapter = 3),
    GAUSS_WITH_PARTIAL_PIVOT_STRATEGY (methodName = "Méthode de Gauss avec stratégie du pivot partiel",chapter = 3),
    GAUSS_WITH_TOTAL_PIVOT_STRATEGY (methodName = "Méthode de Gauss avec stratégie du pivot total",chapter = 3),
    GAUSS_JORDAN (methodName = "Méthode de Gauss-Jordan",chapter = 3),
    CROUT (methodName = "Méthode de Crout",chapter = 3),
    CHOLESKY (methodName = "Méthode de Cholesky",chapter = 3),
    JACOBI (methodName = "Méthode de Jacobi",chapter = 3),
    GAUSS_SEIDEL (methodName = "Méthode de Gauss-Seidel",chapter = 3),

    // méthodes d'interpolation linéaire
    LAGRANGE (methodName = "Polynômes d'interpolation de Lagrange", chapter = 4),
    NEWTON_IN_LINEAR_INTERPOLATION (methodName = "Polynômes d'interpolation de Newton ou méthode des différences divisées", chapter = 4),
    LEAST_SQUARES (methodName = "Approximation au sens des moindres carrées", chapter = 4),

    // méthodes de calcul différentiel
    EULER (methodName = "Méthode d'Euler (du premier ordre)", chapter = 5),
    RUNGE_KUNTA (methodName = "Méthode de Runge-Kunta (d'ordre 2)", chapter = 5),

    // méthode d'integration numérique
    SIMPLIST_INTEGRATION_METHOD (methodName = "Méthodes à un seul point & interpolation linéaire", chapter = 6),
    NEWTON_COTES (methodName = "Méthode de Newton-Cotes", chapter = 6),

    //
    NAM(methodName = "Not A Method",chapter = 0),
    ALL_METHODS("Toutes les méthodes à la fois", chapter = 0),

    ;

    override fun toString() : String = methodName

    companion object {
        var size = 0

        operator fun get(index: Int) : ResolutionMethod = values().find{ it.rank == index }!!//[index]

        operator fun get(name: String) : ResolutionMethod = valueOf(name)

        operator fun get(method: AbstractResolutionMethod): ResolutionMethod = get(method.methodID.rank)

        fun getMethodsFromChapter(chapter: Int) : List<ResolutionMethod> = values().filter { it.chapter == chapter}

        fun fillRanks() {
            for (i in 0 until values().size)
                values()[i].rank = i
        }
    }
}