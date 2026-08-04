package abhishek.gupta.weatherapp.presentation.utilsScreens

import abhishek.gupta.weatherapp.data.local.entity.EntityForecastData
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Deblur
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyForecastItem(hourly: EntityForecastData, time: String) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(
                text = time,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))


            val (iconVector, iconTint) = when (hourly.icon) {
                "01d", "01n" -> Icons.Default.WbSunny to Color(0xFFFFD54F)
                "02d", "02n", "03d", "03n" -> Icons.Default.WbCloudy to Color(0xFF90A4AE)
                "04d", "04n" -> Icons.Default.Cloud to Color(0xFF78909C)
                "09d", "09n", "10d", "10n" -> Icons.Default.WaterDrop to Color(0xFF64B5F6)
                "11d", "11n" -> Icons.Default.Bolt to Color(0xFFFFA000)
                "13d", "13n" -> Icons.Default.AcUnit to Color(0xFFB3E5FC)
                "50d", "50n" -> Icons.Default.Deblur to Color(0xFFB0BEC5)
                else -> Icons.Default.Help to Color.Gray
            }
            Icon(
                imageVector = iconVector,
                contentDescription = hourly.condition,
                modifier = Modifier.size(24.dp),
                tint = iconTint
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Temperature
            Text(
                text = "${hourly.temperature}°",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}