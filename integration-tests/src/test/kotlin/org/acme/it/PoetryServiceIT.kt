package org.acme.it

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldHaveLineCount
import io.restassured.RestAssured
import io.restassured.http.ContentType
import kotlinx.coroutines.test.runTest
import org.acme.ai.aiservice.PoemRequest
import org.acme.ai.aiservice.PoemResponse
import org.junit.jupiter.api.Test

class PoetryServiceIT {

    val env = TestEnvironment

    @Test
    fun `should run integration tests`() = runTest {
        val aiServiceHost = env.dockerCompose.getAiServiceEndpoint()
        val request = PoemRequest(topic = "Mermaid", lines = 5)

        val poetResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("$aiServiceHost/lc4j/poem")
            .then()
            .statusCode(200)
            .extract()
            .`as`(PoemResponse::class.java)

        println("Response: $poetResponse")
        poetResponse.text shouldHaveLineCount 5
    }
}
