package com.mipt.calculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CalculatorState(
    val displayValue: String = "0",
    val expression: String = "",
    val pendingOperation: Operation? = null,
    val firstOperand: Double? = null,
    val isNewInput: Boolean = true,
    val justCalculated: Boolean = false,
    val history: List<HistoryEntry> = emptyList(),
    val isHistoryVisible: Boolean = false,
    val memoryValue: Double = 0.0,
    val error: String? = null
)

data class HistoryEntry(
    val expression: String,
    val result: String
)

private const val MAX_HISTORY = 50
private const val MAX_INPUT_LENGTH = 15

class CalculatorViewModel : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    // ─────────────────────────────────────────────
    //  Digit & Decimal input
    // ─────────────────────────────────────────────

    fun onDigit(digit: String) {
        _state.update { s ->
            if (s.error != null) return@update s.copy(displayValue = digit, error = null, isNewInput = false, justCalculated = false)
            if (s.isNewInput || s.justCalculated) {
                s.copy(displayValue = digit, isNewInput = false, justCalculated = false)
            } else {
                if (s.displayValue.replace("-", "").replace(".", "").length >= MAX_INPUT_LENGTH) return@update s
                val newValue = if (s.displayValue == "0") digit else s.displayValue + digit
                s.copy(displayValue = newValue)
            }
        }
    }

    fun onDecimal() {
        _state.update { s ->
            if (s.error != null) return@update s.copy(displayValue = "0.", error = null, isNewInput = false, justCalculated = false)
            if (s.isNewInput || s.justCalculated) {
                s.copy(displayValue = "0.", isNewInput = false, justCalculated = false)
            } else if (!s.displayValue.contains('.')) {
                s.copy(displayValue = s.displayValue + ".")
            } else s
        }
    }

    // ─────────────────────────────────────────────
    //  Operations
    // ─────────────────────────────────────────────

    fun onOperation(op: Operation) {
        _state.update { s ->
            val current = s.displayValue.toDoubleOrNull() ?: return@update s
            // If there's already a pending op and new input, calculate first
            if (s.firstOperand != null && s.pendingOperation != null && !s.isNewInput && !s.justCalculated) {
                val result = CalculatorEngine.applyOperation(s.firstOperand, current, s.pendingOperation)
                result.fold(
                    onSuccess = { value ->
                        val formatted = CalculatorEngine.formatResult(value)
                        s.copy(
                            displayValue = formatted,
                            expression = "${s.expression} ${s.displayValue} ${op.symbol}",
                            firstOperand = value,
                            pendingOperation = op,
                            isNewInput = true,
                            justCalculated = false,
                            error = null
                        )
                    },
                    onFailure = { e ->
                        s.copy(error = e.message, expression = "", firstOperand = null, pendingOperation = null, isNewInput = true)
                    }
                )
            } else {
                val expr = if (s.justCalculated) "${s.displayValue} ${op.symbol}"
                           else "${s.expression.ifEmpty { s.displayValue }} ${op.symbol}"
                s.copy(
                    expression = expr,
                    firstOperand = current,
                    pendingOperation = op,
                    isNewInput = true,
                    justCalculated = false,
                    error = null
                )
            }
        }
    }

    fun onEquals() {
        _state.update { s ->
            val second = s.displayValue.toDoubleOrNull() ?: return@update s
            val first = s.firstOperand ?: return@update s
            val op = s.pendingOperation ?: return@update s

            val expression = "${s.expression} ${s.displayValue} ="

            val result = CalculatorEngine.applyOperation(first, second, op)
            result.fold(
                onSuccess = { value ->
                    val formatted = CalculatorEngine.formatResult(value)
                    val entry = HistoryEntry(expression, formatted)
                    val newHistory = (listOf(entry) + s.history).take(MAX_HISTORY)
                    s.copy(
                        displayValue = formatted,
                        expression = expression,
                        firstOperand = null,
                        pendingOperation = null,
                        isNewInput = true,
                        justCalculated = true,
                        history = newHistory,
                        error = null
                    )
                },
                onFailure = { e ->
                    s.copy(error = e.message, expression = "", firstOperand = null, pendingOperation = null, isNewInput = true, justCalculated = false)
                }
            )
        }
    }

    // ─────────────────────────────────────────────
    //  Unary operations
    // ─────────────────────────────────────────────

    fun onSquareRoot() {
        _state.update { s ->
            val value = s.displayValue.toDoubleOrNull() ?: return@update s
            val result = CalculatorEngine.squareRoot(value)
            result.fold(
                onSuccess = { v ->
                    val formatted = CalculatorEngine.formatResult(v)
                    val expr = "√(${s.displayValue})"
                    val entry = HistoryEntry("$expr =", formatted)
                    val newHistory = (listOf(entry) + s.history).take(MAX_HISTORY)
                    s.copy(displayValue = formatted, expression = expr, isNewInput = true, justCalculated = true, history = newHistory, error = null)
                },
                onFailure = { e -> s.copy(error = e.message) }
            )
        }
    }

    fun onSquare() {
        _state.update { s ->
            val value = s.displayValue.toDoubleOrNull() ?: return@update s
            val result = CalculatorEngine.square(value)
            result.fold(
                onSuccess = { v ->
                    val formatted = CalculatorEngine.formatResult(v)
                    val expr = "sqr(${s.displayValue})"
                    val entry = HistoryEntry("$expr =", formatted)
                    val newHistory = (listOf(entry) + s.history).take(MAX_HISTORY)
                    s.copy(displayValue = formatted, expression = expr, isNewInput = true, justCalculated = true, history = newHistory, error = null)
                },
                onFailure = { e -> s.copy(error = e.message) }
            )
        }
    }

    fun onReciprocal() {
        _state.update { s ->
            val value = s.displayValue.toDoubleOrNull() ?: return@update s
            val result = CalculatorEngine.reciprocal(value)
            result.fold(
                onSuccess = { v ->
                    val formatted = CalculatorEngine.formatResult(v)
                    val expr = "1/(${s.displayValue})"
                    val entry = HistoryEntry("$expr =", formatted)
                    val newHistory = (listOf(entry) + s.history).take(MAX_HISTORY)
                    s.copy(displayValue = formatted, expression = expr, isNewInput = true, justCalculated = true, history = newHistory, error = null)
                },
                onFailure = { e -> s.copy(error = e.message) }
            )
        }
    }

    fun onPercent() {
        _state.update { s ->
            val value = s.displayValue.toDoubleOrNull() ?: return@update s
            val result = CalculatorEngine.percent(value, s.firstOperand, s.pendingOperation)
            val formatted = CalculatorEngine.formatResult(result)
            s.copy(displayValue = formatted, isNewInput = true, justCalculated = false, error = null)
        }
    }

    fun onToggleSign() {
        _state.update { s ->
            val value = s.displayValue.toDoubleOrNull() ?: return@update s
            val negated = CalculatorEngine.formatResult(-value)
            s.copy(displayValue = negated)
        }
    }

    // ─────────────────────────────────────────────
    //  Clear / Backspace
    // ─────────────────────────────────────────────

    fun onClear() {
        _state.update { it.copy(
            displayValue = "0",
            expression = "",
            firstOperand = null,
            pendingOperation = null,
            isNewInput = true,
            justCalculated = false,
            error = null
        )}
    }

    fun onClearEntry() {
        _state.update { it.copy(displayValue = "0", isNewInput = true, error = null) }
    }

    fun onBackspace() {
        _state.update { s ->
            if (s.isNewInput || s.justCalculated || s.error != null) return@update s
            val newDisplay = if (s.displayValue.length <= 1 || (s.displayValue.length == 2 && s.displayValue.startsWith("-"))) "0"
                             else s.displayValue.dropLast(1)
            s.copy(displayValue = newDisplay)
        }
    }

    // ─────────────────────────────────────────────
    //  History
    // ─────────────────────────────────────────────

    fun onToggleHistory() {
        _state.update { it.copy(isHistoryVisible = !it.isHistoryVisible) }
    }

    fun onClearHistory() {
        _state.update { it.copy(history = emptyList()) }
    }

    fun onHistoryEntrySelected(entry: HistoryEntry) {
        _state.update { it.copy(
            displayValue = entry.result,
            isNewInput = true,
            justCalculated = true,
            isHistoryVisible = false
        )}
    }

    // ─────────────────────────────────────────────
    //  Memory
    // ─────────────────────────────────────────────

    fun onMemoryClear() = _state.update { it.copy(memoryValue = 0.0) }

    fun onMemoryRecall() {
        _state.update { s ->
            s.copy(displayValue = CalculatorEngine.formatResult(s.memoryValue), isNewInput = true, justCalculated = true)
        }
    }

    fun onMemoryAdd() {
        _state.update { s ->
            val current = s.displayValue.toDoubleOrNull() ?: return@update s
            s.copy(memoryValue = s.memoryValue + current)
        }
    }

    fun onMemorySubtract() {
        _state.update { s ->
            val current = s.displayValue.toDoubleOrNull() ?: return@update s
            s.copy(memoryValue = s.memoryValue - current)
        }
    }

    fun onMemoryStore() {
        _state.update { s ->
            val current = s.displayValue.toDoubleOrNull() ?: return@update s
            s.copy(memoryValue = current)
        }
    }
}
