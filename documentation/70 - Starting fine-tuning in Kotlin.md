# Starting fine-tuning in Kotlin

In this topic, we'll walk through the practical steps of launching fine-tuning in Kotlin, from preparing your data to submitting training. This pipeline applies whether you're fine-tuning Command R models through Cohere's API, running Mistral on AWS Bedrock, or training an open-source Llama model on your own hardware.

## Prerequisites for fine-tuning

![The fine-tuning process](https://ucarecdn.com/9b2d9a7b-e8cd-40ee-9e27-74675a790219/)

Before you start a fine-tuning job, you need the right foundation in place:

- **Training data.** You need a dataset that accurately represents the task you want the model to learn. Quality matters more than quantity — each example should demonstrate the exact behavior you want the model to replicate. The minimum number of examples varies by task complexity and model size.

- **Base model.** Different models have different strengths, licensing terms, and cost structures. Cohere's Command R models offer strong multilingual capabilities and a stable managed fine-tuning platform. AWS Bedrock provides access to several foundation models (including Mistral and Amazon's Titan and Nova) through managed infrastructure. Open-source models like Llama, Mistral, or Qwen give you full control and can run on your own infrastructure, but require more technical setup. OpenAI's GPT models also support fine-tuning, but self-serve access is being phased out for new users. Consider model size, licensing restrictions (especially for commercial use), and whether on-premises deployment is required for data privacy.

- **Infrastructure.** Managed services like Cohere and AWS Bedrock require only data preparation and API configuration. For open-source models, you need GPU access — full fine-tuning of large models demands high-end GPUs like NVIDIA A100s or H100s, while parameter-efficient methods such as LoRA and QLoRA make fine-tuning feasible on more modest hardware.

- **Evaluation strategy.** Set this up before training starts. Hold out 10-20% of your data as a validation set the model never sees. For classification tasks, track accuracy, precision, recall, and F1. For generation tasks, BLEU, ROUGE, and perplexity give quantitative signals, but human and model evaluation are also needed for coherence and style. Set up tracking tools like Weights & Biases early so you can compare runs.

## Preparing your training data

No amount of hyperparameter tuning can compensate for poorly formatted, noisy, or misaligned training examples. Start by structuring your data in the format your chosen platform expects. AWS Bedrock, Cohere, and most managed providers expect JSONL (JSON Lines) files where each line is a JSON object containing a `messages` array with role-based conversation turns. Hugging Face datasets typically use conversational formats or simple text pairs, depending on the model architecture. Here's an example in the standard chat JSONL format:



```json
{"messages": [{"role": "user", "content": "Classify the sentiment of this review: The product arrived damaged and customer service was unhelpful."}, {"role": "assistant", "content": "Sentiment: Negative\nConfidence: High\nKey issues: Product quality, Customer service"}]}
{"messages": [{"role": "user", "content": "Classify the sentiment of this review: Great quality and fast shipping! Exactly what I needed."}, {"role": "assistant", "content": "Sentiment: Positive\nConfidence: High\nKey aspects: Product quality, Shipping speed"}]}
{"messages": [{"role": "user", "content": "Classify the sentiment of this review: It's okay, nothing special but does the job."}, {"role": "assistant", "content": "Sentiment: Neutral\nConfidence: Medium\nKey aspects: Functional, Average quality"}]}
```



When working with vision models, some require image datasets with bounding boxes for object detection, while others accept image URLs or base64-encoded data inline in the conversation:



```json
{"messages": [{"role": "system", "content": "You are an assistant that identifies plant diseases from leaf images."}, {"role": "user", "content": "What disease is affecting this plant?"}, {"role": "user", "content": [{"type": "image_url", "image_url": {"url": "https://example.com/images/tomato-leaf-blight.jpg"}}]}, {"role": "assistant", "content": "Early Blight (Alternaria solani). Recommended treatment: Remove affected leaves, apply copper-based fungicide, and ensure proper spacing for air circulation."}]}
```



