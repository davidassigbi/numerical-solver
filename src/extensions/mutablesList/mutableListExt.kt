package extensions.mutablesList

import extensions.array.allBoundedBy
import extensions.number.boundedBy

infix fun <T> List<T>.except(toRemove: T): List<T> {
    return this.toMutableList().apply { remove(toRemove) }
}

infix fun <T> Array<T>.except(toRemove: T): List<T> {
    return this.toMutableList().apply { remove(toRemove) }
}

fun <T> emptyMutableListOf(): MutableList<T> = emptyList<T>().toMutableList()

fun <T : Number> MutableList<T>.allBoundedBy(lowerBound: T , upperBound: T, boundsIncluded: Boolean = true): Boolean {
    return all { n -> n.boundedBy(lowerBound, upperBound, boundsIncluded) }
}

fun <T> MutableList<T>.swap(index1: Int , index2: Int): Boolean {
    val goodForSwap = arrayOf(index1,index2).allBoundedBy(0,size -1)
    if(goodForSwap){
        val tmp = get(index1)
        set(index1,get(index2))
        set(index2,tmp)
    }
    return goodForSwap
}
/*@Deprecated("Use Collection<*>.deepCopy() if you don't care much about type safety",
        ReplaceWith("Collection<*>.deepCopy()"),
        DeprecationLevel.WARNING)
*/
fun <T> MutableList<MutableList<T>>.copy(): MutableList<MutableList<T>> {
    val result = emptyMutableListOf<MutableList<T>>()

    this.forEach { mt: MutableList<T> ->
        result.add(mt)
        mt.forEach { t: T ->
            result.last().add(t)
        }
    }
    return result
}
fun <T> MutableList<MutableList<T>>.swap(row1: Int , col1: Int, row2: Int, col2: Int): Boolean {
    val goodForSwap = arrayOf(row1,row2).allBoundedBy(0, this.size - 1)
            && arrayOf(col1,col2).allBoundedBy(0, first().size - 1)
    if(goodForSwap) {
        val tmp = this[row1][col1]
        this[row1][col1] = this[row2][col2]
        this[row2][col2] = tmp
    }
    return goodForSwap
}

fun <T> MutableList<MutableList<T>>.swapRows(row1: Int, row2: Int): Boolean = swap(row1,row2)

fun <T> MutableList<MutableList<T>>.swapColumns(col1: Int, col2: Int): Boolean {
    val goodForSwap =
            arrayOf(col1,col2).allBoundedBy(0, first().size - 1)
    if(goodForSwap) {
        for(line in 0 until size)
            swap(line,col1,line,col2)
    }
    return goodForSwap
}

fun <T : Number> MutableList<MutableList<T>>.divRow(rowIndex: Int, divider: Number): Boolean {
    val good = rowIndex.boundedBy(0,size-1)
    if(good)
        this[rowIndex].divAssign(divider)

    return good
}


fun <T : Number> MutableList<MutableList<T>>.divColumn(colIndex: Int, divider: Number): Boolean {
    val good = colIndex.boundedBy(0,this.size-1)
    if(good)
        for(row in 0 until this.size)
            this[row][colIndex] = (this[row][colIndex].toDouble() / divider.toDouble()) as T
    return good
}


fun <T> MutableList<MutableList<T>>.setColumn(colIndex:Int, value: Array<T>): Boolean {
    val good = colIndex.boundedBy(0,this.size - 1) && value.size == this.size
    if(good)
        for(line in 0 until size)
            this[line][colIndex] = value[colIndex]
    return good
}

fun <T> MutableList<MutableList<T>>.setColumn(colIndex:Int, values: List<T>): Boolean {
    val good = colIndex.boundedBy(0,this.size - 1) && values.size == this.size
    if(good)
        for(line in 0 until size)
            this[line][colIndex] = values[colIndex]
    return good
}

fun <T> MutableList<MutableList<T>>.getColumn(colIndex:Int): List<T> {
    return mutableListOf<T>().apply {
        for(line in 0 until this@getColumn.size)
            this.add(this@getColumn[line][colIndex])
    }
}

fun <T> MutableList<MutableList<T>>.setDiagonal(value: Array<T>): Boolean {
    val good = size == value.size && all { p -> p.size == size }//this@setDiagonal.size }
    if(good)
        for(i in 0 until size)
            this[i][i] = value[i]
    return good
}

fun <T> MutableList<MutableList<T>>.setDiagonal(values: List<T>): Boolean {
    val good = size == values.size && all { p -> p.size == size }//this@setDiagonal.size }
    if(good)
        for(i in 0 until size)
            this[i][i] = values[i]
    return good
}

operator fun <T> MutableList<MutableList<T>>.get(coordinates: Pair<Int,Int>) = this[coordinates.first][coordinates.second]

operator fun <T> MutableList<MutableList<T>>.get(row: Int, col: Int) = this[row][col]

operator fun <T> MutableList<MutableList<T>>.set(row: Int, col: Int, value: T) = {this[row][col] = value }


// operators overloading
operator fun <T: Number> List<T>.times(m: Number): List<T> {
    return List(size) { index ->
        (this[index].toDouble() * m.toDouble() ) as T
    }
}

operator fun <T: Number> List<T>.times(other: List<T>): List<T> {
    return List(size) { index ->
        (this[index].toDouble() * other[index].toDouble() ) as T
    }
}

operator fun <T: Number> MutableList<T>.timesAssign(other: List<T>) {
    val result = this * other
    forEachIndexed { index, _ ->
        this[index] = result[index]
    }
}

operator fun <T: Number> MutableList<T>.timesAssign(other: Number) {
    val result = this * other
    forEachIndexed { index, _ ->
        this[index] = result[index]
    }
}

operator fun <T: Number> List<T>.div(m: Number): List<T> {
    return List(size) { index ->
        (this[index].toDouble() / m.toDouble() ) as T
    }
}

operator fun <T: Number> List<T>.div(other: List<T>): List<T> {
    return List(size) { index ->
        (this[index].toDouble() / other[index].toDouble() ) as T
    }
}

operator fun <T: Number> MutableList<T>.divAssign(other: List<T>) {
    val result = this / other
    forEachIndexed { index, _ ->
        this[index] = result[index]
    }
}

operator fun <T: Number> MutableList<T>.divAssign(other: Number) {
    val result = this / other
    forEachIndexed { index, _ ->
        this[index] = result[index]
    }
}

fun <T: Number> List<T>.eachPlus(other: List<T>): List<T> {
    return List(size) { index ->
        (this[index].toDouble() + other[index].toDouble() ) as T
    }
}

fun <T, R> List<T>.convert(convert: (item: T) -> R ): List<R> {
    val result = mutableListOf<R>()
    for(item in this) result.add(convert(item))
    return result
}
