package ngod.dev.aicoding.controller.quiz.dto

import ngod.dev.aicoding.data.entity.enum.Difficultly

data class RequestContentDto(
    val category:String,
    val difficultly: Difficultly,
    val prompt:String?
)