Once your data is formatted correctly, validate it thoroughly. Check for duplicates — repeated examples waste training time and can cause the model to overfit. Look for labeling errors or inconsistencies where similar inputs have different outputs without a good reason. Most providers offer a validation step that checks your JSONL file for format errors and estimates token usage before you upload it. Catching format issues early saves time and API costs.

Data balancing matters for classification and structured output tasks. If your training set has 90% examples of one category and only 10% of another, the model will become biased toward the majority class. Either collect more examples of underrepresented classes or use techniques such as oversampling or undersampling to create a more balanced distribution. Also, create an 80-20 or 90-10 split of your data into training and validation sets. Keep the validation set separate and never let the model see it during training.

## Start a fine-tuning job

With your data prepared and validated, you can kick off the actual training process. Each platform has its own API or interface for submitting fine-tuning jobs, but the core concept remains the same: upload your training data, specify the base model, configure hyperparameters, and let the platform handle the rest.

Hyperparameters control how the model learns during fine-tuning, and getting them right makes the difference between effective learning and poor results. Key parameters include learning rate (controls weight updates per step), batch size (examples processed together), number of epochs (passes over the dataset), and warmup steps (gradual learning rate increase at start). For LoRA methods, you'll also set rank (r, controlling parameter count — start with 16) and alpha (scaling factor — typically 2*r). Start with sensible defaults, train a baseline model, then adjust one or two parameters at a time based on your metrics.

**AWS Bedrock** handles training infrastructure for several foundation models and automatically tracks metrics like training and validation loss. Once training completes, your fine-tuned model gets a unique ARN you use for inference. Use the official AWS SDK for Kotlin:



```kotlin
import aws.sdk.kotlin.services.bedrock.BedrockClient
import aws.sdk.kotlin.services.bedrock.model.CustomizationType

suspend fun main() {
    val bedrock = BedrockClient { region = "us-east-1" }
    val response = bedrock.createModelCustomizationJob {
        jobName = "support-classifier-job"
        customModelName = "support-classifier-v1"
        customizationType = CustomizationType.FineTuning
        baseModelIdentifier = "arn:aws:bedrock:us-east-1::foundation-model/mistral.mistral-7b-instruct-v0:2"
        roleArn = "arn:aws:iam::123456789012:role/MyCustomizationRole"
        hyperParameters = mapOf(
            "epochCount" to "3",
            "batchSize" to "8",
            "learningRate" to "0.00001"
        )
        trainingDataConfig { s3Uri = "s3://my-training-bucket/train.jsonl" }
        outputDataConfig { s3Uri = "s3://my-output-bucket/output" }
    }
    println("Fine-tune job ARN: ${response.jobArn}")
}
```



**Cohere** offers fine-tuning for its Command R models via a managed platform with full API access. Cohere ships an official Java SDK (`com.cohere:cohere-java`) that works directly from Kotlin. The workflow is a three-step process: upload a dataset, create the fine-tuned model job referencing that dataset, then poll for completion.

Add the dependency to your Gradle build file first:



```kotlin
dependencies {
    implementation("com.cohere:cohere-java:1.10.1")
}
```



Then upload your JSONL training file as a Cohere dataset and submit the fine-tuning job:



