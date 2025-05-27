package com.nuri.core.network

import com.nuri.core.model.Ingredient
import android.net.Uri
import android.content.Context
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import javax.inject.Inject

class OpenAIIngredientExtractor @Inject constructor(
    private val context: Context,
    private val httpClient: OkHttpClient,
    private val json: Json
) : IngredientExtractor {

    @Serializable
    data class OpenAIRequest(
        val model: String = "gpt-4-vision-preview",
        val messages: List<Message>,
        val max_tokens: Int = 300
    )

    @Serializable
    data class Message(
        val role: String,
        val content: List<Content>
    )

    @Serializable
    data class Content(
        val type: String,
        val text: String? = null,
        val image_url: ImageUrl? = null
    )

    @Serializable
    data class ImageUrl(
        val url: String
    )

    @Serializable
    data class OpenAIResponse(
        val choices: List<Choice>
    )

    @Serializable
    data class Choice(
        val message: ResponseMessage
    )

    @Serializable
    data class ResponseMessage(
        val content: String
    )

    override suspend fun extractIngredients(imageUri: Uri): Result<List<Ingredient>> {
        return withContext(Dispatchers.IO) {
            try {
                val base64Image = encodeImageToBase64(imageUri)
                val request = createOpenAIRequest(base64Image)
                val response = makeApiCall(request)
                val ingredients = parseIngredientsFromResponse(response)
                Result.success(ingredients)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun encodeImageToBase64(imageUri: Uri): String {
        val inputStream: InputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw IllegalArgumentException("Cannot open image URI")
        
        val bytes = inputStream.readBytes()
        inputStream.close()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun createOpenAIRequest(base64Image: String): OpenAIRequest {
        return OpenAIRequest(
            messages = listOf(
                Message(
                    role = "user",
                    content = listOf(
                        Content(
                            type = "text",
                            text = "Analyze this food image and extract the ingredients. Return only a JSON array of objects with 'name' and 'quantity' fields. Example: [{\"name\":\"tomato\",\"quantity\":\"2 medium\"},{\"name\":\"onion\",\"quantity\":\"1 large\"}]"
                        ),
                        Content(
                            type = "image_url",
                            image_url = ImageUrl("data:image/jpeg;base64,$base64Image")
                        )
                    )
                )
            )
        )
    }

    private suspend fun makeApiCall(request: OpenAIRequest): String {
        val requestBody = json.encodeToString(OpenAIRequest.serializer(), request)
            .toRequestBody("application/json".toMediaType())

        val httpRequest = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer ${getApiKey()}")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        val response = httpClient.newCall(httpRequest).execute()
        if (!response.isSuccessful) {
            throw Exception("API call failed: ${response.code}")
        }

        return response.body?.string() ?: throw Exception("Empty response body")
    }

    private fun parseIngredientsFromResponse(responseBody: String): List<Ingredient> {
        val response = json.decodeFromString<OpenAIResponse>(responseBody)
        val content = response.choices.firstOrNull()?.message?.content
            ?: throw Exception("No content in response")

        // Extract JSON array from the response content
        val jsonStart = content.indexOf('[')
        val jsonEnd = content.lastIndexOf(']') + 1
        
        if (jsonStart == -1 || jsonEnd == 0) {
            throw Exception("No JSON array found in response")
        }

        val jsonArray = content.substring(jsonStart, jsonEnd)
        
        @Serializable
        data class IngredientResponse(val name: String, val quantity: String?)
        
        val ingredientResponses = json.decodeFromString<List<IngredientResponse>>(jsonArray)
        
        return ingredientResponses.map { 
            Ingredient(
                name = it.name,
                quantity = it.quantity,
                confidence = 0.9f
            )
        }
    }

    private fun getApiKey(): String {
        // In production, store this securely (e.g., in BuildConfig or encrypted preferences)
        return "your-openai-api-key-here"
    }
} 