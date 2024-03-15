package com.mcdevelopers.valentinelivegreetingscard

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.INVISIBLE
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.VISIBLE
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.mcdevelopers.valentinelivegreetingscard.ui.theme.ValentineLiveGreetingCardTheme
import com.mcdevelopers.valentinelivegreetingscard.utils.Constants
import com.mcdevelopers.valentinelivegreetingscard.utils.toPx
import kotlinx.coroutines.flow.MutableStateFlow
import pl.droidsonroids.gif.GifImageView

class MainActivity : ComponentActivity() {
        private val loaderStateFlow = MutableStateFlow(true)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ValentineLiveGreetingCardTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.ui.graphics.Color.Black
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                        WebViewCompose(loaderStateFlow)
                        GifImageViewCompose(loaderStateFlow)
                    }
                }
            }
        }
    }
}


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewCompose(stateFlow: MutableStateFlow<Boolean>){
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
                )
                this.webChromeClient = WebChromeClient()
                this.webViewClient = buildWebViewClient(stateFlow = stateFlow)
                this.requestFocus()
                this.setBackgroundColor(android.graphics.Color.BLACK)
                this.isSoundEffectsEnabled = true
                settings.javaScriptEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.allowFileAccess = true
                settings.allowContentAccess = true
                settings.domStorageEnabled = true
                settings.setGeolocationEnabled(true)
                settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                visibility = INVISIBLE
            }
        },
        update = { webView ->
            // Update the Android View if needed
            webView.loadUrl(Constants.remoteUrl)
        }
    )
}

fun buildWebViewClient(stateFlow: MutableStateFlow<Boolean>):WebViewClient {
    val webViewClient = object : WebViewClient() {

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            view.visibility = INVISIBLE
            stateFlow.value = true
        }
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            stateFlow.value = false
            view.visibility = VISIBLE

        }
    }
    return  webViewClient;
}

@Composable
fun GifImageViewCompose(stateFlow: MutableStateFlow<Boolean>){
    val visibility by stateFlow.collectAsState()

    AndroidView(
        factory = { context ->
            GifImageView(context).apply {
                layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                ).apply {
                    width=context.toPx(230)
                    height = LayoutParams.WRAP_CONTENT
                }
            }
        },
        update = { view ->
            view.setImageResource(R.drawable.loading)
            view.visibility = if(visibility) View.VISIBLE else View.GONE
        }
    )
}


/*
@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_2")
@Composable
fun GreetingPreview() {
    ValentineLiveGreetingCardTheme {
        WebViewCompose()
    }
}*/