```kotlin
import com.cohere.api.Cohere
import com.cohere.api.resources.finetuning.types.BaseModel
import com.cohere.api.resources.finetuning.types.BaseType
import com.cohere.api.resources.finetuning.types.FinetunedModel
import com.cohere.api.resources.finetuning.types.Hyperparameters
import com.cohere.api.resources.finetuning.types.Settings
import com.cohere.api.resources.datasets.requests.DatasetsCreateRequest
import com.cohere.api.types.DatasetType
import java.nio.file.Files
import java.nio.file.Path

val cohere = Cohere.builder()
    .token(System.getenv("COHERE_API_KEY"))
    .clientName("my-app")
    .build()

// Step 1: Upload the training dataset
val datasetResponse = cohere.datasets().create(
    DatasetsCreateRequest.builder()
        .name("support-classifier-dataset")
        .type(DatasetType.CHAT_FINETUNE_INPUT) // for chat/Command R fine-tuning
        .build(),
    Files.newInputStream(Path.of("training_data.jsonl")),
    null // no separate eval file; Cohere splits automatically
)
val datasetId = datasetResponse.id ?: error("Dataset upload failed")
println("Dataset ID: $datasetId")

// Step 2: Create the fine-tuning job
val ftResponse = cohere.finetuning().createFinetunedModel(
    FinetunedModel.builder()
        .name("support-classifier-command-r")
        .settings(
            Settings.builder()
                .baseModel(
                    BaseModel.builder()
                        .baseType(BaseType.BASE_TYPE_CHAT) // fine-tuning for chat
                        .build()
                )
                .datasetId(datasetId)
                .hyperparameters(
                    Hyperparameters.builder()
                        .trainEpochs(3)
                        .learningRate(0.00001)
                        .build()
                )
                .build()
        )
        .build()
)
val jobId = ftResponse.finetunedModel()?.id ?: error("Job creation failed")
println("Fine-tune job ID: $jobId")

// Step 3: Poll for completion (Cohere also sends an email notification)
val status = cohere.finetuning().getFinetunedModel(jobId)
println("Status: ${status.finetunedModel()?.status}")
// When STATUS_READY, the model is available for inference
```



You can also monitor training metrics in real time by passing a `WandbConfig` to the `Settings` when creating the job — Cohere will push loss curves and validation accuracy directly to your Weights & Biases project.

**For open-source models**, you have full control over the training loop and infrastructure. This is the one part of the pipeline where Kotlin steps aside: the major training frameworks (Hugging Face Transformers and PEFT, or newer tools like Unsloth and Axolotl) are Python-native, and there isn't a JVM equivalent with comparable GPU training support. In practice, a Kotlin application would call out to this step rather than reimplement it — for example by invoking a training script as a subprocess, submitting a job to a Python-based training service, or running it as a separate step in your pipeline, and then loading or serving the resulting weights from your JVM application once training completes. Here's how you'd fine-tune a Mistral model using the Transformers library with LoRA for parameter efficiency, exactly as you would regardless of which language drives the rest of the application:



```python
from transformers import AutoModelForCausalLM, AutoTokenizer, TrainingArguments, Trainer
from peft import LoraConfig, get_peft_model
from datasets import load_dataset

# Load base model and tokenizer
model_name = "mistralai/Mistral-7B-v0.1"
model = AutoModelForCausalLM.from_pretrained(model_name, device_map="auto", load_in_4bit=True)
tokenizer = AutoTokenizer.from_pretrained(model_name)

# Configure LoRA
lora_config = LoraConfig(
    r=16,            # Low-rank dimension
    lora_alpha=32,   # Scaling factor
    target_modules=["q_proj", "v_proj"],  # Which layers to adapt
    lora_dropout=0.05,
    bias="none",
    task_type="CAUSAL_LM"
)

model = get_peft_model(model, lora_config)
model.print_trainable_parameters()  # Shows only ~0.5% of params are trainable

# Load and prepare dataset
dataset = load_dataset("json", data_files={"train": "train.jsonl", "validation": "val.jsonl"})

# Define training arguments
training_args = TrainingArguments(
    output_dir="./mistral-support-finetuned",
    num_train_epochs=3,
    per_device_train_batch_size=4,
    gradient_accumulation_steps=4,
    learning_rate=2e-4,
    fp16=True,           # Mixed precision training
    logging_steps=10,
    save_steps=100,
    evaluation_strategy="steps",
    eval_steps=100,
    report_to="wandb"    # Log metrics to Weights & Biases
)

# Initialize trainer and start fine-tuning
trainer = Trainer(
    model=model,
    args=training_args,
    train_dataset=dataset["train"],
    eval_dataset=dataset["validation"]
)

trainer.train()
trainer.save_model("./mistral-support-final")
```



## Conclusion

In this topic, you've covered the prerequisites for fine-tuning, learned how to format and validate training data, and launched fine-tuning jobs through the AWS Bedrock Kotlin SDK, the Cohere Java SDK, and Python-based tooling for open-source models. In the next topic, you'll evaluate the results, deploy your models to production, and navigate common pitfalls.
