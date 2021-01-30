package abstractClasses

import classes.ResolutionMethod
import com.sun.org.apache.bcel.internal.util.ClassPath
import objects.BasicOutputData
import objects.Functions
import tornadofx.*
import utils.CARRIAGE_RETURN
import kotlin.jvm.internal.Reflection

abstract class AbstractResolutionMethod {
    abstract val methodID : ResolutionMethod

    abstract val outputData: BasicOutputData //= BasicOutputData(rank = methodID.rank)

    open fun getMethodName() : String = methodID.methodName

    override fun toString() : String = getMethodName()

    open fun reportMethodError (error: String) {
        outputData.solvedIt = false
        outputData.errorMessage += error + CARRIAGE_RETURN
        information(header = "Erreur", content = error)
    }
}