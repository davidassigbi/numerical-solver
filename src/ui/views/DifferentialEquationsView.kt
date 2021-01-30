package ui.views

import abstractClasses.AbstractDifferentialEquationsResolutionMethod
import calculateFunctionValues
import classes.Chapter
import classes.ResolutionMethod
import extensions.mutablesList.except
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.Button
import javafx.scene.control.TableView
import objects.DEOutputData
import objects.GUIOutputModelForDifferentialEquations
import objects.InputData
import tornadofx.*
import ui.controllers.ChapterViewController
import ui.dataSource.InputDataProperties
import ui.others.SchemeInterface
import ui.others.WillApplyMethodAndUpdateUI
import utils.*
import utils.differentialEquations.EulerMethod
import utils.differentialEquations.RungeKuntaMethod
import utils.linearInterpolation.leastSquares.LeastSquaresPolynomialMethod
import utils.linearInterpolation.polynomialApproximation.LagrangeInterpolationMethod
import utils.linearInterpolation.polynomialApproximation.NewtonInterpolationMethod


class DifferentialEquationsView: View(), WillApplyMethodAndUpdateUI {
    private val viewController = ChapterViewController(Chapter.DifferentialEquations)
    private val schemesListProperty = SimpleListProperty<String>(EulerMethod.getSchemesAsStrings().observable())
    private val y0Property = SimpleDoubleProperty(0.0)

    companion object {
        const val TN_YN_SERIES = 0
        const val LAGRANGE_SERIES = 1
        const val NEWTON_SERIES = 2
        const val SMALLEST_SQUARES_SERIES = 3
    }

    fun findAndUpdateSchemeViewParts(method: AbstractDifferentialEquationsResolutionMethod, scheme: SchemeInterface, allSeries: Boolean = true, serieIndex: Int = TN_YN_SERIES) {
        val tmpSol = method.solve(InputData.lowerBound, InputData.upperBound,InputData.numberOfPointsToPutInInterval,InputData.intervalScanStep,y0Property.value, scheme.id)
        val guiOutput = methodsAndSchemesWithUI[method]!![scheme]!!

        guiOutput.tableView.items = tmpSol.methodTracks.observable()

        if(allSeries)
            guiOutput.fillAllSeries(tmpSol, guiOutput.smallestPolynomialDegreeProperty.value)
        else
            guiOutput.fillSerie(serieIndex, tmpSol, guiOutput.smallestPolynomialDegreeProperty.value)
    }

    class SampleGUIOutput(val titleProperty: SimpleStringProperty = SimpleStringProperty("Figures")) {
        companion object {
            lateinit var tmpSol: DEOutputData<Double>
        }

        lateinit var ownerMethod: AbstractDifferentialEquationsResolutionMethod
        lateinit var ownerScheme: SchemeInterface
        val possibleDegrees = mutableListOf<Int>().observable()
        val smallestPolynomialDegreeProperty = SimpleIntegerProperty(1)

        val tableView: TableView<GUIOutputModelForDifferentialEquations<Double>> = TableView<GUIOutputModelForDifferentialEquations<Double>>().apply {
            minWidth = SMALL_PREF_MIN_WIDTH_FOR_TABLEVIEW
            columnResizePolicy = SmartResize.POLICY
            readonlyColumn("n/index", GUIOutputModelForDifferentialEquations<Double>::index).makeEditable()
            readonlyColumn("tn", GUIOutputModelForDifferentialEquations<Double>::tn).makeEditable()
            readonlyColumn("yn", GUIOutputModelForDifferentialEquations<Double>::yn).makeEditable()
        }

        val recomputeSmallestSquaresSeriesButton = Button("Ré-interpoler").apply {  }

        val tnAxis = NumberAxis().apply {
            label = "tn"
        }
        val ynAxis = NumberAxis().apply {
            label = "yn"
        }

        fun clearSerie(serieIndex: Int = TN_YN_SERIES) {
            val size = lineChart.data[serieIndex].data.size
            lineChart.data[serieIndex].data.remove(0,size)
        }

