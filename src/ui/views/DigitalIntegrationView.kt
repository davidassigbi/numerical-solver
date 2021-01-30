package ui.views

import classes.Chapter
import classes.ResolutionMethod
import extensions.mutablesList.except
import javafx.application.Platform
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableView
import objects.GUIOutputModelForNewtonCotesMethod
import objects.InputData
import tornadofx.*
import ui.controllers.ChapterViewController
import ui.dataSource.InputDataProperties
import ui.others.SchemeInterface
import ui.others.WillApplyMethodAndUpdateUI
import utils.*
import utils.digitalIntegration.NewtonCotesMethod
import utils.digitalIntegration.SimplistIntegrationMethod

class DigitalIntegrationView: View(), WillApplyMethodAndUpdateUI {
    private val viewController = ChapterViewController(Chapter.DigitalIntegration)
    val schemesListProperty = SimpleListProperty<String>(SimplistIntegrationMethod.getSchemesAsStrings().observable())
    // val resultOfDigitalIntegrationProperty = SimpleDoubleProperty(0.0)

    val newtonCotesSampleTableview
        get() = tableview<GUIOutputModelForNewtonCotesMethod<Double>> {
            columnResizePolicy = SmartResize.POLICY
            minWidth = PREF_MIN_WIDTH_FOR_TABLEVIEW
            readonlyColumn("n", GUIOutputModelForNewtonCotesMethod<Double>::n).makeEditable()
            readonlyColumn("wi", GUIOutputModelForNewtonCotesMethod<Double>::wi).makeEditable()
            readonlyColumn("ksi", GUIOutputModelForNewtonCotesMethod<Double>::ksi).makeEditable()
            readonlyColumn("ti", GUIOutputModelForNewtonCotesMethod<Double>::ti).makeEditable()
        }

    val newtonCotesSchemesMapOfTableview = mutableMapOf<SchemeInterface, TableView<GUIOutputModelForNewtonCotesMethod<Double>>>().apply {
        val allSchemes = NewtonCotesMethod.SCHEMES.ALL_SCHEMES
        for(scheme in NewtonCotesMethod.SCHEMES.values() except allSchemes)
            this[scheme] = newtonCotesSampleTableview
    }
    val newtonCotesSchemesMapOfProperties = mutableMapOf<SchemeInterface, SimpleStringProperty>().apply {
        val allSchemes = NewtonCotesMethod.SCHEMES.ALL_SCHEMES
        for(scheme in NewtonCotesMethod.SCHEMES.values() except allSchemes)
            this[scheme] = SimpleStringProperty()
    }

    val simplistSchemesMapOfProperties = mutableMapOf<SchemeInterface, SimpleStringProperty>().apply {
        val allSchemes = SimplistIntegrationMethod.SCHEMES.ALL_SCHEMES
        for(scheme in SimplistIntegrationMethod.SCHEMES.values() except allSchemes)
            this[scheme] = SimpleStringProperty()
    }

    override fun commitNewValues() {
        InputData.lowerBound = InputDataProperties.lowerBoundProperty.value ?: InputData.lowerBound
        InputData.upperBound = InputDataProperties.upperBoundProperty.value ?: InputData.upperBound
        InputData.intervalScanStep = InputDataProperties.intervalScanStepProperty.value ?: InputData.intervalScanStep
        InputData.numberOfPointsToPutInInterval = InputDataProperties.numberOfPointsToPutInIntervalProperty.value ?: InputData.numberOfPointsToPutInInterval
        InputData.stringVersionOfSchemeToUse = InputDataProperties.stringVersionOfSchemeToUseProperty.value ?: InputData.stringVersionOfSchemeToUse
    }

