package com.example.domi

import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun MapScreen() {
    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Domi Map</title>
            <meta charset="utf-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <style>
                html, body { height: 100%; width: 100%; margin: 0; padding: 0; background-color: #fdf8f1; }
                #map { height: 100%; width: 100%; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <script>
                function initMap() {
                    // Koordinate centra Hrvatske
                    var map = L.map('map').setView([44.5, 16.5], 7);
                    
                    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        maxZoom: 19,
                        attribution: '&copy; OpenStreetMap'
                    }).addTo(map);

                    var shelters = [
                        { lat: 45.8300, lng: 16.1400, name: "Sklonište Dumovec" },
                        { lat: 46.3900, lng: 16.4350, name: "Azil Prijatelji Čakovec" },
                        { lat: 45.5550, lng: 18.6750, name: "Sklonište za pse Osijek" },
                        { lat: 45.3270, lng: 14.4420, name: "Sklonište za životinje Rijeka" },
                        { lat: 45.1600, lng: 18.0150, name: "Azil Šapa Slavonski Brod" },
                        { lat: 42.6400, lng: 18.1100, name: "Sklonište Žarkovica Dubrovnik" }
                    ];

                    shelters.forEach(function(s) {
                        L.marker([s.lat, s.lng]).addTo(map).bindPopup("<b>" + s.name + "</b>");
                    });
                    
                    // Force size update
                    setTimeout(function() {
                        map.invalidateSize();
                    }, 500);
                }

                // Pokreni kad se sve učita
                window.onload = initMap;
            </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()

                loadDataWithBaseURL(
                    "https://appassets.androidview.com",
                    htmlContent,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
