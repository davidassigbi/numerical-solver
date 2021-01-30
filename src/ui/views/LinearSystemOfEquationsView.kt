package ui.views

import abstractClasses.AbstractLinearSystemOfEquationsResolutionMethod
import abstractClasses.DecompositionStrategy
import abstractClasses.PivotStrategy
import classes.Chapter
import classes.EquationUnknown
import classes.ResolutionMethod
import classes.SolutionVector
import extensions.mutablesList.except
import javafx.beans.property.*
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.layout.VBox
import objects.InputData
import objects.matrix
import objects.system
import tornadofx.*
import ui.controllers.ChapterViewController
import ui.dataSource.InputDataProperties
import ui.others.UsualStringConverters
import ui.others.WillApplyMethodAndUpdateUI
import utils.*
import utils.linearSystemOfEquations.*

/* fun getTextArea(property: Property<String>): TextArea {
    return textarea(property) {
        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
    }
} */
/* {
    val extMatrixTextArea = textarea(property = extMatrixProperty) {
        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
    }
    val aMatrixTextArea = textarea(property = aMatrixProperty) {
        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
    }
    val bVectorTextArea = textarea(property = bVectorProperty, converter = UsualStringConverters.forObservableListOfDouble) {
        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA
    }
    val dMatrixTextArea = textarea(property = dMatrixProperty) {
        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
    }
    val eMatrixTextArea = textarea(property = eMatrixProperty) {
        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
    }
    val fMatrixTextArea = textarea(property = fMatrixProperty) {
        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
    }
    val choleskyLMatrixTextArea = textarea(property = choleskyLMatrixProperty) {
        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
    }
    val choleskyLtMatrixTextArea = textarea(property = choleskyLtMatrixProperty) {
        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
    }
    val croutLMatrixTextarea = textarea(property = croutLMatrixProperty) {
        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
    }
    val croutUMatrixTextarea = textarea(property = croutUMatrixProperty) {
        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
    }
    val solVectorTextArea = textarea(property = afterMethodSolVectorProperty) {
        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA
    }
    // val solVectorTextField = textfield (property = extSolVectorProperty){ }
} */

class LinearSystemOfEquationsView: View(), WillApplyMethodAndUpdateUI {
    private val viewController = ChapterViewController(Chapter.SolvingSystemOfLinearEquations)

    val pivotStrategyToUseWithGaussJordanProperty = SimpleObjectProperty<GaussJordanMethod.SCHEMES>()

    val extMatrixProperty = SimpleStringProperty("")
    val aMatrixProperty = SimpleStringProperty("")
    val bVectorProperty = SimpleListProperty<Double>()
    val dMatrixProperty = SimpleStringProperty("")
    val eMatrixProperty = SimpleStringProperty("")
    val fMatrixProperty = SimpleStringProperty("")
    val choleskyLMatrixProperty = SimpleStringProperty("")
    val choleskyLtMatrixProperty = SimpleStringProperty("")
    val croutLMatrixProperty = SimpleStringProperty("")
    val croutUMatrixProperty = SimpleStringProperty("")

    // val solVectorProperty = SimpleStringProperty("")
    // val extSolVectorProperty = SimpleStringProperty("")
    //

    val afterMethodMatrixProperty = SimpleStringProperty("")
    val afterMethodSolVectorProperty = SimpleStringProperty("")
    val afterMethodBVectorProperty = SimpleStringProperty("")
    override fun commitNewValues() {
        InputData.precisionOrTolerance = InputDataProperties.precisionOrToleranceProperty.value
        InputData.maxNumberOfIterations = InputDataProperties.maxNumberOfIterationsProperty.value

        if(extMatrixProperty.value.isNotBlank()) {
            InputData.linearSystem = system.fromString(str = extMatrixProperty.value, prefix = "[")
            aMatrixProperty.value = InputData.linearSystem.nativeMatrix.toStringWithLineBreaks(delimiter = ", ", lineBreak = " end\n")
            bVectorProperty.value = InputData.linearSystem.bVector.observable()
        } else {
            InputData.linearSystem.nativeMatrix = matrix.fromString(str = aMatrixProperty.value, delimiter = ",")
            InputData.linearSystem.bVector = bVectorProperty.value
            extMatrixProperty.value = InputData.linearSystem.toStringWithLineBreaks(prefix = "system[")
            InputData.linearSystem = system.fromString(str = extMatrixProperty.value)
        }
        if(InputDataProperties.initialSolutionListProperty.size == InputData.linearSystem.dimension)
            InputData.initialSolutionVector = SolutionVector(InputDataProperties.initialSolutionListProperty.value)
        else
            InputData.initialSolutionVector = SolutionVector(InputData.linearSystem.solutionVector.values())
    }

