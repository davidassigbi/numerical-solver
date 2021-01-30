package objects

import classes.LinearSystemOfEquations
import extensions.string.parseArgumentsList

object system {
    fun fromString(str: String, delimiter: String = ",",prefix: String = "linearSystem["): LinearSystemOfEquations<Double> {
        val argsList: List<Any> = str.parseArgumentsList(prefix = prefix, delimiter = delimiter)
        return this.get(*argsList.toTypedArray())
    }

    operator fun get(vararg values: Any): LinearSystemOfEquations<Double> {
        val values = if(values.first() is List<*>) values.first() as List<*> else values.toList()
        val numBreaks = values.filter { it is Pair<*,*> }.size
        val numElements = values.size + numBreaks // values.size - numBreaks + 2 * numBreaks
        val rows = numBreaks + 1
        // var cols = numElements / rows
        val result = LinearSystemOfEquations(rows,.0)

        var row = 0
        var col = 0
        // println("current values that will be used to build linearSystem : $values")

        for(element in values) {
            when (element) {
                is Double /* element.toString().isDouble() */ -> {
                    result[row, col] = element
                    col++
                    // println("this element $element use to build linearSystem")
                }
                is Int -> {
                    result[row, col] = element.toDouble()
                    col++
                    // println("this element $element use to build linearSystem")
                }
                is Pair<*,*> -> {
                    result[row, col] = element.first as Double
                    result[row +1 , 0] = element.second  as Double
                    row++
                    col = 1
                    // println("this element $element use to build linearSystem")
                }
                else -> throw Exception("Un ou plusieurs des arguments [${element}] devant servir à construire le système sont d'un type invalide.")
            }
        }
        return result
    }
}