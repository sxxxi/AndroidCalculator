package project.stn991617069.a1_calculator.models

import android.util.Log
import kotlin.math.floor

class Calculator {
    private val ep = EquationParser()
    private var equationBuilder: StringBuilder = StringBuilder()
    private var resultBuilder: StringBuilder = StringBuilder()
    private val ism: InputStateMachine = InputStateMachine()

    val resultString: String
        get() = resultBuilder.toString()

    val equationString: String
        get() = equationBuilder.toString()

    /**
     * Input state machine for calculator inputs.
     * Create equation batch per batch.
     * [+-]//d+[+-/%\*]
     */
    class InputStateMachine {
        private var numberEntered: Boolean = false
        private var state: State = State.NUMBER_OR_SIGN

        private enum class State {
            NUMBER_OR_SIGN,
            NUMBER_OR_OPERATION,
        }

        /**
         * BAAAAAAAAAAAAAAAAAAAAAD IDEA
         * hard to debug.
         */
        fun inputValid(char: Char): Boolean {
            val isNumber = EquationParser.isNumber(char)
            val isOperation = EquationParser.Operation.isOperation(char)
            val validInput = isNumber || isOperation
            if (!validInput) return false

            val b = when (state) {
                State.NUMBER_OR_SIGN -> {
                    if (isNumber || EquationParser.Operation.isSign(char)) {
                        if (isNumber) numberEntered = true
                        state = State.NUMBER_OR_OPERATION
                        Log.d("INPUT", "[input: $char, valid: ${true}, state: ${state}, numberEntered: ${numberEntered}]")
                        return true
                    }

                    false
                }

                State.NUMBER_OR_OPERATION -> {
                    if (isOperation && numberEntered) {
                        resetStates()
                        Log.d("INPUT", "[input: $char, valid: ${true}, state: ${state}, numberEntered: ${numberEntered}]")
                        return true
                    }

                    if (isNumber) {
                        numberEntered = true
                        Log.d("INPUT", "[input: $char, valid: ${true}, state: ${state}, numberEntered: ${numberEntered}]")
                        return true
                    }

                    false
                }
            }
            Log.d("INPUT", "[input: $char, valid: ${b}, state: ${state}, numberEntered: ${numberEntered}]")
            return b
        }

        fun resetStates() {
            numberEntered = false
            state = State.NUMBER_OR_SIGN
        }
    }

    fun inputChar(input: Char) {
        val isValidInput = EquationParser.isNumber(input) || input == '='
        val isOperation = EquationParser.Operation.isOperation(input)

        if (!(isValidInput || isOperation)) return
        if (input == '=') {
            val res = ep.calculate(equationString)
            setFinalResult(resultBuilder, res)
            return
        }

        if (ism.inputValid(input)) {
            equationBuilder.append(input)
        }
    }

    fun realignState() {
        equationString.forEach {
            ism.inputValid(it)
        }
    }

    private fun setFinalResult(dest: StringBuilder, res: Double) {
        Log.d("RESULT", res.toString())
        var toAppend = if (res is Number) {
            if (res % 1 > 0)
                res.toString()
            else
                floor(res).toInt().toString()
        } else {
            res.toString()
        }

        dest.append(toAppend)
    }

    fun backspace() {
        val len = equationString.length
        if (len != 0)
            equationBuilder.delete(len - 1, len)
    }

    fun clear() {
        equationBuilder.clear()
        ism.resetStates()
    }

    fun clearResults() {
        resultBuilder.clear()
    }
}