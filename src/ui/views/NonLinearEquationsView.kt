package ui.views

import classes.Chapter
import classes.ResolutionMethod
import extensions.mutablesList.except
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.scene.Parent
import objects.*
import tornadofx.*
import ui.controllers.ChapterViewController
import ui.dataSource.InputDataProperties
import ui.dataSource.OutputDataProperties
import ui.others.WillApplyMethodAndUpdateUI
import utils.AllMethods
import utils.PREF_SPACING
import utils.VERY_SMALL_PREF_SPACING
import utils.nonLinearEquations.*

class NonLinearEquationsView: View(), WillApplyMethodAndUpdateUI {
    private val viewController = ChapterViewController(Chapter.SolvingNonLinearEquations)
    private val solutionTextField = textfield {  }

    // dichotomie
    val dichotomyTableview = tableview<GUIOutputModelForDichotomyMethod<Double>> {
        // items.addAll(OutputDataProperties.nonLinearEquationsOutputDataProperty.value.dichotomyMethodTracks)
        readonlyColumn("n/index", GUIOutputModelForDichotomyMethod<Double>::index).makeEditable()
        readonlyColumn("a", GUIOutputModelForDichotomyMethod<Double>::a).makeEditable()
        readonlyColumn("f(a)", GUIOutputModelForDichotomyMethod<Double>::fA).makeEditable()
        readonlyColumn("b", GUIOutputModelForDichotomyMethod<Double>::b).makeEditable()
        readonlyColumn("f(b)", GUIOutputModelForDichotomyMethod<Double>::fB).makeEditable()
        readonlyColumn("Milieu courant", GUIOutputModelForDichotomyMethod<Double>::middle).makeEditable()
        readonlyColumn("f(Milieu courant)", GUIOutputModelForDichotomyMethod<Double>::fMiddle).makeEditable()
        readonlyColumn("|a - b|", GUIOutputModelForDichotomyMethod<Double>::absoluteAMoinsB).makeEditable()
    }
    val dichotomySolutionProperty = SimpleStringProperty("")

    // interpolation lineaire
    val linearInterpolationTableview = tableview<GUIOutputModelForDichotomyMethod<Double>> {
        // items = mutableListOf<GUIOutputModelForDichotomyMethod<Double>>().observable()
        readonlyColumn("n/index", GUIOutputModelForDichotomyMethod<Double>::index).makeEditable()
        readonlyColumn("a", GUIOutputModelForDichotomyMethod<Double>::a).makeEditable()
        readonlyColumn("f(a)", GUIOutputModelForDichotomyMethod<Double>::fA).makeEditable()
        readonlyColumn("b", GUIOutputModelForDichotomyMethod<Double>::b).makeEditable()
        readonlyColumn("f(b)", GUIOutputModelForDichotomyMethod<Double>::fB).makeEditable()
        readonlyColumn("xm", GUIOutputModelForDichotomyMethod<Double>::middle).makeEditable()
        readonlyColumn("f(xm)", GUIOutputModelForDichotomyMethod<Double>::fMiddle).makeEditable()
        readonlyColumn("|a - b|", GUIOutputModelForDichotomyMethod<Double>::absoluteAMoinsB).makeEditable()
    }
    val linearInterpolationSolutionProperty = SimpleStringProperty("")

    // methode de substitution ou du point fixe
    val fixedPointTableview = tableview<GUIOutputModelForFixedPointMethod<Double>> {
        // items = mutableListOf<GUIOutputModelForFixedPointMethod<Double>>().observable()
        readonlyColumn("n/index", GUIOutputModelForFixedPointMethod<Double>::index).makeEditable()
        readonlyColumn("X", GUIOutputModelForFixedPointMethod<Double>::x).makeEditable()
        readonlyColumn("f(X)", GUIOutputModelForFixedPointMethod<Double>::fx).makeEditable()
        readonlyColumn("|f(X) - X|", GUIOutputModelForFixedPointMethod<Double>::absoluteFxMoinsX).makeEditable()
    }
    val fixedPointSolutionProperty = SimpleStringProperty("")

