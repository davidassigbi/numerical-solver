package utils

import extensions.number.isOdd
import javafx.beans.property.Property
import javafx.scene.chart.XYChart
import objects.GUIOutputModelForDifferentialEquations
import tornadofx.*
import java.awt.TextArea

fun bestStartCharacterFor(size: Int, currentIndex: Int): String {
    return when {
        currentIndex == 0 -> LEFT_TOP_BAR
        currentIndex == size -1 -> LEFT_BOTTOM_BAR
        size.isOdd() && currentIndex == size / 2 -> HORIZONTAL_BAR
        else -> VERTICAL_BAR
    }.toString()
}

fun bestEndingCharacterFor(size: Int, currentIndex: Int): String {
    return when {
        currentIndex == 0 -> RIGHT_TOP_BAR
        currentIndex == size - 1 -> RIGHT_BOTTOM_BAR
        size.isOdd() && currentIndex == size / 2 -> RIGHT_POINTING_HORIZONTAL_BAR
        else -> VERTICAL_BAR
    }.toString()
}

fun <XReturnType, YReturnType, X: XReturnType, Y: YReturnType> buildListOftXYChartSeries(xValues: List<List<X>>, yValues: List<List<Y>>, names: List<String>): MutableList<XYChart.Series<XReturnType, YReturnType>> {
    val result = mutableListOf<XYChart.Series<XReturnType, YReturnType>>()
    if(xValues.size == yValues.size) {
        for(index in 0 until xValues.size) {
            result.add(XYChart.Series(names[index], buildListOftXYChartData<XReturnType, YReturnType, X, Y>(xValues[index], yValues[index]).observable()))
        }
    }
    return result
}

fun <XReturnType, YReturnType, X: XReturnType, Y: YReturnType> buildListOftXYChartData(xValues: List<X>, yValues: List<Y>): MutableList<XYChart.Data<XReturnType, YReturnType>> {
    val result = mutableListOf<XYChart.Data<XReturnType, YReturnType>>()
    if(xValues.size == yValues.size) {
        for(index in 0 until xValues.size) {
            result.add(XYChart.Data(xValues[index], yValues[index]))
        }
    }
    return result
}

fun <ReturnType: Number, Type: ReturnType> buildListOftXYChartData(values: List<GUIOutputModelForDifferentialEquations<Type>>): MutableList<XYChart.Data<ReturnType, ReturnType>> {
    val result = mutableListOf<XYChart.Data<ReturnType, ReturnType>>()
    for(index in 0 until values.size)
        result.add(XYChart.Data(values[index].tn, values[index].yn))
    return result
}
