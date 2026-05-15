package dam

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.util.Properties

const val configFilePath = "config.properties"

/**
 * Retrieves configuration properties by loading them from a predefined configuration file.
 */
private val configProperties
    get() = loadProperties()

/**
 * Returns the Properties object containing configuration values.
 *
 * @return Properties object containing configuration values
 */
fun getProperties(): Properties = configProperties

/**
 * Loads configuration properties from config.properties file
 *
 * @return Properties object containing configuration values
 */
private fun loadProperties(): Properties {
    val properties = Properties()
    try {
        val configFile = java.io.File(configFilePath)
        if (!configFile.exists()) {
            println("⚠️ Configuration file not found: $configFilePath")
            println("⚠️ Please create this file with your API keys and settings")
            return properties
        }

        FileInputStream(configFile).use { inputStream ->
            properties.load(inputStream)

            // Check if we have at least one API key
            if (properties.getProperty("OPENAI_API_KEY").isNullOrBlank() &&
                properties.getProperty("GEMINI_API_KEY").isNullOrBlank()
            ) {
                println("⚠️ No API keys found in configuration file")
                println("⚠️ Please add at least one API key to $configFilePath")
            } else {
                val apiKeys = listOf("OPENAI_API_KEY", "GEMINI_API_KEY").filter {
                    properties.getProperty(it)?.isNotBlank() == true
                }.toList()

                println("✅ Found ${apiKeys.size} API key(s) in configuration: $apiKeys")
            }
        }
    } catch (e: Exception) {
        println("❌ Error loading properties: ${e.message}")
        println("   Make sure '$configFilePath' exists and is properly formatted")
    }
    return properties
}

/**
 * Configures the logging level for all loggers based on the LOG_LEVEL property.
 * Valid levels are: OFF, ERROR, WARN, INFO, DEBUG, TRACE
 *
 * @param properties The properties containing the LOG_LEVEL setting
 */
fun configureLogging(properties: Properties) {
    // Silence SLF4J initialization messages
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "OFF")
    System.setProperty("org.slf4j.simpleLogger.log.org.slf4j.LoggerFactory", "OFF")

    val defaultLevel = "OFF"
    val logLevelStr = properties.getProperty("LOG_LEVEL", defaultLevel).uppercase()

    try {
        // Convert string to Logback Level
        val logLevel = when (logLevelStr) {
            "OFF" -> Level.OFF
            "ERROR" -> Level.ERROR
            "WARN" -> Level.WARN
            "INFO" -> Level.INFO
            "DEBUG" -> Level.DEBUG
            "TRACE" -> Level.TRACE
            else -> {
                println("⚠️ Invalid LOG_LEVEL '$logLevelStr'. Using $defaultLevel as default.")
                Level.OFF
            }
        }

        // Get the logger context
        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext

        // Set the level on the root logger
        val rootLogger = loggerContext.getLogger("ROOT")
        rootLogger.level = logLevel

        // Set the level for our application package
        val appLogger = loggerContext.getLogger("dam")
        appLogger.level = logLevel

        // Only print logging info if we're not completely silencing logs
        if (logLevel != Level.OFF) {
            println("🔊 Logging level: $logLevelStr")
        }
    } catch (e: Exception) {
        println("❌ Failed to set logging level: ${e.message}")
    }
}




