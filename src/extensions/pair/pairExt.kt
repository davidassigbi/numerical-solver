package extensions.pair

val Pair<Int,Int>.row: Int
    get() = first
val Pair<Int,Int>.col: Int
    get() = second

val Pair<Int,Int>.x: Int
    get() = first
val Pair<Int,Int>.y: Int
    get() = second

val <A , B> Pair<A, B>.key: A
    get() = first
val <A , B> Pair<A, B>.value: B
    get() = second

val <A: Number, B: Number> Pair<A, B>.tn: A
    get() = first
val <A: Number, B: Number> Pair<A, B>.yn: B
    get() = second




typealias Coordinate = Pair<Int,Int>

typealias TnYn<A , B> = Pair<A , B>