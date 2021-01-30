package extensions

fun <T> T.test(block: T.() -> Boolean): Boolean = block()

fun <T> T.isOneOf(vararg values: T): Boolean = this in values

fun <T> T.isNotOneOf(vararg values: T): Boolean = !isOneOf(values)