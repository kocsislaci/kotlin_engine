package com.github.paylike.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.paylike.kotlin_client.domain.dto.payment.request.test.PaymentTestDto
import com.github.paylike.kotlin_engine.error.PaylikeEngineError
import com.github.paylike.kotlin_engine.model.service.ApiMode
import com.github.paylike.kotlin_engine.view.PaylikeWebview
import com.github.paylike.kotlin_engine.viewmodel.PaylikeEngine
import com.github.paylike.kotlin_money.PaymentAmount
import com.github.paylike.sample.ui.theme.PaylikeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

/**
 * Sample activity to demonstrate Paylike payment method with Compose
 */
class SampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val engine = PaylikeEngine(BuildConfig.PaylikeMerchantApiKey, ApiMode.TEST)
        setContent {
            SampleScaffold(engine)
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SampleScaffold(engine: PaylikeEngine) {
    PaylikeTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Scaffold(
                topBar = { TopAppBar(
                    title = { Text("Pay with PayLike") },
                )  },
                content = { SampleScreen(engine) },
            )
        }
    }
}

@Composable
fun SampleScreen(engine: PaylikeEngine, ) {
    val errorMutableState: PaylikeEngineError? = null
    val error = remember { mutableStateOf(errorMutableState) }

    val hintsText = remember { mutableStateOf("0") }
    val transactionID = remember { mutableStateOf("No transaction id yet") }
    val statesListener = StatesListener(hintsText, transactionID, error)
    engine.addObserver(statesListener)

    val webview = PaylikeWebview(engine)
    val shouldWebviewRender = remember { webview.shouldWebviewRender }

    if (error.value != null) {
        Toast.makeText(LocalContext.current, error.value!!.message, Toast.LENGTH_LONG).show()
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .height(30.dp),
                text = hintsText.value,
                textAlign = TextAlign.Center,
            )
        }
        item {
            Text(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .height(30.dp),
                text = transactionID.value,
                textAlign = TextAlign.Center,
            )
        }
        item {
            if (shouldWebviewRender.value) {
                webview.WebviewComposable(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .height(300.dp)
                )
            } else {
                PayButton(
                    engine,
                    webview,
                )
            }
        }
    }
}

/**
 * Starts Payment
 */
@Composable
fun PayButton(
    engine: PaylikeEngine,
    webview: PaylikeWebview
) {
    Button(
        onClick = {
            engine.resetEngineStates()
            webview.shouldWebviewRender.value = true
            CoroutineScope(Dispatchers.IO).async {
                engine.createPaymentDataDto("4012111111111111", "111", 11, 2023)
                engine.startPayment(
                    PaymentAmount("EUR", 1, 0),
                    PaymentTestDto()
                )
            }
        },
    ) {
        Text(text = "Pay")
    }
}
