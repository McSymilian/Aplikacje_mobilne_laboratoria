package com.example.aplikacjatypulista_szczegy

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplikacjatypulista_szczegy.ui.theme.AplikacjaTypuListaszczegolyTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class SplashActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private val sensorX = mutableFloatStateOf(0f)
    private val sensorY = mutableFloatStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            AplikacjaTypuListaszczegolyTheme {
                SplashScreen(
                    sensorTiltX = sensorX.floatValue,
                    sensorTiltY = sensorY.floatValue,
                    onAnimationFinished = {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                        @Suppress("DEPRECATION")
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                sensorX.floatValue += (it.values[0] - sensorX.floatValue) * 0.1f
                sensorY.floatValue += (it.values[1] - sensorY.floatValue) * 0.1f
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

@Composable
private fun SplashScreen(
    sensorTiltX: Float,
    sensorTiltY: Float,
    onAnimationFinished: () -> Unit
) {
    val sunAlpha = remember { Animatable(0f) }
    val sunTranslationY = remember { Animatable(200f) }
    val sunScale = remember { Animatable(0.5f) }

    val mountainAlpha = remember { Animatable(0f) }
    val mountainTranslationY = remember { Animatable(300f) }

    val cloud1Alpha = remember { Animatable(0f) }
    val cloud1TranslationX = remember { Animatable(-400f) }
    val cloud2Alpha = remember { Animatable(0f) }
    val cloud2TranslationX = remember { Animatable(400f) }

    val hikerAlpha = remember { Animatable(0f) }
    val hikerScale = remember { Animatable(0f) }
    val hikerTranslationY = remember { Animatable(100f) }

    val titleAlpha = remember { Animatable(0f) }
    val titleScale = remember { Animatable(0.3f) }

    LaunchedEffect(Unit) {
        launch {
            launch { sunAlpha.animateTo(1f, tween(1200, easing = EaseOutCubic)) }
            launch { sunTranslationY.animateTo(0f, tween(1200, easing = EaseOutCubic)) }
            launch { sunScale.animateTo(1f, tween(1200, easing = EaseOutCubic)) }
        }

        launch {
            launch { mountainAlpha.animateTo(1f, tween(1000, easing = EaseOutBounce)) }
            launch { mountainTranslationY.animateTo(0f, tween(1000, easing = EaseOutBounce)) }
        }

        launch {
            launch { cloud1Alpha.animateTo(0.85f, tween(1400, easing = FastOutSlowInEasing)) }
            launch { cloud1TranslationX.animateTo(0f, tween(1400, easing = FastOutSlowInEasing)) }
        }

        launch {
            launch { cloud2Alpha.animateTo(0.85f, tween(1600, easing = FastOutSlowInEasing)) }
            launch { cloud2TranslationX.animateTo(0f, tween(1600, easing = FastOutSlowInEasing)) }
        }

        launch {
            delay(600)
            launch { hikerAlpha.animateTo(1f, tween(800, easing = EaseOutCubic)) }
            launch { hikerScale.animateTo(1f, tween(800, easing = EaseOutBack)) }
            launch { hikerTranslationY.animateTo(0f, tween(800, easing = EaseOutBack)) }
        }

        launch {
            delay(800)
            launch { titleAlpha.animateTo(1f, tween(800, easing = EaseOutCubic)) }
            launch { titleScale.animateTo(1f, tween(800, easing = EaseOutBack)) }
        }

        delay(3500)
        onAnimationFinished()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ambient")

    val cloudDrift1 by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloudDrift1"
    )
    val cloudDrift2 by infiniteTransition.animateFloat(
        initialValue = 15f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloudDrift2"
    )

    val sunPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sunPulse"
    )

    val hikerSway by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hikerSway"
    )

    val titleFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleFloat"
    )

    val bird1X by infiniteTransition.animateFloat(
        initialValue = -0.1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bird1X"
    )
    val bird2X by infiniteTransition.animateFloat(
        initialValue = -0.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bird2X"
    )
    val birdWing by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "birdWing"
    )

    val leaf1Y by infiniteTransition.animateFloat(
        initialValue = -0.1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "leaf1Y"
    )
    val leaf2Y by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "leaf2Y"
    )
    val leafRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "leafRotation"
    )
    val leafSway by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "leafSway"
    )

    val loadingRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loadingRotation"
    )

    val dot1Scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1Scale"
    )
    val dot2Scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2Scale"
    )
    val dot3Scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3Scale"
    )

    val cloudParallax1 = -sensorTiltX * 8f
    val cloudParallax2 = -sensorTiltX * 12f
    val sunParallaxX = -sensorTiltX * 4f
    val sunParallaxY = sensorTiltY * 3f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(sunAlpha.value)
                .scale(sunScale.value * sunPulse)
                .offset {
                    IntOffset(
                        sunParallaxX.roundToInt(),
                        (sunTranslationY.value + sunParallaxY).roundToInt()
                    )
                }
        ) {
            drawSun()
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(mountainAlpha.value)
                .offset { IntOffset(0, mountainTranslationY.value.roundToInt()) }
        ) {
            drawMountains()
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(hikerAlpha.value)
                .scale(hikerScale.value)
                .offset { IntOffset(hikerSway.roundToInt(), hikerTranslationY.value.roundToInt()) }
        ) {
            drawHiker()
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(cloud1Alpha.value)
                .offset {
                    IntOffset(
                        (cloud1TranslationX.value + cloudParallax1 + cloudDrift1).roundToInt(),
                        0
                    )
                }
        ) {
            drawCloud(0.2f, 0.12f)
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(cloud2Alpha.value)
                .offset {
                    IntOffset(
                        (cloud2TranslationX.value + cloudParallax2 + cloudDrift2).roundToInt(),
                        0
                    )
                }
        ) {
            drawCloud(0.6f, 0.18f)
        }

        Text(
            text = "Moje Szlaki",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
                .alpha(titleAlpha.value)
                .scale(titleScale.value)
                .offset { IntOffset(0, titleFloat.roundToInt()) },
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20)
        )

        Text(
            text = "Twój przewodnik po trasach",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 150.dp)
                .alpha(titleAlpha.value)
                .scale(titleScale.value)
                .offset { IntOffset(0, titleFloat.roundToInt()) },
            fontSize = 16.sp,
            color = Color(0xFF388E3C)
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(mountainAlpha.value)
        ) {
            drawBird(bird1X, 0.08f, birdWing, 1f)
            drawBird(bird2X, 0.15f, birdWing, 0.7f)
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(mountainAlpha.value)
        ) {
            drawLeaf(0.7f + leafSway / size.width, leaf1Y, leafRotation, Color(0xFF8BC34A))
            drawLeaf(0.4f + leafSway * 0.7f / size.width, leaf2Y, leafRotation * 0.8f, Color(0xFFFF9800))
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
                .alpha(titleAlpha.value)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .size(48.dp)
                        .rotate(loadingRotation)
                ) {
                    drawLoadingRing()
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LoadingDot(scale = dot1Scale, color = Color(0xFF1B5E20))
                    LoadingDot(scale = dot2Scale, color = Color(0xFF388E3C))
                    LoadingDot(scale = dot3Scale, color = Color(0xFF4CAF50))
                }

                Text(
                    text = "Ładowanie...",
                    fontSize = 14.sp,
                    color = Color(0xFF388E3C)
                )
            }
        }
    }
}

