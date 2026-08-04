package abhishek.gupta.weatherapp.presentation.homeScreen

import abhishek.gupta.weatherapp.R
import abhishek.gupta.weatherapp.data.local.entity.EntityForecastData
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt



@RequiresApi(Build.VERSION_CODES.O)
private val displayTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
@RequiresApi(Build.VERSION_CODES.O)
private val displayDateFormatter = DateTimeFormatter.ofPattern("d MMMM")



@RequiresApi(Build.VERSION_CODES.O)
private val dateTimeParsers = listOf(
    DateTimeFormatter.ISO_LOCAL_DATE_TIME,
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
)

@RequiresApi(Build.VERSION_CODES.O)
private val timeOnlyParsers = listOf(
    DateTimeFormatter.ofPattern("HH:mm:ss"),
    DateTimeFormatter.ofPattern("HH:mm")
)

@RequiresApi(Build.VERSION_CODES.O)
private fun String.toDisplayTime(): String {
    for (p in dateTimeParsers) {
        runCatching { return LocalDateTime.parse(this, p).format(displayTimeFormatter) }
    }
    for (p in timeOnlyParsers) {
        runCatching { return LocalTime.parse(this, p).format(displayTimeFormatter) }
    }
    return this
}

@RequiresApi(Build.VERSION_CODES.O)
private fun String.toDisplayDateOrNull(): String? {
    for (p in dateTimeParsers) {
        runCatching { return LocalDateTime.parse(this, p).format(displayDateFormatter) }
    }
    return null
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ForecastChartUI(city: String, homeViewModel: HomeViewmodel) {

    val forecastEntity = homeViewModel.localForecastData.collectAsState().value
    val today = remember { LocalDate.now().format(displayDateFormatter) }
    val isDark = isSystemInDarkTheme()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->

        val context = LocalContext.current
        val activity = context as? Activity

        DisposableEffect(Unit) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            onDispose {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }

        val backgroundBrush = if (isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.colorScheme.background
                )
            )
        } else {
            Brush.verticalGradient(colors = listOf(Color(0xFFEAF6FF), Color(0xFFFFFFFF)))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
//                .background(backgroundBrush)
            ,
            contentAlignment = Alignment.Center
        ) {

            AnimatedVisibility(
                visible = forecastEntity != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                forecastEntity?.let {
                    ForecastChart(
                        forecastList = it.DayData,
                        modifier = Modifier.fillMaxSize(),
                        city = city,
                        date = today
                    )
                }
            }

            AnimatedVisibility(
                visible = forecastEntity == null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "Fetching latest forecast…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ForecastChart(
    forecastList: List<EntityForecastData>,
    modifier: Modifier = Modifier,
    city: String,
    date: String
) {
    if (forecastList.isEmpty()) return

    val isDark = isSystemInDarkTheme()
    val minTemp = forecastList.minOf { it.temperature }
    val maxTemp = forecastList.maxOf { it.temperature }
    val avgTemp = (forecastList.sumOf { it.temperature.toDouble() } / forecastList.size).roundToInt()

    val onSurface = MaterialTheme.colorScheme.onSurface.toArgb()
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val surface = MaterialTheme.colorScheme.surface
    val gridColor = if (isDark) 0x33FFFFFF else 0xFFE3F2FD.toInt()
    val lineColor = if (isDark) 0xFF64B5F6.toInt() else 0xFF2196F3.toInt()
    val fillTop = if (isDark) 0x6664B5F6.toInt() else 0x662196F3.toInt()
    val fillBottom = if (isDark) 0x0064B5F6.toInt() else 0x002196F3.toInt()
    val highLightColor = if (isDark) 0xFF546E7A.toInt() else 0xFF90A4AE.toInt()
    val limitLineColor = if (isDark) 0xFFEF9A9A.toInt() else 0xFFFFCDD2.toInt()
    val limitTextColor = 0xFFEF5350.toInt()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Text(
            text = "$city · $date",
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 0.5.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
        )


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            AndroidView(
                factory = { ctx ->
                    LineChart(ctx).apply {
                        description.isEnabled = false
                        setTouchEnabled(true)
                        setPinchZoom(true)
                        isDoubleTapToZoomEnabled = false
                        setDrawGridBackground(false)
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        setExtraOffsets(8f, 28f, 8f, 12f)
                        legend.isEnabled = false

                        val entries = forecastList.mapIndexed { index, forecast ->
                            Entry(index.toFloat(), forecast.temperature.toFloat())
                        }

                        val dataSet = LineDataSet(entries, "Temperature").apply {
                            color = lineColor
                            setCircleColor(lineColor)
                            lineWidth = 2.8f
                            circleRadius = 3.5f
                            circleHoleRadius = 1.6f
                            setDrawCircleHole(true)
                            valueTextColor = onSurface
                            valueTextSize = 10f
                            mode = LineDataSet.Mode.CUBIC_BEZIER
                            cubicIntensity = 0.15f
                            setDrawValues(false)
                            setDrawFilled(true)

                            fillDrawable = GradientDrawable(
                                GradientDrawable.Orientation.TOP_BOTTOM,
                                intArrayOf(fillTop, fillBottom)
                            )

                            this.highLightColor = highLightColor
                            highlightLineWidth = 1.2f
                            setDrawHorizontalHighlightIndicator(false)
                        }

                        data = LineData(dataSet)

                        axisLeft.removeAllLimitLines()

                        axisLeft.addLimitLine(
                            LimitLine(maxTemp.toFloat(), "High").apply {
                                this.lineColor = limitLineColor
                                lineWidth = 1f
                                enableDashedLine(10f, 6f, 0f)
                                textColor = limitTextColor
                                textSize = 9f
                            }
                        )

                        axisLeft.addLimitLine(
                            LimitLine(minTemp.toFloat(), "Low").apply {
                                this.lineColor = if (isDark) 0xFF66BB6A.toInt() else 0xFF4CAF50.toInt()
                                lineWidth = 1f
                                enableDashedLine(10f, 6f, 0f)
                                textColor = if (isDark) 0xFF81C784.toInt() else 0xFF388E3C.toInt()
                                textSize = 9f
                            }
                        )
                        axisLeft.addLimitLine(
                            LimitLine(avgTemp.toFloat(), "Avg").apply {
                                this.lineColor = if (isDark) 0xFF7986CB.toInt() else 0xFF5C6BC0.toInt()
                                lineWidth = 1f
                                enableDashedLine(6f, 4f, 0f)
                                textColor = if (isDark) 0xFF9FA8DA.toInt() else 0xFF3F51B5.toInt()
                                textSize = 9f
                            }
                        )

                        xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(false)
                            setDrawAxisLine(false)
                            textColor = onSurfaceVariant
                            textSize = 10f
                            granularity = 1f
                            labelCount = minOf(6, forecastList.size)
                            valueFormatter = IndexAxisValueFormatter(
                                forecastList.map { it.time.toDisplayTime() }
                            )
                        }

                        axisLeft.apply {
                            textColor = onSurfaceVariant
                            setDrawGridLines(true)
                            this.gridColor = gridColor
                            setDrawAxisLine(false)
                            axisMinimum = (minTemp - 2).toFloat()
                            axisMaximum = (maxTemp + 2).toFloat()
                            valueFormatter = object : ValueFormatter() {
                                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                                    return "${value.toInt()}°"
                                }
                            }
                        }

                        axisRight.isEnabled = false
                        marker = TemperatureMarkerView(ctx, forecastList)

                        animateY(700)
                        invalidate()
                    }
                },
                update = { chart -> chart.invalidate() },
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(surface)
                    .padding(8.dp)
            )


            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.85f)
                    .padding(top = 14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CompactStatChip(label = "LOW", value = "${minTemp}°", color = Color(0xFF42A5F5))
                CompactStatChip(label = "AVG", value = "${avgTemp}°", color = MaterialTheme.colorScheme.primary)
                CompactStatChip(label = "HIGH", value = "${maxTemp}°", color = Color(0xFFEF5350))
            }
        }
    }
}

@Composable
private fun CompactStatChip(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 9.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

private class TemperatureMarkerView(
    context: Context,
    private val forecastList: List<EntityForecastData>
) : MarkerView(context, R.layout.marker_view) {

    private val tvContent: TextView = findViewById(R.id.tvContent)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val index = e?.x?.roundToInt() ?: 0
        val forecast = forecastList.getOrNull(index)
        if (forecast != null) {
            val timeStr = forecast.time.toDisplayTime()
            val dateStr = forecast.time.toDisplayDateOrNull()
            val header = if (dateStr != null) "$dateStr, $timeStr" else timeStr
            tvContent.text = "$header\n${forecast.temperature}°C"
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat() - 12f)
    }
}