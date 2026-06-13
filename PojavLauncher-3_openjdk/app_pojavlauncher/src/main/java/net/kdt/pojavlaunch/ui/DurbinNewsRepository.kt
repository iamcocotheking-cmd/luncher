package net.kdt.pojavlaunch.ui

import com.google.firebase.Timestamp
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
        onResult: (List<DurbinNewsItem>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        try {
            FirebaseFirestore.getInstance()
                .collection("news")
                .whereEqualTo("visible", true)
                .limit(5)
                .get()
                .addOnSuccessListener { snapshot ->
                    val items = snapshot.documents.mapNotNull { doc ->
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
                    onResult(items.ifEmpty { fallbackNews() })
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

    fun fallbackNews(): List<DurbinNewsItem> = listOf(
        DurbinNewsItem(
            title = "DURBIN Launcher News",
            description = "Firebase news is ready. Add posts in your Firestore news collection.",
            buttonText = "Join Discord",
            buttonUrl = "https://discord.gg/PqnbXNrtHR",
            pinned = true
        )
    )
}
