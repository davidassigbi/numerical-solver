package navigation

import abstractClasses.AbstractResolutionMethod
import classes.Chapter
import utils.ESCAPE_VALUE
import utils.getSingleBoundedNumberInput

fun chooseChapter() : Chapter? {
    println("Choisissez le chapitre dont vous voulez tester les programmes.")
    Chapter.forEach { chapter -> println("${chapter.rank}) ${chapter.title}") }
    val chapterChoice: Int = getSingleBoundedNumberInput(2.0 to Chapter.getAll().count().toDouble() + 1)
    return if(chapterChoice != ESCAPE_VALUE )
        Chapter[chapterChoice] else null
}

fun chooseMethod(chapterRank : Int = Chapter.SolvingNonLinearEquations.rank) : AbstractResolutionMethod {
    return chooseMethod(Chapter[chapterRank])
}

fun chooseMethod(chapter : Chapter = Chapter.SolvingNonLinearEquations) : AbstractResolutionMethod {
    println("Choisissez une méthode à tester au chapitre N°${chapter.rank} - ${chapter.title}: ")
    println(chapter.chapterDetails)
    val methodChoice : Int = getSingleBoundedNumberInput(1.0 to chapter.methodsList.count().toDouble())
    return chapter.methodsList[methodChoice - 1]
}