package com.mipt.calculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mipt.calculator.CalculatorViewModel
import com.mipt.calculator.Operation
import com.mipt.calculator.R
import com.mipt.calculator.HistoryEntry

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        // Pagrindinis skaičiuotuvas
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Viršutinė juosta
            CalculatorTopBar(
                onToggleHistory = viewModel::onToggleHistory
            )

            // Ekrano sritis
            CalculatorDisplay(
                expression = state.expression,
                displayValue = if (state.error != null) state.error!! else state.displayValue,
                isError = state.error != null,
                modifier = Modifier.weight(1f)
            )

            // Atminties juosta
            MemoryBar(
                memoryValue = state.memoryValue,
                onMC = viewModel::onMemoryClear,
                onMR = viewModel::onMemoryRecall,
                onMPlus = viewModel::onMemoryAdd,
                onMMinus = viewModel::onMemorySubtract,
                onMS = viewModel::onMemoryStore
            )

            // Mygtukų tinklelis
            ButtonGrid(
                activeOperation = state.pendingOperation,
                onPercent     = viewModel::onPercent,
                onCE          = viewModel::onClearEntry,
                onC           = viewModel::onClear,
                onBackspace   = viewModel::onBackspace,
                onReciprocal  = viewModel::onReciprocal,
                onSquare      = viewModel::onSquare,
                onSquareRoot  = viewModel::onSquareRoot,
                onDivide      = { viewModel.onOperation(Operation.DIVIDE) },
                onSeven       = { viewModel.onDigit("7") },
                onEight       = { viewModel.onDigit("8") },
                onNine        = { viewModel.onDigit("9") },
                onMultiply    = { viewModel.onOperation(Operation.MULTIPLY) },
                onFour        = { viewModel.onDigit("4") },
                onFive        = { viewModel.onDigit("5") },
                onSix         = { viewModel.onDigit("6") },
                onSubtract    = { viewModel.onOperation(Operation.SUBTRACT) },
                onOne         = { viewModel.onDigit("1") },
                onTwo         = { viewModel.onDigit("2") },
                onThree       = { viewModel.onDigit("3") },
                onAdd         = { viewModel.onOperation(Operation.ADD) },
                onToggleSign  = viewModel::onToggleSign,
                onZero        = { viewModel.onDigit("0") },
                onDecimal     = viewModel::onDecimal,
                onEquals      = viewModel::onEquals,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Istorijos skydelis – ModalBottomSheet (iškylantis iš apačios)
        HistoryPanel(
            isVisible = state.isHistoryVisible,
            history = state.history,
            onEntrySelected = viewModel::onHistoryEntrySelected,
            onClearHistory = viewModel::onClearHistory,
            onDismiss = viewModel::onToggleHistory
        )
    }
}

// ─────────────────────────────────────────────
//  Viršutinė juosta
// ─────────────────────────────────────────────

