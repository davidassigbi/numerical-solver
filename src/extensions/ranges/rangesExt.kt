package extensions.ranges

infix fun ClosedRange<Double>.step(step : Double) : Iterable<Double> {
    require(start.isFinite())
    require(endInclusive.isFinite())
    require(step > 0.0) { "Step must be positive : given step was : $this" }
    val sequence = generateSequence(start) { previous ->
        if(previous == Double.POSITIVE_INFINITY) return@generateSequence null
        val next = previous + step
        if(next > endInclusive) null else next
    }
    return sequence.asIterable()
}

infix fun IntRange.except(other: Int): List<Int> = this.toMutableList().apply { remove(other) }

operator fun IntRange.get(index: Int): Int {
    return elementAt(index)
}