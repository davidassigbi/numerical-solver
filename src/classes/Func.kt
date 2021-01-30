package classes

class Func() {

    constructor(function : (Double) -> Double = { x -> x * x}, stringVersion: String = ""): this() {
        f = { x, _ -> function(x)}
        this.stringVersion = stringVersion
    }

    constructor(function : (Double, Double) -> Double = { x, _ -> x * x},stringVersion: String = ""): this() {
        f = function
        this.stringVersion = stringVersion
    }

    lateinit var stringVersion: String

    lateinit var f: (Double, Double) -> Double

    operator fun invoke(x1: Double,x0: Double = .0) = f(x1, x0)

    override fun toString () : String = stringVersion
}