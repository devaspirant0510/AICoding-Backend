package ngod.dev.aicoding.domain.model

data class GptCodingTestDto(
    val title: String,
    val description: String,
    val input: String,
    val output: String,
    val hint: String,
    val inputTestCase: List<String>,
    val outputTestCase: List<String>
)