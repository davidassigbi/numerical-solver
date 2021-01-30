import classes.Chapter
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import objects.InputData
import tornadofx.*
import ui.controllers.ChapterViewController
import ui.dataSource.InputDataProperties
import ui.dataSource.OutputDataProperties
import ui.others.UsualStringConverters
import ui.others.WillApplyMethodAndUpdateUI
import utils.PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA
import utils.PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA
import utils.PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA
import utils.linearInterpolation.leastSquares.LeastSquaresPolynomialMethod
import utils.linearInterpolation.polynomialApproximation.LagrangeInterpolationMethod
import utils.linearInterpolation.polynomialApproximation.NewtonInterpolationMethod

class TesingUI: View("Test UI") {
    val inputs = FXCollections.observableArrayList<SimpleDoubleProperty>()//listOf<SimpleDoubleProperty>().observable()
    val number = SimpleIntegerProperty(0)

    override val root = borderpane {
        number.addListener { _, _, newValue ->
            inputs.remove(0,inputs.size -1 )
            inputs.remove(0,0)
            for(i in 0 until newValue.toInt() - 1 )
                inputs.add(SimpleDoubleProperty(.0))
        }
        top {
            form {
                fieldset {
                    field("Nombre d'inputs à générer") {
                        textfield(number) { prefColumnCount = 10 }
                    }
                }
                button("Générer") {
                    setOnAction {
                        //for(input in inputs)
                        inputs.remove(0, inputs.size - 1)
                        for (i in 0 until number.value - 1)
                            inputs.add(SimpleDoubleProperty(.0))
                    }
                }
            }
        }
        center {
            form {
                flowpane {
                    bindChildren(inputs) {
                        textfield(it) { prefColumnCount = 2 }
                        // textarea(it) {  }
                    }
                }
            }
        }
    }
}

