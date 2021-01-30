package classes

import kotlin.math.pow

class SolutionVector<T: Number> (
        size: Int = 0,
        var onlyChars: Boolean = false,
        var startChar: Char = 'A',
        var prefixChar: Char = 'X',
        var startNumber: Int = 1,
        var lowerCase: Boolean = true,
        init: (index: Int) -> EquationUnknown<T> = { EquationUnknown()}): MutableList<EquationUnknown<T>> by mutableListOf() {

    constructor(vararg pairs: Pair<String,T>): this(init = { index -> EquationUnknown(pairs[index].first, pairs[index].second)})

    constructor(values: List<T>, prefixChar: Char = 'X'): this(size = values.size, init = { index -> EquationUnknown("$prefixChar$index", values[index])})

    init {
        when {
            onlyChars && lowerCase -> for(i in 0 until size) this.add(EquationUnknown<T>("${(startChar+i).toLowerCase()}"))
            onlyChars && !lowerCase -> for(i in 0 until size) this.add(EquationUnknown<T>("${(startChar+i).toUpperCase()}"))
            !onlyChars -> for(i in 0 until size) this.add(EquationUnknown<T>("$prefixChar${startNumber+i}"))
            else ->  for(i in 0 until size) this.add(init(i))
        }
    }

    fun copy(): SolutionVector<T> {
        val result = SolutionVector<T>()
        this.forEach { e: EquationUnknown<T> ->
            result.add(e.copy())
        }
        return result
    }
    fun tobVector(): MutableList<T> {
        return mutableListOf<T>().apply {
            this@SolutionVector.sortedBy { it.name }.forEach {
                this.add(it.value)
            }
        }
    }

    fun values(): List<T> = tobVector()

    fun distanceTo(other: SolutionVector<T>): Double {
        var result = .0
        for(i in 0 until size)
            result += (this[i].value.toDouble() - other[i].value.toDouble()).pow(2)
        result = result.pow(0.5)
        return result
    }
    fun swapValuesWith(other: SolutionVector<T>) {
        var tmp: T
        for(i in 0 until size) {
            tmp = this[i].value
            this[i].value = other[i].value
            other[i].value = tmp
        }
    }

    companion object {
        const val UNKNOWNS_ONLY = 0
        const val VALUES_ONLY = 1
        const val UNKNOWNS_AND_VALUES = 2
        const val INLINE_STRING = 3
    }

    fun toStringWithLineBreaks(unknownsOrValuesOrBoth: Int = 0, prefix: String = "[", suffix: String = "]", lineBreak: String = "\n", delimiter: String = ", "): String {
        var result = prefix
        when(unknownsOrValuesOrBoth) {
            UNKNOWNS_ONLY -> {
                for(i in 0 until size)
                    result += this[i].name+lineBreak
            }
            VALUES_ONLY -> {
                for(i in 0 until size)
                    result += "${this[i].value} $lineBreak"
            }
            UNKNOWNS_AND_VALUES -> {
                for(i in 0 until size)
                    result += "${this[i].name} = ${this[i].value} $lineBreak"
            }
            INLINE_STRING -> {
                for(i in 0 until size)
                    result += "${this[i].name} = ${this[i].value} $delimiter"
                result = result.removeSuffix(delimiter)
            }
            else -> {
                for(i in 0 until size)
                    result += "${this[i].name} = ${this[i].value} $lineBreak"
            }
        }
        result = result.removeSuffix(lineBreak) + suffix
        // result += suffix
        return result
    }
}

/*
typealias SolutionVector<T> = MutableList<EquationUnknown<T>>

val <T: Number> SolutionVector<T>.onlyChars: Boolean
    get(){return this.all { it.name.length == 1 && it.name[0].isLetter()}}

val <T: Number> SolutionVector<T>.startChar: Char
    get(){return if(onlyChars) this.first().name[0] else 'A'}

val <T: Number> SolutionVector<T>.prefixChar: Char
    get(){return if(!onlyChars) this.first().name[0] else 'X'}

val <T: Number> SolutionVector<T>.lowerCase: Boolean
    get(){return if(onlyChars) this.first().name[0].isLowerCase() else true }

val <T: Number> SolutionVector<T>.startNumber: Int
    get(){return if(!onlyChars) this.first().name.substring(1).toInt() else 1 }


fun <T: Number> SolutionVector(size: Int = 0, onlyChars: Boolean = false, startChar: Char = 'A', prefixChar: Char = 'X', startNumber: Int = 1, lowerCase: Boolean = true, init: (index: Int)-> EquationUnknown<T> = { EquationUnknown()}) : SolutionVector<T> {
    return when {
        onlyChars && lowerCase -> MutableList(size) { i -> EquationUnknown<T>("${(startChar+i).toLowerCase()}")}
        onlyChars && !lowerCase -> MutableList(size) { i -> EquationUnknown<T>("${(startChar+i).toUpperCase()}")}
        !onlyChars -> MutableList(size) { i -> EquationUnknown<T>("$prefixChar${startNumber+i}")}
        else ->  MutableList(size, init )
    }
}

fun <T: Number> SolutionVector<T>.copy(): SolutionVector<T> {
    val result = SolutionVector<T>()
    this.forEach { e: EquationUnknown<T> ->
        result.add(e.copy())
    }
    return result
}
fun <T: Number> SolutionVector<T>.copy(): SolutionVector<T> {
    val result = SolutionVector<T>()
    this.forEach { e: EquationUnknown<T> ->
        result.add(e.copy())
    }
    return result
}

fun <T: Number> SolutionVector<T>.tobVector(): MutableList<T> {
    return mutableListOf<T>().apply {
        this@tobVector.sortedBy { it.name }.forEach {
            this.add(it.value)
        }
    }
}

*/
/*
    var onlyChars: Boolean
        get(){return this.all { it.name.length == 1 && it.name[0].isLetter()}}

    var startChar: Char
        get(){return if(onlyChars) this.first().name[0] else 'A'}

    var prefixChar: Char
        get(){return if(!onlyChars) this.first().name[0] else 'X'}

    var lowerCase: Boolean
        get(){return if(onlyChars) this.first().name[0].isLowerCase() else true }

    var startNumber: Int
        get(){return if(!onlyChars) this.first().name.substring(1).toInt() else 1 }
*/
/*
        this.onlyChars = onlyChars
        this.startChar = startChar
        this.prefixChar = prefixChar
        this.lowerCase = lowerCase
        this.startNumber = startNumber
*/
/*        when {
            onlyChars && lowerCase -> MutableList(size) { i -> EquationUnknown<T>("${(startChar+i).toLowerCase()}")}
            onlyChars && !lowerCase -> MutableList(size) { i -> EquationUnknown<T>("${(startChar+i).toUpperCase()}")}
            !onlyChars -> MutableList(size) { i -> EquationUnknown<T>("$prefixChar${startNumber+i}")}
            else ->  MutableList(size, init )
        }
        */