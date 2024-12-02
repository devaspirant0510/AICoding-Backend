package ngod.dev.aicoding.domain.service

import com.google.gson.Gson
import ngod.dev.aicoding.controller.quiz.dto.RequestContentDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import com.google.gson.reflect.TypeToken
import ngod.dev.aicoding.domain.model.*

@Service
class GptService(
    private val webClient: WebClient.Builder
) {
    @Value("\${GPT_API_KEY}")
    private lateinit var gptApiKey:String

    /**
     * gpt api 를 이용한 퀴즈 생성 메서드
     * 프롬프트로 정의한 json 형태로 리턴
     */
    fun generateQuiz(requestContentDto: RequestContentDto):List<GptQuizDto>{
        val request = GPTRequest(
            messages = listOf(
                Message(role = "system", content = "너는 반드시 json 형태로 대답해야되 내가 물어보면 그게 맞는 퀴즈를 내가 지정해준 형태로 json 만 리턴하면되"),
                Message(role = "system", content = "[{\n" +
                        " ‘studyName’:string,\n" +
                        " ‘question’:string,\n" +
                        "‘answer’:int,\n" +
                        "‘choices’:stirng[],\n" +
                        "‘explanation’:string\n" +
                        "}] 이런형태의 값으로 json 리스트 형태로 출력해줘 각 프로파티에 들어갈 값은 studyName=너가 생성한 퀴즈의 제목, question=문제내용,answer=정답 객관식으므로 몇번인지 숫자만,choices=객관식 문항 각 문항이 리스트에 들어간형태,explanation=정답을 찾게 도움을 줄만한 힌트"),
                Message(role="user", content = "${requestContentDto.category} 퀴즈를 ${requestContentDto.difficultly} 난이도로 객관식 5지선다로 문제 5개 생성해줘")
            )
        )
        val response = webClient.build()
            .post()
            .uri("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $gptApiKey")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(GPTResponse::class.java)
            .block()
        val questionListType = object : TypeToken<List<GptQuizDto>>() {}.type
        val gson = Gson()
        println(response?.choices?.firstOrNull()?.message?.content)
        val questions: List<GptQuizDto> = gson.fromJson(response?.choices?.firstOrNull()?.message?.content, questionListType)
        return questions
    }
    fun generateCodingTest(requestContentDto: RequestContentDto):GptCodingTestDto{
        val request = GPTRequest(
            messages = listOf(
                Message(role = "system", content = "너는 반드시 json 형태로 대답해야되 내가 물어보면 그게 맞는 코딩테스트 문제를 내가 지정해준 형태로 json 만 리턴하면되"),
                Message(role = "system", content = "{\n" +
                        " 'title':string,\n" +
                        "'description':string,\n" +
                        "'input':string,\n" +
                        "'output':string,\n" +
                        "'hint':string,\n" +
                        "'inputTestCase':string[]\n" +
                        "'outputTestCase':string[]\n" +
                        "} 이런형태의 값으로 json 형태로 출력해줘 각 프로파티에 들어갈 값은 title=코딩테스트 문제 제목,description=코딩테스트 문제 설명,input=입력 예시 (입력값의 범위 등),output=출력형태 출력값,hint=문제를 푸는데 도움이 될만한 힌트,inputTestCase=해당 문제의 입력 테스트 케이스 20개,outputTestCase=해당 문제의 출력 테스트케이스 20개 입출력 테스트케이스 기반으로 체점을 할거라서 테스트케이스는 다양하게 해줘 최소한 10개 이상"),
                Message(role="user", content = "${requestContentDto.category} 알고리즘 코딩테스트를 ${requestContentDto.difficultly} 난이도로 내가 코딩테스트 문제 내가 지정한 형태의 json 으로 출력해줘")
            )
        )
        val response = webClient.build()
            .post()
            .uri("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $gptApiKey")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(GPTResponse::class.java)
            .block()
        val gson = Gson()
        println(response?.choices?.firstOrNull()?.message?.content)
        val questions: GptCodingTestDto = gson.fromJson(response?.choices?.firstOrNull()?.message?.content, GptCodingTestDto::class.java)
        return questions

    }


}