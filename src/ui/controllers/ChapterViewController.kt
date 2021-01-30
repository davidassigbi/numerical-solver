package ui.controllers

import abstractClasses.AbstractResolutionMethod
import classes.Chapter
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import tornadofx.*
import utils.differentialEquations.EulerMethod
import utils.differentialEquations.RungeKuntaMethod
import utils.digitalIntegration.NewtonCotesMethod
import utils.digitalIntegration.SimplistIntegrationMethod

open class ChapterViewController(val chapter: Chapter = Chapter.SolvingNonLinearEquations): Controller() {
    open var inputsPane: Parent = Pane()

    open var resultsPane: Parent = Pane()

    val applyMethodButton = Button("Appliquer la méthode")

    open fun buildMainPane(inputsPane: Parent = this@ChapterViewController.inputsPane, resultsPane: Parent = this@ChapterViewController.resultsPane): Parent {
        this@ChapterViewController.inputsPane = inputsPane
        this@ChapterViewController.resultsPane = resultsPane

        return VBox().apply {
            form {
                fieldset {
                    field("Choisissez une méthode") {
                        combobox(property = selectedMethodProperty, values = chapter.methodsList) {
                            selectionModel.select(0)
                        }
                        this += applyMethodButton
                    }
                }
            }

            scrollpane {
                // this.usePrefSize = false
                // this@scrollpane.fitToParentSize()
                squeezebox {
                    multiselect = true
                    fold("Entrées", expanded = true) {
                        useMaxWidth = true
                        this += inputsPane
                    }
                    fold("Résultats", expanded = true) {
                        useMaxWidth = true
                        this += resultsPane
                    }
                }
            }

        }
    }

    val selectedMethodProperty = SimpleObjectProperty<AbstractResolutionMethod>()
    var selectedMethod: AbstractResolutionMethod by selectedMethodProperty

    open fun loadSchemesStringListForMethod(methodName: String = ""): List<String> {
        return when(methodName) {
            EulerMethod.getMethodName() -> EulerMethod.getSchemesAsStrings()
            RungeKuntaMethod.getMethodName() -> RungeKuntaMethod.getSchemesAsStrings()
            NewtonCotesMethod.getMethodName() -> NewtonCotesMethod.getSchemesAsStrings()
            SimplistIntegrationMethod.getMethodName() -> SimplistIntegrationMethod.getSchemesAsStrings()
            else -> EulerMethod.getSchemesAsStrings()
        }
    }
    open fun loadSchemesStringListForMethod(method: AbstractResolutionMethod): List<String> {
        return loadSchemesStringListForMethod(method.getMethodName())
    }
    open fun loadSchemesToObservableList(destList: SimpleListProperty<String>) {
        destList.remove(0, destList.size)
        destList.addAll(loadSchemesStringListForMethod(selectedMethodProperty.value))
    }

    init {
        selectedMethodProperty.addListener { _, oldValue, newValue ->
            println("selectedMethodProperty: oldValue = \"$oldValue\" newValue = \"$newValue\"")
        }
    }
}