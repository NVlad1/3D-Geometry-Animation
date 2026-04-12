package io.github.nvlad1.function3danimator.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.nvlad1.function3danimator.model.calculationModel.enums.FunctionDefinitionType

enum class KeyboardMode {
    MAIN, FUNCTIONS
}

@Composable
fun MathKeyboard(
    modifier: Modifier = Modifier,
    type: FunctionDefinitionType,
    onKey: (String) -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit
) {
    var mode by remember { mutableStateOf(KeyboardMode.MAIN) }

    Surface(
        modifier = modifier,
        tonalElevation = 6.dp,
        shadowElevation = 12.dp
    ) {
        when (mode) {
            KeyboardMode.MAIN -> MainKeyboard(
                type = type,
                onKey = onKey,
                onDelete = onDelete,
                onClear = onClear,
                onFunc = { mode = KeyboardMode.FUNCTIONS }
            )

            KeyboardMode.FUNCTIONS -> FunctionKeyboard(
                onKey = onKey,
                onDelete = onDelete,
                onClear = onClear,
                onBack = { mode = KeyboardMode.MAIN }
            )
        }
    }
}

@Composable
private fun MainKeyboard(
    type: FunctionDefinitionType,
    onKey: (String) -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit,
    onFunc: () -> Unit
) {
    val rows = listOf(
        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
        listOf("+", "-", "*", "/",  "^", ".",
            when (type) {
                FunctionDefinitionType.defaultType -> "x"
                FunctionDefinitionType.elliptical -> "r"
                FunctionDefinitionType.spherical -> "φ"
                FunctionDefinitionType.parametric -> "u"
            },
            when (type) {
                FunctionDefinitionType.defaultType -> "y"
                FunctionDefinitionType.elliptical -> "φ"
                FunctionDefinitionType.spherical -> "θ"
                FunctionDefinitionType.parametric -> "v"
            }
        ),
        listOf("t", "(", ")", "Func", "CLR", "⌫")
    )

    KeyboardGrid(
        rows = rows,
        onKey = {
            when (it) {
                "⌫" -> onDelete()
                "CLR" -> onClear()
                "Func" -> onFunc()
                else -> onKey(it)
            }
        }
    )
}

@Composable
private fun FunctionKeyboard(
    onKey: (String) -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit
) {
    val rows = listOf(
        listOf("sin", "cos", "tg", "ctg", "exp", "sqrt", "(", ")"),
        listOf("abs", "log", "ln", "asin", "acos", "atg", "sh", "ch"),
        listOf("back", "⌫", "CLR")
    )

    KeyboardGrid(
        rows = rows,
        onKey = {
            when (it) {
                "⌫" -> onDelete()
                "CLR" -> onClear()
                "back" -> onBack()
                else -> onKey(it + if (it != "(" && it != ")") "(" else "") // functions auto-open
            }
        }
    )
}

@Composable
private fun KeyboardGrid(
    rows: List<List<String>>,
    onKey: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { label ->
                    KeyboardKey(
                        label = label,
                        modifier = Modifier.weight(1f),
                        onClick = { onKey(label) }
                    )
                }
            }
        }
    }
}

@Composable
private fun KeyboardKey(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isAction = label in listOf("⌫", "CLR", "Func", "back")

    Surface(
        modifier = modifier
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        tonalElevation = if (isAction) 6.dp else 2.dp,
        color = if (isAction)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(
    name = "Math Keyboard - Light",
    showBackground = true,
    backgroundColor = 0xFFF2F2F2,
    widthDp = 411
)
@Composable
fun MathKeyboardPreview() {
    MaterialTheme {
        var expression by remember { mutableStateOf("z(x,y) = ") }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Fake input field
            Surface(
                tonalElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = expression.ifEmpty { " " },
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            MathKeyboard(
                type = FunctionDefinitionType.defaultType,
                onKey = { expression += it },
                onDelete = {
                    expression = expression.dropLast(1)
                },
                onClear = {
                    expression = ""
                }
            )
        }
    }
}