    val afterMethodExtEquationsProperty = SimpleStringProperty("")

    open class SampleGUIOutputForPivotStrategy(val method: AbstractLinearSystemOfEquationsResolutionMethod<Double>) {
        val matrixStrProperty = SimpleStringProperty("[]")

        val xVectorStrProperty = SimpleStringProperty()

        val solVectorStrProperty = SimpleStringProperty()

        val bVectorStrProperty = SimpleStringProperty()

        val msgProperty = SimpleStringProperty()

        val root: Parent = VBox().apply {
            form {
                style += CSS_BORDER_STYLE
                fieldset(method.methodID.methodName, labelPosition = Orientation.VERTICAL) {
                    label("Après les opération de résolution:")
                    hbox {
                        spacing = SMALL_PREF_SPACING
                        field("Matrice A") {
                            textarea(matrixStrProperty) {
                                prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                                prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
                            }
                        }
                        field("Vecteur X") {
                            textarea(xVectorStrProperty) {
                                prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                                prefColumnCount = PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA
                            }
                        }
                        field("Vecteur B") {
                            textarea(bVectorStrProperty) {
                                prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                                prefColumnCount = PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA
                            }
                        }
                    }
                    field("Vecteur solution", orientation = Orientation.HORIZONTAL) {
                        textfield(solVectorStrProperty)
                    }
                    field("Sortie", orientation = Orientation.HORIZONTAL) {
                        textarea(msgProperty) {
                            prefRowCount = 2
                        }
                    }
                }
            }
        }
    }

    class SampleGUIOutputForIterativeStrategy(val method: AbstractLinearSystemOfEquationsResolutionMethod<Double>) {
        val matrixStrProperty = SimpleStringProperty("[]")

        val iterationMatrixStrProperty = SimpleStringProperty("[]")

        val xVectorStrProperty = SimpleStringProperty()

        val solVectorStrProperty = SimpleStringProperty()

        val bVectorStrProperty = SimpleStringProperty()

        val msgProperty = SimpleStringProperty()

        val nbIterationsProperty = SimpleIntegerProperty()

        val finalPrecison = SimpleDoubleProperty()

        val itMatrixName = "Matrice d'itération J de Jacobi"

        val root: Parent = VBox().apply {
            style += CSS_BORDER_STYLE
            form {
                fieldset(method.methodID.methodName, labelPosition = Orientation.HORIZONTAL){
                    vbox {
                        spacing = SMALL_PREF_SPACING
                        field(itMatrixName, orientation = Orientation.VERTICAL) {
                            textarea(iterationMatrixStrProperty) {
                                prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                                prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
                            }
                        }
                        label("Après les opération de résolution:")
                        field("Vecteur solution") {
                            textfield(solVectorStrProperty) { prefColumnCount = PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTFIELD }
                        }
                        hbox {
                            spacing = SMALL_PREF_SPACING
                            field("Nombre d'itération effectué") {
                                textfield(nbIterationsProperty)
                            }
                            field("Précison où le calcul s'est arrêté") {
                                textfield(finalPrecison)
                            }
                        }
                        field("Sortie") {
                            textarea(msgProperty) {
                                prefRowCount = 2
                            }
                        }
                    }
                }
            }
        }
    }

    class SampleGUIOutputForDecompositionStrategy(val method: AbstractLinearSystemOfEquationsResolutionMethod<Double>) {
        val matrixStrProperty = SimpleStringProperty("[]")

