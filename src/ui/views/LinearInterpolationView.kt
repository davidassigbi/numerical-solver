package ui.views

import calculateFunctionValues
import classes.Chapter
import classes.ResolutionMethod
import classes.SolutionVector
import extensions.mutablesList.except
import extensions.ranges.step
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import objects.*
import tornadofx.*
import ui.controllers.ChapterViewController
import ui.dataSource.InputDataProperties
import ui.dataSource.OutputDataProperties
import ui.others.UsualStringConverters
import ui.others.WillApplyMethodAndUpdateUI
import utils.*
import utils.linearInterpolation.leastSquares.LeastSquaresPolynomialMethod
import utils.linearInterpolation.polynomialApproximation.LagrangeInterpolationMethod
import utils.linearInterpolation.polynomialApproximation.NewtonInterpolationMethod

class LinearInterpolationView: View(), WillApplyMethodAndUpdateUI {
    private val viewController = ChapterViewController(Chapter.LinearInterpolation)
    val lagrangeOutputPane: Pane = Pane()
    val newtonOutputPane: Pane = Pane()
    val leastSquaresOutputPane: Pane = Pane()

    companion object {
        const val REAL_FUNCTION_SERIES = 0
        const val LAGRANGE_SERIES = 1
        const val NEWTON_SERIES = 2
        const val SMALLEST_SQUARES_SERIES = 3
    }
    val lBoundProperty = SimpleDoubleProperty()
    val uBoundProperty = SimpleDoubleProperty()
    val stepProperty = SimpleDoubleProperty()
    val realFunction = SimpleStringProperty()

    val lagrangeOutputPaneContent: Pane
        get() = VBox().apply {
            form {
                fieldset("Lagrange") {
                    hbox {
                        spacing = PREF_SPACING
                        vbox {
                            for (index in 0 until InputData.xiValues.size) {
                                field("f(${InputData.xiValues[index]})") { textfield(InputData.yiValues[index].toString()) { prefColumnCount = 4 } }
                            }
                        }
                        vbox {
                            for (index in 0 until InputData.xiValues.size) {
                                field("fi$index(x)") {
                                    textfield(OutputDataProperties.lagrangeOutputDataProperty.value.fiPolynomials[index]) { }
                                }
                            }
                        }
                    }
                    field("P${InputData.xiValues.size-1}(x)") {
                        textarea(OutputDataProperties.lagrangeOutputDataProperty.value.finalPolynomial) { prefRowCount = 3 }
                    }
                }
            }
        }

    val newtonOutputPaneContent: Pane
        get() = VBox().apply {
            form {
                fieldset("Newton") {
                    hbox {
                        spacing = PREF_SPACING
                        vbox {
                            for(index in 0 until OutputDataProperties.newtonInterpolationOutputDataProperty.value.alphas.size) {
                                field("a$index") {
                                    textfield(OutputDataProperties.newtonInterpolationOutputDataProperty.value.alphas[index].toString()){
                                        prefColumnCount = 5
                                    }
                                }
                            }
                        }
                        vbox {
                            for(index in 0 until InputData.xiValues.size) {
                                field("N$index(x)") {
                                    textfield(OutputDataProperties.newtonInterpolationOutputDataProperty.value.polynomials[index]){  }
                                }
                            }
                        }
                    }
                    field("P${InputData.xiValues.size-1}(x)") {
                        textarea(OutputDataProperties.newtonInterpolationOutputDataProperty.value.fullPolynomial) {
                            prefRowCount = 3
                        }
                    }
                    field("P${InputData.xiValues.size-1}(x)") {
                        textfield(OutputDataProperties.newtonInterpolationOutputDataProperty.value.reducedPolynomial)
                    }
                }
            }
        }

