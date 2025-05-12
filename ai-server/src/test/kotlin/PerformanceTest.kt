import dev.langchain4j.data.message.SystemMessage.systemMessage
import dev.langchain4j.data.message.UserMessage.userMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.kotlin.model.chat.chat
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiChatRequestParameters
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.kpavlov.aimocks.openai.MockOpenai
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.SECONDS
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime
import kotlin.time.toJavaDuration

const val THREAD_POOL_SIZE = 10
const val CONCURRENT_REQUESTS = 50

private val mockOpenai = MockOpenai()

private val model: ChatModel =
    OpenAiChatModel
        .builder()
        // .apiKey(System.getenv("OPENAI_API_KEY"))
        .apiKey("demo-key")
        .baseUrl("http://127.0.0.1:${mockOpenai.port()}/v1")
        .timeout(60.seconds.toJavaDuration())
        .modelName("gpt4-o")
        .build()

/**
 * Tests traditional blocking approach with a thread pool
 */
fun testTraditionalThreadPool(
    executor: ExecutorService,
    concurrentRequests: Int = CONCURRENT_REQUESTS,
    name: String,
) {
    runTest(name = name) { successCounter, failureCounter, latch ->
        repeat(concurrentRequests) { i ->
            executor.submit {
                try {
                    model.chat(
                        ChatRequest
                            .builder()
                            .messages(
                                listOf(
                                    systemMessage("You are a helpful assistant"),
                                    userMessage("Write a short poem about request #$i"),
                                ),
                            ).parameters(
                                OpenAiChatRequestParameters
                                    .builder()
                                    .seed(i % 2)
                                    .build(),
                            ).build(),
                    )
//                            println("Thread pool - Request #$i completed")
                    print(".")
                    successCounter.incrementAndGet()
                } catch (e: Exception) {
//                            println("Thread pool - Request #$i failed: ${e.message}")
                    print("☠️")
                    failureCounter.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }
    }

    executor.awaitTermination(1, SECONDS)
    executor.shutdown()
}

/**
 * Tests coroutine-based approach
 */
fun testCoroutines(
    concurrentRequests: Int = CONCURRENT_REQUESTS,
    name: String = "Coroutines",
    dispatcher: CoroutineDispatcher,
) = runTest(name = name) { successCounter, failureCounter, latch ->
    coroutineScope {
        repeat(concurrentRequests) { i ->
            launch(dispatcher) {
                try {
                    model.chat {
                        messages += systemMessage("You are a helpful assistant")
                        messages += userMessage("Write a short poem about request #$i")
                        parameters(OpenAiChatRequestParameters.builder()) {
                            builder.seed(i % 2)
                        }
                    }
//                                print("Coroutines - Request #$i completed")
                    print(".")
                    successCounter.incrementAndGet()
                } catch (e: Exception) {
//                                println("Coroutines - Request #$i failed: ${e.message}")
                    println("☠️")
                    failureCounter.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }
    }
}

/**
 * Tests coroutine-based approach
 */
fun runTest(
    name: String,
    block: suspend (
        successCounter: AtomicInteger,
        failureCounter: AtomicInteger,
        latch: CountDownLatch,
    ) -> Unit,
) = runBlocking {
    val successCounter = AtomicInteger(0)
    val failureCounter = AtomicInteger(0)
    val latch = CountDownLatch(CONCURRENT_REQUESTS)
    println("\n🏁Starting test: $name...")
    val elapsedTime =
        measureTime {
            block(successCounter, failureCounter, latch)
            latch.await()
        }

    println(
        """
                |
                |✅Finished test: $name
                |    Completed $CONCURRENT_REQUESTS requests in $elapsedTime
                |    Success: ${successCounter.get()}, Failures: ${failureCounter.get()}
        """.trimMargin(),
    )
}

fun main() {
    mockOpenai.completion {
        model = "gpt4-o"
//            seed = 0
    } responds {
        delay = 1.seconds
    }

    println("\n=== Running performance tests with $CONCURRENT_REQUESTS concurrent requests ===")

    testTraditionalThreadPool(
        executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE),
        name = "Fixed thread pool of $THREAD_POOL_SIZE threads",
    )

    testTraditionalThreadPool(
        name = "Virtual threads",
        executor = Executors.newVirtualThreadPerTaskExecutor(),
    )

    testCoroutines(
        name = "Coroutines IO dispatcher with limitedParallelism($THREAD_POOL_SIZE)",
        dispatcher = Dispatchers.IO.limitedParallelism(THREAD_POOL_SIZE),
    )

    testCoroutines(
        name = "Coroutines IO Dispatcher",
        dispatcher = Dispatchers.IO,
    )

    testCoroutines(
        name = "Coroutines on newVirtualThreadPerTaskExecutor dispatcher",
        dispatcher = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher(),
    )
}
