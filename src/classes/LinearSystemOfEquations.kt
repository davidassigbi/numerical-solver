@file:Suppress("UNCHECKED_CAST")

package classes

import extensions.mutablesList.*
import extensions.number.isOdd
import extensions.number.signedString
import extensions.number.toBoolean
import extensions.pair.col
import extensions.pair.row
import utils.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

open class LinearSystemOfEquations<T>
    where T: Number {

    constructor(dimension: Int, init: T = .0 as T, unknownPrefixChar: Char = 'X') {
        nativeMatrix = MatrixNxN(dimension, init)
        solutionVector = SolutionVector(size = dimension, prefixChar = unknownPrefixChar)
        bVector = MutableList(dimension) {init}

        _nativeMatrix = MatrixNxN(dimension, init)
        _solutionVector = SolutionVector(prefixChar = unknownPrefixChar)
        _bVector = mutableListOf()

        backupSystemOriginalState()
    }

    val dimension: Int get() = nativeMatrix.dimension

    var init: T
        get() = nativeMatrix.initValue
        set(value) { nativeMatrix.initValue = value }

    val nullValue: T get() = nativeMatrix.nullValue

    val unitValue: T get() = nativeMatrix.unitValue

    var A: MatrixNxN<T>
        get() = nativeMatrix
        set(value) { nativeMatrix = value}

    var B: MutableList<T>
        get() = bVector
        set(value) {bVector = value}

    var X: SolutionVector<T>
        get() = solutionVector
        set(value) { solutionVector = value}

    // La matrice A si on considere le système d'équations est sous la forme AX = B
    var nativeMatrix: MatrixNxN<T>
    var _nativeMatrix: MatrixNxN<T>

    // Le vecteur solution du système d'équations ou le vecteur X si les système d'équations est AX = B
    var solutionVector: SolutionVector<T>
    var _solutionVector: SolutionVector<T> = SolutionVector()

    var bVector: MutableList<T>
    var _bVector: MutableList<T> = mutableListOf()

    var matrixString = ""
    lateinit var _matrixString: String

    var linearSystemString = ""
    lateinit var _linearSystemString: String

    var methodsWithSolutions: MutableSet<Pair<ResolutionMethod, SolutionVector<T>>> = mutableSetOf()

    fun backupSystemOriginalState() {
        _bVector.clear() // = mutableListOf()
        bVector.forEach { _bVector.add(it) }

        _nativeMatrix.clear() // = MatrixNxN()
        for(i in 0 until dimension) {
            _nativeMatrix.add(mutableListOf())
            for(j in 0 until dimension) {
                _nativeMatrix[i].add(nativeMatrix[i][j])
            }
        }

        _solutionVector.clear() // = SolutionVector()
        solutionVector.forEach {
            _solutionVector.add(EquationUnknown(it.name,it.value))
        }
    }

    fun restoreSystemOriginalState() {
        bVector.clear()
        _bVector.forEach { bVector.add(it) }

        nativeMatrix.clear()
        for(i in 0 until dimension) {
            nativeMatrix.add(mutableListOf())
            for(j in 0 until dimension) {
                nativeMatrix[i].add(_nativeMatrix[i][j])
            }
        }

        solutionVector.clear()
        _solutionVector.forEach {
            solutionVector.add(EquationUnknown(it.name,it.value))
        }
    }

    fun toStringWithLineBreaks(prefix: String = "[", suffix: String = "]", endLine: String = "end\n", delimiter: String =","): String {
        var result = prefix

        for(i in 0 until dimension) {
            for (j in 0..dimension) {
                result += "${this[i, j].toDouble().signedString()}$delimiter"
            }
            result = result.removeSuffix(delimiter) + endLine
            // result += endLine
        }
        result = result.removeSuffix(endLine) + suffix
        // result += suffix

        return result
    }

    override fun toString(): String {
        var result = ""

        for(line in nativeMatrix.indices){
            result += CARRIAGE_RETURN
            for(col in nativeMatrix[line].indices)
                result += "${nativeMatrix[line][col]} $SINGLE_TAB"
        }
        result += CARRIAGE_RETURN

        return result
    }

    fun toLinearSystemOfEquations(): String {
        var result = ""

        for(line in nativeMatrix.indices){
            result += bestStartCharacterFor(nativeMatrix.size,line)

            for(col in nativeMatrix[line].indices)
                result += "${nativeMatrix[line][col].toDouble().signedString(signedIfPositive = col.toBoolean(), spaced = col.toBoolean())}*${solutionVector[col].name} "

            result += "= ${bVector[line]}$CARRIAGE_RETURN"
        }

        return result
    }

    val detailedSolutionVector: String
        get() {
            var result = ""
            for(i in 0 until solutionVector.size) {
                result += bestStartCharacterFor(solutionVector.size,i)
                result += "${solutionVector[i].name} = ${solutionVector[i].value}"
                if (i != solutionVector.size -1 )
                    result += "$CARRIAGE_RETURN$VERTICAL_BAR$CARRIAGE_RETURN"
            }
            return result
        }

    operator fun get(rowIndex: Int) = nativeMatrix[rowIndex]

    operator fun set(rowIndex: Int, row: Array<T>) {nativeMatrix[rowIndex] = row.toMutableList()}

    operator fun set(rowIndex: Int, row: Collection<T>) {nativeMatrix[rowIndex] = row.toMutableList()}

    operator fun get(rowIndex: Int, colIndex: Int): T {
        return when {
            colIndex < dimension -> nativeMatrix[rowIndex][colIndex]
            colIndex == dimension -> bVector[rowIndex]
            else -> throw IndexOutOfBoundsException("Index: [$rowIndex][$colIndex], rows = $dimension, cols = ${dimension +1}")
        }
    }

    operator fun set(rowIndex: Int, colIndex: Int, value: Number) {
        when {
            colIndex < dimension -> nativeMatrix[rowIndex][colIndex] = value as T
            colIndex == dimension -> bVector[rowIndex] = value as T
            else -> throw IndexOutOfBoundsException("Index: [$rowIndex][$colIndex], rows = $dimension, cols = ${dimension +1}")
        }
    }

    operator fun divAssign(d: Number) {
        this *= (1.toDouble() / d.toDouble())
    }

    operator fun timesAssign(t: Number) {
        nativeMatrix.timesAssign(t)
        bVector.divAssign(t)
    }

    open fun setColumn(colIndex: Int,value: Array<T>): Boolean = nativeMatrix.setColumn(colIndex, value)

    open fun setColumn(colIndex: Int,values: List<T>): Boolean = nativeMatrix.setColumn(colIndex, values)

    open fun getColumn(colIndex: Int): List<T> = nativeMatrix.getColumn(colIndex)

    open fun setRow(rowIndex: Int, values: List<T>) {
        if(values.size == dimension + 1) {
            nativeMatrix[rowIndex] = values.take(nativeMatrix.dimension).toMutableList()
            bVector[rowIndex] = values.last()
        }
    }

    open fun getRow(rowIndex: Int) : List<T> = nativeMatrix[rowIndex] + bVector[rowIndex]

    open fun setDiagonal(value: Array<T>): Boolean = nativeMatrix.setDiagonal(value)

    open fun setDiagonal(values: List<T>): Boolean = nativeMatrix.setDiagonal(values)

    open fun swapRows(row1: Int, row2: Int) {
        nativeMatrix.swap(row1,row2)
        bVector.swap(row1,row2)
    }

    open fun swapColumns(col1: Int, col2: Int) {
        nativeMatrix.swapColumns(col1, col2)
        solutionVector.swap(col1,col2)
    }

    open fun swap(row1: Int, col1: Int, row2: Int, col2: Int) =  nativeMatrix.swap(row1, col1, row2, col2)

    open fun divColumn(colIndex: Int, divider: T = 1.0 as T) = nativeMatrix.divColumn(colIndex, divider)

    open fun divRow(rowIndex: Int, divider: T = 1.0 as T) {
        nativeMatrix[rowIndex].divAssign(divider)
        bVector[rowIndex] = (bVector[rowIndex].toDouble() / divider.toDouble()) as T
    }

    open fun divRow(rowIndex: Int, dividers: List<T>) {
        // trying to do something like this =>
        // linearSystem[rowIndex] /= dividers
        setRow(rowIndex, values = getRow(rowIndex) / dividers )
    }

    open fun addRow(destRowIndex: Int, otherRowIndex: Int, otherRowFactor: T, destFactor: T = 1.0 as T) {
        // trying to do something like this =>
        // destRow = sourceRow*otherRowFactor + destRow*destFactor
        setRow(rowIndex = destRowIndex, values = (getRow(otherRowIndex) * otherRowFactor).eachPlus(getRow(destRowIndex) * destFactor))
    }

    fun deepCopy(): LinearSystemOfEquations<T> {
        val result = LinearSystemOfEquations(dimension, init)
        result.nativeMatrix = MatrixNxN(nativeMatrix)
//        result._nativeMatrix = _nativeMatrix.copy()
        result.bVector = bVector.toMutableList() // as MutableList<T>
//        result._bVector = _bVector.toMutableList() //deepCopy() as MutableList<T>
        result.solutionVector = solutionVector.copy()
        return result
    }

    open fun getMaxPivotRowValueFromRestOfColumn(currentRow: Int): Pair<Int,T> {
        var maxPivotRow = currentRow
        var maxPivotValue = nativeMatrix[currentRow][currentRow]
        for(i in currentRow until nativeMatrix.dimension) {
            if( abs(nativeMatrix[i][currentRow].toDouble()) > abs(maxPivotValue.toDouble()) ) {
                maxPivotRow = i
                maxPivotValue = nativeMatrix[i][currentRow]
            }
        }
        return maxPivotRow to maxPivotValue
    }

    open fun maximizePivotFromRestOfColumn(currentRow: Int): Boolean {
        val maxPivotRow = getMaxPivotRowValueFromRestOfColumn(currentRow).first

        if(currentRow != maxPivotRow)
            swapRows(currentRow,maxPivotRow)

        return nativeMatrix[currentRow][currentRow] != nullValue
    }

    open fun getMaxPivotCoordinatesFromRestOfMatrix(currentRow: Int): Pair<Int,Int> {
        val maxPivotCoordinates = MutablePair(currentRow,currentRow)
        var maxPivotValue = nativeMatrix[currentRow][currentRow]

        for (line in currentRow until nativeMatrix.dimension) {
            for (col in currentRow until nativeMatrix.dimension) {
                if( abs(nativeMatrix[line][col].toDouble()) > abs(maxPivotValue.toDouble())) {
                    maxPivotCoordinates.setWith(line,col)
                    maxPivotValue = nativeMatrix[line][col]
                }
            }
        }
        return maxPivotCoordinates.toPair()
    }

    open fun maximizePivotFromRestOfMatrix(currentRow: Int):Boolean {
        val maxPivotCoordinates = getMaxPivotCoordinatesFromRestOfMatrix(currentRow)

        if(maxPivotCoordinates.row != currentRow)
            swapRows(maxPivotCoordinates.row, currentRow)
        if(maxPivotCoordinates.col != currentRow)
            swapColumns(maxPivotCoordinates.col, currentRow)

        return nativeMatrix[currentRow][currentRow] != nullValue && nativeMatrix[currentRow][currentRow].toDouble() != (-nullValue.toDouble())
    }

    open fun getCroutLRDecomposition(): Pair<LinearSystemOfEquations<T>,LinearSystemOfEquations<T>>? {
//        val (lMatrix, rMatrix) = nativeMatrix.getCroutLRDecomposition()!!

        val L = LinearSystemOfEquations(dimension,nullValue).apply {
            this.solutionVector = SolutionVector(prefixChar = 'L')
            this.bVector = this@LinearSystemOfEquations.bVector.toMutableList()
        }
        val R = LinearSystemOfEquations(dimension,nullValue).apply {
            this.solutionVector = SolutionVector(prefixChar = 'R')
            this.solutionVector = this@LinearSystemOfEquations.solutionVector
        }

        val result: Pair<LinearSystemOfEquations<T>,LinearSystemOfEquations<T>>?

        if( A[0][0] != nullValue ) {
            for(i in 0 until dimension) {
                R[0][i] = A[0][i]
                L[i][0] = (A[i][0].toDouble() / A[0][0].toDouble()) as T
            }

            for(i in 1 until dimension) {
                var tmpSum: Double
                // suite du remplissage de la matrice R
                for(j in i until dimension) {
                    tmpSum = .0
                    for(k in 0 until i) {
                        tmpSum += L[i][k].toDouble() * R[k][j].toDouble()
                    }
                    R[i][j] = (A[i][j].toDouble() - tmpSum) as T
                }

                // suite du remplissage de la matrice L
                for(k in i+1 until dimension) {
                    tmpSum = .0
                    for(j in 0 until i) {
                        tmpSum += L[k][j].toDouble() * R[j][i].toDouble()
                    }
                    if(R[i][i] != nullValue)
                        L[k][i] = ( (A[k][i].toDouble() - tmpSum) / R[i][i].toDouble()) as T
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

    open fun getCholeskyLLTDecompostion(): Pair<LinearSystemOfEquations<T>,LinearSystemOfEquations<T>>? {
        var result: Pair<LinearSystemOfEquations<T>,LinearSystemOfEquations<T>>?
        val L = LinearSystemOfEquations(dimension, nullValue).apply {
            this.solutionVector = SolutionVector(prefixChar = 'L')
            this.bVector = this@LinearSystemOfEquations.bVector.toMutableList()
        }
        fun error(startChar: Char = 'A',i: Int = 0,j: Int = 0,message: String? = null) =
                Exception(message ?: "$startChar[$i][$j] = ${if(startChar == 'A') A[i][j] else L[i][j]} < 0, impossible de faire ($startChar[$i][$j])^(1/2). Impossible d'appliquer la méthode de Cholesky pour résoudre ce système.")
        var tmpSum: Double

        if( A[0][0].toDouble() >= nullValue.toDouble() ) {
            L[0][0] = sqrt(A[0][0].toDouble()) as T
        } else {
            throw error('A',0,0)
        }

        for(i in 1 until dimension) {
            L[i][0] = (A[i][0].toDouble() / L[0][0].toDouble()) as T
        }

        for(j in 1 until dimension) {
            tmpSum = .0
            for(k in 0 until j) {
                tmpSum += L[j][k].toDouble().pow(2)
            }
            tmpSum = A[j][j].toDouble() - tmpSum
            if(tmpSum >= .0) {
                L[j][j] = tmpSum.pow(.5) as T
            } else {
                throw error(message = "La matrice A n'est pas définie positive. Impossible de résoudr ce système avec la méthode de Cholesky.")
            }
        }

        for(j in 1 until dimension) {
            for(i in j+1 until dimension) {
                tmpSum = .0
                for(k in 0 until j) {
                    tmpSum += L[i][k].toDouble() * L[j][k].toDouble()
                }
                tmpSum = A[i][j].toDouble() - tmpSum
                if(L[j][j] != nullValue) {
                    L[i][j] = (tmpSum / L[j][j].toDouble()) as T
                } else {
                    throw error('L',j,j)
                }
            }
        }

        val Lt = LinearSystemOfEquations(dimension,nullValue).apply {
            // this.solutionVector = SolutionVector(prefixChar = 'R')
            this.A = L.A.getTransposed()
            this.solutionVector = this@LinearSystemOfEquations.solutionVector
        }
        result = L to Lt

        return result
    }

    override fun hashCode(): Int {
        var result = dimension
        result = 31 * result + dimension
        result = 31 * result + init.hashCode()
        result = 31 * result + nativeMatrix.hashCode()
        result = 31 * result + solutionVector.hashCode()
        result = 31 * result + bVector.hashCode()
        result = 31 * result + matrixString.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LinearSystemOfEquations<*>

        if (dimension != other.dimension) return false
        if (init != other.init) return false
        if (nativeMatrix != other.nativeMatrix) return false
        if (solutionVector != other.solutionVector) return false
        if (bVector != other.bVector) return false
        if (matrixString != other.matrixString) return false

        return true
    }
}

/*
typealias Matrix2D<T> = MutableList<MutableList<T>>
//typealias Matrix2D<T> = ArrayList<ArrayList<T>>

fun <T : Number> Matrix2D(rows: Int = 0, dimension: Int = 0, init: (row: Int, col: Int) -> T = { _,_ -> .0 as T} ): Matrix2D<T> {
    val result = MutableList(rows) { emptyMutableListOf<T>() }
    result.forEachIndexed { row, mutableList ->
        for(col in 0 until dimension)
            mutableList.add(init(row,col))
    }
    return result
}
fun <T : Number> Matrix2D(rows: Int, dimension: Int, init: (index: Int) -> T = {.0 as T} ): Matrix2D<T> {
    return Matrix2D<T>(rows,dimension){row, _ -> init(row) }
//    return MutableList(rows) { MutableList(dimension, init) }
}
fun <T : Number> Matrix2D(rows: Int, dimension: Int, init: T = .0 as T ): Matrix2D<T> {
    return Matrix2D<T>(rows,dimension) { _, _ -> init }
//    return MutableList(rows) { MutableList(dimension) { init } }
}
*/