    val leastSquaresOutputPaneContent: Pane
        get() = VBox().apply {
            form {
                fieldset("Moindres carrées", labelPosition = Orientation.VERTICAL) {
                    label("Avant les modifications")
                    hbox {
                        spacing = PREF_SPACING
                        field("P") {
                            textarea {
                                text = OutputDataProperties.leastSquaresOutputDataProperty
                                        .value
                                        .builtLinearSystem
                                        ._nativeMatrix
                                        .toStringWithLineBreaks(prefix = "[", delimiter = ", $SINGLE_TAB", lineBreak = CARRIAGE_RETURN)
                                this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                                this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
                            }
                        }
                        field("A") {
                            textarea {
                                text = OutputDataProperties.leastSquaresOutputDataProperty
                                        .value
                                        .builtLinearSystem
                                        ._solutionVector
                                        .toStringWithLineBreaks(unknownsOrValuesOrBoth = SolutionVector.UNKNOWNS_AND_VALUES)
                                this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                                this.prefColumnCount = PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA
                            }
                        }
                        field("B") {
                            textarea {
                                text = OutputDataProperties.leastSquaresOutputDataProperty
                                        .value
                                        .builtLinearSystem
                                        ._bVector
                                        .joinToString(CARRIAGE_RETURN)
                                this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                                this.prefColumnCount = PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA
                            }
                        }
                    }

                    label("Après la resolution du système")
                    hbox {
                        spacing = PREF_SPACING
                        field("P") {
                            textarea {
                                text = OutputDataProperties.leastSquaresOutputDataProperty
                                        .value
                                        .builtLinearSystem
                                        .nativeMatrix
                                        .toStringWithLineBreaks(prefix = "[", delimiter = ", $SINGLE_TAB", lineBreak = CARRIAGE_RETURN)
                                this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                                this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
                            }
                        }
                        field("A") {
                            textarea {
                                text = OutputDataProperties.leastSquaresOutputDataProperty.value.builtLinearSystem.solutionVector.toStringWithLineBreaks(SolutionVector.UNKNOWNS_AND_VALUES)
                                this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                                this.prefColumnCount = PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA
                            }
                        }
                        field("B") {
                            textarea {
                                text = OutputDataProperties.leastSquaresOutputDataProperty.value.builtLinearSystem.bVector.joinToString(CARRIAGE_RETURN)
                                this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                                this.prefColumnCount = PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA
                            }
                        }
                    }

                    field("P(x)") {
                        textfield(OutputDataProperties.leastSquaresOutputDataProperty.value.polynomial)
                    }
                }
            }
        }

    override fun commitNewValues() {
        InputData.xiValues = InputDataProperties.xiValuesProperty.value ?: InputData.xiValues
        InputData.yiValues = InputDataProperties.yiValuesProperty.value ?: InputData.yiValues
        InputData.numberOfKnownValuesPlusOne = InputDataProperties.numberOfKnownValuesProperty.value ?: InputData.numberOfKnownValuesPlusOne
        InputData.leastSquaresPolynomialDegree = InputDataProperties.leastSquaresPolynomialDegreeProperty.value ?: InputData.leastSquaresPolynomialDegree
    }

    override fun applyMethodAndUpdateUI() {
        when(viewController.selectedMethod.methodID) {
            ResolutionMethod.LAGRANGE -> {
                OutputDataProperties.lagrangeOutputDataProperty.value = LagrangeInterpolationMethod.solve(InputData.xiValues, InputData.yiValues)
                lagrangeOutputPane.replaceChildren(*lagrangeOutputPaneContent.children.toTypedArray())
                fillSerie(LAGRANGE_SERIES, OutputDataProperties.lagrangeOutputDataProperty.value)
            }
            ResolutionMethod.NEWTON_IN_LINEAR_INTERPOLATION -> {
                OutputDataProperties.newtonInterpolationOutputDataProperty.value = NewtonInterpolationMethod.solve(InputData.xiValues, InputData.yiValues)
                newtonOutputPane.replaceChildren(*newtonOutputPaneContent.children.toTypedArray())
                fillSerie(NEWTON_SERIES, OutputDataProperties.newtonInterpolationOutputDataProperty.value)
            }
            ResolutionMethod.LEAST_SQUARES -> {
                if(InputData.leastSquaresPolynomialDegree < InputData.xiValues.size - 1) {
                    OutputDataProperties.leastSquaresOutputDataProperty.value = LeastSquaresPolynomialMethod.solve(InputData.xiValues, InputData.yiValues, InputData.leastSquaresPolynomialDegree)
                    leastSquaresOutputPane.replaceChildren(*leastSquaresOutputPaneContent.children.toTypedArray())
                    fillSerie(SMALLEST_SQUARES_SERIES, OutputDataProperties.leastSquaresOutputDataProperty.value)
                } else {
                    LeastSquaresPolynomialMethod.reportMethodError("La valeur du dégré du polynome recherché ne peut pas dépasser ${InputData.xiValues.size - 2}. Veuillez reduire le dégré puis refaites le calcul.")
                }
            }
            ResolutionMethod.ALL_METHODS -> {
                for(method in viewController.chapter.methodsList except AllMethods) {
                    viewController.selectedMethod = method
                    applyMethodAndUpdateUI()
                }
            }
            else -> {
                viewController.selectedMethod = AllMethods
                applyMethodAndUpdateUI()
            }
        }
    }

