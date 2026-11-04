package com.khairul.map

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        setContent { KhairulMapScreen() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KhairulMapScreen() {
    val context = LocalContext.current
    val kualaLumpur = LatLng(3.1390, 101.6869)
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(kualaLumpur, 11f)
    }
    var destination by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("KHAIRUL MAP") }) },
        bottomBar = {
            Column {
                AndroidView(
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    factory = { ctx ->
                        AdView(ctx).apply {
                            setAdSize(AdSize.BANNER)
                            // Official Google test banner. Replace only after AdMob testing/review.
                            adUnitId = "ca-app-pub-3940256099942544/6300978111"
                            loadAd(AdRequest.Builder().build())
                        }
                    }
                )
                Text(
                    "KHAIRUL MAP • Google Maps",
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                singleLine = true,
                label = { Text("Cari alamat atau tempat") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
            Button(
                onClick = {
                    if (destination.isNotBlank()) {
                        val uri = Uri.parse("google.navigation:q=" + Uri.encode(destination))
                        val intent = Intent(Intent.ACTION_VIEW, uri).setPackage("com.google.android.apps.maps")
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(destination)))
                            )
                        }
                    }
                },
                enabled = destination.isNotBlank(),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cari & navigasi dengan Google Maps")
            }
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraState,
                properties = MapProperties(isMyLocationEnabled = false),
                uiSettings = MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = false)
            ) {
                Marker(
                    state = MarkerState(position = kualaLumpur),
                    title = "Kuala Lumpur",
                    snippet = "Lokasi permulaan"
                )
            }
        }
    }
}
