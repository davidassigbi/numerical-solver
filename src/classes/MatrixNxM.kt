package classes

import extensions.ranges.get
import kotlin.math.abs

open class MatrixNxM <T: Number>(): MutableList<MutableList<T>> by mutableListOf() {
    // default value of init could be: { _, _ -> .0 as T}
    constructor(rows: Int = 0, cols: Int = 0, init: (row: Int, col: Int) -> T ) : this() {
        this.rows = rows
        this.cols = cols
        this.clear()
        initValue = init(0,0)

        for(row in 0 until rows){
            this.add(mutableListOf<T>().apply {
                for(col in 0 until cols) {
                    add( init(row, col))
                }
            })
        }
    }
    // default value of init could be: = {.0 as T}
    constructor(rows: Int, cols: Int, init: (row: Int) -> T): this(rows,cols,{ row,_ -> init(row) })
    // default value of init could be: .0 as T
    constructor(rows: Int, cols: Int, init: T): this(rows,cols, { _, _ -> init } )

    constructor(other: MatrixNxM<T>): this(other.rows,other.cols,{ row: Int, col: Int -> other[row][col] }) {
        setUtils(other.initValue, other.unitValue, other.nullValue)
    }
    var rows: Int = 0
    var cols: Int = 0
    // pour pouvoir initialiser et définir correctement les other.nouvelles matrices qui découleront de celle-ci
    var initValue: T = .0 as T
    var unitValue: T = 1.0 as T
    var nullValue: T = .0 as T

    open fun setUtils(initValue: T, unitValue: T, nullValue: T) {
        this.initValue = initValue
        this.unitValue = unitValue
        this.nullValue = nullValue
    }

    open fun changeMinusNullByNull() {
        for(i in 0 until size)
            for(j in 0 until this[i].size)
                if(this[i][j].toDouble() == -nullValue.toDouble())
                    this[i][j] = nullValue
    }

    open fun toSquareMatrix(): MatrixNxN<T> {
        return MatrixNxN<T>(rows) {row, col ->
            this[row][col]
        }
    }

    fun toStringWithLineBreaks(prefix: String = "[", suffix: String = "]", delimiter: String = ", ",lineBreak: String = " end\n"): String {
        var result = prefix
        for(i in 0 until rows) {
            for(j in 0 until cols) {
                result += "${this[i][j]}$delimiter"
            }
            result = result.removeSuffix(delimiter) + lineBreak
            // result += lineBreak
        }
        result = result.removeSuffix(lineBreak) + suffix
        // result += suffix

        return result
    }

    operator fun get(row: Int,col: Int) = this[row][col]

    operator fun set(row: Int,col: Int,value: T) { this[row][col]  = value }

    operator fun get(rowsRange: IntRange, colsRange: IntRange): MatrixNxM<T> {
        val rows = abs(rowsRange.last - rowsRange.first) / rowsRange.step
        val cols = abs(colsRange.last - colsRange.first) / colsRange.step
        val result = MatrixNxM(rows, cols,initValue)
        // val rowsRange = rowsRange.toList()
        // val colsRange = colsRange.toList()

        for(i in 0 until rows)
            for(j in 0 until cols)
                result[i][j] = this[rowsRange[i]][colsRange[j]]

        return result
    }

    open operator fun plus(other: MatrixNxM<T>): MatrixNxM<T> {
        val resultMatrix : MatrixNxM<T>
        if(rows == other.rows && cols == other.cols){
            resultMatrix = MatrixNxM(rows, cols,nullValue)
            for (line in 0 until rows)
                for(col in 0 until cols)
                    resultMatrix[line][col] = (this[line][col].toDouble() + other[line][col].toDouble()) as T
        }else resultMatrix = this
        return resultMatrix
    }

    open operator fun plusAssign(other: MatrixNxM<T>): Unit {
        if(rows == other.rows && cols == other.cols){
            for (line in 0 until rows)
                for(col in 0 until cols)
                    this[line][col] = (this[line][col].toDouble() + other[line][col].toDouble()) as T
        }
    }

    open operator fun plus(other: T): MatrixNxM<T> {
        val resultMatrix = MatrixNxM(rows, cols,nullValue)
        for (line in 0 until rows)
            for(col in 0 until cols)
                resultMatrix[line][col] = (this[line][col].toDouble() + other.toDouble()) as T

        return resultMatrix
    }

    open operator fun unaryMinus(): MatrixNxM<T> {
        val resultMatrix = MatrixNxM(rows, cols,nullValue)
        for (line in 0 until rows)
            for(col in 0 until cols)
                resultMatrix[line][col] = -this[line][col].toDouble() as T
        return resultMatrix
    }

    open operator fun minus(other: MatrixNxM<T>): MatrixNxM<T> = this + (-other)

    open operator fun times(other: Number): MatrixNxM<T> {
        val resultMatrix = MatrixNxM(rows, cols,nullValue)
        for (line in 0 until rows)
            for(col in 0 until cols)
                resultMatrix[line][col] = (this[line][col].toDouble() * other.toDouble()) as T
        return resultMatrix
    }

    open operator fun times(other: MatrixNxM<T>): MatrixNxM<T> {
        val resultMatrix : MatrixNxM<T>
        if(this.cols == other.rows) {
            resultMatrix = MatrixNxM(this.rows, other.cols,nullValue)
            for (line in 0 until this.rows) {
                for(col in 0 until other.cols) {
                    for (k in 0 until cols) {
                        resultMatrix[line][col] = (resultMatrix[line][col].toDouble() + this[line][k].toDouble() + other[k][col].toDouble()) as T
                    }
                }
            }
        } else resultMatrix = this
        return resultMatrix
    }

    open operator fun timesAssign(m: Number) {
        if(m.toDouble() != .0)
            for (line in 0 until rows)
                for (col in 0 until cols)
                    this[line][col] = (this[line][col].toDouble() + m.toDouble() ) as T
    }

    open operator fun div(d: T): MatrixNxM<T> = this * (1.toDouble() / d.toDouble())

    open fun getTransposed(): MatrixNxM<T> {
        val resultMatrix = MatrixNxM(cols, rows, nullValue)
        for (line in 0 until cols)
            for(col in 0 until rows)
                resultMatrix[line][col] = this[col][line]

        return resultMatrix
    }

    fun isSquareMatrix(): Boolean = this.all { numbersList -> numbersList.size == this.size }

    fun isColumnMatrix(): Boolean = this.size == 1

    fun isLineMatrix(): Boolean = this.all { numbersList -> numbersList.size == 1 }// this[0].size == 1

    fun isUnitMatrix(): Boolean {
        this.forEachIndexed { line, numbersList ->
            numbersList.forEachIndexed { col, number ->
                if (line != col && number != .0 || line == col && number != 1.0)
                    return false
            }
        }
        return true
    }

    fun isNullMatrix(): Boolean {
        this.forEachIndexed { _, numbersList ->
            numbersList.forEachIndexed { _, number ->
                if (number != 0)
                    return false
            }
        }
        return true
    }
}