package ngod.dev.aicoding.domain.service

import ngod.dev.aicoding.controller.quiz.dto.RequestContentDto
import ngod.dev.aicoding.data.entity.enum.Difficultly
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GptServiceTest{
    @Autowired
    lateinit var gptService: GptService
    @Test
    fun testGptGenerateQuiz(){
        val result = gptService.generateQuiz(RequestContentDto(category = "파이썬", difficultly = Difficultly.EASY,))
        println(result)
        println(result.size)
    }
    @Test
    fun testGptGenerateCodingTest(){
        val result = gptService.generateCodingTest(RequestContentDto(category = "BFS", difficultly = Difficultly.HARD,))
        println(result)

    }
}