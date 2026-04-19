package io.github.nvlad1.function3danimator.ui.functions_list

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import io.github.nvlad1.function3danimator.R
import io.github.nvlad1.function3danimator.model.EnumColor
import io.github.nvlad1.function3danimator.model.FunctionModel
import io.github.nvlad1.function3danimator.ui.function_screen.FunctionActivityCompose
import io.github.nvlad1.function3danimator.ui.function_screen.FunctionActivityCompose.Companion.EXTRA_FUNCTION_ID

@Composable
fun FunctionListScreen(
    modifier: Modifier,
    viewModel: FunctionListViewModel
) {
    val context = LocalContext.current
    val functions = viewModel.functions.observeAsState(listOf())
    val functionToDelete: MutableState<FunctionModel?> = rememberSaveable { mutableStateOf(null) }
    val onClickEdit: (function: FunctionModel) -> Unit = {
        val i = Intent(context, FunctionActivityCompose::class.java)
        i.putExtra(EXTRA_FUNCTION_ID, it.id)
        context.startActivity(i)
    }
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ){
        for (function in functions.value){
            if (function.isParametric){
                FunctionCardParametric(
                    function = function,
                    onClickEdit = {onClickEdit.invoke(function)},
                    onClickDelete = {functionToDelete.value = function}
                )
            } else {
                FunctionCardOrdinary(
                    function = function,
                    onClickEdit = {onClickEdit.invoke(function)},
                    onClickDelete = {functionToDelete.value = function}
                )
            }
        }
    }
    AlertDialogDeleteFunction(viewModel = viewModel, function = functionToDelete)
}

@Composable
private fun FunctionCardOrdinary(
    function: FunctionModel,
    onClickEdit: (() -> Unit)? = null,
    onClickDelete: (() -> Unit)? = null
){
    val functionTextColor = function.color?.colorResId?.let { colorResource(id = it) }
        ?: MaterialTheme.colors.onSurface
    Card(
        modifier = Modifier.padding(16.dp),
        elevation = 5.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        ConstraintLayout(modifier = Modifier
            .fillMaxWidth()){
            val (functionText, editIcon, removeIcon) = createRefs()
            Text(
                text = function.cardString(),
                modifier = Modifier.constrainAs(functionText){
                    top.linkTo(parent.top, 16.dp)
                    linkTo(parent.start, parent.end, startMargin = 16.dp, endMargin = 16.dp, bias = 0f)
                    width = Dimension.fillToConstraints
                },
                style = TextStyle(
                    color = functionTextColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            )
            Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_close_black_24dp),
                contentDescription = stringResource(R.string.delete_function),
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .constrainAs(removeIcon) {
                        top.linkTo(functionText.bottom, 6.dp)
                        end.linkTo(parent.end, 16.dp)
                        bottom.linkTo(parent.bottom, 16.dp)
                    }
                    .clickable {
                        onClickDelete?.invoke()
                    }
            )
            Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_edit_black_24dp),
                contentDescription = stringResource(R.string.edit_function),
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .constrainAs(editIcon) {
                        top.linkTo(functionText.bottom, 6.dp)
                        end.linkTo(removeIcon.start, 16.dp)
                        bottom.linkTo(parent.bottom, 16.dp)
                    }
                    .clickable {
                        onClickEdit?.invoke()
                    }
            )
        }
    }
}

@Composable
private fun FunctionCardParametric(
    function: FunctionModel,
    onClick: (() -> Unit)? = null,
    onClickEdit: (() -> Unit)? = null,
    onClickDelete: (() -> Unit)? = null
){
    val functionTextColor = function.color?.colorResId?.let { colorResource(id = it) }
        ?: MaterialTheme.colors.onSurface
    Card(
        modifier = Modifier.padding(16.dp),
        elevation = 5.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        ConstraintLayout(modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick?.invoke() }) {
            val (functionTextX, functionTextY, functionTextZ, editIcon, removeIcon) = createRefs()
            val cardStringTriple = function.cardStringParametric()
            Text(
                text = cardStringTriple.first,
                modifier = Modifier.constrainAs(functionTextX) {
                    top.linkTo(parent.top, 16.dp)
                    linkTo(parent.start, parent.end, startMargin = 16.dp, endMargin = 16.dp, bias = 0f)
                    width = Dimension.fillToConstraints
                },
                style = TextStyle(
                    color = functionTextColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            )
            Text(
                text = cardStringTriple.second,
                modifier = Modifier.constrainAs(functionTextY) {
                    top.linkTo(functionTextX.bottom, 8.dp)
                    linkTo(parent.start, parent.end, startMargin = 16.dp, endMargin = 16.dp, bias = 0f)
                    width = Dimension.fillToConstraints
                },
                style = TextStyle(
                    color = functionTextColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            )
            Text(
                text = cardStringTriple.third,
                modifier = Modifier.constrainAs(functionTextZ) {
                    top.linkTo(functionTextY.bottom, 8.dp)
                    linkTo(parent.start, parent.end, startMargin = 16.dp, endMargin = 16.dp, bias = 0f)
                    width = Dimension.fillToConstraints
                },
                style = TextStyle(
                    color = functionTextColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            )
            Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_close_black_24dp),
                contentDescription = stringResource(R.string.delete_function),
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .constrainAs(removeIcon) {
                        top.linkTo(functionTextZ.bottom, 6.dp)
                        end.linkTo(parent.end, 16.dp)
                        bottom.linkTo(parent.bottom, 16.dp)
                    }
                    .clickable {
                        onClickDelete?.invoke()
                    }
            )
            Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_edit_black_24dp),
                contentDescription = stringResource(R.string.edit_function),
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .constrainAs(editIcon) {
                        top.linkTo(functionTextZ.bottom, 6.dp)
                        end.linkTo(removeIcon.start, 16.dp)
                        bottom.linkTo(parent.bottom, 16.dp)
                    }
                    .clickable {
                        onClickEdit?.invoke()
                    }
            )
        }
    }
}

@Composable
private fun AlertDialogDeleteFunction(viewModel: FunctionListViewModel, function: MutableState<FunctionModel?>){
    if (function.value == null) return
    AlertDialog(
        onDismissRequest = {
            function.value = null
        },
        title = {
            Text(text = stringResource(id = R.string.delete_function_confirmation_dialog_title))},
        text = {
            Text(text = stringResource(id = R.string.delete_function_confirmation_dialog_text))},
        confirmButton = {
            Button(
                onClick = {
                    val functionValue = function.value ?: return@Button
                    viewModel.deleteFunction(functionValue)
                    function.value = null
                }
            ) {
                Text(
                    text = stringResource(id = R.string.delete)
                )
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    function.value = null
                }
            ) {
                Text(
                    text = stringResource(id = R.string.cancel)
                )
            }
        },
    )
}

@Preview
@Composable
fun PreviewFunctionCard(){
    val function = FunctionModel("1", EnumColor.BLUE)
    FunctionCardOrdinary(function = function)
}
