package net.kdt.pojavlaunch.durbin.firebase

data class DurbinNewsItem(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val imageUrl: String = "",
    val linkUrl: String = "",
    val tag: String = "News",
    val timestamp: Long = 0L,
    val pinned: Boolean = false
)

data class DurbinTierEntry(
    val uid: String = "",
    val ign: String = "",
    val tier: String = "LT5",
    val score: Int = 0,
    val region: String = "",
    val country: String = "",
    val notes: String = "",
    val profileImageUrl: String = "",
    val verified: Boolean = false,
    val updatedAt: Long = 0L
)

data class DurbinTierCategory(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val entries: List<DurbinTierEntry> = emptyList()
)

data class DurbinMyRank(
    val categoryId: String = "",
    val categoryName: String = "",
    val tier: String = "Unranked",
    val score: Int = 0,
    val ign: String = "",
    val region: String = "",
    val profileImageUrl: String = "",
    val updatedAt: Long = 0L
)
