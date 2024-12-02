package ngod.dev.aicoding.configuration

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Value("\${VERSION}")
    private lateinit var version:String
    @Bean
    fun customSwaggerApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("AI Coding API 문서")
                    .version(version)
                    .description("Restful API 명세서")
            )

    }
}