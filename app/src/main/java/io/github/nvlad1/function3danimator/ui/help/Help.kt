package io.github.nvlad1.function3danimator.ui.help

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.nvlad1.function3danimator.R

@Composable
fun HelpScreen(modifier: Modifier){
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.help1),
            color = colorResource(id = R.color.colorBlack),
            fontSize = 22.sp
        )
        Text(stringResource(R.string.help2))
        Text(
            text = stringResource(R.string.help3),
            color = colorResource(id = R.color.colorBlack),
            fontSize = 22.sp
        )
        Text(stringResource(R.string.help4))
        Text(
            text = stringResource(R.string.help5),
            color = colorResource(id = R.color.colorBlack),
            fontSize = 22.sp
        )
        Text(stringResource(R.string.help6))
        Text(
            text = stringResource(R.string.help7),
            color = colorResource(id = R.color.colorBlack),
            fontSize = 22.sp
        )
        Text(stringResource(R.string.help8))
    }
}

@Preview
@Composable
private fun PreviewHelpScreen(){
    HelpScreen(Modifier)
}
