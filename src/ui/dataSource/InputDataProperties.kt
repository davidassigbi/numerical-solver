package ui.dataSource

import classes.LinearSystemOfEquations
import classes.SolutionVector
import javafx.beans.property.*
import objects.InputData
import tornadofx.*

object InputDataProperties {
    val lowerBoundProperty = SimpleDoubleProperty(InputData.lowerBound)
    val upperBoundProperty = SimpleDoubleProperty(InputData.upperBound)

    val aPointProperty = SimpleDoubleProperty(InputData.aPoint)
    val anotherPointProperty = SimpleDoubleProperty(InputData.anotherPoint)
    val startPointProperty = SimpleDoubleProperty(InputData.startPoint)

    val scanningDoneProperty = SimpleBooleanProperty(InputData.scanningDone)

    val maxNumberOfIterationsProperty = SimpleIntegerProperty(InputData.maxNumberOfIterations)

    val precisionOrToleranceProperty = SimpleDoubleProperty(InputData.precisionOrTolerance)
    val intervalScanStepProperty = SimpleDoubleProperty(InputData.intervalScanStep)

    val matrixDimensionProperty = SimpleIntegerProperty(InputData.matrixDimension)
    val linearSystemProperty = SimpleObjectProperty(InputData.linearSystem)
    val memorizedLinearSystemsProperty = SimpleObjectProperty(InputData.memorizedSystems)
    val initialSolutionVectorProperty = SimpleObjectProperty(InputData.initialSolutionVector)
    val initialSolutionListProperty = SimpleListProperty(InputData.initialSolutionList.observable())

    val xiValuesProperty = SimpleListProperty(InputData.xiValues.observable())
    val yiValuesProperty = SimpleListProperty(InputData.yiValues.observable())
    val numberOfKnownValuesProperty = SimpleIntegerProperty(InputData.numberOfKnownValuesPlusOne)
    val leastSquaresPolynomialDegreeProperty = SimpleIntegerProperty(InputData.leastSquaresPolynomialDegree)

    val numberOfPointsToPutInIntervalProperty = SimpleIntegerProperty(InputData.numberOfPointsToPutInInterval)
    val stringVersionOfSchemeToUseProperty = SimpleStringProperty("")


    /*
    init {
        lowerBoundProperty.bindObjectPropertyWithConverter(InputData::a,Number::toDouble)
        upperBoundProperty.bindObjectPropertyWithConverter(InputData::b,Number::toDouble)

        aPointProperty.bindObjectPropertyWithConverter(InputData::aPoint,Number::toDouble)
        anotherPointProperty.bindObjectPropertyWithConverter(InputData::anotherPoint,Number::toDouble)
        startPointProperty.bindObjectPropertyWithConverter(InputData::startPoint,Number::toDouble)

        scanningDoneProperty.bindObjectPropertyWithConverter(InputData::scanningDone, { this })

        maxNumberOfIterationsProperty.bindObjectPropertyWithConverter(InputData::maxNumberOfIterations,Number::toInt)
        precisionOrToleranceProperty.bindObjectPropertyWithConverter(InputData::precisionOrTolerance,Number::toDouble)
        intervalScanStepProperty.bindObjectPropertyWithConverter(InputData::intervalScanStep,Number::toDouble)

        exactSolutionsProperty.bindObjectPropertyWithConverter(InputData::exactSolutions, { this.toMutableList() })
        intervalsWithOneSolutionProperty.bindObjectPropertyWithConverter(InputData::intervalsWithOneSolution, { this.toMutableList() })

        matrixDimensionProperty.bindObjectPropertyWithConverter(InputData::matrixDimension,Number::toInt)
        //systemProperty.bindObjectPropertyWithConverter(InputData::linearSystem,Number::toDouble)
        //memorizedSystemsProperty.bindObjectPropertyWithConverter(InputData::memorizedSystems,Number::toDouble)
        //initialSolutionVectorProperty.bindObjectPropertyWithConverter(InputData::initialSolutionVector,Number::toDouble)
    }
    */
}