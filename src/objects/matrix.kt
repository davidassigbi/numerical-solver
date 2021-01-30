package objects

import classes.MatrixNxN
import extensions.string.parseArgumentsList
import extensions.test
import tornadofx.*

object matrix {

    fun fromString(str: String, delimiter: String = ",", prefix: String = "["): MatrixNxN<Double> {
        val argsList: List<Any> = str.parseArgumentsList(prefix = prefix)
        return this.get(argsList)
    }

    operator fun get(vararg values: Any): MatrixNxN<Double> {
        val values = if(values.first() is List<*>) values.first() as List<*> else values.asList()
        val numBreaks = values.filter { it is Pair<*,*> }.size
        val numElements = values.size + numBreaks // values.size - numBreaks + 2 * numBreaks
        val rows = numBreaks + 1
        val cols = numElements / rows
        val result = MatrixNxN(rows,.0)

        if(rows != cols)
            throw Exception("Il manque plusieurs arguments pour pouvoir créer la mtrice carrée., rows = $rows, cols = $cols")
        else {
            var row = 0
            var col = 0
            for(element in values) {
                when (element) {
                    is Double -> {
                        result[row, col] = element
                        col++
                    }
                    is Int -> {
                        result[row, col] = element.toDouble()
                        col++
                    }
                    is Pair<*,*> -> {
                        result[row, col] = element.first as Double
                        result[row +1 , 0] = element.second  as Double
                        row++
                        col = 1
                    }
                    else -> throw Exception("Un ou plusieurs des arguments devant servir à construire le système sont d'un type invalide.")
                }
            }
        }
        return result
    }
}