package extensions.collection

import extensions.mutablesList.emptyMutableListOf

fun Collection<*>.deepCopy(): Collection<*> {
    val result = emptyMutableListOf<Any?>()

    this.forEachIndexed { index, any ->
        result.add(any)
        if(any is Collection<*>)
            result[index] = any.deepCopy()
        else
            result[index] = any
    }
    return result
}

fun Collection<*>.toDeepString(): String {
    var result = ""
    if(this.all { it !is Collection<*> }) {
        result = toString()
    }
    else {
        result += "["
        forEach { content ->
            result += when {
                content is Collection<*> && content.any { it is Collection<*> } -> "["+content.toDeepString()+"\n"
                content is Collection<*> -> content.toDeepString()+"\n"
                else -> content.toString()
            }
        }
        result += "]"
    }
    return result
}