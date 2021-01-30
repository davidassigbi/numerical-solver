package utils

import abstractClasses.AbstractResolutionMethod
import classes.ResolutionMethod
import objects.BasicOutputData

object AllMethods: AbstractResolutionMethod() {
    override val methodID: ResolutionMethod = ResolutionMethod.ALL_METHODS

    override val outputData: BasicOutputData = BasicOutputData()
}
