package dam

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

/**
 * OpenAIAssistant class provides an interface to communicate with OpenAI's GPT models.
 * This class handles API authentication, request formatting, response parsing, and error handling.
 * It implements retry logic for rate-limited requests and validates JSON responses.
 *
 * @param properties Properties containing an API key for authentication with OpenAI services
 */
class AIAssistantOpenAIClasses(override val properties: Properties) : AIAssistant {

    override fun getSystem() = "OPENAI"
    override val apiKeyName = "OPENAI_API_KEY"

    // Model selection - uncomment the desired model
    // Different models have different capabilities, costs, and response characteristics
    // private var model = "gpt-3.5-turbo" // OK - Faster, less expensive, good for most tasks
    //private var model = "gpt-4"  // OK - More capable, better reasoning, more expensive
    // private var model = "o1"  // OK - Multi-modal model, can handle images
    override var model = "gpt-4o" //  OK - Optimized version of GPT-4
    // private var model = "o3-mini" // OK - Smaller, faster version with reduced capabilities
    // private var model = "gpt-4o-mini" // OK - Smaller optimized model
    // private var model = "o3-mini-high" // not working - an Experimental model
    // private var model = "gpt-4.5" // not working - Future model not yet available

    // Data classes for OpenAI API request structure
    data class Message(val role: String, val content: String)
    data class OpenAIRequest(
        val model: String,
        val messages: List<Message>,
        val temperature: Double = 0.7,    // Controls randomness (0.0 to 2.0)
        val max_tokens: Int = 800,        // Maximum length of response
        val top_p: Double = 1.0,         // Alternative to temperature for nucleus sampling
        val frequency_penalty: Double = 0.0,  // Reduces repetition of token sequences
        val presence_penalty: Double = 0.0    // Reduces repetition of topics
    )


    // Gson instance for JSON serialization
    private val gson = Gson()

    /**
     * Constructs and formats a structured request from the given input prompt.
     * This method is intended to prepare the necessary request structure for
     * sending to an AI-powered model or API.
     *
     * @param prompt The user's input query or prompt that needs to be formatted into a request
     */
    override fun buildRequest(prompt: String): Request {
        // Create request structure using data classes
        val messages = listOf(
            Message(role = "system", content = "You are a friendly and helpful assistant."),
            Message(role = "user", content = prompt)
        )
        val openAIRequest = OpenAIRequest(model = model, messages = messages)

        // Convert to JSON string using Gson
        val requestBody = gson.toJson(openAIRequest)

        // Configure the HTTP request with proper headers and authentication
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")  // OpenAI chat endpoint
            .addHeader("Authorization", "Bearer $apiKey")  // API key authentication
            .addHeader("Content-Type", "application/json")  // Specify content type
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))  // Set the request body
            .build()
        return request
    }
}