//// file: com/dholsagar/app/presentation/onboarding_provider/components/PortfolioPage.kt
//package com.dholsagar.app.presentation.onboarding_provider.components
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AccountCircle
////import androidx.compose.material.icons.filled.AddAPhoto
//import androidx.compose.material.icons.filled.AddCircle
////import androidx.compose.material.icons.filled.Videocam
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun PortfolioPage() {
//    Column(
//        modifier = Modifier.fillMaxSize().padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text("Showcase Your Work", style = MaterialTheme.typography.headlineSmall)
//        Spacer(modifier = Modifier.height(32.dp))
//
//        Button(onClick = { /* TODO: Implement image picker logic */ }) {
//            Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Upload Photos")
//            Spacer(modifier = Modifier.width(8.dp))
//            Text("Upload Portfolio Photos")
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        OutlinedTextField(
//            value = "",
//            onValueChange = { /* TODO: Handle video link state */ },
//            label = { Text("YouTube Video Link (Optional)") },
//            leadingIcon = { Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null) },
//            modifier = Modifier.fillMaxWidth()
//        )
//    }
//}

// file: com/dholsagar/app/presentation/onboarding_provider/components/PortfolioPage.kt
package com.dholsagar.app.presentation.onboarding_provider.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.PlayArrow
//import androidx.compose.material.icons.outlined.VideoCall
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import android.provider.OpenableColumns // <-- ADD THIS IMPORT
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext // <-- ADD THIS IMPORT
import coil.compose.AsyncImage

fun getDisplayNameFromUri(uri: Uri, fallback: String): String {
    // A simple way to get a more readable name, though it's not guaranteed to be the original file name.
    // For a more robust solution, you would query the ContentResolver.
    return uri.lastPathSegment?.substringAfterLast('/') ?: fallback
}

@Composable
fun PortfolioPage(
    selectedImageUris: List<Uri>,
    onImagesSelected: (List<Uri>) -> Unit,
    onRemoveImage: (Uri) -> Unit,
    selectedVideoUri: Uri?,
    onVideoSelected: (Uri?) -> Unit,
    youtubeLink: String,
    onYoutubeLinkChange: (String) -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris -> onImagesSelected(uris) }
    )
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> onVideoSelected(uri) }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column {
            Text("Showcase Your Work", style = MaterialTheme.typography.headlineSmall)
            Text(
                "A great portfolio helps you get more bookings.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Portfolio Photos", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Upload 5 to 10 photos of you or your band performing.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(selectedImageUris) { uri ->
                        ImagePreviewItem(uri = uri, onRemoveClick = { onRemoveImage(uri) })
                    }
                    if (selectedImageUris.size < 10) {
                        item {
                            AddMoreBox(onClick = { imagePickerLauncher.launch("image/*") })
                        }
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("1-Minute Performance Video", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                if (selectedVideoUri != null) {
                    FilePreviewItem(
                        uri = selectedVideoUri,
                        onRemoveClick = { onVideoSelected(null) }
                    )
                } else {
                    Text("Upload a short video (max 1 min) to show your talent.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { videoPickerLauncher.launch("video/*") }) {
                        Icon(Icons.Outlined.PlayArrow, contentDescription = "Upload Video")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Select Video")
                    }
                }
            }
        }

        OutlinedTextField(
            value = youtubeLink,
            onValueChange = onYoutubeLinkChange,
            label = { Text("YouTube Video Link (Optional)") },
            leadingIcon = { Icon(Icons.Outlined.PlayArrow, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            singleLine = true
        )
    }
}

// --- THESE HELPER COMPOSABLES WERE MISSING ---

@Composable
fun ImagePreviewItem(uri: Uri, onRemoveClick: () -> Unit) {
    Box {
        AsyncImage(
            model = uri,
            contentDescription = "Selected image",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = onRemoveClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), CircleShape)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Remove image", tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun AddMoreBox(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add more photos")
    }
}

@Composable
fun FilePreviewItem(uri: Uri, onRemoveClick: () -> Unit) {
    val context = LocalContext.current

    // This is the improved way to get a readable file name for any Uri
    val fileName = remember(uri) {
        var name: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = cursor.getString(nameIndex)
                }
            }
        }
        name ?: uri.lastPathSegment ?: "Selected File"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = fileName, // Use the new, clean file name
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
        IconButton(onClick = onRemoveClick) {
            Icon(Icons.Default.Close, contentDescription = "Remove file")
        }
    }
}