package com.example.speedreader.data.repository

import com.example.speedreader.data.model.QuizQuestion
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GroqApiService {
    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: ChatRequest
    ): ChatResponse
}

data class ChatRequest(

    val messages: List<Message>,
    val model: String,
    val temperature: Float,
    @SerializedName("max_tokens") val maxTokens: Int // Cambiamos a maxTokens y usamos @SerializedName
)

data class Message(val role: String, val content: String)

data class ChatResponse(val choices: List<Choice>)

data class Choice(val message: Message)

class GroqRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.groq.com/openai/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(GroqApiService::class.java)
    private val apiKey = "Bearer TU_CLAVE_API_AQUÍ" // Reemplaza con tu clave de Groq

    suspend fun generateQuizQuestion(context: String): QuizQuestion? {
        val prompt = """
            Given this text: $context
            
            Generate a multiple choice question based on the text. Return ONLY a JSON object with these exact properties:
            {"question": "your question here", "options": ["option1", "option2", "option3", "option4"], "correctAnswer": number between 0-3}
        """.trimIndent()

        val request = ChatRequest(
            messages = listOf(Message("user", prompt)),
            model = "llama-3.3-70b-versatile",
            temperature = 0.3f,
            maxTokens = 1000
        )

        return try {
            val response = service.createChatCompletion(apiKey, request)
            val json = response.choices[0].message.content
            Gson().fromJson(json, QuizQuestion::class.java)
        } catch (e: Exception) {
            null
        }
    }
}