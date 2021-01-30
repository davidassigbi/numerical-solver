

package classes

@Suppress("UNCHECKED_CAST")
data class EquationUnknown <T: Number>(var name: String = "", var value: T = .0 as T) {
    override fun toString(): String = name
    fun asPair() : Pair<String,T> = name to value
    fun asMap() : Map<String,T> = mapOf(name to value)
}