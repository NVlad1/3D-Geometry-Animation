package io.github.nvlad1.function3danimator.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.begoml.tooltippopup.tooltip.TooltipPopup
import io.github.nvlad1.function3danimator.R

@Composable
fun SettingsScreen(
    modifier: Modifier,
    viewModel: SettingsViewModel
) {
    val isGridShown = viewModel.isGridShown.observeAsState(initial = false)
    val isCustomKeyboardShown = viewModel.isCustomKeyboardShown.observeAsState(initial = false)
    val gridSizeString = viewModel.gridSizeString.observeAsState("")
    val graphScreenOrientation: EnumMainActivityOrientation by viewModel.graphScreenOrientation.observeAsState(EnumMainActivityOrientation.landscape)
    val dialogState: MutableState<DialogState> = rememberSaveable { mutableStateOf(DialogState.none) }

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        TwoRowBlockWithPopup(
            titleText = stringResource(id = R.string.grid_size),
            subtitleText = gridSizeString.value,
            tooltipText = stringResource(id = R.string.grid_size_explanation),
            onClick = {dialogState.value = DialogState.gridSize}
        )
        Divider(color = colorResource(id = R.color.colorLightGray2), thickness = 1.dp)
        SimpleCheckboxBlock(
            text = stringResource(id = R.string.show_surface_grid),
            isChecked = isGridShown.value,
            onClick = {viewModel.onClickShowGrid()}
        )
        Divider(color = colorResource(id = R.color.colorLightGray2), thickness = 1.dp)
        SimpleCheckboxBlock(
            text = stringResource(id = R.string.show_custom_keyboard),
            isChecked = isCustomKeyboardShown.value,
            onClick = {viewModel.onClickCustomKeyboard()}
        )
        Divider(color = colorResource(id = R.color.colorLightGray2), thickness = 1.dp)
        TwoRowBlockSimple(
            titleText = stringResource(id = R.string.graph_screen_orientation),
            subtitleText = graphScreenOrientation.description,
            onClick = {dialogState.value = DialogState.screenOrientation}
        )
    }



    AlertDialogMainScreenOrientation(
        dialogState = dialogState,
        orientation = viewModel.graphScreenOrientation.value ?: EnumMainActivityOrientation.portrait
    ) { viewModel.setGraphScreenOrientation(it) }

    AlertDialogGridSize(
        dialogState = dialogState,
        gridSize = Pair(viewModel.gridX, viewModel.gridY),
        onClick = {x, y -> viewModel.setGridSize(x,y)}
    )
}