private fun DrawScope.drawSun() {
    val cx = size.width * 0.75f
    val cy = size.height * 0.2f
    drawCircle(Color(0x40FFC107), 90f, Offset(cx, cy))
    drawCircle(Color(0xFFFFC107), 60f, Offset(cx, cy))
}

private fun DrawScope.drawMountains() {
    val w = size.width
    val h = size.height
    val baseY = h * 0.85f

    val mountain1 = Path().apply {
        moveTo(w * 0.05f, baseY)
        lineTo(w * 0.4f, h * 0.35f)
        lineTo(w * 0.75f, baseY)
        close()
    }
    drawPath(mountain1, Color(0xFF388E3C))

    val snow1 = Path().apply {
        moveTo(w * 0.35f, h * 0.38f)
        lineTo(w * 0.4f, h * 0.35f)
        lineTo(w * 0.45f, h * 0.38f)
        lineTo(w * 0.42f, h * 0.42f)
        lineTo(w * 0.38f, h * 0.41f)
        close()
    }
    drawPath(snow1, Color.White)

    val mountain2 = Path().apply {
        moveTo(w * 0.3f, baseY)
        lineTo(w * 0.65f, h * 0.45f)
        lineTo(w * 1.0f, baseY)
        close()
    }
    drawPath(mountain2, Color(0xFF2E7D32))

    val snow2 = Path().apply {
        moveTo(w * 0.61f, h * 0.48f)
        lineTo(w * 0.65f, h * 0.45f)
        lineTo(w * 0.69f, h * 0.48f)
        lineTo(w * 0.66f, h * 0.51f)
        lineTo(w * 0.63f, h * 0.50f)
        close()
    }
    drawPath(snow2, Color.White)

    drawRect(Color(0xFF4CAF50), Offset(0f, baseY), Size(w, h - baseY))
}

