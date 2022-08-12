package com.github.paylike.sample

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.JavascriptInterface
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.github.paylike.kotlin_client.domain.dto.payment.request.test.PaymentTestDto
import com.github.paylike.kotlin_engine.model.service.ApiMode
import com.github.paylike.kotlin_engine.view.JsListener
import com.github.paylike.kotlin_engine.viewmodel.EngineState
import com.github.paylike.kotlin_engine.viewmodel.PaylikeEngine
import com.github.paylike.kotlin_money.PaymentAmount
import com.github.paylike.kotlin_request.exceptions.ServerErrorException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val engine = PaylikeEngine("e393f9ec-b2f7-4f81-b455-ce45b02d355d", ApiMode.TEST)

class DummyListener : JsListener {
    @JavascriptInterface
    override fun receiveMessage(data: String) {
        Log.d("Listener", data)
    }
}

val paylikeGreen= Color(0xFF2e8f29)

fun shouldBeActive(): Boolean {
    var currentState = engine.getCurrentState()

    return currentState === EngineState.WEBVIEW_CHALLENGE_STARTED ||
            currentState === EngineState.WEBVIEW_CHALLENGE_REQUIRED
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScaffoldDemo() {
    var isActive by remember { mutableStateOf(shouldBeActive()) }
    println("isActive: $isActive")
    Scaffold(
        topBar = { TopAppBar(title = {Text("Pay with PayLike")},backgroundColor = paylikeGreen)  },

        content = {
            SampleScreen(isActive, isActiveChange = { isActive = it })
        },
    )
}

@Composable
fun SampleScreen(isActive: Boolean, isActiveChange: (Boolean) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(), // 1
        horizontalAlignment = Alignment.CenterHorizontally, // 2
        verticalArrangement = Arrangement.Center // 3
    ) {
        println(isActive)
        if (isActive) {
            EngineSampleComposable(engine)
        } else {
            TransactionIDText()
            val error = engine.getError()
            if(error != null) {
                ErrorText(error.message)
            }
            PayButton(isActiveChange)
        }
    }
}

@Composable
fun TransactionIDText() {
    Text(
        "TransactionID:", // TODO: On success show ID (put to state)
        fontSize = 30.sp,
    )
}

@Composable
fun ErrorText(errorMessage: String) {
    Text(
    "Error: $errorMessage", // TODO: On error show error message (put to state)
        fontSize = 30.sp,
    )
}

@Composable
fun PayButton(isActiveChange: (Boolean) -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = paylikeGreen,
            contentColor = androidx.compose.ui.graphics.Color.White
        ),
        onClick = {
//            GlobalScope.launch(Dispatchers.IO) {
//                if (engine.countObservers() == 0) {
//                    engine.addObserver { _, _ ->
//                        println("engine change")
//                        println(engine.getCurrentState())

//                        isActiveChange(shouldBeActive())
//                  }
//                }
                engine.resetPaymentFlow()
                runBlocking {
                    try {
                        engine.createPaymentDataDto("4012111111111111", "111", 11, 2023)
                        engine.startPayment(
                            PaymentAmount("EUR", 10, 0),
                            PaymentTestDto()
                        )
                        isActiveChange(shouldBeActive())
                    } catch (e: ServerErrorException) {
                        println("serverErrorException " + e.status.toString())
                        println("serverErrorException " + e.headers.toString())
                    }

            }
        }
    ) {
        Text("Pay")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScreen1() {
    ScaffoldDemo()
}
