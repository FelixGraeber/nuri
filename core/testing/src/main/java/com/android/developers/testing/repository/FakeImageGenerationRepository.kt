 
package com.android.developers.testing.repository

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import app.getnuri.data.ImageGenerationRepository
import java.io.File

class FakeImageGenerationRepository : ImageGenerationRepository {
    override suspend fun initialize() {
    }
    var exceptionToThrow: Exception? = null

    override suspend fun generateFromDescription(
        description: String,
        skinTone: String,
    ): Bitmap {
        if (exceptionToThrow != null) throw exceptionToThrow!!
        return createBitmap(1, 1)
    }

    override suspend fun generateFromImage(
        file: File,
        skinTone: String,
    ): Bitmap {
        if (exceptionToThrow != null) throw exceptionToThrow!!
        return createBitmap(1, 1)
    }

    override suspend fun saveImage(imageBitmap: Bitmap): Uri {
        if (exceptionToThrow != null) throw exceptionToThrow!!
        return "content://com.example.app/images/saveImageInternal.jpg".toUri()
    }

    override suspend fun saveImageToExternalStorage(imageBitmap: Bitmap): Uri {
        if (exceptionToThrow != null) throw exceptionToThrow!!
        return "content://com.example.app/images/original.jpg".toUri()
    }

    override suspend fun saveImageToExternalStorage(imageUri: Uri): Uri {
        if (exceptionToThrow != null) throw exceptionToThrow!!
        return imageUri
    }
}
