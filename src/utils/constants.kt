package utils

const val CARRIAGE_RETURN = "\n"
const val THREE_TABS = "\t\t\t"
const val SINGLE_TAB = "\t"
const val CONSECUTIVE_SPACES_REGEX = "\\s+"
const val ESCAPE_VALUE = 99
const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" //"abcdefghijklmnopqrstuvwxyz"

const val LEFT_TOP_BAR = 218.toChar() // ┌
const val LEFT_BOTTOM_BAR = 192.toChar() // └
const val RIGHT_TOP_BAR = 191.toChar() // ┐
const val RIGHT_BOTTOM_BAR = 218.toChar() // ┌
const val HORIZONTAL_BAR = 196.toChar() // ─
const val VERTICAL_BAR = 179.toChar() // │
const val CROSS = 197.toChar() // ┼
const val LEFT_POINTING_HORIZONTAL_BAR = 180.toChar() // ┤
const val RIGHT_POINTING_HORIZONTAL_BAR = 195.toChar() // ├
const val UP_POINTING_VERTICAL_BAR = 193.toChar() // ┴
const val DOWN_POINTING_VERTICAL_BAR = 194.toChar() // ┬
const val PLUS_OR_MINUS = 241.toChar() // ±
const val LESS_OR_EQUAL = 243.toChar() // ≤
const val SUP_OR_EQUAL = 242.toChar() // ≥
const val MOD = 240.toChar() // ≡

const val PREF_ROW_COUNT_FOR_MATRIX_TEXTAREA = 6
const val PREF_COLUMN_COUNT_FOR_MATRIX_TEXTAREA = 20
const val PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTFIELD = 20
const val PREF_COLUMN_COUNT_FOR_SOLUTION_VECTOR_TEXTAREA = 5
const val PREF_COLUMN_COUNT_FOR_NUMBER_TEXTFIELD = 10
const val PREF_SPACING = 30.0
const val SMALL_PREF_SPACING = 20.0
const val VERY_SMALL_PREF_SPACING = 10.0

const val VERY_SMALL_PREF_MIN_WIDTH_FOR_TABLEVIEW = 300.0
const val SMALL_PREF_MIN_WIDTH_FOR_TABLEVIEW = 400.0
const val PREF_MIN_WIDTH_FOR_TABLEVIEW = 600.0
const val PREF_MIN_WIDTH_FOR_LINECHART = 600.0
const val PREF_MIN_HEIGHT_FOR_LINECHART = 550.0

const val CSS_BORDER_STYLE =
        "-fx-border-color: grey;" +
//            "-fx-border-insets: -10;" +
                "-fx-border-width: 1;" +
                "-fx-border-style: solid;" // +
//            "-fx-border-radius: 0;"
//                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);"
