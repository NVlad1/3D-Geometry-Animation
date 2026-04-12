package io.github.nvlad1.function3danimator.ui.function_screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.LocalTextStyle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.InterceptPlatformTextInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.nvlad1.function3danimator.R
import io.github.nvlad1.function3danimator.keyboard.MathKeyboard
import io.github.nvlad1.function3danimator.model.EnumColor
import io.github.nvlad1.function3danimator.model.FunctionModel
import io.github.nvlad1.function3danimator.model.calculationModel.enums.FunctionDefinitionType
import io.github.nvlad1.function3danimator.model.calculationModel.enums.TimeUnit
import io.github.nvlad1.function3danimator.ui.utils.UnderlineTextField
import kotlinx.coroutines.awaitCancellation

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FunctionScreen(
    modifier: Modifier,
    viewModel: FunctionViewModelCompose
) {
    val function by viewModel.functionModel.collectAsStateWithLifecycle()
    val isCustomKeyboardEnabled by viewModel.isCustomKeyboardEnabled.collectAsStateWithLifecycle()
    var showFunctionTypeDialog by rememberSaveable { mutableStateOf(false) }
    var showTimeMeasurementModeDialog by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var functionStrX by remember {
        mutableStateOf(TextFieldValue(function.strX ?: ""))
    }
    var functionStrY by remember {
        mutableStateOf(TextFieldValue(function.strY ?: ""))
    }
    var functionStrZ by remember {
        mutableStateOf(TextFieldValue(function.strZ ?: ""))
    }
    var functionStr by remember {
        mutableStateOf(TextFieldValue(function.string ?: ""))
    }
    var isTextFieldFocused by remember { mutableStateOf(false) }
    var isTextFieldXFocused by remember { mutableStateOf(false) }
    var isTextFieldYFocused by remember { mutableStateOf(false) }
    var isTextFieldZFocused by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        //focusRequester.requestFocus()
        // Refresh keyboard setting in case it was changed in settings
        viewModel.updateKeyboardSettingFromPreference()
    }

    LaunchedEffect(isCustomKeyboardEnabled) {
        if (isCustomKeyboardEnabled) {
            // Hide the system keyboard when custom keyboard is enabled
            keyboardController?.hide()
        }
        // Don't explicitly show - let the TextField handle it based on focus
    }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        Column(modifier = Modifier
            .weight(1f)
            .verticalScroll(scrollState)) {
            SettingsElement(
                title = stringResource(R.string.type),
                value = function.type.description
            ) {
                showFunctionTypeDialog = true
            }
            SettingsElement(
                title = stringResource(R.string.time_unit),
                value = function.timeMeasurementMode.description
            ) {
                showTimeMeasurementModeDialog = true
            }
            RenderTextFieldsWithKeyboardControl(
                isCustomKeyboardEnabled = isCustomKeyboardEnabled,
                function = function,
                functionStr = functionStr,
                functionStrX = functionStrX,
                functionStrY = functionStrY,
                functionStrZ = functionStrZ,
                focusRequester = focusRequester,
                isTextFieldFocused = isTextFieldFocused,
                isTextFieldXFocused = isTextFieldXFocused,
                isTextFieldYFocused = isTextFieldYFocused,
                isTextFieldZFocused = isTextFieldZFocused,
                onFunctionStrChange = { functionStr = it; viewModel.setFunctionStr(it.text) },
                onFunctionStrXChange = { functionStrX = it; viewModel.setFunctionStrX(it.text) },
                onFunctionStrYChange = { functionStrY = it; viewModel.setFunctionStrY(it.text) },
                onFunctionStrZChange = { functionStrZ = it; viewModel.setFunctionStrZ(it.text) },
                onFocusChanged = { x, y, z, main ->
                    isTextFieldXFocused = x
                    isTextFieldYFocused = y
                    isTextFieldZFocused = z
                    isTextFieldFocused = main
                }
            )
            FunctionBordersField(
                function = function,
                onXMinChange = viewModel::setXMin,
                onXMaxChange = viewModel::setXMax,
                onYMinChange = viewModel::setYMin,
                onYMaxChange = viewModel::setYMax
            )
            ColorPickerField(selectedColor = function.color ?: EnumColor.RED) {
                viewModel.setColor(it)
            }
        }
        if (isCustomKeyboardEnabled && (isTextFieldFocused || isTextFieldXFocused || isTextFieldYFocused || isTextFieldZFocused)) {
            MathKeyboard(
                modifier = Modifier.fillMaxWidth(),
                type = function.type,
                onKey = {
                    if (isTextFieldFocused) {
                        functionStr = insertText(functionStr, it)
                        viewModel.setFunctionStr(functionStr.text)
                    } else if (isTextFieldXFocused) {
                        functionStrX = insertText(functionStrX, it)
                        viewModel.setFunctionStrX(functionStrX.text)
                    } else if (isTextFieldYFocused) {
                        functionStrY = insertText(functionStrY, it)
                        viewModel.setFunctionStrY(functionStrY.text)
                    } else if (isTextFieldZFocused) {
                        functionStrZ = insertText(functionStrZ, it)
                        viewModel.setFunctionStrZ(functionStrZ.text)
                    }
                },
                onDelete = {
                    if (isTextFieldFocused) {
                        functionStr = deleteText(functionStr)
                        viewModel.setFunctionStr(functionStr.text)
                    } else if (isTextFieldXFocused) {
                        functionStrX = deleteText(functionStrX)
                        viewModel.setFunctionStrX(functionStrX.text)
                    } else if (isTextFieldYFocused) {
                        functionStrY = deleteText(functionStrY)
                        viewModel.setFunctionStrY(functionStrY.text)
                    } else if (isTextFieldZFocused) {
                        functionStrZ = deleteText(functionStrZ)
                        viewModel.setFunctionStrZ(functionStrZ.text)
                    }
                },
                onClear = {
                    if (isTextFieldFocused) {
                        functionStr = TextFieldValue("")
                        viewModel.setFunctionStr(functionStr.text)
                    } else if (isTextFieldXFocused) {
                        functionStrX = TextFieldValue("")
                        viewModel.setFunctionStrX(functionStrX.text)
                    } else if (isTextFieldYFocused) {
                        functionStrY = TextFieldValue("")
                        viewModel.setFunctionStrY(functionStrY.text)
                    } else if (isTextFieldZFocused) {
                        functionStrZ = TextFieldValue("")
                        viewModel.setFunctionStrZ(functionStrZ.text)
                    }
                }
            )
        }
    }

    if (showFunctionTypeDialog) {
        SingleChoiceDialog(
            title = stringResource(R.string.choose_function_type),
            options = FunctionDefinitionType.entries.map { it.description },
            selectedOption = function.type.description,
            onOptionSelected = {
                viewModel.setFunctionType(it)
                showFunctionTypeDialog = false
            },
            onDismiss = { showFunctionTypeDialog = false }
        )
    }
    if (showTimeMeasurementModeDialog) {
        SingleChoiceDialog(
            title = stringResource(R.string.choose_time_measurement_mode),
            options = TimeUnit.entries.map { it.description },
            selectedOption = function.timeMeasurementMode.description,
            onOptionSelected = { newTimeUnit ->
                viewModel.setTimeUnit(TimeUnit.entries.find { it.description == newTimeUnit })
                showTimeMeasurementModeDialog = false
            },
            onDismiss = { showTimeMeasurementModeDialog = false }
        )
    }
}