private fun DrawScope.drawHiker() {
    val cx = size.width * 0.3f
    val cy = size.height * 0.72f

    drawLine(Color(0xFF795548), Offset(cx + 15f, cy - 20f), Offset(cx + 35f, cy + 40f), 4f)
    drawRect(Color(0xFF5D4037), Offset(cx - 12f, cy - 15f), Size(24f, 40f))
    drawRect(Color(0xFFF44336), Offset(cx - 20f, cy - 10f), Size(12f, 25f))
    drawCircle(Color(0xFFFFCC80), 12f, Offset(cx, cy - 25f))
    drawLine(Color(0xFF795548), Offset(cx - 6f, cy + 25f), Offset(cx - 10f, cy + 42f), 4f)
    drawLine(Color(0xFF795548), Offset(cx + 6f, cy + 25f), Offset(cx + 10f, cy + 42f), 4f)
}

private fun DrawScope.drawCloud(offsetX: Float, offsetY: Float) {
    val cx = size.width * offsetX
    val cy = size.height * offsetY
    val c = Color(0xFFECEFF1)
    drawCircle(c, 45f, Offset(cx, cy))
    drawCircle(c, 35f, Offset(cx + 35f, cy - 10f))
    drawCircle(c, 30f, Offset(cx - 30f, cy + 5f))
    drawCircle(c, 28f, Offset(cx + 60f, cy + 5f))
}

private fun DrawScope.drawBird(progressX: Float, offsetY: Float, wingAngle: Float, scale: Float) {
    val cx = size.width * progressX
    val cy = size.height * offsetY
    val wingLength = 18f * scale
    val bodyColor = Color(0xFF37474F)

    drawCircle(bodyColor, 4f * scale, Offset(cx, cy))

    val leftWingEnd = Offset(
        cx - wingLength * cos(Math.toRadians(wingAngle.toDouble())).toFloat(),
        cy - wingLength * sin(Math.toRadians((30f + wingAngle).toDouble())).toFloat()
    )
    drawLine(bodyColor, Offset(cx, cy), leftWingEnd, 2.5f * scale, cap = StrokeCap.Round)

    val rightWingEnd = Offset(
        cx + wingLength * cos(Math.toRadians(wingAngle.toDouble())).toFloat(),
        cy - wingLength * sin(Math.toRadians((30f + wingAngle).toDouble())).toFloat()
    )
    drawLine(bodyColor, Offset(cx, cy), rightWingEnd, 2.5f * scale, cap = StrokeCap.Round)
}

private fun DrawScope.drawLeaf(progressX: Float, progressY: Float, rotation: Float, leafColor: Color) {
    val cx = size.width * progressX
    val cy = size.height * progressY
    val leafSize = 12f

    rotate(rotation, Offset(cx, cy)) {
        val leafPath = Path().apply {
            moveTo(cx, cy - leafSize)
            quadraticTo(cx + leafSize, cy, cx, cy + leafSize)
            quadraticTo(cx - leafSize, cy, cx, cy - leafSize)
            close()
        }
        drawPath(leafPath, leafColor)
        drawLine(
            Color(0xFF33691E),
            Offset(cx, cy - leafSize + 2f),
            Offset(cx, cy + leafSize - 2f),
            1.5f
        )
    }
}

private fun DrawScope.drawLoadingRing() {
    val strokeWidth = 4.dp.toPx()
    val radius = (size.minDimension - strokeWidth) / 2f

    drawCircle(
        color = Color(0x304CAF50),
        radius = radius,
        style = Stroke(width = strokeWidth)
    )

    drawArc(
        brush = Brush.sweepGradient(
            colors = listOf(
                Color.Transparent,
                Color(0xFF1B5E20),
                Color(0xFF4CAF50),
                Color(0xFF8BC34A)
            )
        ),
        startAngle = 0f,
        sweepAngle = 270f,
        useCenter = false,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
        size = Size(size.width - strokeWidth, size.height - strokeWidth)
    )
}

@Composable
private fun LoadingDot(scale: Float, color: Color) {
    Surface(
        modifier = Modifier
            .size(10.dp)
            .scale(scale),
        shape = CircleShape,
        color = color
    ) {}
}