@Composable
private fun CalculatorTopBar(
    onToggleHistory: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hamburger meniu
        IconButton(onClick = { /* Menu - future navigation */ }) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = stringResource(R.string.menu),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        // "Standard" tekstas ir pop-out mygtukas šalia jo
        Text(
            text = stringResource(R.string.app_mode_standard),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        IconButton(
            onClick = { /* Pop-out */ },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = stringResource(R.string.pop_out),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(16.dp)
            )
        }

        // Spacer stumia istorijos mygtuką į dešinę
        Spacer(modifier = Modifier.weight(1f))

        // Istorijos mygtukas dešinėje
        IconButton(onClick = onToggleHistory) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = stringResource(R.string.history_title),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Ekranas
// ─────────────────────────────────────────────

@Composable
private fun CalculatorDisplay(
    expression: String,
    displayValue: String,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        // Išraiška (mažesnė, blankesnė)
        Text(
            text = expression,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            textAlign = TextAlign.End,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Pagrindinis rezultatas (didelis)
        val fontSize = when {
            displayValue.length > 14 -> 28.sp
            displayValue.length > 10 -> 36.sp
            else -> 48.sp
        }
        Text(
            text = displayValue,
            fontSize = fontSize,
            fontWeight = FontWeight.Light,
            color = if (isError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ─────────────────────────────────────────────
//  Atminties juosta
// ─────────────────────────────────────────────

@Composable
private fun MemoryBar(
    memoryValue: Double,
    onMC: () -> Unit,
    onMR: () -> Unit,
    onMPlus: () -> Unit,
    onMMinus: () -> Unit,
    onMS: () -> Unit
) {
    val hasMemory = memoryValue != 0.0
    val scrollState = rememberScrollState()

    val mcLabel = stringResource(R.string.mem_clear)
    val mrLabel = stringResource(R.string.mem_recall)

    val memButtons = listOf(
        Triple(mcLabel,                          onMC,       !hasMemory),
        Triple(mrLabel,                          onMR,       !hasMemory),
        Triple(stringResource(R.string.mem_add),      onMPlus,    false),
        Triple(stringResource(R.string.mem_subtract), onMMinus,   false),
        Triple(stringResource(R.string.mem_store),    onMS,       false)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .horizontalScroll(scrollState)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        memButtons.forEach { (label, action, disabled) ->
            TextButton(
                onClick = action,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                enabled = !disabled
            ) {
                Text(text = label, fontSize = 13.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Mygtukų tinklelis
// ─────────────────────────────────────────────

@Composable
private fun ButtonGrid(
    activeOperation: Operation?,
    onPercent: () -> Unit,
    onCE: () -> Unit,
    onC: () -> Unit,
    onBackspace: () -> Unit,
    onReciprocal: () -> Unit,
    onSquare: () -> Unit,
    onSquareRoot: () -> Unit,
    onDivide: () -> Unit,
    onSeven: () -> Unit,
    onEight: () -> Unit,
    onNine: () -> Unit,
    onMultiply: () -> Unit,
    onFour: () -> Unit,
    onFive: () -> Unit,
    onSix: () -> Unit,
    onSubtract: () -> Unit,
    onOne: () -> Unit,
    onTwo: () -> Unit,
    onThree: () -> Unit,
    onAdd: () -> Unit,
    onToggleSign: () -> Unit,
    onZero: () -> Unit,
    onDecimal: () -> Unit,
    onEquals: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = listOf(
        listOf(
            CalcButton(stringResource(R.string.btn_percent), ButtonType.FUNCTION, onPercent),
            CalcButton(stringResource(R.string.btn_ce), ButtonType.FUNCTION, onCE),
            CalcButton(stringResource(R.string.btn_c), ButtonType.FUNCTION, onC),
            CalcButton(stringResource(R.string.btn_backspace), ButtonType.FUNCTION, onBackspace)
        ),
        listOf(
            CalcButton(stringResource(R.string.btn_reciprocal), ButtonType.FUNCTION, onReciprocal),
            CalcButton(stringResource(R.string.btn_square), ButtonType.FUNCTION, onSquare),
            CalcButton(stringResource(R.string.btn_sqrt), ButtonType.FUNCTION, onSquareRoot),
            CalcButton(stringResource(R.string.btn_divide), ButtonType.OPERATOR, onDivide, activeOp = activeOperation == Operation.DIVIDE)
        ),
        listOf(
            CalcButton("7", ButtonType.DIGIT, onSeven),
            CalcButton("8", ButtonType.DIGIT, onEight),
            CalcButton("9", ButtonType.DIGIT, onNine),
            CalcButton(stringResource(R.string.btn_multiply), ButtonType.OPERATOR, onMultiply, activeOp = activeOperation == Operation.MULTIPLY)
        ),
        listOf(
            CalcButton("4", ButtonType.DIGIT, onFour),
            CalcButton("5", ButtonType.DIGIT, onFive),
            CalcButton("6", ButtonType.DIGIT, onSix),
            CalcButton(stringResource(R.string.btn_subtract), ButtonType.OPERATOR, onSubtract, activeOp = activeOperation == Operation.SUBTRACT)
        ),
        listOf(
            CalcButton("1", ButtonType.DIGIT, onOne),
            CalcButton("2", ButtonType.DIGIT, onTwo),
            CalcButton("3", ButtonType.DIGIT, onThree),
            CalcButton(stringResource(R.string.btn_add), ButtonType.OPERATOR, onAdd, activeOp = activeOperation == Operation.ADD)
        ),
        listOf(
            CalcButton(stringResource(R.string.btn_toggle_sign), ButtonType.DIGIT, onToggleSign),
            CalcButton("0", ButtonType.DIGIT, onZero),
            CalcButton(stringResource(R.string.btn_decimal), ButtonType.DIGIT, onDecimal),
            CalcButton(stringResource(R.string.btn_equals), ButtonType.EQUALS, onEquals)
        )
    )

    Column(modifier = modifier) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { btn ->
                    CalculatorButton(
                        btn = btn,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.1f)
                            .padding(1.dp)
                    )
                }
            }
        }
    }
}

private data class CalcButton(
    val label: String,
    val type: ButtonType,
    val onClick: () -> Unit,
    val activeOp: Boolean = false
)

private enum class ButtonType { DIGIT, FUNCTION, OPERATOR, EQUALS }

@Composable
private fun CalculatorButton(
    btn: CalcButton,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    // ── Spalvų schema (Windows skaičiuotuvo stilius) ────────────────────────
    // EQUALS   → mėlynas (Windows akcento spalva)
    // DIGIT    → balta (švari, šviesi)
    // FUNCTION → šviesiai pilka (surfaceVariant)
    // OPERATOR → vidutiniškai pilka, aktyvus → tamsiai pilka su balta tekstu
    val (containerColor, contentColor) = when {
        btn.type == ButtonType.EQUALS ->
            colors.primary to colors.onPrimary
        btn.activeOp ->
            // Aktyvus operatorius: tamsiai pilkas fonas, baltas tekstas
            Color(0xFF4A4A4A) to Color(0xFFFFFFFF)
        btn.type == ButtonType.OPERATOR ->
            // Neutralus pilkas – identiškas funkcijų mygtukams, kaip Windows skaičiuotuve
            Color(0xFFE6E6E6) to Color(0xFF1A1A1A)
        btn.type == ButtonType.FUNCTION ->
            colors.surfaceVariant to colors.onSurfaceVariant
        else ->
            // DIGIT – baltas
            colors.surface to colors.onSurface
    }

    Button(
        onClick = btn.onClick,
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = btn.label,
            fontSize = 18.sp,
            fontWeight = if (btn.type == ButtonType.EQUALS) FontWeight.Medium else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
