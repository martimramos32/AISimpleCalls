package dam

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * AIAssistantKimi class provides an interface to communicate with Nvidia's Kimi AI models.
 * This class handles API authentication, request formatting, response parsing, and error handling.
 */
class AIAssistantKimi(override val properties: Properties) : AIAssistant {

    override fun getSystem() = "KIMI"
    override val apiKeyName = "NVIDIA_KIMI_API_KEY"
    override var model = "moonshotai/kimi-k2.6"

    override val client: okhttp3.OkHttpClient
        get() = okhttp3.OkHttpClient.Builder()
            .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()

    /**
     * Constructs and formats a structured request from the given input prompt.
     */
    override fun buildRequest(prompt: String): Request {
        val messagesArray = JSONArray().put(
            JSONObject()
                .put("role", "user")
                .put("content", prompt)
        )

        val requestBody = JSONObject()
            .put("model", model)
            .put("messages", messagesArray)
            .put("max_tokens", 16384)
            .put("temperature", 1.0)
            .put("top_p", 1.0)
            .put("stream", false)
            .put("chat_template_kwargs", JSONObject().put("thinking", true))
            .toString()

        return Request.Builder()
            .url("https://integrate.api.nvidia.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()
    }

    /**
     * Makes an API call with the provided prompt and processes the Kimi JSON response.
     * Overrides the default Gemini-specific implementation.
     */
    override fun makeApiCall(prompt: String): String {
        logger.info("Prompt:\n$prompt")
        val request = buildRequest(prompt)

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                throw Exception("Error in API call: ${response.code} - ${response.message}\nResponse: $errorBody")
            }

            val responseBody = response.body?.string() ?: return "Error: empty response"

            try {
                val json = JSONObject(responseBody)
                logger.debug("Raw API response: {}", responseBody)

                if (!json.has("choices") || json.getJSONArray("choices").length() == 0) {
                    return "Error: No choices found in the API response"
                }

                val choices = json.getJSONArray("choices")
                val firstChoice = choices.getJSONObject(0)

                if (!firstChoice.has("message")) {
                    return "Error: No message found in the API response"
                }

                val message = firstChoice.getJSONObject("message")

                if (!message.has("content") || message.isNull("content")) {
                    // Sometimes content might be null if it's only reasoning
                    val reasoning = if (message.has("reasoning_content") && !message.isNull("reasoning_content")) {
                        message.getString("reasoning_content")
                    } else ""
                    if (reasoning.isNotBlank()) {
                        return "Thinking:\n$reasoning"
                    }
                    return "Error: No content found in the API response"
                }

                val content = message.getString("content")

                val reasoningContent = if (message.has("reasoning_content") && !message.isNull("reasoning_content")) {
                    message.getString("reasoning_content")
                } else null

                if (reasoningContent != null && reasoningContent.isNotBlank()) {
                    return "Thinking:\n$reasoningContent\n\nResponse:\n" + content.trim()
                }

                return content.trim()

            } catch (e: JSONException) {
                val truncatedResponse = if (responseBody.length > 200) "${responseBody.substring(0, 200)}..." else responseBody
                logger.error("Error parsing JSON response: ${e.message}")
                logger.error("Response body (truncated): $truncatedResponse")
                throw Exception("Failed to parse API response: ${e.message}", e)
            }
        }
    }
}