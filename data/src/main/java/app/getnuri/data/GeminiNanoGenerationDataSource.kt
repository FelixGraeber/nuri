 
package app.getnuri.data

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

interface GeminiNanoGenerationDataSource {
    suspend fun initialize()
    suspend fun generatePrompt(prompt: String): String?
}

@Singleton
class GeminiNanoGenerationDataSourceImpl @Inject constructor(val downloader: GeminiNanoDownloader) :
    GeminiNanoGenerationDataSource {

    override suspend fun initialize() {
        downloader.downloadModel()
    }

    /**
     * Generate a prompt to create an Android bot using Gemini Nano.
     * If Gemini Nano is not available, return null.
     */
    override suspend fun generatePrompt(prompt: String): String? {
        if (!downloader.isModelDownloaded()) return null
        val response = downloader.generativeModel?.generateContent(prompt)
        Log.d("GeminiNanoGenerationDataSource", "generatePrompt: ${response?.text}")
        return response?.text
    }
}
