package ui

import javafx.beans.property.Property
import tornadofx.*
import kotlin.reflect.KMutableProperty0

fun <T, R> Property<T>.bindObjectPropertyWithConverter(toBeBound: KMutableProperty0<R>, convert: T.() -> R = { this as R}) {
    addListener { _, _, newValue -> ;toBeBound.setter.call(newValue.convert()) }
}