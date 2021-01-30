package ui.controllers

import classes.Chapter
import javafx.scene.layout.Pane
import tornadofx.*
import ui.views.*

class WelcomeViewController : Controller() {
    val observableChapters = Chapter.getAll().observable()//FXCollections.observableArrayList<Chapter>()

    fun loadChapterView(chapter: Chapter): View {
        return when(chapter.rank) {
            Chapter.SolvingNonLinearEquations.rank -> find(NonLinearEquationsView::class)
            Chapter.SolvingSystemOfLinearEquations.rank -> find(LinearSystemOfEquationsView::class)
            Chapter.LinearInterpolation.rank -> find(LinearInterpolationView::class)
            Chapter.DifferentialEquations.rank -> find(DifferentialEquationsView::class)
            Chapter.DigitalIntegration.rank -> find(DigitalIntegrationView::class)
            else -> find(LinearInterpolationView::class)
        }
    }

    private fun loadResultsPaneForMethod(methodIndex: Int): Pane {
        return Pane()
    }

    /* init {
        Chapter.getAll().forEach { observableChapters.add(it) }
    } */
}