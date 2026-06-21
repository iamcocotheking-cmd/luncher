package net.kdt.pojavlaunch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.instances.Instances
import net.kdt.pojavlaunch.ui.theme.PojavTheme
import java.io.IOException

class ProfileTypeSelectFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PojavTheme {
                    DurbinVersionSelectScreen(
                        onBack = { parentFragmentManager.popBackStack() },
                        onSelect = { version -> createDurbinProfile(version) }
                    )
                }
            }
        }
    }

    private fun createDurbinProfile(version: String) {
        try {
            val versionId = "fabric-loader-0.19.3-$version"
            val instance = Instances.createInstance({ i ->
                i.name = "DURBIN Fabric $version"
                i.icon = "durbin"
                i.sharedData = false
                i.versionId = versionId
                i.renderer = "opengles3_ltw"
            }, "DURBIN-Fabric-$version")
            Instances.setSelectedInstance(instance)
            Toast.makeText(requireContext(), "Selected DURBIN $version", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStackImmediate()
        } catch (e: IOException) {
            Tools.showError(requireContext(), e)
        }
    }

    companion object {
        const val TAG = "ProfileTypeSelectFragment"
    }
}

@Composable
private fun DurbinVersionSelectScreen(
    onBack: () -> Unit,
    onSelect: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.72f),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.92f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.weight(1f))
                    Text("DURBIN MOD VERSION", fontWeight = FontWeight.Black, fontSize = 24.sp)
                    Spacer(Modifier.weight(1f))
                    Spacer(Modifier.width(48.dp))
                }
                Text(
                    "Only official DURBIN mod versions are shown here.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    DurbinVersionButton(modifier = Modifier.weight(1f), version = "1.21.11", onSelect = onSelect)
                    DurbinVersionButton(modifier = Modifier.weight(1f), version = "1.20.1", onSelect = onSelect)
                }
            }
        }
    }
}

@Composable
private fun DurbinVersionButton(modifier: Modifier, version: String, onSelect: (String) -> Unit) {
    Button(
        onClick = { onSelect(version) },
        modifier = modifier.height(86.dp),
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Icon(Icons.Rounded.PlayArrow, contentDescription = null)
        Spacer(Modifier.width(10.dp))
        Text("DURBIN $version", fontWeight = FontWeight.Black, fontSize = 18.sp)
    }
}
