package com.khairul.map

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import android.location.Location
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
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var myLocationMarker by remember { mutableStateOf<Marker?>(null) }
    var locationStatus by remember { mutableStateOf("Lokasi belum dikesan") }

    fun locateMe() {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fine && !coarse) return
        val client = LocationServices.getFusedLocationProviderClient(context)
        try {
            client.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val point = GeoPoint(location.latitude, location.longitude)
                    mapView?.controller?.animateTo(point)
                    mapView?.controller?.setZoom(16.0)
                    myLocationMarker?.let { mapView?.overlays?.remove(it) }
                    myLocationMarker = mapView?.let { view ->
                        Marker(view).apply {
                            position = point
                            title = "Lokasi Saya"
                            snippet = "Kedudukan semasa"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            view.overlays.add(this)
                        }
                    }
                    mapView?.invalidate()
                    locationStatus = "Lokasi semasa berjaya dikesan"
                } else {
                    locationStatus = "Lokasi belum tersedia. Hidupkan GPS dan cuba lagi."
                }
            }.addOnFailureListener {
                locationStatus = "Tidak dapat mendapatkan lokasi. Cuba lagi."
            }
        } catch (_: SecurityException) {
            locationStatus = "Kebenaran lokasi diperlukan."
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            locateMe()
        } else {
            locationStatus = "Kebenaran lokasi tidak diberikan."
        }
    }

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
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Cari tempat")
                }
                OutlinedButton(
                    onClick = {
                        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        if (fine || coarse) locateMe()
                        else locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Lokasi Saya")
                }
            }
            Text(locationStatus, modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.bodySmall)
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
                        mapView = this
                    }
                }
            )
        }
    }
}
