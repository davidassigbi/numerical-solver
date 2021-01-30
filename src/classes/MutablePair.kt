package classes

data class MutablePair<T,R> constructor (var first: T,var second: R) {
    fun toPair(): Pair<T,R> = Pair(first,second)
    fun setWith(first: T,second: R) {
        this.first = first
        this.second = second
    }
    fun setWith(v: Pair<T,R>) {
        setWith(v.first,v.second)
    }
}