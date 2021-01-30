package utils

import extensions.string.isNumberInputValid
import extensions.string.toTypedValue

inline fun <reified T : Number> getSingleNumberInput(
        displayMessage : String="Entrer un nombre: ",
        errorMessage : String = "Format incorrect pour la saisie !",
        interval : Pair<Double,Double> = .0 to .0,
        incomingInputString: String? = null) : T {

    lateinit var result : T
    var inputSuccessful = false

    val check : (T) -> Boolean = {value ->
        when {
            interval.first == interval.second -> true
            value.toDouble() >= interval.first && value.toDouble() <= interval.second -> true
            value.toInt() == ESCAPE_VALUE -> true
            else -> {
                println("Choix hors limite : veuillez refaire un choix compris entre [${interval.first.toInt()} ; ${interval.second.toInt()}] ou $ESCAPE_VALUE.");
                false
            }
        }
    }

    run {
        if(incomingInputString == null){
            println("$ESCAPE_VALUE) Revenir en arrière.")
            println()
        }
        while(!inputSuccessful){
            if(incomingInputString == null)
                print(displayMessage)

            val inputString = if(incomingInputString == null) readLine() else incomingInputString
            if(inputString != null){
                if (inputString.isNumberInputValid()) {
                    try {
                        result = inputString.trim().toTypedValue()
                        inputSuccessful = check(result)
                    }catch (e : Exception){
                        println(e)
                        inputSuccessful = false
                    }
                } else {
                    println("$errorMessage , valeur saisie : $inputString")
                }
            }
        }
        return result
    }
}

inline fun <reified T : Number> String.getSingleNumberInput() : T = utils.getSingleNumberInput(incomingInputString = this)

inline fun <reified T : Number> getSingleBoundedNumberInput(
        interval : Pair<Double,Double> = .0 to .0,
        errorMessage: String = "Votre saisie ne peut être correctement traitée : veuillez recommencer. ",
        displayMessage : String = "Veuillez entrer un nombre (${interval.first.toInt()} - ${interval.second.toInt()}) pour faire votre choix : ",
        incomingInputString: String? = null) : T = getSingleNumberInput(
                displayMessage = displayMessage,
                errorMessage = errorMessage,
                interval = interval,
                incomingInputString = incomingInputString)

inline fun <reified T : Number> String.getSingleBoundedNumberInput(interval : Pair<Double,Double> = .0 to .0) : T = utils.getSingleBoundedNumberInput(
        interval = interval, incomingInputString = this)


inline fun <reified T : Number> getMultipleNumberInput(
        displayMessage : String = "Enter some numbers : ",
        errorMessage : String = "Wrong input format !",
        numberOfExpectedArgs : Int = 0,
        incomingInputString: String? = null) : Array<T> {

    lateinit var result : Array<T>
    var inputSuccessful = false
    var valuesList: List<String>

    run {
        while (!inputSuccessful) {
            if(incomingInputString == null)
                print(displayMessage)
            val inputString = if(incomingInputString == null) readLine() else incomingInputString
            if (inputString != null) {
                if (inputString.isNumberInputValid()) {
                    valuesList = inputString
                            .trim()
                            .split(regex = CONSECUTIVE_SPACES_REGEX.toRegex())
                            .run {take(if(numberOfExpectedArgs==0) size else numberOfExpectedArgs)}
                    try {
                        result = Array(valuesList.size) { i -> valuesList[i].toTypedValue<T>() }
                        inputSuccessful = true
                    } catch (e: Exception) {
                        println(e.message)
                        inputSuccessful = false
                    }
                } else {
                    println("$errorMessage , valeur saisie : $inputString")
                }
            }
        }
        return result
    }
}

inline fun <reified  T : Number> String.getMultipleNumberInput(numberOfExpectedArgs: Int = 0) : Array<T> = utils.getMultipleNumberInput(
        numberOfExpectedArgs = numberOfExpectedArgs,incomingInputString = this)