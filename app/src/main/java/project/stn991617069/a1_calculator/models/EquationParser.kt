package project.stn991617069.a1_calculator.models

class EquationParser {
    var evalStack: MutableList<Pair<Double, Operation?>> = mutableListOf()

    companion object {
        fun isNumber(char: Char): Boolean {
            return char.code in 48 .. 57 || char == '.'
        }
    }

    // is in PEMDAS order. (Im gonna be lazy real quick)
    enum class Operation(val op: Char, val precedence: Int) {
        MULTIPLY('*', 0),
        DIVIDE('/', 0),
        MODULO('%', 0),
        ADD('+', 1),
        SUBTRACT('-', 1);

        companion object {
            private val map = HashMap<Char, Operation>()

            init {
                Operation.values().forEach {
                    map[it.op] = it
                }
            }

            fun from(char: Char): Operation? {
            return map[char]
            }

            fun isOperation(char: Char): Boolean {
                return from(char) != null
            }

            fun isSign(char: Char): Boolean {
                return when (from(char)) {
                    ADD, SUBTRACT -> true
                    else -> false
                }
            }
        }

        override fun toString(): String {
            return this.op.toString()
        }
    }

    private fun findOperationIndices(from: String): List<Int> {
        val indices = mutableListOf<Int>()
        var prevIndex: Int = -1

        for((i, char) in from.withIndex()) {
            // We don't care about numbers
            if (!Operation.isOperation(char)) continue

            //Check if char at 0 index or one following an operator is a valid sign
            if (i == 0 || (prevIndex >= 0 && i - prevIndex == 1))
                if (!Operation.isSign(char))
            throw IllegalArgumentException("Invalid sign")
                else {
                prevIndex = i
                continue
            }

            // From here, assume that the next operator is valid. (cant be invalid)
            indices.add(i)
            prevIndex = i
        }
        return indices
    }

    private fun prepareCalculation(equation: String) {
        if (equation.isEmpty())
            throw java.lang.IllegalArgumentException("String empty")

        evalStack = mutableListOf()

        val indices = findOperationIndices(equation)

        //probably just a number
        if (indices.isEmpty()) {
            evalStack.add(Pair(equation.toDouble(), null))
            return
        }

        var numStart = 0
        for (opIndex in indices) {
            // Pair the operator with the number on its LHS.
            val num = equation.substring(numStart  until opIndex).toDouble()
            val op = Operation.from(equation[opIndex])
            numStart = opIndex
            evalStack.add(Pair(num, op))
        }

        // Push pair with null operation when string is still not empty.
        if (numStart < equation.length) {
            val num = equation.substring(numStart + 1 until equation.length).toDouble()
            evalStack.add(Pair(num, null))
        }
    }

    fun calculate(equation: String): Double {
        prepareCalculation(equation)

        var opPrecedence = 0
        var removeOnBreak = mutableListOf<Int>()
        try {
            repeat(2) {
                evalStack.mapIndexed { i, (num, op) ->
                    if (op?.precedence == opPrecedence && (i + 1) < evalStack.size) {
                        val next = evalStack[i + 1]
                        val tem = applyOp(num, next.first, op)
                        evalStack[i + 1] = Pair(tem, next.second)
                        removeOnBreak.add(i)
                    }
                }
                removeOnBreak.reverse()
                removeOnBreak.forEach { evalStack.removeAt(it) }
                removeOnBreak = mutableListOf()
                opPrecedence++
            }
        } catch (e: Exception) {
        }
        return evalStack[0].first
    }

    private fun applyOp(f: Double, s: Double, op: Operation): Double {
        return when (op) {
            Operation.ADD -> f + s
            Operation.SUBTRACT -> f - s
            Operation.MULTIPLY -> f * s
            Operation.DIVIDE -> f / s
            else -> f % s
        }
    }
}


