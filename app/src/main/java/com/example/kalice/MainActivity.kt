package com.example.kalice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.sqrt

// Ready to 2.0

// Variable
var setAB = false
var mySetValue = false
var operandChange = false
var finish = false
var error = false
var dotMode = false
var operator = "null"
var oouControl = 1
var dotControl = 0
var a = BigDecimal("0")
var b = BigDecimal("0")
var m = BigDecimal("0")
var dot = BigDecimal("0.1")
var dotCount = 1

// Function
fun show(): String {
    var output = ""
    if (!dotMode || (dotMode && (dot < BigDecimal("0.1")))) {
        output = if (!operandChange) "$a " else "$b "
    } else if (dotMode && (dot == BigDecimal("0.1"))) {
        output = if (!operandChange) "$a. " else "$b. "
    }
    return output
}

fun execution(i: String): String {
    var step = ""
    var output = ""
    if (i == "c") {
        setAB = false
        mySetValue = false
        dotMode = false
        dot = BigDecimal("0.1")
        dotCount = 1
        if (((a > BigDecimal("0")) || (a < BigDecimal("0"))) || (operator != "pow") || (b > BigDecimal("0"))) {
            try {
                when (operator) {
                    "add" -> a += b
                    "sub" -> a -= b
                    "mul" -> a *= b
                    "div" -> a = a.divide(b, 4, RoundingMode.HALF_UP)
                    "pow" -> a = a.pow(b.toInt())
                }
                b = BigDecimal("0")
                if (!finish) {
                    step = if (a.toString().length <= 13) "a" else "e"
                } else {
                    operandChange = false
                    operator = "null"
                    step = "f"
                }
            } catch (e: java.lang.Exception) {
                // a/0 error
                step = "e"
            }
        } else {
            // 0 pow (0, -b) error
            step = "e"
        }
    }
    if ((i == "f") || (step == "f")) {
        when (oouControl) {
            // 0 -> out
            // 1 -> 4 out 5 up
            // 2 -> up
            0 -> if (!operandChange) a = a.setScale(dotControl, RoundingMode.DOWN) else b = b.setScale(dotControl, RoundingMode.DOWN)
            1 -> if (!operandChange) a = a.setScale(dotControl, RoundingMode.HALF_UP) else b = b.setScale(dotControl, RoundingMode.HALF_UP)
            2 -> if (!operandChange) a = a.setScale(dotControl, RoundingMode.UP) else b = b.setScale(dotControl, RoundingMode.UP)
        }
        if (finish) {
            step = if (a.toString().length <= 13) "a" else "e"
        }
    }
    if ((i == "e") || (step == "e")) {
        error = true
        output = "E "
    }
    if (step == "a") output = "$a "
    return output
}

