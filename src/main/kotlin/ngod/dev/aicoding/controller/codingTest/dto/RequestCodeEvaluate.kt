package ngod.dev.aicoding.controller.codingTest.dto

data class RequestCodeEvaluate(
    val code:String,
    val accountId:Long,
    val language:String,
)