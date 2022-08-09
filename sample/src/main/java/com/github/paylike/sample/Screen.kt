package com.github.paylike.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.github.paylike.kotlin_client.domain.dto.payment.request.test.PaymentTestDto
import com.github.paylike.kotlin_engine.model.service.ApiMode
import com.github.paylike.kotlin_engine.viewmodel.PaylikeEngine
import com.github.paylike.kotlin_money.PaymentAmount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val engine = PaylikeEngine("testKotlin", ApiMode.TEST)

// Listens for the engine state changes
fun addEngineListener() {
    if (engine.countObservers() > 0) {
        return
    }
    engine.addObserver { observable, any ->
        println("engine change")
        println(engine.getCurrentState())
    }
}

@Composable
fun SampleScreen() {
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
            onClick = {
                GlobalScope.launch(Dispatchers.IO) {
                    addEngineListener()
                    engine.createPaymentDataDto("4012111111111111", "111", 11, 2023)
                    engine.startPayment(PaymentAmount(
                        currency = "EUR",
                        value = 100,
                        exponent = 0
                    ), PaymentTestDto())
                }
            },
        ) {
            Text("Pay")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewScreen1() {
    SampleScreen()
}