    override fun applyMethodAndUpdateUI() {
        when(viewController.selectedMethod.methodID) {
            ResolutionMethod.SIMPLIST_INTEGRATION_METHOD -> {
                val scheme = SimplistIntegrationMethod.SCHEMES.get(InputData.stringVersionOfSchemeToUse)
                if(scheme == SimplistIntegrationMethod.SCHEMES.ALL_SCHEMES) {
                    for(currentScheme in SimplistIntegrationMethod.SCHEMES.values() except scheme) {
                        val tmpSol = SimplistIntegrationMethod.invoke(InputData.lowerBound, InputData.upperBound, currentScheme.id)
                        simplistSchemesMapOfProperties[currentScheme]!!.value = tmpSol.result.toString()
                    }
                }
                else {
                    val tmpSol = SimplistIntegrationMethod.invoke(InputData.lowerBound, InputData.upperBound, scheme.id)
                    simplistSchemesMapOfProperties[scheme]!!.value = tmpSol.result.toString()
                }
            }
            ResolutionMethod.NEWTON_COTES -> {
                val scheme = NewtonCotesMethod.SCHEMES.get(InputData.stringVersionOfSchemeToUse)
                if(scheme == NewtonCotesMethod.SCHEMES.ALL_SCHEMES) {
                    for(currentScheme in NewtonCotesMethod.SCHEMES.values() except scheme) {
                        val tmpSol = NewtonCotesMethod.invoke(InputData.lowerBound, InputData.upperBound, currentScheme.id)
                        newtonCotesSchemesMapOfProperties[currentScheme]!!.value = tmpSol.result.toString()
                        newtonCotesSchemesMapOfTableview[currentScheme]!!.items = tmpSol.newtonCotesMethodTracks.observable()
                    }
                } else {
                    val tmpSol = NewtonCotesMethod.invoke(InputData.lowerBound, InputData.upperBound, scheme.id)
                    newtonCotesSchemesMapOfProperties[scheme]!!.value = tmpSol.result.toString()
                    newtonCotesSchemesMapOfTableview[scheme]!!.items = tmpSol.newtonCotesMethodTracks.observable()
                }
            }
            ResolutionMethod.ALL_METHODS -> {
                InputDataProperties.stringVersionOfSchemeToUseProperty.value = ui.others.ALL_SCHEMES.schemeName
                commitNewValues()
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
            fieldset("Intervalle") {
                field("Borne inférieure") { textfield(InputDataProperties.lowerBoundProperty) { prefColumnCount = PREF_COLUMN_COUNT_FOR_NUMBER_TEXTFIELD } }
                field("Borne supérieure") { textfield(InputDataProperties.upperBoundProperty) { prefColumnCount = PREF_COLUMN_COUNT_FOR_NUMBER_TEXTFIELD } }
            }
            fieldset("Paramètres d'itération") {
                field("Pas de balayage") { textfield(InputDataProperties.intervalScanStepProperty) { prefColumnCount = PREF_COLUMN_COUNT_FOR_NUMBER_TEXTFIELD } }
                field("Nombre de points") { textfield(InputDataProperties.numberOfPointsToPutInIntervalProperty) { prefColumnCount = PREF_COLUMN_COUNT_FOR_NUMBER_TEXTFIELD } }
            }
        }
        fieldset {
            field("Choisissez le schéma à utiliser"){
                combobox(property = InputDataProperties.stringVersionOfSchemeToUseProperty, values = schemesListProperty)
            }
        }
        button("Enregistrer les valeurs") {
            setOnAction {
                commitNewValues()
            }
        }
    }

    private val resultsPane = hbox {
        spacing = PREF_SPACING
        // pour les méthodes simples d'intégration numérique
        vbox {
            style += CSS_BORDER_STYLE
            form {
                fieldset(SimplistIntegrationMethod.methodID.methodName) {
                    for((scheme, prop) in simplistSchemesMapOfProperties) {
                        field(scheme.schemeName) {
                            textfield(prop)
                        }
                    }
                }
            }
        }
        // pour les schémas de Newton-Cotes
        vbox {
            spacing = PREF_SPACING
            label(NewtonCotesMethod.methodID.methodName)
            for((scheme, table) in newtonCotesSchemesMapOfTableview) {
                vbox {
                    style += CSS_BORDER_STYLE
                    spacing = VERY_SMALL_PREF_SPACING
                    label(scheme.schemeName)
                    hbox {
                        spacing = VERY_SMALL_PREF_SPACING
                        label("Résultat")
                        textfield(newtonCotesSchemesMapOfProperties[scheme]!!)
                    }
                    this += table
                }
            }
        }
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
            Platform.runLater { applyMethodAndUpdateUI() }
        }
        super.init()
    }
}