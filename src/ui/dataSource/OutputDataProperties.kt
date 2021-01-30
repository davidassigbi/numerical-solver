package ui.dataSource

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import objects.*
import tornadofx.*

object OutputDataProperties {
    val newtonCotesGUIOutputDetails = SimpleListProperty<GUIOutputModelForNewtonCotesMethod<Double>>()
    val newtonCotesGUIOutputData = SimpleObjectProperty<NewtonCotesOutputData<Double>>()

    val intervalsWithOneSolutionProperty = SimpleListProperty<Double>()
    val exactSolutionsProperty = SimpleListProperty<Double>()

    val nonLinearEquationsOutputDataProperty = SimpleObjectProperty<NLEOutputData<Double>>()

    val lagrangeOutputDataProperty = SimpleObjectProperty(LagrangeOutputData<Double>())
    val newtonInterpolationOutputDataProperty = SimpleObjectProperty(NewtonInterpolationOutputData<Double>())
    val leastSquaresOutputDataProperty = SimpleObjectProperty(LeastSquaresOutputData<Double>())

    val differentialEquationsOutputDataProperty =  SimpleObjectProperty<DEOutputData<Double>>()
    val differentialEquationsGUIOutputDetails = SimpleListProperty<GUIOutputModelForDifferentialEquations<Double>>()

    val digitalIntegrationOutputDataProperty =  SimpleObjectProperty<DIOutputData<Double>>()


    init {
        nonLinearEquationsOutputDataProperty.addListener { _, _, _ ->
            intervalsWithOneSolutionProperty.value = InputData.intervalsWithOneSolution.observable()
            exactSolutionsProperty.value = InputData.exactSolutions.observable()
        }

    }
}