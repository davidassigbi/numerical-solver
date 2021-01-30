package classes

import extensions.boolean.toInt
import extensions.ranges.except
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

open class MatrixNxN<T>: MatrixNxM<T>
    where T: Number {

    constructor(): super()

    constructor(dimension: Int = 0, init: (row:Int, col:Int)-> T): super(dimension,dimension,init) {
        this.dimension = dimension
    }

    constructor(dimension: Int = 0, init: (row:Int)-> T): this(dimension,{ row,_ -> init(row)})

    constructor(dimension: Int = 0, init: T): this(dimension,{_, _ -> init})

    constructor(other: MatrixNxN<T>): super(other) {
        dimension = other.dimension
    }
    constructor(other: MatrixNxM<T>): super(other) {
        dimension = other.size
    }

    var dimension: Int = 0
        // get() = size

    override fun getTransposed(): MatrixNxN<T> {
        val result = super.getTransposed()
        return MatrixNxN<T>(result.size) { row, col -> result[row][col] }
    }

    fun hasDiagonalNonNull(): Boolean {
        for (i in 0 until dimension)
            if(this[i][i].toDouble() == .0 || this[i][i].toDouble() == -.0)
                return false
        return true
    }

    fun isDiagonalMatrix(strict: Boolean = false): Boolean {
        val compFor: (Int,Int)->Boolean =
                if(!strict) { _, _ -> false }
                else { i, j -> i == j && this[i][j] == .0 }

        for(line in 0 until size)
            for(col in 0 until size)
                if((line != col && this[line][col] != nullValue) || compFor(line,col))
                    return false

        return true;
    }

    open fun isDominantDiagonalMatrix(strict: Boolean = false): Boolean {
        var tmpSum: Number // = .0

        val compFor:  Double.(other: Double) -> Boolean =
                if(strict) { other -> this > other }
                else { other -> this >= other }

        for(line in 0 until size) {
            tmpSum = .0
            for(col in 0 until size except line)
                tmpSum += abs(this[line][col].toDouble())

            if(!abs(this[line][line].toDouble()).compFor(tmpSum))
                return false
        }
        return true
    }

    fun getDEFDecomposition(): Triple<MatrixNxN<T>, MatrixNxN<T>, MatrixNxN<T>> {
        return Triple(
                this.getDiagonalMatrix(),
                (-this.getLowerTriangularMatrix(strict = true)).toSquareMatrix().apply{ changeMinusNullByNull() },
                (-this.getUpperTriangularMatrix(strict = true)).toSquareMatrix().apply{ changeMinusNullByNull() })
    }

    open fun getCroutLRDecomposition(): Pair<MatrixNxN<T>,MatrixNxN<T>>? {
        val L = MatrixNxN(dimension,nullValue)
        val R = MatrixNxN(dimension,nullValue)
        val result: Pair<MatrixNxN<T>,MatrixNxN<T>>?

        if( this[0][0] != nullValue ) {
            for(i in 0 until dimension) {
                R[0][i] = this[0][i]
                L[i][0] = (this[i][0].toDouble() / this[0][0].toDouble()) as T
            }

            for(i in 1 until dimension) {
                var tmpSum: Double
                // suite du remplissage de la matrice R
                for(j in i until dimension) {
                    tmpSum = .0
                    for(k in 0 until i) {
                        tmpSum += L[i][k].toDouble() * R[k][j].toDouble()
                    }
                    R[i][j] = (this[i][j].toDouble() - tmpSum) as T
                }

                // suite du remplissage de la matrice L
                for(k in i+1 until dimension) {
                    tmpSum = .0
                    for(j in 0 until i) {
                        tmpSum += L[k][j].toDouble() * R[j][i].toDouble()
                    }
                    if(R[i][i] != nullValue)
                        L[k][i] = ( (this[k][i].toDouble() - tmpSum) / R[i][i].toDouble()) as T
                    else{
                        // result = null
                        throw Exception("Le coefficient R[${i+1}][${i+1}] est nul donc on ne peut pas décomposer la matrice A, elle est singulière")
                    }
                }
                L[i][i] = unitValue
            }
            result = L to R
        } else {
            // result = null
            throw Exception("Le coefficient A[1][1] est nul donc on ne peut pas décomposer la matrice A, elle est singulière")
        }

        return result
    }

    open fun getCholeskyLLTDecomposition(): Pair<MatrixNxN<T>,MatrixNxN<T>>? {
        val result: Pair<MatrixNxN<T>,MatrixNxN<T>>?
        val L = MatrixNxN(dimension,nullValue)
        // var errorThrown = false
        fun error(startChar: Char = 'A',i: Int = 0,j: Int = 0,message: String? = null) =
                Exception(message ?: "$startChar[$i][$j] = ${if(startChar == 'A')this[i][j] else L[i][j]} < 0, impossible de faire ($startChar[$i][$j])^(1/2). Impossible d'appliquer la méthode de Cholesky pour résoudre ce système.")
        var tmpSum: Double

        if( this[0][0].toDouble() >= nullValue.toDouble() ) {
            L[0][0] = sqrt(this[0][0].toDouble()) as T
        } else {
            throw error('A',0,0)
        }

        for(i in 1 until dimension) {
            L[i][0] = (this[i][0].toDouble() / L[0][0].toDouble()) as T
        }

        for(j in 1 until dimension) {
            tmpSum = .0
            for(k in 0 until j) {
                tmpSum += L[j][k].toDouble().pow(2)
            }
            tmpSum = this[j][j].toDouble() - tmpSum
            if(tmpSum >= .0) {
                L[j][j] = tmpSum.pow(.5) as T
            } else {
                throw error(message = "La matrice A n'est pas définie positive. Impossible de résoudre ce système avec la méthode de Cholesky.")
            }
        }

        for(j in 1 until dimension) {
            for(i in j+1 until dimension) {
                tmpSum = .0
                for(k in 0 until j) {
                    tmpSum += L[i][k].toDouble() * L[j][k].toDouble()
                }
                tmpSum = this[i][j].toDouble() - tmpSum
                if(L[j][j] != nullValue) {
                    L[i][j] = (tmpSum / L[j][j].toDouble()) as T
                } else {
                    throw error('L',j,j)
                }
            }
        }

        val Lt = L.getTransposed()
        result = Pair(L,Lt)
        return result
    }

    fun isUpperTriangularMatrix(strict: Boolean = false): Boolean {
        val sup: Int.(other: Int) -> Boolean =
            if (strict)
                fun Int.(other: Int): Boolean = this >= other // Int::compStrict // as KFunction2<Int, Int, Boolean>
            else
                fun Int.(other: Int): Boolean = this > other // Int::compNotStrict //

        for(line in 0 until dimension)
            for(col in 0 until dimension)
                if ( line.sup(col) && this[line][col] != nullValue && this[line][col] != -nullValue.toDouble())
                    return false

        return true
    }

    fun isLowerTriangularMatrix(strict: Boolean = false): Boolean {
        val inf: Int.(other: Int) -> Boolean =
            if (strict)
                fun Int.(other: Int): Boolean = this <= other // Int::compStrict // as KFunction2<Int, Int, Boolean>
            else
                fun Int.(other: Int): Boolean = this < other // Int::compNotStrict //

        for(line in 0 until dimension)
            for(col in 0 until dimension)
                if (line.inf(col) && this[line][col] != nullValue && this[line][col] != -nullValue.toDouble())
                    return false

/*
        forEachIndexed { line, numbersList ->
            numbersList.forEachIndexed { col, number ->
                if (line.inf(col) && number != nullValue && number != -nullValue.toDouble())
                    return false
            }
        }
        if(strict) {
            forEachIndexed { line, numbersList ->
                numbersList.forEachIndexed { col, number ->
                    if (line <= col && number != 0)
                        return false
                }
            }
        } else {
            forEachIndexed { line, numbersList ->
                numbersList.forEachIndexed { col, number ->
                    if (line < col && number != 0)
                        return false
                }
            }
        }
*/
        return true
    }

    fun isSymmetricMatrix(): Boolean {
        for (line in 0 until this.size)
            for(col in 0..line)
                if(this[line][col] != this[col][line])
                    return false
        return true
    }

    fun isAntiSymmetricMatrix(): Boolean {
        for (line in 0 until this.size)
            for(col in 0..line)
                if(this[line][col].toDouble() != -this[col][line].toDouble())
                    return false
        return true
    }

    fun getUpperTriangularMatrix(strict: Boolean = false): MatrixNxN<T> {
        val result = MatrixNxN(dimension,nullValue)
        for(line in 0 until dimension){
            for(col in 0 until line)
                result[line][col] = nullValue

            for(col in line+strict.toInt() until dimension)
                result[line][col] = this[line][col]
        }
        return result
    }

    fun getDiagonalMatrix(): MatrixNxN<T> {
        val result = MatrixNxN(dimension,nullValue)
        for(line in 0 until dimension){
            for(col in 0 until this[line].size)
                result[line][col] = nullValue
            result[line][line] = this[line][line]
        }
        return result
    }

    fun getLowerTriangularMatrix(strict: Boolean = false): MatrixNxN<T> {
        val result = MatrixNxN(dimension,nullValue)
        for(line in 0 until dimension){
            // col in line until size => on remplit pour la ligne courante les éléments où col > line
            for(col in line until dimension)
                result[line][col] = nullValue

            // col in 0..line => col varie de 0 jusqu'à la ligne courante et donc on remplit à chaque fois que les éléments de la matrice diagonale inférieure
            for(col in 0..line-strict.toInt())
                result[line][col] = this[line][col]
        }
        return result
    }

    fun indexOfFirst(condition: (T) -> Boolean ): Pair<Int,Int> {
        for(i in 0 until dimension)
            for(j in 0 until dimension)
                if(condition(this[i][j]))
                    return i to j

        return -1 to -1
    }

    fun indexOfFirstOnDiagonal(condition: (T) -> Boolean): Int {
        for(i in 0 until dimension)
            if(condition(this[i][i]))
                return i

        return -1
    }

    fun getUpperLeftSubMatrix(dim: Int): MatrixNxN<T> {
        val result = MatrixNxN(dim,initValue)
        for(i in 0 until dim)
            for(j in 0 until dim)
                result[i][j] = this[i][j]
        return result
    }

    fun getLowerRightSubMatrix(dim: Int): MatrixNxN<T> {
        val result = MatrixNxN(dim,initValue)
        for(i in 0 until dim)
            for(j in 0 until dim)
                result[i][j] = this[size - dim + i][size - dim + j]

        return result
    }

    fun isSymmetricPositiveDefinite(): Boolean {
        var result = false

        return isSymmetricMatrix() && result
    }

    fun isPositiveDefinite(): Boolean {
        var result = false

        return result
    }

    fun indexOfLastOnDiagonal(predicate: (T) -> Boolean): Int {
        for(i in dimension -1 downTo 0)
            if(predicate(this[i][i]))
                return i

        return -1
    }

    fun indexOfFirstNullOnDiagonal(): Int = indexOfFirstOnDiagonal { t -> t == nullValue }
}