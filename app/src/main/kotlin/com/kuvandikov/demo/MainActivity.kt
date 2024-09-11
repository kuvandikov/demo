package com.kuvandikov.demo


import android.graphics.RenderEffect
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kuvandikov.demo.ui.theme.DemoTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Image(
                        painter = painterResource(id = R.drawable.image_water_background),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Greeting(
                            deviceName = Settings.Global.getString(contentResolver, Settings.Global.DEVICE_NAME),
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(deviceName: String,modifier: Modifier = Modifier) {
    Text(
        text = getInfo(deviceName),
        color = Color.Black,
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    DemoTheme {
        Greeting("")
    }
}

fun getInfo(deviceName: String): String{
    val builder = StringBuilder()
    builder.append("MANUFACTURER: ${Build.MANUFACTURER}")
    builder.append("\n")
    builder.append("PRODUCT: ${Build.PRODUCT}")
    builder.append("\n")
    builder.append("MODEL: ${Build.MODEL}")
    builder.append("\n")
    builder.append("BRAND: ${Build.BRAND}")
    builder.append("\n")
    builder.append("BOARD: ${Build.BOARD}")
    builder.append("\n")
    builder.append("BOOTLOADER: ${Build.BOOTLOADER}")
    builder.append("\n")
    builder.append("DISPLAY: ${Build.DISPLAY}")
    builder.append("\n")
    builder.append("DEVICE_NAME: $deviceName")
    return builder.toString()
}