fun reset() {
    setAB = true
    if (mySetValue || finish) {
        mySetValue = false
        finish = false
        if (!operandChange) a = BigDecimal("0") else b = BigDecimal("0")
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Weight
        val screen: TextView = findViewById(R.id.textViewOfScreen)
        val labelOutOrUp: TextView = findViewById(R.id.textViewOfOutOrUp)
        val labelDot: TextView = findViewById(R.id.textViewOfDot)
        val scaleOutOrUp: SeekBar = findViewById(R.id.seekBarOfOutOrUp)
        val scaleDot: SeekBar = findViewById(R.id.seekBarOfDot)
        val buttonBackSpace: Button = findViewById(R.id.buttonOfBackSpace)
        val buttonClear: Button = findViewById(R.id.buttonOfClear)
        val buttonPosOrNeg: Button = findViewById(R.id.buttonOfPosOrNeg)
        val buttonSqrt: Button = findViewById(R.id.buttonOfSqrt)
        val buttonDot: Button = findViewById(R.id.buttonOfDot)
        val buttonEqual: Button = findViewById(R.id.buttonOfEqual)
        val buttonMC: Button = findViewById(R.id.buttonOfMC)
        val buttonMR: Button = findViewById(R.id.buttonOfMR)
        val buttonMSub: Button = findViewById(R.id.buttonOfMSub)
        val buttonMAdd: Button = findViewById(R.id.buttonOfMAdd)

        // Load
        screen.text = show()
        labelOutOrUp.text = getString(R.string.string4Out5Up)
        labelDot.text = "$dotControl"

        // Weight Listener
        scaleOutOrUp.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when (progress) {
                    0 -> labelOutOrUp.text = getString(R.string.stringOut)
                    1 -> labelOutOrUp.text = getString(R.string.string4Out5Up)
                    2 -> labelOutOrUp.text = getString(R.string.stringUp)
                }
                oouControl = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // nothing
            }
        })

        scaleDot.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                labelDot.text = "$progress"
                dotControl = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // nothing
            }
        })

        buttonBackSpace.setOnClickListener {
            if (!error) {
                if (dotMode && (dotCount > 1)) {
                    dot = dot.divide(BigDecimal("0.1"))
                    dotCount -= 1
                } else if (dotMode && (dotCount == 1)) {
                    dotMode = false
                } else if (!dotMode) {
                    if (!operandChange) {
                        a = a.divide(BigDecimal("10"))
                    } else {
                        b = b.divide(BigDecimal("10"))
                    }
                }
                if (!operandChange) a = a.setScale((dotCount - 1), RoundingMode.DOWN) else b = b.setScale((dotCount - 1), RoundingMode.DOWN)
                screen.text = show()
            }
        }

        buttonClear.setOnClickListener {
            setAB = false
            mySetValue = false
            operandChange = false
            finish = false
            error = false
            dotMode = false
            operator = "null"
            a = BigDecimal("0")
            b = BigDecimal("0")
            dot = BigDecimal("0.1")
            dotCount = 1
            screen.text = show()
        }

        buttonPosOrNeg.setOnClickListener {
            if (!error) {
                if (!operandChange) {
                    if ((a > BigDecimal("0")) || (a < BigDecimal("0"))) a *= BigDecimal("-1")
                } else {
                    if ((b > BigDecimal("0")) || (b < BigDecimal("0"))) b *= BigDecimal("-1")
                }
                screen.text = show()
            }
        }

        buttonSqrt.setOnClickListener {
            if (!error) {
                mySetValue = true
                dotMode = false
                dot = BigDecimal("0.1")
                dotCount = 1
                try {
                    if (!operandChange) a = BigDecimal(sqrt(a.toDouble()).toString()) else b = BigDecimal(sqrt(b.toDouble()).toString())
                    execution("f")
                    screen.text = show()
                } catch (e: java.lang.Exception) {
                    // sqrt(a) or sqrt(b) error
                    screen.text = execution("e")
                }
            }
        }

        buttonDot.setOnClickListener {
            if (!error) {
                if ((!operandChange && (a.toString().length < 12)) || (operandChange && (b.toString().length < 12))) {
                    reset()
                    dotMode = true
                    screen.text = show()
                }
            }
        }

        buttonEqual.setOnClickListener {
            if (!error) {
                finish = true
                screen.text = execution("c")
            }
        }

        buttonMC.setOnClickListener {
            m = BigDecimal("0")
        }

        buttonMR.setOnClickListener {
            if (!error) {
                reset()
                mySetValue = true
                dotMode = false
                dot = BigDecimal("0.1")
                dotCount = 1
                if (!operandChange) a = m else b = m
                screen.text = show()
            }
        }

        buttonMSub.setOnClickListener {
            if (!error) {
                m -= if (!operandChange) a else b
            }
        }

        buttonMAdd.setOnClickListener {
            if (!error) {
                m += if (!operandChange) a else b
            }
        }
    }

    fun buttonNumberClick(view: View) {
        // Weight
        val screen: TextView = findViewById(R.id.textViewOfScreen)

        if (!error) {
            val input = when (view.id) {
                R.id.buttonOf1 -> BigDecimal("1")
                R.id.buttonOf2 -> BigDecimal("2")
                R.id.buttonOf3 -> BigDecimal("3")
                R.id.buttonOf4 -> BigDecimal("4")
                R.id.buttonOf5 -> BigDecimal("5")
                R.id.buttonOf6 -> BigDecimal("6")
                R.id.buttonOf7 -> BigDecimal("7")
                R.id.buttonOf8 -> BigDecimal("8")
                R.id.buttonOf9 -> BigDecimal("9")
                else -> BigDecimal("0")
            }
            reset()
            if (!dotMode && !operandChange && (a.toString().length < 13)) {
                a *= BigDecimal("10")
                a += input
            } else if (!dotMode && operandChange && (b.toString().length < 13)) {
                b *= BigDecimal("10")
                b += input
            } else if (dotMode && !operandChange && (a.toString().length < 13)) {
                a += (input * dot)
                dot *= BigDecimal("0.1")
                dotCount += 1
            } else if (dotMode && operandChange && (b.toString().length < 13)) {
                b += (input * dot)
                dot *= BigDecimal("0.1")
                dotCount += 1
            }
            screen.text = show()
        }
    }

    fun buttonOperatorClick(view: View) {
        // Weight
        val screen: TextView = findViewById(R.id.textViewOfScreen)

        if (!error) {
            val input = when (view.id) {
                R.id.buttonOfAdd -> "add"
                R.id.buttonOfSub -> "sub"
                R.id.buttonOfMul -> "mul"
                R.id.buttonOfDiv -> "div"
                R.id.buttonOfPow -> "pow"
                else -> "null"
            }
            finish = false
            if (!operandChange) {
                setAB = false
                mySetValue = false
                operandChange = true
                dotMode = false
                dot = BigDecimal("0.1")
                dotCount = 1
            } else if (operandChange && setAB) screen.text = execution("c")
            if (!error) operator = input
        }
    }
}