    private val inputsPane = form {
        hbox {
            spacing = PREF_SPACING
            vbox {
                fieldset {
                    field("Nombre de valeurs que vous connaissez") {
                        textfield(InputDataProperties.numberOfKnownValuesProperty) { prefColumnCount = 3 }
                    }
                }
                fieldset("Valeurs de la fonction") {
                    field("xi") {
                        textfield(InputDataProperties.xiValuesProperty, converter = UsualStringConverters.forObservableListOfDouble)
                    }
                    field("yi") {
                        textfield(InputDataProperties.yiValuesProperty,converter = UsualStringConverters.forObservableListOfDouble)
                    }
                }
                fieldset {
                    field("Dégré du polynôme des moindres carrées") {
                        textfield(InputDataProperties.leastSquaresPolynomialDegreeProperty)
                    }
                }
            }
            vbox {
                fieldset("Quelques paramètres pour le tracé des courbes") {
                    // spacing = PREF_SPACING
                    field("Vraie fonction:") {
                        textfield(realFunction)
                    }
                    field("Borne inférieure") {
                        textfield(lBoundProperty)
                    }
                    field("Borne supérieure") {
                        textfield(uBoundProperty)
                    }
                    field("Pas de parcours de l'intervalle") {
                        textfield(stepProperty)
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

    val lineChart: LineChart<Number, Number> = LineChart(NumberAxis(), NumberAxis()).apply {
        title = "Interpolation linéaire"
        minWidth = PREF_MIN_WIDTH_FOR_LINECHART
        minHeight = PREF_MIN_HEIGHT_FOR_LINECHART
        series("Vraie fonction")
        series("Interpolé de Lagrange")
        series("Interpolé de Newton")
        series("Interpolé des moindres carrées")
    }

    fun clearSerie(serieIndex: Int = REAL_FUNCTION_SERIES) {
        val size = lineChart.data[serieIndex].data.size
        lineChart.data[serieIndex].data.remove(0,size)
    }

    fun fillSerie(serieIndex: Int, sol: BasicOutputData, degree: Int = 2) {
        val listOfXYChartData: MutableList<XYChart.Data<Number, Number>>
        val yValues: MutableList<Double>

        val plotingRange = mutableListOf<Double>()
        val j = lBoundProperty.value
        while(j < uBoundProperty.value) {
            plotingRange.add(j)
        }


        listOfXYChartData = when(serieIndex) {
            REAL_FUNCTION_SERIES -> {
                null!!
            }
            LAGRANGE_SERIES -> {
                val lagrangeOutput = (sol as LagrangeOutputData<Double>)
                yValues = calculateFunctionValues(plotingRange, lagrangeOutput.finalPolynomial)
                buildListOftXYChartData(plotingRange, yValues)
            }
            NEWTON_SERIES -> {
                val newtonOutput = (sol as NewtonInterpolationOutputData<Double>)
                yValues = calculateFunctionValues(plotingRange, newtonOutput.reducedPolynomial)
                buildListOftXYChartData(plotingRange, yValues)
            }
            SMALLEST_SQUARES_SERIES -> {
                val smallestSquaresOutput = (sol as LeastSquaresOutputData<Double>) // tmpSol.tnValues.size - 2
                yValues = calculateFunctionValues(plotingRange, smallestSquaresOutput.polynomial)
                buildListOftXYChartData(plotingRange, yValues)
            }
            else -> {
                yValues = calculateFunctionValues(plotingRange, realFunction.value)
                buildListOftXYChartData(plotingRange, yValues)
            }
        }
        clearSerie(serieIndex)
        lineChart.data[serieIndex].data.addAll(listOfXYChartData)
        println("inpterpolationMethod = ${lineChart.data[serieIndex].name}; lineChart.data[serieIndex].data.addAll(listOfXYChartData) = ${lineChart.data[serieIndex].data.toList()}\n")
    }


    private val resultsPane = hbox {
        vbox {
            this += lagrangeOutputPane
            this += newtonOutputPane
            this += leastSquaresOutputPane
        }
        this += lineChart
    }

    override val root = viewController.buildMainPane(inputsPane, resultsPane)

    init {
        viewController.applyMethodButton.setOnAction {
            applyMethodAndUpdateUI()
        }
        super.init()
    }
}