        val xVectorStrProperty = SimpleStringProperty()

        val sol1VectorStrProperty = SimpleStringProperty()
        val sol2VectorStrProperty = SimpleStringProperty()

        val bVectorStrProperty = SimpleStringProperty()

        val msgProperty = SimpleStringProperty()

        val lMatrixStrProperty = SimpleStringProperty()
        val yVectorStrProperty = SimpleStringProperty()
        val rMatrixStrProperty = SimpleStringProperty()

        val firstMatrixName = ""

        val secondMatrixName = ""

        val firstSolVectorName = ""

        val secondSolVectorName = ""

        fun builMainPane(): Parent {

            return null!!
        }

        val root: Parent = VBox().apply {
            style += CSS_BORDER_STYLE
            form {
                fieldset(method.methodID.methodName) {  }
                fieldset(labelPosition  = Orientation.VERTICAL) {
                    vbox {
                        // style += CSS_BORDER_STYLE
                        label("Après les opération de résolution:")
                        hbox {
                            spacing = PREF_SPACING
                            field("Matrice L") {
                                textarea {
                                    this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                                    this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
                                }
                            }
                            field("Y") {
                                textarea {
                                    this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                                    this.prefColumnCount = PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA
                                }
                            }
                            field("B") {
                                textarea {
                                    this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                                    this.prefColumnCount = PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA
                                }
                            }
                        }
                    }
                    field("Vecteur solution Y", orientation = Orientation.HORIZONTAL) {
                        textfield(sol2VectorStrProperty)
                    }
                    vbox {
                        // style += CSS_BORDER_STYLE
                        label("Après les opération de résolution:")
                        hbox {
                            spacing = PREF_SPACING
                            field("Matrice L") {
                                textarea {
                                    this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                                    this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
                                }
                            }
                            field("X") {
                                textarea {
                                    this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                                    this.prefColumnCount = PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA
                                }
                            }
                            field("Y") {
                                textarea {
                                    this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                                    this.prefColumnCount = PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA
                                }
                            }
                        }
                    }
                    field("Vecteur solution X", orientation = Orientation.HORIZONTAL) {
                        textfield(sol2VectorStrProperty)
                    }
                    field("Sortie") {
                        textarea(msgProperty) {
                            prefRowCount = 2
                        }
                    }
                }
            }
        }
    }