    // methode de Newton-Raphson
    val newtonRaphsonTableview = tableview<GUIOutputModelForNewtonRaphsonMethod<Double>> {
        // items = mutableListOf<GUIOutputModelForNewtonRaphsonMethod<Double>>().observable()
        readonlyColumn("n/index", GUIOutputModelForNewtonRaphsonMethod<Double>::index).makeEditable()
        readonlyColumn("Xn", GUIOutputModelForNewtonRaphsonMethod<Double>::xn).makeEditable()
        readonlyColumn("Xn+1", GUIOutputModelForNewtonRaphsonMethod<Double>::xnPlus1).makeEditable()
        readonlyColumn("|Xn+1 - X|", GUIOutputModelForNewtonRaphsonMethod<Double>::absoluteXnMoinsXnPlus1).makeEditable()
    }
    val newtonRaphsonSolutionProperty = SimpleStringProperty("")

    // méthode de la sécante
    val secantTableview = tableview<GUIOutputModelForSecantMethod<Double>> {
        // items = mutableListOf<GUIOutputModelForSecantMethod<Double>>().observable()
        readonlyColumn("n/index", GUIOutputModelForSecantMethod<Double>::index).makeEditable()
        readonlyColumn("Xn-1", GUIOutputModelForSecantMethod<Double>::xnMoins1).makeEditable()
        readonlyColumn("Xn", GUIOutputModelForSecantMethod<Double>::xn).makeEditable()
        readonlyColumn("Xn+1", GUIOutputModelForSecantMethod<Double>::xnPlus1).makeEditable()
        readonlyColumn("|Xn+1 - X|", GUIOutputModelForSecantMethod<Double>::absoluteXnMoinsXnPlus1).makeEditable()
    }
    val secantSolutionProperty = SimpleStringProperty("")

    val iterationsOutputView = vbox {
        spacing = PREF_SPACING
        vbox {
            spacing = VERY_SMALL_PREF_SPACING
            hbox {
                spacing = PREF_SPACING
                label(ResolutionMethod.DICHOTOMY.methodName)
                textfield(property = dichotomySolutionProperty)
            }
            this += dichotomyTableview
        }

        vbox {
            spacing = VERY_SMALL_PREF_SPACING
            hbox {
                spacing = PREF_SPACING
                label(ResolutionMethod.NEWTON_RAPHSON.methodName)
                textfield(property = newtonRaphsonSolutionProperty)
            }
            this += newtonRaphsonTableview
        }

        vbox {
            spacing = VERY_SMALL_PREF_SPACING
            hbox {
                spacing = PREF_SPACING
                label(ResolutionMethod.SECANT.methodName)
                textfield(property = secantSolutionProperty)
            }
            this += secantTableview
        }

        vbox {
            spacing = VERY_SMALL_PREF_SPACING
            hbox {
                spacing = PREF_SPACING
                label(ResolutionMethod.LINEAR_INTERPOLATION_IN_NON_LINEAR_EQUATIONS.methodName)
                textfield(property = linearInterpolationSolutionProperty)
            }
            this += linearInterpolationTableview
        }

        vbox {
            spacing = VERY_SMALL_PREF_SPACING
            hbox {
                spacing = PREF_SPACING
                label(ResolutionMethod.FIXED_POINT.methodName)
                textfield(property = fixedPointSolutionProperty)
            }
            this += fixedPointTableview
        }
    }

