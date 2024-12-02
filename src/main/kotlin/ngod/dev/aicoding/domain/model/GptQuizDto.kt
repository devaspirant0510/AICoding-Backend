package ngod.dev.aicoding.domain.model

data class GptQuizDto(
    val studyName:String,
    val question: String,
    val answer: Int,
    val choices: List<String>,
    val explanation: String
)

