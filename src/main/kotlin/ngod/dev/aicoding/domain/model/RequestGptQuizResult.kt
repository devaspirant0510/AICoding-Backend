package ngod.dev.aicoding.domain.model

import ngod.dev.aicoding.data.entity.Quiz

data class RequestGptQuizResult(
    val quiz:Quiz,
    val result:String
)