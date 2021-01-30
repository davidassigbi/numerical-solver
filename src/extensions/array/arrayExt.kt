package extensions.array

import extensions.number.boundedBy

fun <T : Number> Array<T>.allBoundedBy(lowerBound: T , upperBound: T, boundsIncluded: Boolean = true) : Boolean {
    return all { n -> n.boundedBy(lowerBound, upperBound, boundsIncluded) }
}
fun <T> Array<T>.swap(index1: Int , index2: Int) : Boolean {
    val goodForSwap = arrayOf(index1,index2).allBoundedBy(0,size -1)
    if(goodForSwap){
        val tmp = get(index1)
        set(index1,get(index2))
        set(index2,tmp)
    }
    return goodForSwap
}
fun <T> Array<Array<T>>.swap(row1: Int , col1: Int, row2: Int, col2: Int) : Boolean {
    val goodForSwap = arrayOf(row1,row2).allBoundedBy(0, this.size - 1)
            && arrayOf(col1,col2).allBoundedBy(0, first().size - 1)
    if(goodForSwap) {
        val tmp = this[row1][col1]
        this[row1][col1] = this[row2][col2]
        this[row2][col2] = tmp
    }
    return goodForSwap
}

fun <T> Array<Array<T>>.swapColumns(col1: Int, col2: Int) : Boolean {
    val goodForSwap =
            arrayOf(col1,col2).allBoundedBy(0, first().size - 1)
    if(goodForSwap) {
        for(line in 0 until size)
            swap(line,col1,line,col2)
    }
    return goodForSwap
}

operator fun <T : Number> Array<T>.divAssign(divider: T) {
    for(i in 0 until size){
        set(i,(get(i).toDouble() / divider.toDouble()) as T )
    }
}

fun <T : Number> Array<Array<T>>.divRow(rowIndex: Int, divider: T) : Boolean {
    val good = rowIndex.boundedBy(0,size-1)
    if(good)
        this[rowIndex] /= divider

    return good
}


fun <T : Number> Array<Array<T>>.divColumn(colIndex: Int, divider: T) : Boolean {
    val good = colIndex.boundedBy(0,this.size-1)
    if(good)
        for(row in 0 until this.size)
            this[row][colIndex] = (this[row][colIndex].toDouble() / divider.toDouble()) as T
    return good
}


fun <T : Number> Array<Array<T>>.setColumn(colIndex:Int, value: Array<T>) : Boolean {
    val good = colIndex.boundedBy(0,this.size - 1) && value.size == this.size
    if(good)
        for(line in 0 until size)
            this[line][colIndex] = value[colIndex]
    return good
}

fun <T : Number> Array<Array<T>>.setDiagonal(value: Array<T>) : Boolean {
    val good = size == value.size && all { p -> p.size == size }//this@setDiagonal.size }
    if(good)
        for(i in 0 until size)
            this[i][i] = value[i]
    return good
}