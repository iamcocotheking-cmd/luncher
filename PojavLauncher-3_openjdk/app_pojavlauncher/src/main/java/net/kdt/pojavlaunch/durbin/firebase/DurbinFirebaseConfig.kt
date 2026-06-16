package net.kdt.pojavlaunch.durbin.firebase

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import net.ashmeet.hyperlauncher.R

object DurbinFirebaseConfig {
    fun ensureInitialized(context: Context): Boolean {
        if (FirebaseApp.getApps(context).isNotEmpty()) return true

        val apiKey = context.getString(R.string.durbin_firebase_api_key).trim()
        val appId = context.getString(R.string.durbin_firebase_application_id).trim()
        val projectId = context.getString(R.string.durbin_firebase_project_id).trim()
        val databaseUrl = context.getString(R.string.durbin_firebase_database_url).trim()

        if (apiKey.isBlank() || appId.isBlank() || projectId.isBlank() || databaseUrl.isBlank()) return false
        if (apiKey.startsWith("PASTE_") || appId.startsWith("PASTE_") || projectId.startsWith("PASTE_") || databaseUrl.startsWith("PASTE_")) return false

        val options = FirebaseOptions.Builder()
            .setApiKey(apiKey)
            .setApplicationId(appId)
            .setProjectId(projectId)
            .setDatabaseUrl(databaseUrl)
            .build()

        FirebaseApp.initializeApp(context.applicationContext, options)
        return FirebaseApp.getApps(context).isNotEmpty()
    }

    fun isGoogleLoginConfigured(context: Context): Boolean {
        val clientId = context.getString(R.string.durbin_firebase_web_client_id).trim()
        return clientId.isNotBlank() && !clientId.startsWith("PASTE_")
    }
}