        fun clearAllSeries() {
            for(i in TN_YN_SERIES..SMALLEST_SQUARES_SERIES)
                clearSerie(i)
        }

        fun fillSerie(serieIndex: Int, sol: DEOutputData<Double>, degree: Int = 2) {
            val listOfXYChartData: MutableList<XYChart.Data<Number, Number>>
            val yValues: MutableList<Double>

            listOfXYChartData = when(serieIndex) {
                TN_YN_SERIES -> buildListOftXYChartData(sol.methodTracks)
                LAGRANGE_SERIES -> {
                    val lagrangeOutput = LagrangeInterpolationMethod.solve(sol.tnValues, sol.ynValues)
                    yValues = calculateFunctionValues(sol.tnValues, lagrangeOutput.finalPolynomial)
                    buildListOftXYChartData(sol.tnValues, yValues)
                }
                NEWTON_SERIES -> {
                    val newtonOutput = NewtonInterpolationMethod.solve(sol.tnValues, sol.ynValues)
                    yValues = calculateFunctionValues(sol.tnValues, newtonOutput.reducedPolynomial)
                    buildListOftXYChartData(sol.tnValues, yValues)
                }
                SMALLEST_SQUARES_SERIES -> {
                    val smallestSquaresOutput = LeastSquaresPolynomialMethod.solve(sol.tnValues, sol.ynValues, degree) // tmpSol.tnValues.size - 2
                    yValues = calculateFunctionValues(sol.tnValues, smallestSquaresOutput.polynomial)
                    run {
                        // if(smallestPolynomialDegreeProperty.value != )
                        if(possibleDegrees.isNotEmpty())
                            possibleDegrees.remove(0,possibleDegrees.size)
                        smallestPolynomialDegreeProperty.value = degree
                        possibleDegrees.addAll((0..InputData.numberOfPointsToPutInInterval-2).toList())
                    }
                    buildListOftXYChartData(sol.tnValues, yValues)
                }
                else -> buildListOftXYChartData(sol.methodTracks)
            }
            clearSerie(serieIndex)
            lineChart.data[serieIndex].data.addAll(listOfXYChartData)
            println("inpterpolationMethod = ${lineChart.data[serieIndex].name}; lineChart.data[serieIndex].data.addAll(listOfXYChartData) = ${lineChart.data[serieIndex].data.toList()}\n")
        }

        fun fillAllSeries(sol: DEOutputData<Double>, degree: Int = 2) {
            for(i in (TN_YN_SERIES..SMALLEST_SQUARES_SERIES).reversed())
                fillSerie(i, sol, degree)
        }

        val lineChart: LineChart<Number, Number> = LineChart(tnAxis, ynAxis).apply {
            title = titleProperty.value
            minWidth = PREF_MIN_WIDTH_FOR_LINECHART
            minHeight = PREF_MIN_HEIGHT_FOR_LINECHART

            /*
            this.onDoubleClick {
                object: View() {
                    override val root = this@apply
                }.openModal()
            }
            */

            series("Les points (tn,yn)")
            series("Interpolé de Lagrange")
            series("Interpolé de Newton")
            series("Interpolé des moindres carrées")
        }
    }

    val methodsAndSchemesWithUI = mutableMapOf<AbstractDifferentialEquationsResolutionMethod, Map<SchemeInterface, SampleGUIOutput>>().apply {
        for(method in viewController.chapter.methodsList except AllMethods) {
            if(method is AbstractDifferentialEquationsResolutionMethod) {
                this[method] = mutableMapOf<SchemeInterface, SampleGUIOutput>().apply {
                    for(scheme in method.getSchemesAsListOfSchemeInterface().toMutableList().apply { removeIf { it.schemeName == ui.others.ALL_SCHEMES.schemeName } }) {
                        this[scheme] = SampleGUIOutput().apply {
                            ownerMethod = method
                            ownerScheme = scheme
                            recomputeSmallestSquaresSeriesButton.setOnAction {
                                commitNewValues()
                                findAndUpdateSchemeViewParts(method, scheme,false, SMALLEST_SQUARES_SERIES)
                            }
                        }
                    }
                }
            }
        }
    }

