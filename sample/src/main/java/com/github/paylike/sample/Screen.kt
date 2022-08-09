package com.github.paylike.sample

import android.util.Log
import android.webkit.JavascriptInterface
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.github.paylike.kotlin_client.domain.dto.payment.request.test.PaymentTestDto
import com.github.paylike.kotlin_engine.model.service.ApiMode
import com.github.paylike.kotlin_engine.view.JsListener
import com.github.paylike.kotlin_engine.view.TdsWebView
import com.github.paylike.kotlin_engine.viewmodel.EngineState
import com.github.paylike.kotlin_engine.viewmodel.PaylikeEngine
import com.github.paylike.kotlin_money.PaymentAmount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

val engine = PaylikeEngine("MERCHANT_ID", ApiMode.TEST)

class DummyListener : JsListener {
    @JavascriptInterface
    override fun receiveMessage(data: String) {
        Log.d("Listener", data)
    }
}

@Composable
fun SampleScreen() {
    Column(
        modifier = Modifier.fillMaxSize(), // 1
        horizontalAlignment = Alignment.CenterHorizontally, // 2
        verticalArrangement = Arrangement.Center // 3
    ) {
        var isActive by remember { mutableStateOf(false) }
        if (isActive) {
            TdsWebView(engine.repository.htmlRepository as String, listener = DummyListener())
        } else {
            Text(
                "TransactionID:", // TODO: On success show ID (put to state)
                fontSize = 30.sp,
            )
            Text(
                "Error:", // TODO: On error show error message (put to state)
                fontSize = 30.sp,
            )
            Button(
                onClick = {
                    GlobalScope.launch(Dispatchers.IO) {
                        if (engine.countObservers() == 0) {
                            engine.addObserver { _, _ ->
                                println("engine change")
                                println(engine.getCurrentState())
                                // TODO: Should be dependent on the current engine state
                                // SUCCESS, ERROR, WAITING_FOR_INPUT -> FALSE
                                // Everything else -> TRUE
                                isActive = true
                            }
                        }
                        engine.createPaymentDataDto("4012111111111111", "111", 11, 2023)
                        engine.startPayment(PaymentAmount(
                            currency = "EUR",
                            value = 100,
                            exponent = 0
                        ), PaymentTestDto())
                        // TODO: Remove after engine fixed
                        engine.bumpState()
                    }
                },
            ) {
                Text("Pay")
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewScreen1() {
    SampleScreen()
}
