 
package com.android.developers.testing.data

import app.getnuri.data.GeminiNanoGenerationDataSource

class TestGeminiNanoGenerationDataSource(val promptOutput: String?) : GeminiNanoGenerationDataSource {
    override suspend fun initialize() {
    }

    override suspend fun generatePrompt(prompt: String): String? {
        return promptOutput
    }
}
