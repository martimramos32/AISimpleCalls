package dam

import kotlinx.coroutines.runBlocking

/**
 * Main entry point for the LLM Assistant application
 */
fun main() = runBlocking {
    println("\n🤖 Starting LLM Assistant application... 😀😀😀😀😀\n")

    // Get configuration properties
    val properties = getProperties()

    // Set up logging
    configureLogging(properties)
    println()

    // Write LLM used
    println("✨ Using AI_LLM: ${properties.getProperty("AI_LLM")}")

    // Use the factory to create the appropriate assistant based on configuration
    val assistant: AIAssistant = AIAssistantFactory.createAssistant(properties)
    println()

    // Write system and model
    println("✨ Using: ${assistant.getSystem()} ${assistant.model}\n")

    // Display a welcome message
    println("💬 Type your questions and press Enter to chat with the AI.")
    println("💬 Press Ctrl+D (Unix/Mac) or Ctrl+Z (Windows) to exit.\n")

    // Main interaction loop
    while (true) {
        println("➖➖➖➖➖➖➖➖➖➖")
        // Ask for question input and read it from the console
        print("🧠 Your question: ")
        val input = readlnOrNull() ?: break

        // If blank input, write a help message and continue to ask for input
        if (input.isBlank()) {
            println("⚠️ Please enter a question or press Ctrl+D to exit.")
            continue
        }

        // Process input
        val output = assistant.processInput(input)
        println("\n🤖 Answer: $output\n\n")
    }

    // Bye message
    println("\n👋 Thank you for using LLM Assistant. Goodbye!")

}

/**
 * The temperature value (typically between 0.0 and 1.0) affects how deterministic
 * or creative the AI model's responses will be:
 * - Low temperature (e.g., 0.1-0.3): More deterministic, focused, and predictable responses.
 *   The model is more likely to choose the most probable next token at each step.
 * - Medium temperature (e.g., 0.4-0.7): Balanced between determinism and creativity,
 *   providing reasonably varied responses while maintaining coherence.
 * - High temperature (e.g., 0.8-1.0): More random, diverse, and creative responses.
 *   The model may take more risks and generate more surprising content.
 *
 * Use cases:
 *  1. For technical documentation: use low temperature (0.1-0.3)
 *  2. For creative storytelling: use high temperature (0.8-1.0)
 *  3. For conversation: use medium temperature (0.4-0.7)
 *  4. For code generation: use low-medium temperature (0.2-0.5)
 *  5. For summarization: use medium temperature (0.4-0.7)
 *  6. For sentiment analysis: use high temperature (0.8-1.0)
 *  7. For image generation: use medium temperature (0.4-0.7)
 *  8. For image captioning: use medium temperature (0.4-0.7)
 *  9. For question answering: use medium temperature (0.4-0.7)
 * 10. For chatbots: use medium temperature (0.4-0.7)
 * 11. For summarization: use medium temperature (0.4-0.7)
 * 12. For translation: use low temperature (0.1-0.3)
 * 13. For voice conversion: use low temperature (0.1-0.3)
 */