    override fun applyMethodAndUpdateUI() {
        val DEF = InputData.linearSystem.nativeMatrix.getDEFDecomposition()
        dMatrixProperty.value = DEF.first.toStringWithLineBreaks(delimiter = ", ", lineBreak = " end\n")
        eMatrixProperty.value = DEF.second.toStringWithLineBreaks(delimiter = ", ", lineBreak = " end\n")
        fMatrixProperty.value = DEF.third.toStringWithLineBreaks(delimiter = ", ", lineBreak = " end\n")
        InputData.linearSystem.backupSystemOriginalState()

        when(viewController.selectedMethod.methodID) {
            DiagonalMatrixMethod.methodID -> {
                DiagonalMatrixMethod.invoke(system = InputData.linearSystem)
            }
            AscentMethod.methodID -> {
                AscentMethod.invoke(system = InputData.linearSystem)
            }
            RollbackMethod.methodID -> {
                RollbackMethod.invoke(system = InputData.linearSystem)
            }
            CroutMethod.methodID -> {
                try {
                    val LU = InputData.linearSystem.nativeMatrix.getCroutLRDecomposition()
                    if(LU != null) {
                        croutLMatrixProperty.value = LU.first.toStringWithLineBreaks(delimiter = ", ", lineBreak = " end\n")
                        croutUMatrixProperty.value = LU.second.toStringWithLineBreaks(delimiter = ", ", lineBreak = " end\n")
                    } else {
                        information(header = "Message d'erreur", content ="Impossible de décomposser la matrice A sous la forme A = L x Lt")
                    }
                    CroutMethod.invoke(system = InputData.linearSystem)
                } catch(e: Exception) {
                    information(header = "Message", content = e.message)
                }
            }
            CholeskyMethod.methodID -> {
                try {
                    val LLt = InputData.linearSystem.nativeMatrix.getCholeskyLLTDecomposition()
                    if(LLt != null) {
                        choleskyLMatrixProperty.value = LLt.first.toStringWithLineBreaks(delimiter = ", $SINGLE_TAB", lineBreak = " $SINGLE_TAB end\n")
                        choleskyLtMatrixProperty.value = LLt.second.toStringWithLineBreaks(delimiter = ", $SINGLE_TAB", lineBreak = "$SINGLE_TAB end\n")
                    } else {
                        information(header = "Message d'erreur", content ="Impossible de décomposser la matrice A sous la forme A = L x Lt")
                    }
                    CholeskyMethod.invoke(system = InputData.linearSystem)
                } catch(e: Exception ) {
                    information(header = "Message", content = e.message)
                }
            }
            GaussSeidelMethod.methodID -> {
                GaussSeidelMethod.invoke(system = InputData.linearSystem)
            }
            GaussWithoutPivotStrategy.methodID -> {
                GaussWithoutPivotStrategy.invoke(system = InputData.linearSystem)
            }
            GaussWithPartialPivotStrategy.methodID -> {
                GaussWithPartialPivotStrategy.invoke(system = InputData.linearSystem)
            }
            GaussWithTotalPivotStrategy.methodID -> {
                GaussWithTotalPivotStrategy.invoke(system = InputData.linearSystem)
            }
            GaussJordanMethod.methodID -> {
                GaussJordanMethod.pivotStrategy = pivotStrategyToUseWithGaussJordanProperty.value.id
                GaussJordanMethod.invoke(system = InputData.linearSystem)
            }
            JacobiMethod.methodID -> {
                JacobiMethod.invoke(system = InputData.linearSystem)
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

        afterMethodExtEquationsProperty.value = InputData.linearSystem.toLinearSystemOfEquations()
        afterMethodMatrixProperty.value = InputData.linearSystem.nativeMatrix.toStringWithLineBreaks(delimiter = ", ", lineBreak = " end\n")
        afterMethodBVectorProperty.value  = InputData.linearSystem.bVector.joinToString(" \n")
        afterMethodSolVectorProperty.value = InputData.linearSystem.solutionVector.toStringWithLineBreaks(unknownsOrValuesOrBoth = SolutionVector.UNKNOWNS_AND_VALUES)
        InputData.linearSystem.restoreSystemOriginalState()
    }

    private val inputsPane = form {
        hbox {
            fieldset("Système d'équations", labelPosition = Orientation.VERTICAL) {
                hbox {
                    this.spacing = PREF_SPACING
                    field("Matrice étendue") {
                        textarea(property = extMatrixProperty) {
                            this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                            this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
                        }
                    }
                    field("Matrice A") {
                        textarea(property = aMatrixProperty) {
                            this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                            this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
                        }
                    }
                    field("Vecteur B") {
                        textarea(property = bVectorProperty, converter = UsualStringConverters.forObservableListOfDoubleToMultilineString) {
                            this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                            this.prefColumnCount = PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA
                        }
                    }
                }
            }
            fieldset("Paramètres d'itérations", labelPosition = Orientation.HORIZONTAL) {
                field("Vecteur solution initial") {
                    textfield(InputDataProperties.initialSolutionListProperty, converter = UsualStringConverters.forObservableListOfDouble) {  }
                }
                field("Précision ou tolérance") {
                    textfield(InputDataProperties.precisionOrToleranceProperty) {  }
                }
                field("Nombre maximal d'itérations") {
                    textfield(InputDataProperties.maxNumberOfIterationsProperty) {  }
                }
                field("Stratégie de pivot à utilliser avec la \n méthode de Gauss-Jordan") {
                    combobox(property = pivotStrategyToUseWithGaussJordanProperty, values = GaussJordanMethod.SCHEMES.values().asList())
                }
            }
        }
        button("Enregistrer les valeurs") {
            setOnAction {
                commitNewValues()
            }
        }
    }

    private val resultsPane = form {
        spacing = PREF_SPACING
        fieldset(labelPosition = Orientation.VERTICAL) {
            hbox {
                spacing = PREF_SPACING
                field("Matrice D"){
                    textarea(property = dMatrixProperty) {
                        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
                    }
                }
                field("Matrice E") {
                    textarea(property = eMatrixProperty) {
                        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
                    }
                }
                field("Matrice F") {
                    textarea(property = fMatrixProperty) {
                        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
                    }
                }
            }
        }
        fieldset(labelPosition = Orientation.VERTICAL) {
            hbox {
                spacing = PREF_SPACING
                field("Matrice L") {
                    textarea(property = choleskyLMatrixProperty) {
                        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
                    }
                }
                field("Matrice Lt") {
                    textarea(property = choleskyLtMatrixProperty) {
                        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
                    }
                }
            }
        }
        fieldset(labelPosition = Orientation.VERTICAL) {
            hbox {
                spacing = PREF_SPACING
                field("Matrice L") {
                    textarea(property = croutLMatrixProperty) {
                        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
                    }
                }
                field("Matrice U") {
                    textarea(property = croutUMatrixProperty) {
                        this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                        this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
                    }
                }
            }
        }
        fieldset("Après l'application de la méthode") {
            hbox {
                spacing = PREF_SPACING
                textarea(afterMethodMatrixProperty) {
                    this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                    this.prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
                }
                label("X")
                textarea(afterMethodSolVectorProperty) {
                    this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                    this.prefColumnCount = PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA
                }
                label(" = ")
                textarea(afterMethodBVectorProperty) {
                    this.prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                    this.prefColumnCount = PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA
                }
            }
        }
        // essaie d'une nouvelle maniere de faire

        for(method in Chapter.SolvingSystemOfLinearEquations.methodsList) {
            when (method) {
                is PivotStrategy<*>, RollbackMethod, AscentMethod, DiagonalMatrixMethod -> this += SampleGUIOutputForPivotStrategy(method as AbstractLinearSystemOfEquationsResolutionMethod<Double>).root
                is DecompositionStrategy<*> -> this += SampleGUIOutputForDecompositionStrategy(method as AbstractLinearSystemOfEquationsResolutionMethod<Double>).root
                is AllMethods -> {}
                else -> this += SampleGUIOutputForIterativeStrategy(method as AbstractLinearSystemOfEquationsResolutionMethod<Double>).root
            }
        }

        // startegie de pivot
        /*
        fieldset(GaussWithoutPivotStrategy.getMethodName(), labelPosition = Orientation.VERTICAL) {
            field("Après la resolution du système") {
                textarea() {
                    prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                    prefColumnCount = PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
                }
                textarea() {
                    prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                    prefColumnCount = PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA
                }
                textarea() {
                    prefRowCount = PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
                    prefColumnCount = PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA
                }
            }
            field("X", Orientation.HORIZONTAL) {
                textfield() {  }
            }
            field("Information", Orientation.HORIZONTAL) {
                textfield() {  }
            }
        }
        fieldset(GaussWithPartialPivotStrategy.getMethodName()) {
        }
        fieldset(GaussWithTotalPivotStrategy.getMethodName()) {
        }
        fieldset(GaussJordanMethod.getMethodName()) {
        }
        // iteration
        fieldset(GaussSeidelMethod.getMethodName()) {
        }
        fieldset(JacobiMethod.getMethodName()) {
        }
        // decomposition
        fieldset(GaussSeidelMethod.getMethodName()) {
        }
        fieldset(JacobiMethod.getMethodName()) {
        }
        */
    }

    override val root = viewController.buildMainPane(inputsPane, resultsPane)

    init {
        InputDataProperties.initialSolutionListProperty.addListener { _, _, newValue ->
            println("New value of initialSolutionVectorProperty = $newValue, and value is = ${InputDataProperties.initialSolutionListProperty.value}")
        }
        viewController.applyMethodButton.setOnAction {
            // commitNewValues()
            applyMethodAndUpdateUI()
        }
        super.init()
    }
}