package net.kdt.pojavlaunch.ui

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

internal data class DurbinNewsItem(
    val title: String,
    val description: String,
    val imageUrl: String = "",
    val buttonText: String = "",
    val buttonUrl: String = "",
    val pinned: Boolean = false,
    val createdAtMillis: Long = 0L
)

internal object DurbinNewsRepository {
    fun loadNews(
        context: Context? = null,
        onResult: (List<DurbinNewsItem>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        try {
            if (context != null && FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            val db = FirebaseFirestore.getInstance()
            db.collection("news")
                .whereEqualTo("visible", true)
                .limit(10)
                .get()
                .addOnSuccessListener { snapshot ->
                    val items = snapshot.documents.toNewsItems()
                    if (items.isNotEmpty()) {
                        onResult(items)
                    } else {
                        // Some beginner Firestore setups accidentally store visible as a string.
                        // Try a normal read and filter locally, then fallback if still empty.
                        db.collection("news")
                            .limit(10)
                            .get()
                            .addOnSuccessListener { anySnapshot ->
                                val localItems = anySnapshot.documents.toNewsItems(allowStringVisible = true)
                                onResult(localItems.ifEmpty { fallbackNews() })
                            }
                            .addOnFailureListener { secondError ->
                                onError(secondError)
                                onResult(fallbackNews())
                            }
                    }
                }
                .addOnFailureListener { error ->
                    onError(error)
                    onResult(fallbackNews())
                }
        } catch (error: Throwable) {
            onError(error)
            onResult(fallbackNews())
        }
    }

    private fun List<DocumentSnapshot>.toNewsItems(allowStringVisible: Boolean = false): List<DurbinNewsItem> {
        return mapNotNull { doc ->
            val visibleValue = doc.get("visible")
            val visible = visibleValue == true ||
                    (allowStringVisible && visibleValue is String && visibleValue.equals("true", ignoreCase = true))
            if (!visible) return@mapNotNull null
            val title = doc.getString("title")?.trim().orEmpty()
            if (title.isEmpty()) return@mapNotNull null
            DurbinNewsItem(
                title = title,
                description = doc.getString("description")?.trim().orEmpty(),
                imageUrl = doc.getString("imageUrl")?.trim().orEmpty(),
                buttonText = doc.getString("buttonText")?.trim().orEmpty(),
                buttonUrl = doc.getString("buttonUrl")?.trim().orEmpty(),
                pinned = doc.getBoolean("pinned") ?: false,
                createdAtMillis = when (val value = doc.get("createdAt")) {
                    is Timestamp -> value.toDate().time
                    is Number -> value.toLong()
                    else -> 0L
                }
            )
        }.sortedWith(
            compareByDescending<DurbinNewsItem> { it.pinned }
                .thenByDescending { it.createdAtMillis }
        )
    }

    fun fallbackNews(): List<DurbinNewsItem> = listOf(
        DurbinNewsItem(
            title = "DURBIN Launcher News",
            description = "Firebase news is ready. If this shows after adding news, publish Firestore read rules and rebuild the latest APK.",
            buttonText = "Join Discord",
            buttonUrl = "https://discord.gg/PqnbXNrtHR",
            pinned = true
        )
    )
}
