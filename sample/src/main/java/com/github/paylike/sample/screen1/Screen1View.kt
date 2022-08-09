package com.github.paylike.sample.screen1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun Screen1() {
    Column(
        modifier = Modifier.fillMaxSize(), // 1
        horizontalAlignment = Alignment.CenterHorizontally, // 2
        verticalArrangement = Arrangement.Center // 3
    ) {
        Text(
            "TransactionID:",
            fontSize = 30.sp,
        )
        Text(
            "Error:",
            fontSize = 30.sp,
        )
        Button(
            onClick = { /* ... */ },
        ) {
            Text("Pay")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewScreen1() {
    Screen1()
}
