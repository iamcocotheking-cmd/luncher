package net.kdt.pojavlaunch.ui

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

internal data class DurbinGoogleProfile(
    val email: String,
    val displayName: String = "",
    val photoUrl: String = ""
)

internal data class DurbinModeTier(
    val mode: String,
    val tier: String = "UNRANKED"
)

internal data class DurbinTierAssignment(
    val email: String = "",
    val displayName: String = "",
    val tier: String = "UNRANKED",
    val tierTitle: String = "",
    val note: String = "",
    val modeTiers: List<DurbinModeTier> = emptyList(),
    val updatedAtMillis: Long = 0L
)

internal object DurbinTierRepository {
    private const val PREFS = "durbin_tier_profile"
    private const val KEY_EMAIL = "email"
    private const val KEY_NAME = "displayName"
    private const val KEY_PHOTO = "photoUrl"

    val defaultModeNames = listOf("Tank", "Crystal", "NetPot", "Sword", "Axe", "UHC", "SMP", "Pot")

    fun profileFromGoogle(account: GoogleSignInAccount): DurbinGoogleProfile? {
        val email = account.email?.trim()?.lowercase().orEmpty()
        if (email.isEmpty()) return null
        return DurbinGoogleProfile(
            email = email,
            displayName = account.displayName?.trim().orEmpty(),
            photoUrl = account.photoUrl?.toString().orEmpty()
        )
    }

    fun saveProfile(context: Context, profile: DurbinGoogleProfile) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_EMAIL, profile.email)
            .putString(KEY_NAME, profile.displayName)
            .putString(KEY_PHOTO, profile.photoUrl)
            .apply()
    }

    fun loadSavedProfile(context: Context): DurbinGoogleProfile? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val email = prefs.getString(KEY_EMAIL, null)?.trim()?.lowercase().orEmpty()
        if (email.isEmpty()) return null
        return DurbinGoogleProfile(
            email = email,
            displayName = prefs.getString(KEY_NAME, "").orEmpty(),
            photoUrl = prefs.getString(KEY_PHOTO, "").orEmpty()
        )
    }

    fun clearProfile(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun loadMyTier(
        email: String,
        onResult: (DurbinTierAssignment?) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        try {
            FirebaseFirestore.getInstance()
                .collection("tier_assignments")
                .document(email.trim().lowercase())
                .get()
                .addOnSuccessListener { document ->
                    if (!document.exists()) {
                        onResult(null)
                    } else {
                        onResult(documentToTier(document.id, document.data ?: emptyMap()))
                    }
                }
                .addOnFailureListener { error -> onError(error) }
        } catch (error: Throwable) {
            onError(error)
        }
    }

    fun loadPublicTierList(
        onResult: (List<DurbinTierAssignment>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        try {
            FirebaseFirestore.getInstance()
                .collection("tier_assignments")
                .whereEqualTo("visible", true)
                .limit(50)
                .get()
                .addOnSuccessListener { snapshot ->
                    val tiers = snapshot.documents.mapNotNull { doc ->
                        documentToTier(doc.id, doc.data ?: return@mapNotNull null)
                    }.sortedWith(
                        compareBy<DurbinTierAssignment> { tierSortIndex(it.tier) }
                            .thenBy { it.displayName.ifBlank { it.email } }
                    )
                    onResult(tiers)
                }
                .addOnFailureListener { error -> onError(error) }
        } catch (error: Throwable) {
            onError(error)
        }
    }

    private fun documentToTier(documentId: String, data: Map<String, Any>): DurbinTierAssignment {
        val email = (data["email"] as? String)?.trim()?.lowercase().orEmpty().ifBlank { documentId.trim().lowercase() }
        val tier = readTier(data, "tier", "overallTier", "mainTier").ifBlank { "UNRANKED" }
        val updated = when (val value = data["updatedAt"]) {
            is Timestamp -> value.toDate().time
            is Number -> value.toLong()
            else -> 0L
        }
        return DurbinTierAssignment(
            email = email,
            displayName = (data["displayName"] as? String)?.trim().orEmpty(),
            tier = tier,
            tierTitle = (data["tierTitle"] as? String)?.trim().orEmpty(),
            note = (data["note"] as? String)?.trim().orEmpty(),
            modeTiers = readModeTiers(data),
            updatedAtMillis = updated
        )
    }

    private fun readModeTiers(data: Map<String, Any>): List<DurbinModeTier> = listOf(
        DurbinModeTier("Tank", readTier(data, "tank", "tankTier", "tank_tier")),
        DurbinModeTier("Crystal", readTier(data, "crystal", "cristel", "crystalTier", "cristelTier", "crystal_tier", "crystalPvp", "crystalPvpTier", "cpvp", "cpvpTier", "cpvp_tier")),
        DurbinModeTier("NetPot", readTier(data, "netpot", "netPot", "netPotTier", "netpotTier", "net_pot", "nethpot", "nethPot", "nethPotTier", "nethpotTier", "netheritePot", "netheritePotTier")),
        DurbinModeTier("Sword", readTier(data, "sword", "swordTier", "sword_tier")),
        DurbinModeTier("Axe", readTier(data, "axe", "axeTier", "axe_tier")),
        DurbinModeTier("UHC", readTier(data, "uhc", "uhcTier", "uhc_tier")),
        DurbinModeTier("SMP", readTier(data, "smp", "smpTier", "smp_tier")),
        DurbinModeTier("Pot", readTier(data, "pot", "potTier", "potion", "potionTier"))
    ).map { if (it.tier.isBlank()) it.copy(tier = "UNRANKED") else it }

    private fun readTier(data: Map<String, Any>, vararg keys: String): String {
        for (key in keys) {
            val value = data[key]
            val text = when (value) {
                is String -> value.trim().uppercase()
                is Number -> value.toString().trim().uppercase()
                else -> ""
            }
            if (text.isNotBlank()) return text
        }
        return ""
    }

    fun tierSortIndex(tier: String): Int = when (tier.trim().uppercase()) {
        "HT1" -> 1
        "LT1" -> 2
        "HT2" -> 3
        "LT2" -> 4
        "HT3" -> 5
        "LT3" -> 6
        "HT4" -> 7
        "LT4" -> 8
        "HT5" -> 9
        "LT5" -> 10
        else -> 99
    }
}
