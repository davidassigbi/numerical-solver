package ui.views

import classes.Chapter
import javafx.scene.control.TabPane
import tornadofx.*
import ui.controllers.WelcomeViewController

class WelcomeView : View("Algorithmes numeriques") {
    // val chapterObservableList = emptyMutableListOf<String>().observable() //= Chapter.getAll()
    val controller: WelcomeViewController by inject()
    // var selectedChapterIndex = 0
    // var selectedMethod = SimpleObjectProperty<AbstractResolutionMethod>()

    override val root = borderpane {
        top = menubar {
            menu("Fichier") {
                menu("Aller à") {
                    controller.observableChapters.forEachIndexed { chapterIndex, chapter ->
                        menu(chapter.title) {
                            chapter.methodsList.forEachIndexed { _, method ->
                                item(method.getMethodName()) {
                                    this.setOnAction {
                                        (this@borderpane.center as TabPane).selectionModel.select(chapterIndex)
                                        // controller.loadChapterView(chapter)
                                    }
                                }
                            }
                        }
                    }
                }
                separator()
                item("Enregistrer","Shortcut+S") {
                    // println("En train d'enregistrer!")
                }
                item("Quitter","Shortcut+Q") {
                    // println("En train de quitter!")
                }
            }
        }

        center = tabpane {
            Chapter.getAll().forEach {
                tab(it.title) {
                    this += controller.loadChapterView(it)
                }
            }
        }
    }
}