@Composable
private fun TwoRowBlockWithPopup(
    titleText: String,
    subtitleText: String,
    tooltipText: String,
    onClick: () -> Unit,
){
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titleText,
                fontSize = 16.sp
            )
            TooltipPopup(
                modifier = Modifier
                    .padding(start = 16.dp),
                requesterView = { modifier ->
                    Icon(
                        modifier = modifier,
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = "TooltipPopup",
                        tint = colorResource(id = R.color.colorGray45)
                    )
                },
                tooltipContent = {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(vertical = 8.dp),
                        text = tooltipText,
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = Color.White,
                    )
                }
            )
        }
        Text(
            text = subtitleText,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun TwoRowBlockSimple(
    titleText: String,
    subtitleText: String,
    onClick: () -> Unit
){
    val interactionSource = remember { MutableInteractionSource() }
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
        .clickable(interactionSource = interactionSource, indication = null) { onClick() }) {
        Text(
            text = titleText,
            fontSize = 16.sp
        )
        Text(
            text = subtitleText,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun SimpleCheckboxBlock(
    text: String,
    isChecked: Boolean,
    onClick: () -> Unit){
    val interactionSource = remember { MutableInteractionSource() }
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = text,
            fontSize = 16.sp
        )
        Checkbox(checked = isChecked, onCheckedChange = {onClick()})
    }
}

@Composable
private fun AlertDialogMainScreenOrientation(
    dialogState: MutableState<DialogState>,
    orientation: EnumMainActivityOrientation,
    onClick: (EnumMainActivityOrientation) -> Unit
) {
    if (dialogState.value != DialogState.screenOrientation) return
    val radioButtons = EnumMainActivityOrientation.values()
    val selectedButton = remember { mutableStateOf(radioButtons.find { it == orientation } ?: EnumMainActivityOrientation.portrait) }

    AlertDialog(
        onDismissRequest = {
            dialogState.value = DialogState.none
        },

        title = { Text(text = stringResource(id =
            R.string.choose_main_screen_orientation))},
        text = {
            Column {
                radioButtons.forEach { orientation ->
                    val isSelected = orientation == selectedButton.value
                    val colors = RadioButtonDefaults.colors(
                        selectedColor = colorResource(id = R.color.colorPrimary),
                        unselectedColor = colorResource(id = R.color.colorPrimaryDark),
                        disabledColor = Color.LightGray
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            colors = colors,
                            selected = isSelected,
                            onClick = { selectedButton.value = orientation }
                        )
                        Text(
                            modifier = Modifier.clickable { selectedButton.value = orientation },
                            text = orientation.description,
                            fontSize = 16.sp,
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    dialogState.value = DialogState.none
                    onClick(selectedButton.value)
                }
            ) {
                Text(
                    text = stringResource(id = R.string.ok).uppercase()
                )
            }
        }
    )
}

@Composable
private fun AlertDialogGridSize(
    dialogState: MutableState<DialogState>,
    gridSize: Pair<Int, Int>,
    onClick: (String, String) -> Unit
){
    if (dialogState.value != DialogState.gridSize) return
    val gridXstr = remember {mutableStateOf(gridSize.first.toString())}
    val gridYstr = remember {mutableStateOf(gridSize.second.toString())}

    AlertDialog(
        onDismissRequest = {
            dialogState.value = DialogState.none
        },

        title = { Text(text = stringResource(id = R.string.set_grid_size))},
        text = {
            Column() {
                HackySpacer(0.dp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GridTextField(
                        value = gridXstr.value,
                        onValueChange = { if (it.length <= 3) gridXstr.value = it }
                    )
                    Text(text = "x", modifier = Modifier.padding(8.dp))
                    GridTextField(
                        value = gridYstr.value,
                        onValueChange = { if (it.length <= 3) gridYstr.value = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    dialogState.value = DialogState.none
                    onClick(gridXstr.value, gridYstr.value)
                }
            ) {
                Text(
                    text = stringResource(id = R.string.ok).uppercase()
                )
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    dialogState.value = DialogState.none
                }
            ) {
                Text(
                    text = stringResource(id = R.string.cancel).uppercase()
                )
            }
        }
    )
}

@Composable
private fun GridTextField(value: String, onValueChange: (String) -> Unit){
    TextField(
        modifier = Modifier.width(80.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White
        ),
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
    )
}

@Composable
private fun HackySpacer(space: Dp) {
    Box(
        modifier = Modifier
            .height(space)
            .fillMaxWidth()
    ) {
        Text(text = "")
    }
}

@Preview
@Composable
private fun PreviewGridSizeBlock() {
    TwoRowBlockWithPopup(
        titleText = "Title",
        subtitleText = "33x33",
        tooltipText = "some tooltip text",
        onClick = {}
    )
}

@Preview
@Composable
private fun PreviewSimpleBlock() {
    TwoRowBlockSimple(
        titleText = "Title",
        subtitleText = "33x33",
        onClick = {}
    )
}

@Preview
@Composable
private fun PreviewShowGridBlock() {
    SimpleCheckboxBlock(
        text = "Title",
        isChecked = true,
        onClick = {})
}

@Preview
@Composable
private fun PreviewAlertDialogMainScreenOrientation(){
    AlertDialogMainScreenOrientation(
        rememberSaveable { mutableStateOf(DialogState.screenOrientation)},
        EnumMainActivityOrientation.portrait
    ){}
}

@Preview
@Composable
private fun PreviewAlertDialogSetGridSize(){
    AlertDialogGridSize(
        rememberSaveable { mutableStateOf(DialogState.gridSize)},
        Pair(50,50)
    ){x,y -> }
}

private enum class DialogState{
    none, gridSize, screenOrientation
}