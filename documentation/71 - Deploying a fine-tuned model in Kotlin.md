# Deploying a fine-tuned model in Kotlin

With a fine-tuned model trained, this topic covers how to track progress, evaluate quality, deploy for production traffic, and avoid common pitfalls that can undermine your results.

## Tracking and evaluating your fine-tuned model

Once training starts, you need to monitor progress and evaluate whether the model is actually improving. Different platforms offer different monitoring and evaluation tools, but the core principles remain the same across all providers.

For managed platforms such as AWS Bedrock and Cohere, evaluation happens through their dashboards and APIs. Bedrock automatically writes training and validation loss metrics to the S3 output location you configured, and you can also view them through CloudWatch or the console. For Cohere, you can poll the job status programmatically using `getFinetunedModel()` from the previous topic, which returns the current status and, once training completes, validation metrics. Both platforms support integration with Weights & Biases: Bedrock can stream metrics to W&B directly, and Cohere does so via the `WandbConfig` you pass when creating the job.

These managed platforms handle most of the evaluation infrastructure for you, but they often limit the custom metrics you can track or the granularity of insights you can access.

Open-source fine-tuning gives you complete control over evaluation. Tools like Weights & Biases and MLflow let you track every aspect of training in real time. With W&B, you can log training loss, validation loss, learning rate schedule, and training speed, and watch the curves update as training progresses. If something looks wrong — loss spiking, validation metrics not improving — you can stop the run early instead of waiting hours for a failed experiment to finish.

Once your model is deployed, evaluation doesn't stop — it actually becomes more critical. Production evaluation monitors how the model performs on real user queries, not just curated test sets. Set up logging to capture model inputs, outputs, and any user feedback signals like thumbs up/down ratings, edit rates, or task completion metrics. For Bedrock and Cohere deployments, you can track API usage metrics through their dashboards, but you'll need your own logging layer to capture application-specific feedback — a plain HTTP call from your Kotlin backend to a tool like Langfuse or LangSmith works well here.

## Deploying your fine-tuned model

Finally, your fine-tuned model must handle real traffic, scale to meet demand, and integrate with your application. The deployment process varies significantly between managed APIs and self-hosted open-source models.

For managed platforms, deployment is straightforward once training completes — the fine-tuned model becomes available through the same API you already use, but with a new model identifier.

For **Cohere**, once `getFinetunedModel()` returns `STATUS_READY`, you call the fine-tuned model via the same `chat()` endpoint, passing the model's ID with the `-ft` suffix:



```kotlin
import com.cohere.api.requests.ChatRequest

val response = cohere.chat(
    ChatRequest.builder()
        .model("$jobId-ft")
        .message("Classify the sentiment of this review: Great product, fast shipping!")
        .build()
)
println(response.text)
// Sentiment: Positive
// Confidence: High
// Key aspects: Product quality, Shipping speed
```



For **AWS Bedrock**, once the customization job completes you invoke the fine-tuned model using the same `BedrockRuntimeClient` you'd use for any other Bedrock model, referencing the custom model ARN returned by the job. Note that some models require provisioned throughput before they can serve inference traffic — check your specific base model's documentation for details:



```kotlin
import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient
import aws.sdk.kotlin.services.bedrockruntime.model.ContentBlock
import aws.sdk.kotlin.services.bedrockruntime.model.ConversationRole
import aws.sdk.kotlin.services.bedrockruntime.model.ConverseRequest
import aws.sdk.kotlin.services.bedrockruntime.model.Message

val runtime = BedrockRuntimeClient { region = "us-east-1" }

val result = runtime.converse(
    ConverseRequest {
        // Use the custom model ARN (or provisioned throughput ARN) from the job
        modelId = "arn:aws:bedrock:us-east-1:123456789012:custom-model/support-classifier-v1"
        messages = listOf(
            Message {
                role = ConversationRole.User
                content = listOf(
                    ContentBlock.Text("Classify the sentiment: Great product, fast shipping!")
                )
            }
        )
    }
)
println(result.output)
```



These deployment models work well when you value simplicity and managed infrastructure. The main tradeoff is vendor lock-in — you depend entirely on the provider's availability, pricing, and terms of service.

For **open-source models**, you have full control but more responsibility. After fine-tuning a model like Mistral or Llama using the Python training pipeline from the previous topic, you need to deploy it on infrastructure you manage. For maximum control and zero per-token API costs, you can self-host using tools like vLLM. Conveniently, vLLM exposes an OpenAI-compatible REST API, so you can use the `openai-java` client with a custom base URL — your Kotlin application code doesn't change at all, just the server it points to:



```kotlin
import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient

val client: OpenAIClient = OpenAIOkHttpClient.builder()
    .baseUrl("http://localhost:8000/v1") // your vLLM server
    .apiKey("not-needed-for-local-vllm")
    .build()
```



You can deploy vLLM on Kubernetes for autoscaling and add caching layers for repeated queries. AWS Bedrock also provides a middle ground: you can upload fine-tuned weights and deploy them through Bedrock's managed infrastructure, getting autoscaling and secure multi-tenancy without managing the serving stack yourself.

## Fine-tuning risks

Fine-tuning isn't without risks. Understanding these pitfalls before you commit time and resources helps you avoid costly mistakes.

One of the most common problems is overfitting, where the model learns your training data so well that it loses the ability to generalize. It essentially memorizes specific examples instead of learning underlying patterns. You can mitigate overfitting by using enough training data, keeping validation and training loss aligned, enabling weight decay, stopping training early when validation metrics stop improving, and testing on diverse examples that differ from your training set.

Data quality issues undermine everything. If your training data contains errors, biases, inconsistent labels, or examples that don't match your actual use case, the model learns those flaws and amplifies them. Always audit your data before fine-tuning, and have multiple reviewers check for labeling consistency. Remove outliers and edge cases that don't represent real usage. A model trained on leaked customer data or biased annotations creates legal and ethical risks that far outweigh any performance gains.

Catastrophic forgetting happens when fine-tuning erases useful knowledge the base model originally had. If you train too aggressively on a narrow task, the model might lose its ability to handle related but slightly different queries. Mitigate this by using moderate learning rates, training for fewer epochs, using PEFT methods like LoRA that modify fewer parameters, and testing the fine-tuned model on diverse inputs beyond your specific task. If the model's general capabilities degrade unacceptably, you may need to mix in some general instruction-following examples alongside your task-specific data.

Cost and resource management can spiral quickly, especially with managed APIs or full fine-tuning of large models. Managed fine-tuning costs include both training and inference charges. Cloud GPU rentals for self-hosted training can also exceed budgets if you don't monitor usage. Set spending limits, estimate costs before starting, use smaller models or PEFT methods when possible, and shut down resources immediately after fine-tuning jobs are complete.

## Conclusion

You've walked through the full second half of the fine-tuning pipeline: tracking training with W&B and provider dashboards, deploying fine-tuned models through the Cohere Java SDK and AWS Bedrock Kotlin SDK, self-hosting open-source models with vLLM behind an OpenAI-compatible endpoint, and avoiding common pitfalls like overfitting and catastrophic forgetting. Together with the previous topic, you now have everything you need to adapt foundation models to your specific needs in Kotlin.
