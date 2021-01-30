package ui.others

import extensions.string.parseArgumentsList
import javafx.collections.ObservableList
import javafx.util.StringConverter
import tornadofx.*

fun String.isNumberInputValid() = all { c -> c.isDigit() || c == '.' || c == ' '}

@Suppress("IMPLICIT_CAST_TO_ANY")
inline fun <reified T : Number> String.toTypedValue() : T = when(T::class) {
    Short::class -> toShort()
    Int::class -> toInt()
    Long::class -> toLong()
    Float::class -> toFloat()
    Double::class -> toDouble()
    else -> toShort()
} as T

fun String.parseArgumentsList (
        whitespacesRegex: Regex = """\s*""".toRegex(),
        prefix: String = "[",
        suffix: String = "]",
        endLineString: String = "end",
        argsRegex: Regex = """(\s*[-+]?\s*\d+\s*\.?\s*\d*\s*|\s*$endLineString\s*)""".toRegex(),
        delimiter: String = ","): List<Any> {
    val argsList : MutableList<Any>
    // val splittedArgsRegex = """(\s*[-+]?\s*\d*\s*\.?\s*\d*\s*|\s*$endLineString\s*)""".toRegex()

    argsList = this.removePrefix(prefix)
            .removeSuffix(suffix)
            .split(endLineString)
            .joinToString("$delimiter$endLineString$delimiter")
            .split(delimiter)
            .filter { it.matches(argsRegex) } //.apply { println("Current values of argsList = $this") }
            .toMutableList().apply {
                forEachIndexed { index, s ->
                    this[index] = s.replace(whitespacesRegex,"")
                    this[index].test { isDouble() || equals(endLineString) }
                }
            }.toMutableList()

    println("Current values of argsList = $argsList")
    var i = 0
    while(i < argsList.size) {
        if(argsList[i].toString() != "end") {
            argsList[i] = argsList[i].toString().toDouble()
        } else {
            argsList[i-1] = Pair(argsList[i-1].toString().toDouble(),argsList[i+1].toString().toDouble())
            argsList.removeAt(i+1)
        }
        i += 1
    }
    argsList.removeIf { it.toString() == endLineString }
    println("Current values of argsList = $argsList")

    return argsList
}

object UsualStringConverters {
    val forObservableListOfBigDecimal = createStringConverterForObservableListOf(String::toBigDecimal)
    val forObservableListOfInteger = createStringConverterForObservableListOf(String::toInt)
    val forObservableListOfDouble
        get() = createStringConverterForObservableListOf(String::toDouble)
    val forObservableListOfFloat = createStringConverterForObservableListOf(String::toFloat)
    val forObservableListOfDoubleToMultilineString
        get() = createStringConverterForObservableListOf(String::toDouble, separator = "\n")
    // val forScheme = createStringConverterFor()
}


fun <T> createStringConverterFor(convert: String.() -> T = { this as T},toString: (T) -> String = {it.toString()}): StringConverter<T> {
    return object: StringConverter<T>() {
        override fun fromString(str: String?): T = str?.convert()!!
        override fun toString(obj: T): String  = toString(obj)
    }
}

fun <T> stringConverterForObservableListOf(singleConvert: String.() -> T, delimiter: String = ",", separator: String = ", "): StringConverter<ObservableList<T>> {
    return object: StringConverter<ObservableList<T>>() {
        val result = emptyList<T>().toMutableList()
        override fun fromString(str: String?): ObservableList<T> {
            val values = (str ?: "").parseArgumentsList()
            result.clear()
            values.forEach { result.add(it.toString().singleConvert()) }
            println("values = $values")
            return result.observable()
        }

        override fun toString(obj: ObservableList<T>?): String {
            return if(obj != null) {
                obj.toList().joinToString(separator)
            } else "[]"
        }
    }
}