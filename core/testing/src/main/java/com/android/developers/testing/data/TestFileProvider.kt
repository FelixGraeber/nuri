 
package com.android.developers.testing.data

import android.graphics.Bitmap
import android.net.Uri
import app.getnuri.util.LocalFileProvider
import java.io.File
/**
 * A test implementation of [LocalFileProvider].
 *
 * This class provides a stub implementation for testing purposes.
 * It does not perform any actual file operations.
 */
class TestFileProvider : LocalFileProvider {
    override fun saveBitmapToFile(
        bitmap: Bitmap,
        file: File,
    ): File {
        TODO("Not yet implemented")
    }

    override fun getFileFromCache(fileName: String): File {
        TODO("Not yet implemented")
    }

    override fun createCacheFile(fileName: String): File {
        TODO("Not yet implemented")
    }

    override fun saveToSharedStorage(
        file: File,
        fileName: String,
        mimeType: String,
    ): Uri {
        TODO("Not yet implemented")
    }

    override fun sharingUriForFile(file: File): Uri {
        TODO("Not yet implemented")
    }

    override fun copyToInternalStorage(uri: Uri): File {
        return File("")
    }

    override fun saveUriToSharedStorage(
        inputUri: Uri,
        fileName: String,
        mimeType: String,
    ): Uri {
        TODO("Not yet implemented")
    }
}
