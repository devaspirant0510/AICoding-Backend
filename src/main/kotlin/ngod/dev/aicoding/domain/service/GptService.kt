package ngod.dev.aicoding.domain.service

import com.google.gson.Gson
import ngod.dev.aicoding.controller.quiz.dto.RequestContentDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import com.google.gson.reflect.TypeToken
import ngod.dev.aicoding.controller.codeReview.dto.RequestCodeReviewDto
import ngod.dev.aicoding.domain.model.*

@Service
class GptService(
    private val webClient: WebClient.Builder
) {
    @Value("\${GPT_API_KEY}")
    private lateinit var gptApiKey: String

    fun feedBackQuiz(requestGptQuizResult: List<RequestGptQuizResult>): String {
        val promptMessage =
            mutableListOf(
                Message(role = "system", content = "너는 나의 퀴즈 결과를 보고 전체적인 평가와 어떤부분이 부족한지 전체적으로 피드백을 주면되 앞으로 어떤 걸 공부하면 개선할수 있을지 한국어로 피드백 해줘 텍스트 형태는 마크다운형태로 보여줘"),
                Message(role = "user", content = "퀴즈 결과는 다음과 같아 $requestGptQuizResult"))
        val request = GPTRequest(
            messages = promptMessage
        )
        val response = webClient.build()
            .post()
            .uri("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $gptApiKey")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(GPTResponse::class.java)
            .block()
        val gptTextResponse = response?.choices?.firstOrNull()?.message?.content
        return gptTextResponse ?: "gpt api 오류 발생"

    }

    /**
     * gpt api 를 이용한 퀴즈 생성 메서드
     * 프롬프트로 정의한 json 형태로 리턴
     */
    fun generateQuiz(requestContentDto: RequestContentDto): List<GptQuizDto> {
        val promptMessage =
            mutableListOf(
                Message(
                    role = "system",
                    content = "너는 반드시 json 형태로 대답해야되 내가 물어보면 그게 맞는 퀴즈를 내가 지정해준 형태로 json 만 리턴하면되며 언어는 한국어로 응답해줘"
                ),
                Message(
                    role = "system",
                    content = "[{\n 'studyName':string,\n'question':string,\n'answer':int,\n'choices':stirng[],\n'explanation':string\n}]" +
                            "이런형태의 값으로 json 리스트 형태로 출력해줘 각 프로파티에 들어갈 값은 studyName=너가 생성한 퀴즈의 제목, question=문제내용,answer=정답 객관식으므로 몇번인지 숫자만(1~5) ," +
                            "choices=객관식 문항 각 문항이 리스트에 들어간형태,explanation=정답을 찾게 도움을 줄만한 힌트 단, 힌트에 정답이 될만한 문장이 포함되서는 안됨"
                ),
                Message(
                    role = "user",
                    content = "${requestContentDto.category} 퀴즈를 ${requestContentDto.difficultly} 난이도로 객관식 5지선다로 문제 5개 생성해줘"
                )
            )
        if (requestContentDto.prompt != null)  // 사용자가 추가 프롬프트를 작성하였을때 해당 내용 gpt api 에 추가
            promptMessage.add(
                Message(
                    role = "user",
                    content = "다음은 퀴즈 만드는데 추가적인 정보야 이 정보를 기반으로 퀴즈를 만들면되 내용:${requestContentDto}"
                )
            )
        val request = GPTRequest(
            messages = promptMessage
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
        var gptTextResponse = response?.choices?.firstOrNull()?.message?.content
        gptTextResponse = gptTextResponse?.replaceFirst("json", "")
        gptTextResponse = gptTextResponse?.replace("```", "")
        val questions: List<GptQuizDto> =
            gson.fromJson(gptTextResponse, questionListType)
        return questions
    }

    fun generateCodingTest(requestContentDto: RequestContentDto): GptCodingTestDto {
        val promptMessage = mutableListOf(
            Message(
                role = "system",
                content = "너는 반드시 json 형태로 대답해야되 내가 물어보면 그게 맞는 코딩테스트 문제를 내가 지정해준 형태로 json 만 리턴하면되 또한 대답은 한국어로 해줘"
            ),
            Message(
                role = "system",
                content = "{\n'title':string,\n'description':string,\n'input':string,\n'output':string,\n'hint':string,\n'inputTestCase':string[]\n" +
                        "'outputTestCase':string[]\n } 이런형태의 값으로 json 형태로 출력해줘 각 프로파티에 들어갈 값은 title=코딩테스트 문제 제목,description=코딩테스트 문제 설명,input=입력 예시 (입력값의 범위 등),output=출력형태 출력값,hint=문제를 푸는데 도움이 될만한 힌트,inputTestCase=해당 문제의 입력 테스트 케이스 10개,outputTestCase=해당 문제의 출력 테스트케이스 10개 입출력 테스트케이스 기반으로 체점을 할거라서 테스트케이스는 다양하게 해줘"
            ),
            Message(
                role = "user",
                content = "${requestContentDto.category} 알고리즘 코딩테스트를 ${requestContentDto.difficultly} 난이도로 내가 코딩테스트 문제 내가 지정한 형태의 json 으로 출력해줘"
            )
        )
        if (requestContentDto.prompt != null)
            promptMessage.add(Message(role = "user", content = "문제는 ${requestContentDto.prompt} 의 내용을 참고해서 작성해줘"))
        val request = GPTRequest(
            messages = promptMessage,
            model = "gpt-4o"
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
        var message = response?.choices?.firstOrNull()?.message?.content
        message = message?.replaceFirst("json", "")
        message = message?.replace("```", "")
        println(message)
        val questions: GptCodingTestDto =
            gson.fromJson(message, GptCodingTestDto::class.java)
        return questions
    }

    fun generateCodeReview(requestCodeReviewDto: RequestCodeReviewDto): GptCodeReviewDto {
        val request = GPTRequest(
            messages = listOf(
                Message(role = "system", content = "내가 코드를 보내주면 너는 해당코드의 개선점을 분석해서 코드 리뷰를 상세히 해줘 또한 한국어로 말해줘"),
                Message(role = "system",
                    content = "{title:string,\ndescription:string\n} 다음과 같은 형태의 json 으로 응답해주고 각 항목에 대한 설명은 다음과 같아" +
                            "title = 해당 코드 리뷰 제목 description = 코디리뷰할 내용 내용이 길어질시 이스케이프를 통해 처리 3000자 이내 "),
                Message(role = "user",
                    content = "작성언어 :${requestCodeReviewDto.language}\n코드 : ${requestCodeReviewDto.code} ")))
        val response = webClient.build()
            .post()
            .uri("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $gptApiKey")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(GPTResponse::class.java)
            .block()
        val gson = Gson()
        var message = response?.choices?.firstOrNull()?.message?.content
        message = message?.replaceFirst("json", "")
        message = message?.replace("```", "")
        println(message)
        val resultCodeView = gson.fromJson(message, GptCodeReviewDto::class.java)
        return resultCodeView
    }


}