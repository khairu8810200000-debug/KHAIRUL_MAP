package com.khairul.map

import android.app.Activity
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
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = packageName
        MobileAds.initialize(this) {}
        setContent { KhairulMapScreen() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KhairulMapScreen() {
    val context = LocalContext.current
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
                            adUnitId = "ca-app-pub-3940256099942544/6300978111"
                            loadAd(AdRequest.Builder().build())
                        }
                    }
                )
                Text("KHAIRUL MAP • OpenStreetMap",
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    style = MaterialTheme.typography.labelSmall)
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
                        val openDestination = {
                            val url = "https://www.openstreetmap.org/search?query=" + Uri.encode(destination)
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        }
                        val activity = context as? Activity
                        if (activity == null) openDestination()
                        else InterstitialAd.load(
                            context,
                            "ca-app-pub-3940256099942544/1033173712",
                            AdRequest.Builder().build(),
                            object : InterstitialAdLoadCallback() {
                                override fun onAdLoaded(ad: InterstitialAd) {
                                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                                        override fun onAdDismissedFullScreenContent() = openDestination()
                                        override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) = openDestination()
                                    }
                                    ad.show(activity)
                                }
                                override fun onAdFailedToLoad(error: LoadAdError) = openDestination()
                            }
                        )
                    }
                },
                enabled = destination.isNotBlank(),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cari di OpenStreetMap")
            }
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    Configuration.getInstance().userAgentValue = ctx.packageName
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(11.0)
                        val kl = GeoPoint(3.1390, 101.6869)
                        controller.setCenter(kl)
                        overlays.add(Marker(this).apply {
                            position = kl
                            title = "Kuala Lumpur"
                            snippet = "Lokasi permulaan"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        })
                    }
                }
            )
        }
    }
}
