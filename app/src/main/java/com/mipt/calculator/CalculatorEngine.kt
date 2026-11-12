package com.mipt.calculator

import kotlin.math.sqrt

/**
 * Calculator engine built from scratch — no 3rd party math libraries.
 * Handles: +, -, ×, ÷, √x, x², %, 1/x
 */
object CalculatorEngine {

    /**
     * Applies a binary operation and returns the result.
     * Returns null if the operation is invalid (e.g. division by zero).
     */
    fun applyOperation(operand1: Double, operand2: Double, operation: Operation): Result<Double> {
        return when (operation) {
            Operation.ADD      -> Result.success(operand1 + operand2)
            Operation.SUBTRACT -> Result.success(operand1 - operand2)
            Operation.MULTIPLY -> Result.success(operand1 * operand2)
            Operation.DIVIDE   -> {
                if (operand2 == 0.0) Result.failure(ArithmeticException("Division by zero"))
                else Result.success(operand1 / operand2)
            }
        }
    }

    /** Calculates √x. Returns error if x < 0. */
    fun squareRoot(value: Double): Result<Double> {
        return if (value < 0) Result.failure(ArithmeticException("Invalid input"))
        else Result.success(sqrt(value))
    }

    /** Calculates x². */
    fun square(value: Double): Result<Double> =
        Result.success(value * value)

    /** Calculates 1/x. Returns error if x == 0. */
    fun reciprocal(value: Double): Result<Double> {
        return if (value == 0.0) Result.failure(ArithmeticException("Division by zero"))
        else Result.success(1.0 / value)
    }

    /**
     * Calculates percentage.
     * If there is a pending binary operation: operand1 * (operand2 / 100)
     * Otherwise: value / 100
     */
    fun percent(value: Double, pendingOperand: Double? = null, pendingOp: Operation? = null): Double {
        return if (pendingOperand != null && pendingOp != null) {
            pendingOperand * (value / 100.0)
        } else {
            value / 100.0
        }
    }

    /**
     * Formats a Double for display: removes trailing zeros after decimal point,
     * and uses integer representation when the value is whole.
     */
    fun formatResult(value: Double): String {
        return if (value.isNaN()) "Not a number"
        else if (value.isInfinite()) if (value > 0) "Infinity" else "-Infinity"
        else {
            // Limit to 10 significant digits to avoid floating-point noise
            val formatted = "%.10g".format(value)
            // Remove trailing zeros
            val cleaned = formatted.trimEnd('0').trimEnd('.')
            // Handle edge cases like "1e+10"
            cleaned.ifEmpty { "0" }
        }
    }
}

enum class Operation(val symbol: String) {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("×"),
    DIVIDE("÷")
}