    val iterationsOutputView = hbox {
        spacing = PREF_SPACING
        for((method,mapOfSchemeToTableview) in methodsAndSchemesWithUI) {
            vbox {
                spacing = SMALL_PREF_SPACING
                label(method.methodID.methodName)
                for((scheme,guiParts) in mapOfSchemeToTableview) {
                    vbox {
                        style += CSS_BORDER_STYLE
                        // label(scheme.schemeName)
                        guiParts.titleProperty.value = scheme.schemeName
                        hbox {
                            spacing = VERY_SMALL_PREF_SPACING
                            this += guiParts.tableView
                            vbox {
                                this += guiParts.lineChart.apply { title = scheme.schemeName }
                                hbox {
                                    spacing = SMALL_PREF_SPACING
                                    label("Dégré du polynome des moindres carrées")
                                    combobox(guiParts.smallestPolynomialDegreeProperty, guiParts.possibleDegrees)
                                    this += guiParts.recomputeSmallestSquaresSeriesButton
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun commitNewValues() {
        InputData.lowerBound = InputDataProperties.lowerBoundProperty.value
        InputData.upperBound = InputDataProperties.upperBoundProperty.value
        InputData.intervalScanStep = InputDataProperties.intervalScanStepProperty.value ?: InputData.intervalScanStep
        InputData.numberOfPointsToPutInInterval = InputDataProperties.numberOfPointsToPutInIntervalProperty.value
        InputData.stringVersionOfSchemeToUse = InputDataProperties.stringVersionOfSchemeToUseProperty.value
        // InputData.leastSquaresPolynomialDegree = InputDataProperties.leastSquaresPolynomialDegreeProperty.value ?: InputData.leastSquaresPolynomialDegree
    }

    override fun applyMethodAndUpdateUI() {
        when(viewController.selectedMethod.methodID) {
            ResolutionMethod.EULER -> {
                val scheme = EulerMethod.SCHEMES.get(InputData.stringVersionOfSchemeToUse)
                if(scheme == EulerMethod.SCHEMES.ALL_SCHEMES) {
                    for(currentScheme in EulerMethod.SCHEMES.values() except scheme) {
                        findAndUpdateSchemeViewParts(EulerMethod, currentScheme)
                    }
                }
                else {
                    findAndUpdateSchemeViewParts(EulerMethod, scheme)
                }
            }
            ResolutionMethod.RUNGE_KUNTA -> {
                val scheme = RungeKuntaMethod.SCHEMES.get(InputData.stringVersionOfSchemeToUse)
                if(scheme == RungeKuntaMethod.SCHEMES.ALL_SCHEMES) {
                    for(currentScheme in RungeKuntaMethod.SCHEMES.values() except scheme) {
                        findAndUpdateSchemeViewParts(RungeKuntaMethod, currentScheme)
                    }
                }
                else {
                    findAndUpdateSchemeViewParts(RungeKuntaMethod, scheme)
                }
            }
            ResolutionMethod.ALL_METHODS -> {
                InputDataProperties.stringVersionOfSchemeToUseProperty.value = ui.others.ALL_SCHEMES.schemeName
                commitNewValues()
                for(method in viewController.chapter.methodsList except AllMethods) {
                    viewController.selectedMethod = method
                    // Ensuite on met manuellement le shcéma choisi à la ALL_METHODS pour faire qu'a chaque appel de ApplyMethodAndUpdateUI on exécute tous les schémas
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
            spacing = 30.0
            fieldset("Intervalle") {
                field("Borne inférieure") { textfield(InputDataProperties.lowerBoundProperty) { prefColumnCount } }
                field("Borne supérieure") { textfield(InputDataProperties.upperBoundProperty) { prefColumnCount } }
            }
            fieldset("Paramètres d'itération") {
                field("Pas de balayage") { textfield(InputDataProperties.intervalScanStepProperty) { prefColumnCount } }
                field("Nombre de points") { textfield(InputDataProperties.numberOfPointsToPutInIntervalProperty) { prefColumnCount } }
                field("Valeur de y0") { textfield(y0Property) }
            }
        }
        fieldset {
            field("Choisissez le schéma à utiliser"){
                combobox(property = InputDataProperties.stringVersionOfSchemeToUseProperty, values = schemesListProperty) {
                }
            }
        }
        button("Enregistrer les valeurs") {
            setOnAction {
                commitNewValues()
            }
        }
    }

    private val resultsPane = vbox {
        this += iterationsOutputView
    }

    override val root = viewController.buildMainPane(inputsPane, resultsPane)

    init {
        schemesListProperty.addListener { _, oldValue, newValue ->
            if(newValue.isNotEmpty())
                InputDataProperties.stringVersionOfSchemeToUseProperty.value = newValue.last()
            else
                schemesListProperty.value = oldValue
        }
        viewController.selectedMethodProperty.addListener { _, _, _ ->
            viewController.loadSchemesToObservableList(schemesListProperty)
        }
        viewController.applyMethodButton.setOnAction {
            applyMethodAndUpdateUI()
        }
        super.init()
    }
}
/* hbox {
    button("Voir les résultats de toutes les méthodes")
    button("Revenir vers le résultat d'une seule méthode")
} */

/*    mapOf<SchemeInterface, TableView<GUIOutputModelForDifferentialEquations<Double>> > (
            EulerMethod.SCHEMES.PROGRESSIVE_EULER_SCHEME to differentialEquationsTableview,
            EulerMethod.SCHEMES.RETROGRADE_EULER_SCHEME to differentialEquationsTableview
    )
*/
/*
    mapOf(
            RungeKuntaMethod.SCHEMES.CRANK_NICOLSON to differentialEquationsTableview,
            RungeKuntaMethod.SCHEMES.EULER_MODDED to differentialEquationsTableview,
            RungeKuntaMethod.SCHEMES.HEUN to differentialEquationsTableview,
            RungeKuntaMethod.SCHEMES.RK2_MIDDLE_POINT to differentialEquationsTableview,
            RungeKuntaMethod.SCHEMES.RK4_SIMPSON to differentialEquationsTableview
    )
*/
/*        vbox {
            spacing = VERY_SMALL_PREF_SPACING
            label(ResolutionMethod.EULER.methodName)
            for((scheme,table) in eulerSchemesMapOfTableview) {
                vbox {
                    style += CSS_BORDER_STYLE
                    label(scheme.schemeName)
                    hbox {
                        spacing = VERY_SMALL_PREF_SPACING
                        this += table
                        this += chartPlaceholder
                    }
                }
            }
        }
        // la partie qui montre les différents schémas de la méthode de Runge-Kunta
        vbox {
            spacing = VERY_SMALL_PREF_SPACING
            label(ResolutionMethod.RUNGE_KUNTA.methodName)
            for((scheme, table) in rungeKuntaSchemesMapOfTableview) {
                vbox {
                    style += CSS_BORDER_STYLE
                    label(scheme.schemeName)
                    hbox {
                        spacing = VERY_SMALL_PREF_SPACING
                        this += table
                        this += chartPlaceholder
                    }
                }
            }
        }
*/
/*
    val differentialEquationsTableview: TableView<GUIOutputModelForDifferentialEquations<Double>>
        get() = tableview {
            minWidth = 100.0
            columnResizePolicy = SmartResize.POLICY
            itemsProperty().addListener { _, _, _ ->
                this.requestResize()
            }
            readonlyColumn("n/index", GUIOutputModelForDifferentialEquations<Double>::index).makeEditable()
            readonlyColumn("tn", GUIOutputModelForDifferentialEquations<Double>::tn) {
                makeEditable()
            }
            readonlyColumn("yn", GUIOutputModelForDifferentialEquations<Double>::yn).makeEditable()
        }

    // val eulerTableview = differentialEquationsTableview
    // val rungeKuntaTableview = differentialEquationsTableview

    val eulerSchemesMapOfTableview = mutableMapOf<SchemeInterface, TableView<GUIOutputModelForDifferentialEquations<Double>>>().apply {
        val allSchemes = EulerMethod.SCHEMES.ALL_SCHEMES
        for(scheme in EulerMethod.SCHEMES.values() except allSchemes) {
            this[scheme] = differentialEquationsTableview
        }
    }
    val rungeKuntaSchemesMapOfTableview = mutableMapOf<SchemeInterface, TableView<GUIOutputModelForDifferentialEquations<Double>>>().apply {
        val allSchemes = RungeKuntaMethod.SCHEMES.ALL_SCHEMES
        for(scheme in RungeKuntaMethod.SCHEMES.values() except allSchemes) {
            this[scheme] = differentialEquationsTableview
        }
    }
*/
/*                    val tmpSol = RungeKuntaMethod.solve(InputData.lowerBound, InputData.upperBound,InputData.numberOfPointsToPutInInterval,InputData.intervalScanStep,y0Property.value, scheme.id)
                    methodsAndSchemesWithUI[RungeKuntaMethod]!![scheme]!!.tableView.items = tmpSol.methodTracks.observable()
                    val listOfXYChartData = buildListOftXYChartData<Number,Number,Double,Double>(tmpSol.tnValues, tmpSol.ynValues)
                    val size = methodsAndSchemesWithUI[RungeKuntaMethod]!![scheme]!!
                            .lineChart
                            .data.get(0).data.size

                    methodsAndSchemesWithUI[RungeKuntaMethod]!![scheme]!!.lineChart.data[0].remove(0, size)

                    methodsAndSchemesWithUI[RungeKuntaMethod]!![scheme]!!
                            .lineChart
                            .data[0]
                            .dataProperty()
                            .value = listOfXYChartData.observable()

                    // OutputDataProperties.differentialEquationsOutputDataProperty.value = tmpSol
                    // rungeKuntaTableview.items = OutputDataProperties.differentialEquationsOutputDataProperty.value.methodTracks.observable()
                    // methodsAndSchemesWithUI[RungeKuntaMethod]!![scheme]!!.lineChart.data[0].data = buildListOftXYChartData<Number,Number,Double,Double>(tmpSol.tnValues, tmpSol.ynValues).observable()
*/
/*                        val tmpSol = EulerMethod.solve(InputData.lowerBound, InputData.upperBound,InputData.numberOfPointsToPutInInterval,InputData.intervalScanStep, y0Property.value,currentScheme.id)
                        // eulerSchemesMapOfTableview[currentScheme]!!.items = tmpSol.methodTracks.observable()
                        methodsAndSchemesWithUI[EulerMethod]!![currentScheme]!!.tableView.items = tmpSol.methodTracks.observable()
                        methodsAndSchemesWithUI[EulerMethod]!![scheme]!!.lineChart.data[0].data = buildListOftXYChartData<Number,Number,Double,Double>(tmpSol.tnValues, tmpSol.ynValues).observable()
*/
/*                    val tmpSol = EulerMethod.solve(InputData.lowerBound, InputData.upperBound,InputData.numberOfPointsToPutInInterval,InputData.intervalScanStep, y0Property.value, scheme.id)
                    OutputDataProperties.differentialEquationsOutputDataProperty.value = tmpSol
                    methodsAndSchemesWithUI[EulerMethod]!![scheme]!!.tableView.items = tmpSol.methodTracks.observable()
                    methodsAndSchemesWithUI[EulerMethod]!![scheme]!!.lineChart.data[0].data = buildListOftXYChartData<Number,Number,Double,Double>(tmpSol.tnValues, tmpSol.ynValues).observable()
                    //eulerTableview.items = OutputDataProperties.differentialEquationsOutputDataProperty.value.methodTracks.observable()
*/
/*                        val tmpSol = RungeKuntaMethod.solve(InputData.lowerBound, InputData.upperBound,InputData.numberOfPointsToPutInInterval,InputData.intervalScanStep, y0Property.value,currentScheme.id)
                        // rungeKuntaSchemesMapOfTableview[currentScheme]!!.items = tmpSol.methodTracks.observable()
                        methodsAndSchemesWithUI[RungeKuntaMethod]!![scheme]!!.tableView.items = tmpSol.methodTracks.observable()
                        // methodsAndSchemesWithUI[RungeKuntaMethod]!![scheme]!!.lineChart.data
                        methodsAndSchemesWithUI[RungeKuntaMethod]!![scheme]!!.lineChart.data[0].dataProperty().value = buildListOftXYChartData<Number,Number,Double,Double>(tmpSol.tnValues, tmpSol.ynValues).observable()
*/

/*
guiOutput
        .lineChart
        .data[0]
        .dataProperty()
        .value = listOfXYChartData.observable()
*/
// guiOutput.fillSerie(TN_YN_SERIES, tmpSol, InputData.leastSquaresPolynomialDegree)
// var size: Int = 0
// lateinit var listOfXYChartData: MutableList<XYChart.Data<Number,Number>>
// lateinit var yValues: MutableList<Double>

/*
        // println("tmpSol.tnValues.size = ${tmpSol.tnValues.size}; tmpSol.ynValues.size = ${tmpSol.ynValues.size}")
        listOfXYChartData = buildListOftXYChartData(tmpSol.methodTracks)
        // println("listOfXYChartData.size = ${listOfXYChartData.size}")
        size = guiOutput.lineChart.data[TN_YN_SERIES].data.size
        guiOutput.lineChart.data[TN_YN_SERIES].data.remove(0, size)
        guiOutput.lineChart.data[TN_YN_SERIES].data.addAll(listOfXYChartData)
        // println("guiOutput.lineChart.data[0].data.size = ${guiOutput.lineChart.data[0].data.size}")


        // on extrait le polynome de Lagrange pour la calculer et placer les points correrspondant sur le graphe
        val lagrangeOutput = LagrangeInterpolationMethod.solve(tmpSol.tnValues, tmpSol.ynValues)
        yValues = calculateFunctionValues(tmpSol.tnValues, lagrangeOutput.finalPolynomial)
        listOfXYChartData = buildListOftXYChartData(tmpSol.tnValues, yValues)
        size = guiOutput.lineChart.data[LAGRANGE_SERIES].data.size
        guiOutput.lineChart.data[LAGRANGE_SERIES].data.remove(0, size)
        guiOutput.lineChart.data[LAGRANGE_SERIES].data.addAll(listOfXYChartData)


        // on extrait le polynome de Newton pour la calculer et placer les points correrspondant sur le graphe
        val newtonOutput = NewtonInterpolationMethod.solve(tmpSol.tnValues, tmpSol.ynValues)
        yValues = calculateFunctionValues(tmpSol.tnValues, newtonOutput.reducedPolynomial)
        listOfXYChartData = buildListOftXYChartData(tmpSol.tnValues, yValues)
        size = guiOutput.lineChart.data[NEWTON_SERIES].data.size
        guiOutput.lineChart.data[NEWTON_SERIES].data.remove(0, size)
        guiOutput.lineChart.data[NEWTON_SERIES].data.addAll(listOfXYChartData)

        // println("tmpSol.tnValues.size = ${tmpSol.tnValues.size}; tmpSol.ynValues.size = ${tmpSol.ynValues.size}")

        // on extrait le polynome des Moindres carrées pour la calculer et placer les points correrspondant sur le graphe
        val smallestSquaresOutput = LeastSquaresPolynomialMethod.solve(tmpSol.tnValues, tmpSol.ynValues, 2) // tmpSol.tnValues.size - 2
        yValues = calculateFunctionValues(tmpSol.tnValues, smallestSquaresOutput.polynomial)
        listOfXYChartData = buildListOftXYChartData(tmpSol.tnValues, yValues)
        size = guiOutput.lineChart.data[SMALLEST_SQUARES_SERIES].data.size
        guiOutput.lineChart.data[SMALLEST_SQUARES_SERIES].data.remove(0, size)
        guiOutput.lineChart.data[SMALLEST_SQUARES_SERIES].data.addAll(listOfXYChartData)


        // println("guiOutput.lineChart.data[0].data.size = ${guiOutput.lineChart.data[0].data.size}")

* */
