package org.acme.it

import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldHaveLineCount
import io.kotest.matchers.string.shouldStartWith
import io.restassured.RestAssured
import io.restassured.http.ContentType
import kotlinx.coroutines.test.runTest
import org.acme.ai.aiservice.PoemRequest
import org.acme.ai.aiservice.PoemResponse
import kotlin.test.Test

internal class PoetryServiceIT {

    val env = TestEnvironment

    @Test
    fun `should run integration tests`() = runTest {
        val aiServiceHost = env.dockerCompose.getAiServiceEndpoint()
        val request = PoemRequest(topic = "Mermaid", lines = 5)

        val llmReply = """
            In azure depths where mortal eyes see naught,
            A maiden fair with scales of emerald hue,
            Whose songs the raging tempests have oft fought,
            Doth yearn for realms where love might prove most true.
            Her siren call, both sweet and filled with rue.
            """.trimIndent()

        env.openai.completion {
            userMessageContains("Mermaid")
        } responds {
            assistantContent = llmReply
        }

        val poetResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("$aiServiceHost/lc4j/poem")
            .then()
            .statusCode(200)
            .extract()
            .`as`(PoemResponse::class.java)

        println("Response: $poetResponse")

        val poem = poetResponse.text

        poem.let {
            it shouldHaveLineCount llmReply.lines().size + 1
            it shouldStartWith "In azure depths where mortal eyes see naught"
            it shouldEndWith "© AI-Server, ${java.time.Year.now()}"
        }
    }
}