    override fun commitNewValues() {
        InputData.intervalScanStep = InputDataProperties.intervalScanStepProperty.value ?: InputData.intervalScanStep
        InputData.lowerBound = InputDataProperties.lowerBoundProperty.value ?: InputData.lowerBound
        InputData.upperBound = InputDataProperties.upperBoundProperty.value ?: InputData.upperBound
        InputData.aPoint = InputDataProperties.aPointProperty.value ?: InputData.aPoint
        InputData.anotherPoint = InputDataProperties.anotherPointProperty.value ?: InputData.anotherPoint
        InputData.startPoint = InputDataProperties.startPointProperty.value ?: InputData.startPoint
        InputData.maxNumberOfIterations = InputDataProperties.maxNumberOfIterationsProperty.value ?: InputData.maxNumberOfIterations
        InputData.precisionOrTolerance = InputDataProperties.precisionOrToleranceProperty.value ?: InputData.precisionOrTolerance
        /* run {
        println("New value of intervalScanStep: ${InputDataProperties.intervalScanStepProperty.value}; new value of InputData.intervalScanStepProperty = ${InputData.intervalScanStep}")
        println("New value of lowerBoundProperty: ${InputDataProperties.lowerBoundProperty.value}; new value of InputData.lowerBoundProperty = ${InputData.a}")
        println("New value of upperBoundProperty: ${InputDataProperties.upperBoundProperty.value}; new value of InputData.upperBoundProperty = ${InputData.b}")
        println("New value of aPointProperty: ${InputDataProperties.aPointProperty.value}; new value of InputData.aPointProperty = ${InputData.aPoint}")
        println("New value of anotherPointProperty: ${InputDataProperties.anotherPointProperty.value}; new value of InputData.anotherPointProperty = ${InputData.anotherPoint}")
        println("New value of startPointProperty: ${InputDataProperties.startPointProperty.value}; new value of InputData.startPointProperty = ${InputData.startPoint}")
        println("New value of maxNumberOfIterationsProperty: ${InputDataProperties.maxNumberOfIterationsProperty.value}; new value of InputData.maxNumberOfIterationsProperty = ${InputData.maxNumberOfIterations}")
        println("New value of precisionOrToleranceProperty: ${InputDataProperties.precisionOrToleranceProperty.value}; new value of InputData.precisionOrToleranceProperty = ${InputData.precisionOrTolerance}")
        } */
    }

    override fun applyMethodAndUpdateUI() {
        scanInterval()
        OutputDataProperties.exactSolutionsProperty.value = InputData.exactSolutions.observable()
        OutputDataProperties.intervalsWithOneSolutionProperty.value = InputData.intervalsWithOneSolution.observable()

        OutputDataProperties.nonLinearEquationsOutputDataProperty.value = when(viewController.selectedMethod.methodID) {
            ResolutionMethod.DICHOTOMY -> {
                val tmp = DichotomyMethod.solve(InputData.lowerBound, InputData.upperBound)
                dichotomyTableview.items = tmp.dichotomyMethodTracks.observable()
                dichotomySolutionProperty.value = tmp.solutions.toString()
                // iterationsOutputView.replaceChildren(dichotomyTableview)
                tmp
            }
            ResolutionMethod.FIXED_POINT -> {
                val tmp = FixedPointMethod.solve(InputData.startPoint)
                fixedPointTableview.items = tmp.fixedPointMethodTracks.observable()
                fixedPointSolutionProperty.value = tmp.solutions.toString()
                // iterationsOutputView.replaceChildren(fixedPointTableview)
                tmp
            }
            ResolutionMethod.LINEAR_INTERPOLATION_IN_NON_LINEAR_EQUATIONS -> {
                val tmp = LinearInterpolationMethod.solve (InputData.aPoint, InputData.anotherPoint)
                linearInterpolationTableview.items = tmp.linearInterpolationMethodTracks.observable()
                linearInterpolationSolutionProperty.value = tmp.solutions.toString()
                // iterationsOutputView.replaceChildren(linearInterpolationTableview)
                tmp
            }
            ResolutionMethod.NEWTON_RAPHSON -> {
                val tmp = NewtonRaphsonMethod.solve(InputData.startPoint)
                newtonRaphsonTableview.items = tmp.newtonRaphsonMethodTracks.observable()
                newtonRaphsonSolutionProperty.value = tmp.solutions.toString()
                // iterationsOutputView.replaceChildren(newtonRaphsonTableview)
                tmp
            }
            ResolutionMethod.SECANT -> {
                val tmp = SecantMethod.solve(InputData.aPoint, InputData.anotherPoint)
                secantTableview.items = tmp.secantMethodTracks.observable()
                secantSolutionProperty.value = tmp.solutions.toString()
                // iterationsOutputView.replaceChildren(secantTableview)
                tmp
            }
            ResolutionMethod.ALL_METHODS -> {
                for(method in viewController.chapter.methodsList except AllMethods) {
                    viewController.selectedMethod = method
                    applyMethodAndUpdateUI()
                }
                OutputDataProperties.nonLinearEquationsOutputDataProperty.value
            }
            else -> {
                viewController.selectedMethod = AllMethods
                applyMethodAndUpdateUI()
                OutputDataProperties.nonLinearEquationsOutputDataProperty.value
            }
        }
        solutionTextField.text = OutputDataProperties.nonLinearEquationsOutputDataProperty.value.solutions.toString()
    }

