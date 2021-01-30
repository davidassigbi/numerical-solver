package extensions.boolean

fun Boolean.runIf(testValue: Boolean, block: Boolean.() -> Unit) {
    if(this == testValue) block()
}

fun Boolean.ifTrue(block: ()-> Unit) {
    if(this) block()
}

fun Boolean.ifFalse(block: () -> Unit) {
    if(!this) block()
}

fun Boolean.toInt(): Int = if(this) 1 else 0