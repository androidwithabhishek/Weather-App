package abhishek.gupta.weatherapp.presentation.utilsScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun WeatherDetailItem(
    icon: ImageVector,
    label: String,
    value: String,
) {

    val iconColor = when (label) {
        "Humidity" -> Color(0xFF2196F3)
        "Pressure" -> Color(0xFF4CAF50)
        "AQI" -> Color(0xFFFF9800)
        else -> Color.Black
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = iconColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}