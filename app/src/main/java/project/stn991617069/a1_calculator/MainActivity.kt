package project.stn991617069.a1_calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import project.stn991617069.a1_calculator.models.Calculator
import project.stn991617069.a1_calculator.models.EquationParser.Operation
import project.stn991617069.a1_calculator.models.EquationParser

/**
 * TODO Implement backspace using array of indexes
 */
class MainActivity : AppCompatActivity() {
    private var realignRequired = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calcBackend = Calculator()

        val inputView: EditText = findViewById(R.id.input)
        val backspace: Button = findViewById(R.id.backspace)
        val clickableOp: Button = findViewById(R.id.selectedOp)
        val clear: Button = findViewById(R.id.clear)
        val equals: Button = findViewById(R.id.equals)
        val decimalPoint: Button = findViewById(R.id.decPoint)
        val numButtons: Array<Button> = arrayOf(
            findViewById(R.id.num0),
            findViewById(R.id.num1),
            findViewById(R.id.num2),
            findViewById(R.id.num3),
            findViewById(R.id.num4),
            findViewById(R.id.num5),
            findViewById(R.id.num6),
            findViewById(R.id.num7),
            findViewById(R.id.num8),
            findViewById(R.id.num9),
        )

        numButtons.forEach { button ->
            button.setOnClickListener {
                getChar(button.text[0], calcBackend)
                updateInputView(inputView, calcBackend)
            }
        }

        decimalPoint.setOnClickListener {
            getChar(decimalPoint.text[0], calcBackend)
            updateInputView(inputView, calcBackend)
        }

        equals.setOnClickListener {
            calcBackend.clearResults()
            getChar(equals.text[0], calcBackend)
            inputView.setText(calcBackend.resultString)
            calcBackend.clear()
        }

        clickableOp.setOnClickListener {
            getChar(clickableOp.text[0], calcBackend)
            updateInputView(inputView, calcBackend)
        }

        clear.setOnClickListener {
            calcBackend.clear()
            inputView.setText(calcBackend.equationString)
        }

        backspace.setOnClickListener {
            backspace(inputView, calcBackend)
        }

        val operations = Operation.values()
        val opSpinner = findViewById<Spinner>(R.id.operation_spinner)
        val opSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, operations)

        opSpinner.adapter = opSpinnerAdapter
        opSpinner.onItemSelectedListener = object: OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                clickableOp.text = operations[position].op.toString()
                getChar(clickableOp.text[0], calcBackend)
                updateInputView(inputView, calcBackend)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                getChar(clickableOp.text[0], calcBackend)
                updateInputView(inputView, calcBackend)
            }
        }
    }

    private fun getChar(char: Char, calcBackend: Calculator) {
        if (realignRequired) {
            calcBackend.realignState()
            realignRequired = false
        }

        try {
            calcBackend.inputChar(char)
        } catch (e: Exception) {
            return
        }
    }

    private fun backspace(view: EditText, calcBackend: Calculator) {
        calcBackend.backspace()
        view.setText(calcBackend.equationString)
        realignRequired = true
    }

    private fun updateInputView(view: EditText, calcBackend: Calculator) {
        view.setText(calcBackend.equationString)
    }
}