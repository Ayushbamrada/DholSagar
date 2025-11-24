// file: com/dholsagar/app/core/util/FileUtils.kt
package com.dholsagar.app.core.util

import android.content.ContentResolver
import android.net.Uri

object FileUtils {
    fun getMimeType(contentResolver: ContentResolver, uri: Uri): String? {
        return contentResolver.getType(uri)
    }
}