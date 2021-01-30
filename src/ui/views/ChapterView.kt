package ui.views

import abstractClasses.AbstractResolutionMethod
import classes.Chapter
import javafx.scene.control.ComboBox
import javafx.scene.layout.Pane
import tornadofx.*

class ChapterView(val chapter: Chapter = Chapter.LinearInterpolation): View() {
    val innerComboxBox = ComboBox<AbstractResolutionMethod>()

    private fun loadParametersInputPaneForChapter(): View {
        return when(chapter.rank){
            Chapter.SolvingNonLinearEquations.rank -> find(NonLinearEquationsView::class)
            Chapter.SolvingSystemOfLinearEquations.rank -> find(LinearSystemOfEquationsView::class)
            Chapter.LinearInterpolation.rank -> find(LinearInterpolationView::class)
            Chapter.DifferentialEquations.rank -> find(DifferentialEquationsView::class)
            Chapter.DigitalIntegration.rank -> find(DigitalIntegrationView::class)
            else -> find(LinearInterpolationView::class)
        }
    }

    private fun loadResultsPaneForMethod(method: AbstractResolutionMethod): Pane {
        return Pane()
    }

    override val root = vbox {
        form {
            fieldset {
                field("Choisissez une méthode") {
                    this += innerComboxBox
                    button("Evaluer") {
                        this.setOnAction {
                            runAsync {
                                loadParametersInputPaneForChapter() // innerComboxBox.selectionModel.selectedIndex)
                                loadResultsPaneForMethod(innerComboxBox.selectionModel.selectedItem)
                            }
                        }
                    }
                }
            }
        }
        squeezebox {
            multiselect = true
            fold("Entrées", expanded = false) {
                this += loadParametersInputPaneForChapter()
            }
            fold("Résultats", expanded = false) {
                this += loadResultsPaneForMethod(innerComboxBox.selectionModel.selectedItem)
            }
        }
    }

    init {
        chapter.methodsList.forEach { innerComboxBox.items.add(it) }
    }
}