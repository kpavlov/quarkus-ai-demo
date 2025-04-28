# Quarkus LC4J Demo

## Prerequisites
       
1. JDK 21
2. Docker Compose environment (Docker or OrbStack)
3. Maven
4. Get OpenAI API Key and setup `.env` file like this:
    ```dotenv
    OPENAI_API_KEY=sk-proj-xxxx
    ```

### Observability

[docker-otel-lgtm](https://github.com/grafana/docker-otel-lgtm):

```shell
docker run -p 3000:3000 -p 4317:4317 -p 4318:4318 --rm -ti grafana/otel-lgtm
```
**TODO:** show dashboard with [gen_ai.*](https://opentelemetry.io/docs/specs/semconv/gen-ai/) metrics.
          
## Build project and run tests

```shell
mvn verify
```

## Run with Docker Compose

```shell
docker compose start
```

## Demo 1: ChatModelResource

Demonstrates creating LC4j ChatLanguageModel manually and calling it from RESTful resource

Blocking call:

```shell
curl -X POST http://localhost:8080/lc4j/blocking \
-H "Content-Type: application/json" \
-d '{
  "systemPrompt": "You are a Danish master of fairy tales. Write short, inspiring stories for children around 10 years old. Keep it under 300 chars. Provide only the story text in your response."
  "userPrompt": "Tell a story about a brave little squirrel who overcomes challenges to save its forest home."
}' | jq
```

Suspend function call:

```shell
curl -X POST http://localhost:8080/lc4j/suspend \
-H "Content-Type: application/json" \
-d '{
  "systemPrompt": "You are a Danish master of fairy tales. Write short, inspiring stories for children around 5 years old. Keep it under 300 chars. Provide only the story text in your response, never reveal your thinking process."
  "userPrompt": "Tell a story about a brave little squirrel who overcomes challenges to save its forest home."
}' | jq
```

## Demo 2 - Service with Mock Openai

1. Run [LlmSimulator.kt](ai-server/src/test/kotlin/LlmSimulator.kt) - Starts MockOpenai on port 8089

2. [application-dev.yaml](ai-server/src/main/resources/application-dev.properties) has OpenAI test URL
```properties
quarkus.langchain4j.openai.base-url=http://localhost:8089/v1/
      api-key: "sk_dummy"
```

Good request, poem expected
```shell
curl -X POST http://localhost:8080/lc4j/poem \
-H "Content-Type: application/json" \
-d '{
  "topic": "Mermaid",
  "lines": 5
}' | jq
```

Testing `@Fallback`

Simulate error 500 from OpenAI
```shell
curl -X POST http://localhost:8080/lc4j/poem \
-H "Content-Type: application/json" \
-d '{
  "topic": "Boom",
  "lines": 5
}' | jq
```

Simulate 8 seconds delay, when timeout is 7 seconds
```shell
curl -X POST http://localhost:8080/lc4j/poem \
-H "Content-Type: application/json" \
-d '{
  "topic": "Slow",
  "lines": 5
}' | jq
```

## Performance test

A [PerformanceTest.kt](ai-server/src/test/kotlin/PerformanceTest.kt) 

```log
=== Running performance tests with 50 concurrent requests ===

🏁Starting test: Fixed thread pool of 10 threads...
..................................................
✅Finished test: Fixed thread pool of 10 threads
    Completed 50 requests in 5.486333084s
    Success: 50, Failures: 0

🏁Starting test: Virtual threads...
..................................................
✅Finished test: Virtual threads
    Completed 50 requests in 1.041490584s
    Success: 50, Failures: 0

🏁Starting test: Coroutines IO dispatcher with limitedParallelism(10)...
..................................................
✅Finished test: Coroutines IO dispatcher with limitedParallelism(10)
    Completed 50 requests in 5.094990291s
    Success: 50, Failures: 0

🏁Starting test: Coroutines IO Dispatcher...
..................................................
✅Finished test: Coroutines IO Dispatcher
    Completed 50 requests in 1.026892916s
    Success: 50, Failures: 0

🏁Starting test: Coroutines on newVirtualThreadPerTaskExecutor dispatcher...
..................................................
✅Finished test: Coroutines on newVirtualThreadPerTaskExecutor dispatcher
    Completed 50 requests in 1.029352917s
    Success: 50, Failures: 0
```

### Note on Coroutine parallelism

Kotlin [IO Dispatcher](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/-i-o.html) is limited to 64 threads by default. System property `kotlinx.coroutines.io.parallelism` can be used to override this value. If 64 is too small, then using dispatcher built from `newVirtualThreadPerTaskExecutor` would be a safe choice in JDK 24 for most scenarios, except native calls, since virtual thread pinning is no longer an issue ([JEP 491: Synchronize Virtual Threads without Pinning](https://openjdk.org/jeps/491)).