    private val inputsPane = form {
        hbox {
            spacing = 30.0
            fieldset("Paramètres d'itération",labelPosition = Orientation.HORIZONTAL) {
                field("Pas de balayage") {
                    textfield(InputDataProperties.intervalScanStepProperty) {
                        prefColumnCount = 5
                    }
                }
                field("Precision ou tolérance") {
                    textfield(InputDataProperties.precisionOrToleranceProperty) {
                        prefColumnCount = 5
                    }
                }
                field("Nombre maximal d'itérations") {
                    textfield(InputDataProperties.maxNumberOfIterationsProperty) {
                        prefColumnCount = 5
                    }
                }
            }

            fieldset("Quelques points quelconques",labelPosition = Orientation.HORIZONTAL) {
                field("Un point") {
                    textfield(InputDataProperties.aPointProperty)  {
                        prefColumnCount = 5
                    }
                }
                field("Un autre point") {
                    textfield(InputDataProperties.anotherPointProperty) {
                        prefColumnCount = 5
                    }
                }
                field("Point de départ") {
                    textfield(InputDataProperties.startPointProperty) {
                        prefColumnCount = 5
                    }
                }
            }

            fieldset("Intervalle",labelPosition = Orientation.HORIZONTAL) {
                field("Borne inférieure") {
                    textfield(InputDataProperties.lowerBoundProperty) {
                        prefColumnCount = 5
                    }
                }
                field("Borne supérieure") {
                    textfield(InputDataProperties.upperBoundProperty) {
                        prefColumnCount = 5
                    }
                }
            }
        }
        button("Enregistrer les valeurs") {
            setOnAction {
                commitNewValues()
            }
        }
    }

    private val resultsPane = hbox {
        spacing = 30.0
        vbox {
            spacing = 30.0
            hbox {
                // Méthode de balayage
                spacing = 30.0
                vbox {
                    label("Intervalles")
                    listview(values = OutputDataProperties.intervalsWithOneSolutionProperty) {
                        cellFormat {
                            text = "[${it};\t${it + InputData.intervalScanStep}]"
                        }
                    }
                }
                vbox {
                    label("Solutions exactes")
                    listview(values = OutputDataProperties.exactSolutionsProperty) {
                        cellFormat {
                            text = it.toString()
                        }
                    }
                }
            }
            /* hbox {
                spacing = 30.0
                label("Solution")
                this += solutionTextField
            } */
        }
        this += iterationsOutputView
    }

    override val root: Parent = viewController.buildMainPane(inputsPane, resultsPane)

    init {
        viewController.applyMethodButton.setOnAction {
            applyMethodAndUpdateUI()
        }
        super.init()
    }
}