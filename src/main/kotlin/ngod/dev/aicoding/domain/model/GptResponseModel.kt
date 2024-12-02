package ngod.dev.aicoding.domain.model

// 요청 DTO
data class GPTRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>
)

data class Message(
    val role: String, // "user" or "assistant"
    val content: String
)

// 응답 DTO
data class GPTResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)
