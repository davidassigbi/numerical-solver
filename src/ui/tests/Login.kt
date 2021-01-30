package ui.tests

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import tornadofx.*

class UserModel: ViewModel() {
    val usernameProperty = SimpleStringProperty("")
    val username by usernameProperty

    val passwordProperty = SimpleStringProperty("")
    val password by passwordProperty

    var validProperty = SimpleBooleanProperty(false)
    //val valid by validProperty

    fun login(){
        println("Action invoked on UserModel object.")
    }
}

class LoginForm : View("Login") {
    val user = UserModel()

    override val root =
    vbox {
        form {
            this.
            fieldset(title,labelPosition = Orientation.VERTICAL) {
                field("Username") {
                    textfield()//(user.username).required()//("").required()
                }
                field("Pasword") {
                    passwordfield()//(user.password).required()//("").required()
                }
                button("Log in") {
                    //enableWhen(user.validProperty)
                    //action { user.login() }
                }
            }
        }
    }

}