@Composable
private fun SettingsElement(title: String, value: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Column() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onClick() }) {
            Text(
                text = title,
                fontSize = 16.sp
            )
            Text(
                text = value,
                fontSize = 14.sp
            )
        }
        Divider(
            color = colorResource(id = R.color.colorLightGray2),
            thickness = 1.dp
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun RenderTextFieldsWithKeyboardControl(
    isCustomKeyboardEnabled: Boolean,
    function: FunctionModel,
    functionStr: TextFieldValue,
    functionStrX: TextFieldValue,
    functionStrY: TextFieldValue,
    functionStrZ: TextFieldValue,
    focusRequester: FocusRequester,
    isTextFieldFocused: Boolean,
    isTextFieldXFocused: Boolean,
    isTextFieldYFocused: Boolean,
    isTextFieldZFocused: Boolean,
    onFunctionStrChange: (TextFieldValue) -> Unit,
    onFunctionStrXChange: (TextFieldValue) -> Unit,
    onFunctionStrYChange: (TextFieldValue) -> Unit,
    onFunctionStrZChange: (TextFieldValue) -> Unit,
    onFocusChanged: (Boolean, Boolean, Boolean, Boolean) -> Unit
) {
    ConditionalInterceptPlatformTextInput(isCustomKeyboardEnabled) {
        RenderTextFields(
            function = function,
            functionStr = functionStr,
            functionStrX = functionStrX,
            functionStrY = functionStrY,
            functionStrZ = functionStrZ,
            focusRequester = focusRequester,
            isTextFieldFocused = isTextFieldFocused,
            isTextFieldXFocused = isTextFieldXFocused,
            isTextFieldYFocused = isTextFieldYFocused,
            isTextFieldZFocused = isTextFieldZFocused,
            onFunctionStrChange = onFunctionStrChange,
            onFunctionStrXChange = onFunctionStrXChange,
            onFunctionStrYChange = onFunctionStrYChange,
            onFunctionStrZChange = onFunctionStrZChange,
            onFocusChanged = onFocusChanged,
            isCustomKeyboardEnabled = isCustomKeyboardEnabled
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ConditionalInterceptPlatformTextInput(
    isCustomKeyboardEnabled: Boolean,
    content: @Composable () -> Unit
) {
    if (isCustomKeyboardEnabled) {
        InterceptPlatformTextInput(
            interceptor = { request, nextHandler ->
                // We capture the "start input" request and do nothing.
                // This effectively kills the system keyboard.
                awaitCancellation()
            }
        ) {
            content()
        }
    } else {
        content()
    }
}

@Composable
private fun RenderTextFields(
    function: FunctionModel,
    functionStr: TextFieldValue,
    functionStrX: TextFieldValue,
    functionStrY: TextFieldValue,
    functionStrZ: TextFieldValue,
    focusRequester: FocusRequester,
    isTextFieldFocused: Boolean,
    isTextFieldXFocused: Boolean,
    isTextFieldYFocused: Boolean,
    isTextFieldZFocused: Boolean,
    onFunctionStrChange: (TextFieldValue) -> Unit,
    onFunctionStrXChange: (TextFieldValue) -> Unit,
    onFunctionStrYChange: (TextFieldValue) -> Unit,
    onFunctionStrZChange: (TextFieldValue) -> Unit,
    onFocusChanged: (Boolean, Boolean, Boolean, Boolean) -> Unit,
    isCustomKeyboardEnabled: Boolean = true
) {
    if (function.type == FunctionDefinitionType.parametric) {
        FunctionField(
            prefixText = function.type.getTextFieldPrefix("x"),
            functionStr = functionStrX,
            focusRequester = focusRequester,
            onValueChange = onFunctionStrXChange,
            onFocusChanged = { focusState ->
                onFocusChanged(focusState.isFocused, isTextFieldYFocused, isTextFieldZFocused, isTextFieldFocused)
            },
            isCustomKeyboardEnabled = isCustomKeyboardEnabled
        )
        FunctionField(
            prefixText = function.type.getTextFieldPrefix("y"),
            functionStr = functionStrY,
            focusRequester = focusRequester,
            onValueChange = onFunctionStrYChange,
            onFocusChanged = { focusState ->
                onFocusChanged(isTextFieldXFocused, focusState.isFocused, isTextFieldZFocused, isTextFieldFocused)
            },
            isCustomKeyboardEnabled = isCustomKeyboardEnabled
        )
        FunctionField(
            prefixText = function.type.getTextFieldPrefix("z"),
            functionStr = functionStrZ,
            focusRequester = focusRequester,
            onValueChange = onFunctionStrZChange,
            onFocusChanged = { focusState ->
                onFocusChanged(isTextFieldXFocused, isTextFieldYFocused, focusState.isFocused, isTextFieldFocused)
            },
            isCustomKeyboardEnabled = isCustomKeyboardEnabled
        )
    } else {
        FunctionField(
            prefixText = function.type.getTextFieldPrefix(),
            functionStr = functionStr,
            focusRequester = focusRequester,
            onValueChange = onFunctionStrChange,
            onFocusChanged = { focusState ->
                onFocusChanged(isTextFieldXFocused, isTextFieldYFocused, isTextFieldZFocused, focusState.isFocused)
            },
            isCustomKeyboardEnabled = isCustomKeyboardEnabled
        )
    }
}

@Composable
private fun FunctionField(
    prefixText: String,
    functionStr: TextFieldValue,
    focusRequester: FocusRequester,
    onValueChange: (TextFieldValue) -> Unit,
    onFocusChanged: (FocusState) -> Unit,
    isCustomKeyboardEnabled: Boolean = true
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp)) {
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = prefixText
        )
        Spacer(modifier = Modifier.size(8.dp))
        TextField(
            value = functionStr,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = if (isCustomKeyboardEnabled) {
                Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { onFocusChanged.invoke(it) }
            } else {
                Modifier
                    .onFocusChanged { onFocusChanged.invoke(it) }
            }
        )
    }

}

private fun insertText(
    value: TextFieldValue,
    insert: String
): TextFieldValue {
    val start = value.selection.start
    val end = value.selection.end

    val newText = buildString {
        append(value.text.substring(0, start))
        append(insert)
        append(value.text.substring(end))
    }

    val newCursor = start + insert.length

    return value.copy(
        text = newText,
        selection = TextRange(newCursor)
    )
}

private fun deleteText(value: TextFieldValue): TextFieldValue {
    val start = value.selection.start
    val end = value.selection.end

    if (start != end) {
        // Delete selection
        return value.copy(
            text = value.text.removeRange(start, end),
            selection = TextRange(start)
        )
    }

    if (start == 0) return value

    return value.copy(
        text = value.text.removeRange(start - 1, start),
        selection = TextRange(start - 1)
    )
}

@Composable
private fun FunctionBordersField(
    function: FunctionModel,
    onXMinChange: (Float) -> Unit,
    onXMaxChange: (Float) -> Unit,
    onYMinChange: (Float) -> Unit,
    onYMaxChange: (Float) -> Unit
) {
    // Get the correct variable names based on function type
    val (var1, var2) = when (function.type) {
        FunctionDefinitionType.defaultType -> "x" to "y"
        FunctionDefinitionType.elliptical -> "r" to "φ"
        FunctionDefinitionType.spherical -> "θ" to "φ"
        FunctionDefinitionType.parametric -> "u" to "v"
    }
    var xminText by rememberSaveable(function.id, function.type) { mutableStateOf(function.xmin.toString()) }
    var xmaxText by rememberSaveable(function.id, function.type) { mutableStateOf(function.xmax.toString()) }
    var yminText by rememberSaveable(function.id, function.type) { mutableStateOf(function.ymin.toString()) }
    var ymaxText by rememberSaveable(function.id, function.type) { mutableStateOf(function.ymax.toString()) }

    Column(
        modifier = Modifier
            .padding(vertical = 24.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row() {
            FunctionBorderTextField(
                value = xminText,
                onValueChange = { value ->
                    xminText = value
                    value.toFloatOrNull()?.let(onXMinChange)
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(modifier = Modifier.padding(top = 2.dp), text = " ≤ $var1 ≤ ", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(16.dp))
            FunctionBorderTextField(
                value = xmaxText,
                onValueChange = { value ->
                    xmaxText = value
                    value.toFloatOrNull()?.let(onXMaxChange)
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row() {
            FunctionBorderTextField(
                value = yminText,
                onValueChange = { value ->
                    yminText = value
                    value.toFloatOrNull()?.let(onYMinChange)
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(modifier = Modifier.padding(top = 2.dp), text = " ≤ $var2 ≤ ", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(16.dp))
            FunctionBorderTextField(
                value = ymaxText,
                onValueChange = { value ->
                    ymaxText = value
                    value.toFloatOrNull()?.let(onYMaxChange)
                }
            )
        }
    }
}

@Composable
private fun FunctionBorderTextField(value: String, onValueChange: (String) -> Unit) {
    UnderlineTextField(
        modifier = Modifier.width(90.dp),
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
    )
}

@Composable
fun SingleChoiceDialog(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var currentSelection by remember { mutableStateOf(selectedOption) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { currentSelection = option }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option == currentSelection,
                            onClick = { currentSelection = option }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onOptionSelected(currentSelection) }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ColorPickerField(
    selectedColor: EnumColor,
    onColorChange: (EnumColor) -> Unit
) {
    Column() {
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = stringResource(id = R.string.choose_color)
        )
        Row() {
            for (i in 0 until 3) {
                ColorPickerElement(
                    color = EnumColor.entries[i],
                    isActive = selectedColor == EnumColor.entries[i]
                ) {
                    onColorChange.invoke(EnumColor.entries[i])
                }
            }
        }
        Row() {
            for (i in 3 until 6) {
                ColorPickerElement(
                    color = EnumColor.entries[i],
                    isActive = selectedColor == EnumColor.entries[i]
                ) {
                    onColorChange.invoke(EnumColor.entries[i])
                }
            }
        }
    }
}

@Composable
private fun ColorPickerElement(color: EnumColor, isActive: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = colorResource(id = color.colorResId)
        ),
        onClick = { onClick.invoke() }) {
        if (!isActive) return@OutlinedButton
        val colorId = colorResource(id = color.colorResId)
        Canvas(
            modifier = Modifier
                .size(16.dp)
        ) {
            drawCircle(
                brush = SolidColor(Color.White),
                radius = size.height
            )
            drawCircle(
                brush = SolidColor(colorId),
                radius = size.height * 0.8f
            )
        }
    }
}

private val previewFunction = FunctionModel("x*y/2", EnumColor.BLUE)


@Preview
@Composable
private fun PreviewColorPickerField() {
    ColorPickerField(selectedColor = previewFunction.color!!) {}
}
