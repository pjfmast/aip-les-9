# Opdrachten en Vragen per Onderwerp

Dit overzichtsdocument bundelt alle opdrachten, oefenvragen en zelftoetsen behorende bij alle 71 theorie-onderwerpen (van *01 - Introduction to Android* tot en met *71 - Deploying a fine-tuned model in Kotlin*).

### Structuur en Methodiek per Vraag
Iedere vraag in dit document volgt een gestandaardiseerde, logische opbouw:
1. **Vraagnummer en Titel**: Direct gekoppeld aan het onderwerpnummer (bijv. `Vraag 21.1: Slot API for composables`).
2. **Metagegevens**: Bron / Hyperskill Step ID, vraagtype (Multiple Choice, Open vraag, Codeopdracht) en de specifieke theorie-paragraaf.
3. **Vraagstelling**: Probleemdefinitie of contextuele vraag / casus.
4. **Keuzeopties of Opdracht**: De keuzes of criteria voor de opdracht.
5. **Antwoord & Toelichting**: Het juiste antwoord inclusief uitgebreide toelichting en codevoorbeeld (uitklapbaar via `<details>`).
6. **Koppeling naar de theorie**: Directe link naar het desbetreffende theoriebestand in `documentation/`.

---

## Inhoudsopgave

- [01 - Introduction to Android](#01---introduction-to-android)
- [02 - API levels](#02---api-levels)
- [03 - XML](#03---xml)
- [04 - Android project structure](#04---android-project-structure)
- [05 - AndroidManifest file](#05---androidmanifest-file)
- [06 - Resources](#06---resources)
- [07 - Introduction to Jetpack Compose](#07---introduction-to-jetpack-compose)
- [08 - Jetpack Compose project structure](#08---jetpack-compose-project-structure)
- [09 - Hello, Compose!](#09---hello-compose)
- [10 - Text Composable](#10---text-composable)
- [11 - TextField Composable](#11---textfield-composable)
- [12 - Column, Row and Box](#12---column-row-and-box)
- [13 - Recomposition](#13---recomposition)
- [14 - Compose State](#14---compose-state)
- [15 - Recomposition scope](#15---recomposition-scope)
- [16 - Remember in Compose](#16---remember-in-compose)
- [17 - State hoisting](#17---state-hoisting)
- [18 - Modifying Composables](#18---modifying-composables)
- [19 - Buttons](#19---buttons)
- [20 - Events in Compose](#20---events-in-compose)
- [21 - Reusing Composables](#21---reusing-composables)
- [22 - Shapes in Compose](#22---shapes-in-compose)
- [23 - Navigation in Compose](#23---navigation-in-compose)
- [24 - Theming](#24---theming)
- [25 - Introduction to Kotlin Multiplatform (KMP)](#25---introduction-to-kotlin-multiplatform-kmp)
- [26 - Multiplatform targets and architecture](#26---multiplatform-targets-and-architecture)
- [27 - Tools for KMP](#27---tools-for-kmp)
- [28 - Basic project in KMP](#28---basic-project-in-kmp)
- [29 - Large language models - from basics to Kotlin](#29---large-language-models---from-basics-to-kotlin)
- [30 - Prompting fundamentals](#30---prompting-fundamentals)
- [31 - What is an LLM agent](#31---what-is-an-llm-agent)
- [32 - Memory in agents](#32---memory-in-agents)
- [33 - The agentic loop](#33---the-agentic-loop)
- [34 - OpenAI Java SDK in Kotlin](#34---openai-java-sdk-in-kotlin)
- [35 - Introduction to Spring AI](#35---introduction-to-spring-ai)
- [36 - Chat memory in Spring AI](#36---chat-memory-in-spring-ai)
- [37 - Tool calling in Spring AI](#37---tool-calling-in-spring-ai)
- [38 - AI safety for applications that use tools](#38---ai-safety-for-applications-that-use-tools)
- [39 - Setting up your first Ktor application](#39---setting-up-your-first-ktor-application)
- [40 - Ktor Client - First view](#40---ktor-client---first-view)
- [41 - Ktor Client - Engines](#41---ktor-client---engines)
- [42 - Ktor Client - Responses](#42---ktor-client---responses)
- [43 - Ktor Client - Default request and Type-safe request](#43---ktor-client---default-request-and-type-safe-request)
- [44 - Ktor Client - Content negotiation and serialization](#44---ktor-client---content-negotiation-and-serialization)
- [45 - Ktor Client - Custom plugins](#45---ktor-client---custom-plugins)
- [46 - Dependency injection](#46---dependency-injection)
- [47 - Koin basics](#47---koin-basics)
- [48 - Advanced Koin](#48---advanced-koin)
- [49 - Gradle multiplatform configuration](#49---gradle-multiplatform-configuration)
- [50 - The expect - actual mechanism vs interfaces](#50---the-expect---actual-mechanism-vs-interfaces)
- [51 - Kotlin - Native](#51---kotlin---native)
- [52 - Dependency injection in KMP](#52---dependency-injection-in-kmp)
- [53 - Testing and scopes with Koin in KMP](#53---testing-and-scopes-with-koin-in-kmp)
- [54 - Working with JDBC](#54---working-with-jdbc)
- [55 - SQLDelight](#55---sqldelight)
- [56 - SQLDelight - advanced use](#56---sqldelight---advanced-use)
- [57 - Repository pattern in KMP](#57---repository-pattern-in-kmp)
- [58 - SQLDelight in a KMP project](#58---sqldelight-in-a-kmp-project)
- [59 - Key-value storage (Multiplatform Settings)](#59---key-value-storage-multiplatform-settings)
- [60 - Offline-first architecture](#60---offline-first-architecture)
- [61 - Introduction to vector search with Kotlin](#61---introduction-to-vector-search-with-kotlin)
- [62 - Introduction to RAG with Kotlin](#62---introduction-to-rag-with-kotlin)
- [63 - Chunking strategies in RAG with Kotlin](#63---chunking-strategies-in-rag-with-kotlin)
- [64 - Embeddings and vector stores in Spring AI](#64---embeddings-and-vector-stores-in-spring-ai)
- [65 - Qdrant fundamentals](#65---qdrant-fundamentals)
- [66 - Filtering in Qdrant with Kotlin](#66---filtering-in-qdrant-with-kotlin)
- [67 - Building a RAG pipeline in Kotlin](#67---building-a-rag-pipeline-in-kotlin)
- [68 - Retrieval-augmented generation with Spring AI](#68---retrieval-augmented-generation-with-spring-ai)
- [69 - Introduction to model fine-tuning](#69---introduction-to-model-fine-tuning)
- [70 - Starting fine-tuning in Kotlin](#70---starting-fine-tuning-in-kotlin)
- [71 - Deploying a fine-tuned model in Kotlin](#71---deploying-a-fine-tuned-model-in-kotlin)

---

## 01 - Introduction to Android

📖 **Theoriedocument**: [01 - Introduction to Android.md](01%20-%20Introduction%20to%20Android.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 02 - API levels

📖 **Theoriedocument**: [02 - API levels.md](02%20-%20API%20levels.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 03 - XML

📖 **Theoriedocument**: [03 - XML.md](03%20-%20XML.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 04 - Android project structure

📖 **Theoriedocument**: [04 - Android project structure.md](04%20-%20Android%20project%20structure.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 05 - AndroidManifest file

📖 **Theoriedocument**: [05 - AndroidManifest file.md](05%20-%20AndroidManifest%20file.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 06 - Resources

📖 **Theoriedocument**: [06 - Resources.md](06%20-%20Resources.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 07 - Introduction to Jetpack Compose

📖 **Theoriedocument**: [07 - Introduction to Jetpack Compose.md](07%20-%20Introduction%20to%20Jetpack%20Compose.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 08 - Jetpack Compose project structure

📖 **Theoriedocument**: [08 - Jetpack Compose project structure.md](08%20-%20Jetpack%20Compose%20project%20structure.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 09 - Hello, Compose!

📖 **Theoriedocument**: [09 - Hello, Compose!.md](09%20-%20Hello%2C%20Compose!.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 10 - Text Composable

📖 **Theoriedocument**: [10 - Text Composable.md](10%20-%20Text%20Composable.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 11 - TextField Composable

📖 **Theoriedocument**: [11 - TextField Composable.md](11%20-%20TextField%20Composable.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 12 - Column, Row and Box

📖 **Theoriedocument**: [12 - Column, Row and Box.md](12%20-%20Column%2C%20Row%20and%20Box.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 13 - Recomposition

📖 **Theoriedocument**: [13 - Recomposition.md](13%20-%20Recomposition.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 14 - Compose State

📖 **Theoriedocument**: [14 - Compose State.md](14%20-%20Compose%20State.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 15 - Recomposition scope

📖 **Theoriedocument**: [15 - Recomposition scope.md](15%20-%20Recomposition%20scope.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 16 - Remember in Compose

📖 **Theoriedocument**: [16 - Remember in Compose.md](16%20-%20Remember%20in%20Compose.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 17 - State hoisting

📖 **Theoriedocument**: [17 - State hoisting.md](17%20-%20State%20hoisting.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 18 - Modifying Composables

📖 **Theoriedocument**: [18 - Modifying Composables.md](18%20-%20Modifying%20Composables.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 19 - Buttons

📖 **Theoriedocument**: [19 - Buttons.md](19%20-%20Buttons.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 20 - Events in Compose

📖 **Theoriedocument**: [20 - Events in Compose.md](20%20-%20Events%20in%20Compose.md)

### Vraag 20.1: Long click en double click afhandelen

- **Bron**: Hyperskill Step (Events in Compose)
- **Type**: Meerkeuzevraag (Modifier gestures)
- **Theorie-sectie**: *Click modifiers & gestures*

#### Vraagstelling
> What modifier should be used if we want to do long or double click?

#### Keuzeopties
- [ ] `Modifier.verticalScroll`
- [x] `Modifier.combinedClickable`
- [ ] `Modifier.clickable`

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** `Modifier.combinedClickable`

**Toelichting:**
Waar `Modifier.clickable` uitsluitend standaard kliks (`onClick`) afhandelt, biedt `Modifier.combinedClickable` ondersteuning voor meervoudige touch-interacties via `onClick`, `onDoubleClick` en `onLongClick` callbacks.
</details>

---

## 21 - Reusing Composables

📖 **Theoriedocument**: [21 - Reusing Composables.md](21%20-%20Reusing%20Composables.md)

### Vraag 21.1: Slot API for composables

- **Bron**: [Hyperskill Step 41507](https://hyperskill.org/learn/step/41507)
- **Type**: Meerkeuzevraag (Conceptueel / Composable API Design)
- **Theorie-sectie**: *Achieving flexible layouts: using slot-based layout*

#### Vraagstelling
> What does "Slot API" mean in the context of Jetpack Compose composables?

#### Keuzeopties
- [ ] **A.** Een API waarmee componenten data direct vanuit een database of storage cachen in vooraf gedefinieerde geheugenslots.
- [x] **B.** Een ontwerppatroon waarbij een composable een lambda-parameter (zoals `@Composable () -> Unit`) accepteert, zodat de aanroeper zelf kan bepalen welke UI-elementen binnen die specifieke plek (slot) worden geplaatst.
- [ ] **C.** Een speciale `Modifier` die composables automatisch rangschikt in rijen en kolommen.
- [ ] **D.** Een mechanisme om recomposities te pauzeren wanneer een composable buiten het scherm scrolt.

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** **B**

**Toelichting:**
In Jetpack Compose laat een **Slot API** een 'lege ruimte' (slot) over in een composable functie door een `@Composable () -> Unit` (of `@Composable Scope.() -> Unit`) parameter te accepteren. Hierdoor hoeft de composable niet hardcoded te bepalen wat erin getoond wordt, maar kan de aanroeper elk gewenst UI-element injecteren.

Voorbeeld uit `21 - Reusing Composables.md`:
```kotlin
@Composable
fun IdentifierField(
    identifier: String,
    label: String,
    placeholder: String,
    onIdentifierChange: (String) -> Unit,
    leadingIcon: @Composable () -> Unit // <-- Slot API parameter
) {
    TextField(
        modifier = modifier.padding(10.dp),
        value = identifier,
        onValueChange = { onIdentifierChange(it) },
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        leadingIcon = {
            leadingIcon() // Het slot wordt hier aangeroepen/weergegeven
        }
    )
}
```

Hierdoor kan dezelfde `IdentifierField` flexibel worden hergebruikt voor zowel een gebruikersnaamveld (met een `Person` icoon) als een e-mailveld (met een `Email` icoon) zonder duplicatie van layout-code.
</details>

### Vraag 21.2: Naming Patterns for Compose State

- **Bron**: [Hyperskill Step 41511](https://hyperskill.org/learn/step/41511)
- **Type**: Open vraag (Design Pattern / Architecture)
- **Theorie-sectie**: *State hoisting for composable reusability* (zie ook *17 - State hoisting*)

#### Vraagstelling
> What's the name of the pattern that should be used with the `text` state to make the `CustomText` composable reusable across various call sites with custom texts and event handlers?
>
> ```kotlin
> @Composable
> fun CustomText() {
>     var text by remember {
>         mutableStateOf("")
>     }
> 
>     TextField(value = text, onValueChange = { text = it })
> }
> ```

#### Invoer / Opdracht
Noem de naam van het ontwerppatroon (in het Engels of Nederlands) waarmee de interne state wordt geëxtraheerd en verplaatst naar de bovenliggende composable om de component stateless en herbruikbaar te maken.

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** `State hoisting` (of `state hoisting`)

**Toelichting:**
In de huidige code is `CustomText` **stateful** en hardcoded gebonden aan zijn eigen interne `text` state via `remember`. Hierdoor kan een aanroepende composable geen eigen initiële tekst meegeven en niet reageren op wijzigingen.

Door het **State Hoisting** patroon toe te passen:
1. Wordt de state (`text: String`) verheven naar een parameter van de composable functie.
2. Wordt het event (`onTextChange: (String) -> Unit`) als callback-parameter meegegeven.

Herstructureerde, stateless en herbruikbare versie:
```kotlin
@Composable
fun CustomText(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier
    )
}
```

Nu bezit de parent/caller de state (Single Source of Truth) en is `CustomText` volledig herbruikbaar overal in de applicatie.
</details>

---

## 22 - Shapes in Compose

📖 **Theoriedocument**: [22 - Shapes in Compose.md](22%20-%20Shapes%20in%20Compose.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 23 - Navigation in Compose

📖 **Theoriedocument**: [23 - Navigation in Compose.md](23%20-%20Navigation%20in%20Compose.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 24 - Theming

📖 **Theoriedocument**: [24 - Theming.md](24%20-%20Theming.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 25 - Introduction to Kotlin Multiplatform (KMP)

📖 **Theoriedocument**: [25 - Introduction to Kotlin Multiplatform (KMP).md](25%20-%20Introduction%20to%20Kotlin%20Multiplatform%20(KMP).md)

### Vraag 25.1: Capabilities and limitations of Kotlin Multiplatform

- **Bron**: [Hyperskill Step 53889](https://hyperskill.org/learn/step/53889)
- **Type**: Meerkeuzevraag (Multiple Choice - Meerdere antwoorden mogelijk)
- **Theorie-sectie**: *Benefits and use cases of KMP* & *Implementing platform-specific code*

#### Vraagstelling
> Which of the following statements correctly describe the capabilities and limitations of Kotlin Multiplatform for a project aimed at multiple operating systems?

#### Keuzeopties
- [x] **A.** The Kotlin/Native compiler produces self-contained binaries that do not require a virtual machine to execute on iOS and other native platforms. (Correct)
- [ ] **B.** Kotlin Multiplatform can share code across all layers of an application, including UI logic and styling.
- [x] **C.** A single codebase in Kotlin Multiplatform can be used to generate binaries for JVM, Android, iOS, JavaScript, and Native platforms. (Correct)
- [x] **D.** Kotlin Multiplatform projects can include platform-specific code using `expect` and `actual` keywords to handle platform-dependent implementations. (Correct)

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correcte Antwoorden:** **A**, **C** en **D**

**Toelichting:**

1. **Optie A (Correct):**
   - *The Kotlin/Native compiler produces self-contained binaries that do not require a virtual machine to execute on iOS and other native platforms.*
   - Kotlin/Native compileert Kotlin-code met behulp van de LLVM-backend rechtstreeks naar standalone native binaire bestanden (zoals frameworks en executables voor iOS, macOS, Linux en Windows) die geen JVM of virtuele machine vereisen om te draaien.

2. **Optie B (Onjuist):**
   - *Kotlin Multiplatform can share code across all layers of an application, including UI logic and styling.*
   - KMP richt zich primair op het delen van businesslogica, datamodellen, algoritmes en netwerkcode in een gedeelde module (`commonMain`). Hoewel Compose Multiplatform (CMP) het delen van UI mogelijk maakt, deelt KMP zelf niet automatisch alle UI-lagen en native styling (zoals native platformstijlen en widgets) over alle lagen en platforms heen.

3. **Optie C (Correct):**
   - *A single codebase in Kotlin Multiplatform can be used to generate binaries for JVM, Android, iOS, JavaScript, and Native platforms.*
   - Zoals beschreven in het theoriedocument kan Kotlin worden gecompileerd naar JVM-bytecode (Android/Desktop/Server), JavaScript/WebAssembly (Web), en native binaire bestanden via LLVM (iOS/Desktop), allemaal vanuit één Kotlin Multiplatform project.

4. **Optie D (Correct):**
   - *Kotlin Multiplatform projects can include platform-specific code using `expect` and `actual` keywords to handle platform-dependent implementations.*
   - Wanneer gedeelde code platformspecifieke API's moet aanroepen, definieert KMP een contract in `commonMain` met het keyword `expect`, waarna elke platform-source set (`androidMain`, `iosMain`, etc.) de concrete implementatie levert met `actual`.
</details>

### Vraag 25.2: Project's source sets

- **Bron**: [Hyperskill Step 53887](https://hyperskill.org/learn/step/53887)
- **Type**: Meerkeuzevraag (Multiple Choice - Meerdere antwoorden mogelijk)
- **Theorie-sectie**: *Setting up a KMP project* & *Implementing platform-specific code*

#### Vraagstelling
> Given the Kotlin Multiplatform project structure below, identify which statements are accurate about the organization and compilation targets of a multiplatform library.

#### Keuzeopties
- [x] **A.** The `iosArm64` and `iosX64` source sets are specific to iOS platform compiling for different architectures. (Correct)
- [ ] **B.** The `jvm` source set is dedicated for compiling to WebAssembly target.
- [ ] **C.** Every platform-specific source set must have its own implementation of expected declarations from the `commonMain` source set.
- [x] **D.** The `commonMain` source set contains the shared code that can be utilized by all target platforms. (Correct)
- [x] **E.** Including `nativeMain` allows sharing code between different native targets, such as iOS and Linux, without duplication. (Correct)

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correcte Antwoorden:** **A**, **D** en **E**

**Toelichting:**

1. **Optie A (Correct):**
   - *The `iosArm64` and `iosX64` source sets are specific to iOS platform compiling for different architectures.*
   - In KMP worden verschillende doel-architecturen voor hetzelfde platform gemodelleerd via specifieke source sets: `iosArm64` (voor fysieke 64-bit ARM iOS-apparaten en Apple Silicon simulators) en `iosX64` (voor x86_64 Intel simulators).

2. **Optie B (Onjuist):**
   - *The `jvm` source set is dedicated for compiling to WebAssembly target.*
   - De `jvm` source set compileert naar Java Virtual Machine bytecode. Voor WebAssembly wordt gebruikgemaakt van de `wasmJs` of `wasmWasi` source sets.

3. **Optie C (Onjuist):**
   - *Every platform-specific source set must have its own implementation of expected declarations from the `commonMain` source set.*
   - Dankzij de hiërarchische source set-structuur (intermediate source sets zoals `nativeMain` of `appleMain`) kan een gezamenlijke `actual` implementatie worden geleverd voor meerdere onderliggende platforms, waardoor niet elke afzonderlijke platform-specifieke source set de implementatie opnieuw hoeft te dupliceren.

4. **Optie D (Correct):**
   - *The `commonMain` source set contains the shared code that can be utilized by all target platforms.*
   - `commonMain` is de gemeenschappelijke source set in KMP waarin platformonafhankelijke code (zoals businesslogica, datamodellen en algoritmes) wordt geschreven die door alle gedefinieerde platforms kan worden gebruikt.

5. **Optie E (Correct):**
   - *Including `nativeMain` allows sharing code between different native targets, such as iOS and Linux, without duplication.*
   - Een tussenliggende source set zoals `nativeMain` bundelt alle Kotlin/Native platforms (zoals iOS, macOS, Linux en Windows). Hierdoor kan gedeelde native code eenmalig worden geschreven en hergebruikt zonder duplicatie.
</details>

---

## 26 - Multiplatform targets and architecture

📖 **Theoriedocument**: [26 - Multiplatform targets and architecture.md](26%20-%20Multiplatform%20targets%20and%20architecture.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 27 - Tools for KMP

📖 **Theoriedocument**: [27 - Tools for KMP.md](27%20-%20Tools%20for%20KMP.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 28 - Basic project in KMP

📖 **Theoriedocument**: [28 - Basic project in KMP.md](28%20-%20Basic%20project%20in%20KMP.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 29 - Large language models - from basics to Kotlin

📖 **Theoriedocument**: [29 - Large language models - from basics to Kotlin.md](29%20-%20Large%20language%20models%20-%20from%20basics%20to%20Kotlin.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 30 - Prompting fundamentals

📖 **Theoriedocument**: [30 - Prompting fundamentals.md](30%20-%20Prompting%20fundamentals.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 31 - What is an LLM agent

📖 **Theoriedocument**: [31 - What is an LLM agent.md](31%20-%20What%20is%20an%20LLM%20agent.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 32 - Memory in agents

📖 **Theoriedocument**: [32 - Memory in agents.md](32%20-%20Memory%20in%20agents.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 33 - The agentic loop

📖 **Theoriedocument**: [33 - The agentic loop.md](33%20-%20The%20agentic%20loop.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 34 - OpenAI Java SDK in Kotlin

📖 **Theoriedocument**: [34 - OpenAI Java SDK in Kotlin.md](34%20-%20OpenAI%20Java%20SDK%20in%20Kotlin.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 35 - Introduction to Spring AI

📖 **Theoriedocument**: [35 - Introduction to Spring AI.md](35%20-%20Introduction%20to%20Spring%20AI.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 36 - Chat memory in Spring AI

📖 **Theoriedocument**: [36 - Chat memory in Spring AI.md](36%20-%20Chat%20memory%20in%20Spring%20AI.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 37 - Tool calling in Spring AI

📖 **Theoriedocument**: [37 - Tool calling in Spring AI.md](37%20-%20Tool%20calling%20in%20Spring%20AI.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 38 - AI safety for applications that use tools

📖 **Theoriedocument**: [38 - AI safety for applications that use tools.md](38%20-%20AI%20safety%20for%20applications%20that%20use%20tools.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 39 - Setting up your first Ktor application

📖 **Theoriedocument**: [39 - Setting up your first Ktor application.md](39%20-%20Setting%20up%20your%20first%20Ktor%20application.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 40 - Ktor Client - First view

📖 **Theoriedocument**: [40 - Ktor Client - First view.md](40%20-%20Ktor%20Client%20-%20First%20view.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 41 - Ktor Client - Engines

📖 **Theoriedocument**: [41 - Ktor Client - Engines.md](41%20-%20Ktor%20Client%20-%20Engines.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 42 - Ktor Client - Responses

📖 **Theoriedocument**: [42 - Ktor Client - Responses.md](42%20-%20Ktor%20Client%20-%20Responses.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 43 - Ktor Client - Default request and Type-safe request

📖 **Theoriedocument**: [43 - Ktor Client - Default request and Type-safe request.md](43%20-%20Ktor%20Client%20-%20Default%20request%20and%20Type-safe%20request.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 44 - Ktor Client - Content negotiation and serialization

📖 **Theoriedocument**: [44 - Ktor Client - Content negotiation and serialization.md](44%20-%20Ktor%20Client%20-%20Content%20negotiation%20and%20serialization.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 45 - Ktor Client - Custom plugins

📖 **Theoriedocument**: [45 - Ktor Client - Custom plugins.md](45%20-%20Ktor%20Client%20-%20Custom%20plugins.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 46 - Dependency injection

📖 **Theoriedocument**: [46 - Dependency injection.md](46%20-%20Dependency%20injection.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 47 - Koin basics

📖 **Theoriedocument**: [47 - Koin basics.md](47%20-%20Koin%20basics.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 48 - Advanced Koin

📖 **Theoriedocument**: [48 - Advanced Koin.md](48%20-%20Advanced%20Koin.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 49 - Gradle multiplatform configuration

📖 **Theoriedocument**: [49 - Gradle multiplatform configuration.md](49%20-%20Gradle%20multiplatform%20configuration.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 50 - The expect - actual mechanism vs interfaces

📖 **Theoriedocument**: [50 - The expect - actual mechanism vs interfaces.md](50%20-%20The%20expect%20-%20actual%20mechanism%20vs%20interfaces.md)

### Vraag 50.1: Alternative approach to platform-specific code

- **Bron**: [Hyperskill Step 53900](https://hyperskill.org/learn/step/53900)
- **Type**: Meerkeuzevraag (Multiple Choice - Enkel antwoord)
- **Theorie-sectie**: *The interface-based alternative*

#### Vraagstelling
> Besides `expect`/`actual`, what is the other common approach KMP developers use to handle code that needs a different implementation per platform?

#### Keuzeopties
- [ ] **A.** Using reflection to detect the platform at runtime
- [x] **B.** A plain Kotlin interface, implemented once per platform and wired together (often via dependency injection) (Correct)
- [ ] **C.** Compiling the same function twice with different names
- [ ] **D.** Writing separate apps for each platform with no shared code

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** **B**

**Toelichting:**
- In Kotlin Multiplatform is het definiëren van een gewone Kotlin interface in `commonMain` en het implementeren ervan per doelplatform (vaak samengebonden via Dependency Injection of constructor parameters) het belangrijkste alternatief voor het `expect`/`actual` mechanisme.
</details>

### Vraag 50.2: Missing actual implementation

- **Bron**: [Hyperskill Step 53901](https://hyperskill.org/learn/step/53901)
- **Type**: Meerkeuzevraag (Multiple Choice - Enkel antwoord)
- **Theorie-sectie**: *Recap: expect/actual*

#### Vraagstelling
> What happens if a target is missing an `actual` implementation for an `expect` declaration used by that target?

#### Keuzeopties
- [ ] **A.** Kotlin generates a default implementation automatically.
- [ ] **B.** The function silently returns `null` at runtime.
- [ ] **C.** It only produces a warning, not an error.
- [x] **D.** The build fails for that target. (Correct)

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** **D**

**Toelichting:**
- De Kotlin-compiler dwingt af dat voor elke `expect` declaratie een bijbehorende `actual` implementatie aanwezig is in de target source set. Als een target deze mist, faalt de compilatie/build voor dat target.
</details>

### Vraag 50.3: Trade-offs of interfaces vs expect/actual

- **Bron**: [Hyperskill Step 53902](https://hyperskill.org/learn/step/53902)
- **Type**: Meerkeuzevraag (Multiple Choice - Meerdere antwoorden mogelijk)
- **Theorie-sectie**: *Comparing the two*

#### Vraagstelling
> Compared to `expect`/`actual`, what do you give up and gain by using a plain interface for a platform-specific abstraction?

#### Keuzeopties
- [x] **A.** You lose the compile-time guarantee that every platform supplies an implementation. (Correct)
- [x] **B.** You gain the ability to swap a fake implementation into shared code for tests without a platform-specific test source set. (Correct)
- [ ] **C.** You gain a compiler error if a platform forgets to implement the interface.
- [x] **D.** You gain the ability to have multiple implementations (real, fake, test double) for the same platform. (Correct)

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correcte Antwoorden:** **A**, **B** en **D**

**Toelichting:**

1. **Optie A (Correct):** Bij interfaces controleert de compiler niet automatisch of elk platform een implementatie heeft geleverd (dat gebeurt pas bij runtime bedrading/DI).
2. **Optie B (Correct):** Je kunt eenvoudig een test-fake of mock doorgeven in `commonTest` zonder platformspecifieke test source sets met `actual` declaraties op te zetten.
3. **Optie C (Onjuist):** Dit is wat `expect`/`actual` biedt; interfaces bieden geen compile-time afdwinging per platform.
4. **Optie D (Correct):** Met interfaces kun je probleemloos meerdere implementaties (bijv. live, cached, fake) op hetzelfde platform hebben, terwijl `expect`/`actual` slechts één `actual` per target toestaat.
</details>

### Vraag 50.4: Interface-based alternative implementation

- **Bron**: [Hyperskill Step 53903](https://hyperskill.org/learn/step/53903)
- **Type**: Invuloefening (Fill in the blanks)
- **Theorie-sectie**: *The interface-based alternative*

#### Vraagstelling
> Complete the interface-based alternative to `expect`/`actual` for getting the current time.

```kotlin
// commonMain
___ ClockProvider {
    fun currentTimeMillis(): Long
}

// androidMain
class AndroidClockProvider : ClockProvider {
    ___ fun currentTimeMillis() = System.currentTimeMillis()
}

// commonMain
class SessionManager(private val clock: ___) {
    fun sessionAgeMillis(startedAt: Long): Long = clock.currentTimeMillis() - startedAt
}
```

#### In te vullen waarden
1. `interface` (Vak 1 in `commonMain`)
2. `override` (Vak 2 in `androidMain`)
3. `ClockProvider` (Vak 3 in `commonMain`)

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correcte Antwoorden:**
1. `interface`
2. `override`
3. `ClockProvider`

**Volledige code:**
```kotlin
// commonMain
interface ClockProvider {
    fun currentTimeMillis(): Long
}

// androidMain
class AndroidClockProvider : ClockProvider {
    override fun currentTimeMillis() = System.currentTimeMillis()
}

// commonMain
class SessionManager(private val clock: ClockProvider) {
    fun sessionAgeMillis(startedAt: Long): Long = clock.currentTimeMillis() - startedAt
}
```

**Toelichting:**
- In `commonMain` definieer je een `interface ClockProvider`.
- In `androidMain` implementeert `AndroidClockProvider` de interface en overschrijft de methode met het keyword `override`.
- In `commonMain` injecteer je het type `ClockProvider` via de constructor van `SessionManager`.
</details>

### Vraag 50.5: Matching scenarios to approach

- **Bron**: [Hyperskill Step 53904](https://hyperskill.org/learn/step/53904)
- **Type**: Koppeltabel (Matching / Table)
- **Theorie-sectie**: *Comparing the two* & *Conclusion*

#### Vraagstelling
> Match each scenario to the approach it best fits.

#### Tabel / Koppelingen
| Scenario | `expect`/`actual` | Interface |
| :--- | :---: | :---: |
| Getting a platform name as a single string | [x] | [ ] |
| A network client with multiple methods, internal state, and a need for test doubles | [ ] | [x] |
| Generating a UUID using a native platform API | [x] | [ ] |
| An analytics tracker with configurable behavior and several methods | [ ] | [x] |

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correcte Koppelingen:**
- **Getting a platform name as a single string** $\rightarrow$ `expect`/`actual`
- **A network client with multiple methods, internal state, and a need for test doubles** $\rightarrow$ `Interface`
- **Generating a UUID using a native platform API** $\rightarrow$ `expect`/`actual`
- **An analytics tracker with configurable behavior and several methods** $\rightarrow$ `Interface`

**Toelichting:**
- **`expect`/`actual`** is bij uitstek geschikt voor kleine, staatloze platformverschillen met een enkel doel (zoals platformnaam ophalen of native UUID-generatie).
- **`Interface`** is de beste keuze voor complexere componenten en services met interne toestand, meerdere methoden, configuratiemogelijkheden en de behoefte aan test doubles / mocks in tests.
</details>

---

## 51 - Kotlin - Native

📖 **Theoriedocument**: [51 - Kotlin - Native.md](51%20-%20Kotlin%20-%20Native.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 52 - Dependency injection in KMP

📖 **Theoriedocument**: [52 - Dependency injection in KMP.md](52%20-%20Dependency%20injection%20in%20KMP.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 53 - Testing and scopes with Koin in KMP

📖 **Theoriedocument**: [53 - Testing and scopes with Koin in KMP.md](53%20-%20Testing%20and%20scopes%20with%20Koin%20in%20KMP.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 54 - Working with JDBC

📖 **Theoriedocument**: [54 - Working with JDBC.md](54%20-%20Working%20with%20JDBC.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 55 - SQLDelight

📖 **Theoriedocument**: [55 - SQLDelight.md](55%20-%20SQLDelight.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 56 - SQLDelight - advanced use

📖 **Theoriedocument**: [56 - SQLDelight - advanced use.md](56%20-%20SQLDelight%20-%20advanced%20use.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 57 - Repository pattern in KMP

📖 **Theoriedocument**: [57 - Repository pattern in KMP.md](57%20-%20Repository%20pattern%20in%20KMP.md)

### Vraag 57.1: The three layers in a shared data layer

- **Bron**: [Hyperskill Step 53931](https://hyperskill.org/learn/step/53931)
- **Type**: Koppelvraag (Matching)
- **Theorie-sectie**: *Three layers*

#### Vraagstelling
> Match each term to its role in a shared data layer:

#### Termen en Rollen
1. **DTO (Data Transfer Object)**: De `@Serializable` klasse die exact overeenkomt met de structuur en het formaat van de API/wire-response.
2. **Domain Model**: Een pure Kotlin-klasse die uitsluitend gevormd is naar de functionele behoeften en datatypes van de applicatie zelf (onafhankelijk van een specifieke backend).
3. **Repository**: De interface/component waar de rest van de app mee communiceert; haalt DTO's op en levert domeinmodellen terug.
4. **Mapper (toDomain)**: De vertaalfunctie die ruwe data uit het DTO transformeert naar het schone domeinmodel.

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correcte Koppeling:**
- **DTO**: Matches the API / wire format shape (`@Serializable`).
- **Domain model**: Plain class shaped around what the application actually needs.
- **Repository**: The boundary component that the application talks to; fetches DTOs and exposes domain models.
- **Mapper function**: Translates wire-format DTOs into application domain models.

**Toelichting:**
- In een robuuste KMP-architectuur worden netwerk-/database-modellen (DTO's) strikt gescheiden van domeinmodellen. De repository vormt de grenslaag die DTO's ophaalt via netwerkbibliotheken zoals Ktor, en deze mapt naar schone domeinobjecten voor gebruik in ViewModels en UI.
</details>

### Vraag 57.2: Why isolate DTOs from the rest of the application

- **Bron**: [Hyperskill Step 53932](https://hyperskill.org/learn/step/53932)
- **Type**: Meerkeuzevraag (Multiple Choice - Enkel antwoord)
- **Theorie-sectie**: *Repository pattern in KMP / Three layers*

#### Vraagstelling
> Why does the topic argue against using a DTO like `TaskDto` directly throughout the app, instead of mapping it to a domain model?

#### Keuzeopties
- [ ] **A.** Because DTOs cannot be compiled for multiplatform targets like iOS.
- [x] **B.** Because wire-format changes or field renames on the backend would ripple into every screen and component that uses the DTO directly. (Correct)
- [ ] **C.** Because Kotlin Multiplatform requires all classes used in UI to be interfaces.
- [ ] **D.** Because `@Serializable` classes consume significantly more memory than plain domain classes.

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** **B**

**Toelichting:**
- DTO's weerspiegelen het draadformaat van de backend (inclusief optionele nullable velden, backend-specifieke naamgeving en API-versie eigenaardigheden). Wanneer DTO's overal in de UI en business logica worden gebruikt, zorgt elke wijziging in de backend-API voor brekende wijzigingen door de hele applicatie. Door te mappen naar een domeinmodel blijft de impact van API-wijzigingen geïsoleerd tot de mapper/repository laag.
</details>

### Vraag 57.3: Mapping a DTO to a Domain model

- **Bron**: [Hyperskill Step 53933](https://hyperskill.org/learn/step/53933)
- **Type**: Invuloefening (Fill in the blanks)
- **Theorie-sectie**: *Three layers*

#### Vraagstelling
> Complete the mapper that translates a `TaskDto` into a domain `Task`:

```kotlin
// commonMain
@Serializable
data class TaskDto(
    val id: Int,
    val name: String,
    val status: String,
    val due_date: String? = null
)

data class Task(
    val id: Int,
    val name: String,
    val status: TaskStatus,
    val dueDate: LocalDate?
)

enum class TaskStatus { TODO, ONGOING, DONE }

fun TaskDto.toDomain(): Task = Task(
    id = id,
    name = name,
    status = TaskStatus.___(status.___()),
    dueDate = due_date?.let { LocalDate.___(it) }
)
```

#### In te vullen waarden
1. `valueOf` (in `TaskStatus.valueOf(...)`)
2. `uppercase` (in `status.uppercase()`)
3. `parse` (in `LocalDate.parse(it)`)

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correcte Code:**
```kotlin
fun TaskDto.toDomain(): Task = Task(
    id = id,
    name = name,
    status = TaskStatus.valueOf(status.uppercase()),
    dueDate = due_date?.let { LocalDate.parse(it) }
)
```

**Toelichting:**
- `TaskStatus.valueOf(status.uppercase())` zet de inkomende string om naar de juiste enum-waarde.
- `due_date?.let { LocalDate.parse(it) }` parseert de optionele ISO-datumstring via `kotlinx-datetime` veilig naar een type-safe `LocalDate` instantie.
</details>

### Vraag 57.4: Data representation exposed to the rest of the application

- **Bron**: [Hyperskill Step 53934](https://hyperskill.org/learn/step/53934)
- **Type**: Meerkeuzevraag (Multiple Choice - Enkel antwoord)
- **Theorie-sectie**: *Three layers*

#### Vraagstelling
> After a `TaskDto` is mapped to a `Task` using `toDomain()`, what does the rest of the app see?

#### Keuzeopties
- [ ] **A.** The raw JSON payload and HTTP response headers.
- [ ] **B.** Nullable strings for `due_date` and unvalidated strings for `status`.
- [x] **C.** A nullable `dueDate: LocalDate?` and a typed `TaskStatus` enum — never the DTO's raw string shape. *(Gecorrigeerd: in de originele Hyperskill-tekst staat abusievelijk "non-nullable dueDate: LocalDate?")* (Correct)
- [ ] **D.** An `expect` declaration that must be resolved per platform target.

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** **C**

**Toelichting:**
- Zodra de mapping voltooid is, ziet de rest van de applicatie uitsluitend het domeinmodel `Task`. Ruwe JSON-details, snake_case veldnamen (`due_date`) en ongevalideerde primitieve types (`String`) worden vervangen door strongly-typed concepten zoals `TaskStatus` en `LocalDate?`.
- **Noot over originele Hyperskill-formulering**: In de oorspronkelijke meerkeuzeoptie op Hyperskill stond de formulering *"A non-nullable dueDate: LocalDate? and a typed TaskStatus enum..."*. Dit is een tegenstrijdigheid/typefout van de auteur: in Kotlin is `LocalDate?` (met vraagteken) een **nullable** type, wat overeenkomt met de optionele `due_date: String? = null` in `TaskDto`. De gecorrigeerde bewoording is daarom **nullable `dueDate: LocalDate?`** (of sterk getypeerd t.o.v. de ruwe string).
</details>

### Vraag 57.5: Why use an Interface instead of expect/actual for Repositories

- **Bron**: [Hyperskill Step 53935](https://hyperskill.org/learn/step/53935)
- **Type**: Meerkeuzevraag (Multiple Choice - Meerdere antwoorden mogelijk)
- **Theorie-sectie**: *Defining the repository as an interface / Testing behind the interface*

#### Vraagstelling
> Which properties of `TaskRepository` make it a case for an interface rather than `expect`/`actual`, per the interface-vs-expect/actual guidance covered earlier?

#### Keuzeopties
- [x] **A.** The repository has multiple methods and operations rather than a single top-level utility function. (Correct)
- [x] **B.** It has no platform-specific behavior or native OS dependencies; multiplatform libraries like Ktor and kotlinx.serialization run directly in `commonMain`. (Correct)
- [x] **C.** There is an obvious need to substitute a fake implementation (`FakeTaskRepository`) in unit tests in `commonTest` without requiring platform-specific test sets. (Correct)
- [ ] **D.** The Kotlin compiler prohibits using `expect`/`actual` for classes that use coroutines or `suspend` functions.

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correcte Antwoorden:** **A**, **B** en **C**

**Toelichting:**
1. **Optie A (Correct):** Interfaces zijn uitermate geschikt voor componenten met meerdere gerelateerde functies en bewerkingen.
2. **Optie B (Correct):** Omdat Ktor en kotlinx.serialization multiplatform zijn, is er geen platformspecifieke code nodig; alles leeft in `commonMain`.
3. **Optie C (Correct):** Met een interface kan in `commonTest` eenvoudig een `FakeTaskRepository` worden geïnjecteerd via Koin zonder dat er platform-specifieke test sourcesets (`androidUnitTest`, `iosTest`) nodig zijn.
4. **Optie D (Onjuist):** `expect`/`actual` ondersteunt gewoon `suspend` functies, maar is hier architectonisch overbodig omdat er geen platformverschillen bestaan.
</details>

---

## 58 - SQLDelight in a KMP project

📖 **Theoriedocument**: [58 - SQLDelight in a KMP project.md](58%20-%20SQLDelight%20in%20a%20KMP%20project.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 59 - Key-value storage (Multiplatform Settings)

📖 **Theoriedocument**: [59 - Key-value storage (Multiplatform Settings).md](59%20-%20Key-value%20storage%20(Multiplatform%20Settings).md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 60 - Offline-first architecture

📖 **Theoriedocument**: [60 - Offline-first architecture.md](60%20-%20Offline-first%20architecture.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 61 - Introduction to vector search with Kotlin

📖 **Theoriedocument**: [61 - Introduction to vector search with Kotlin.md](61%20-%20Introduction%20to%20vector%20search%20with%20Kotlin.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 62 - Introduction to RAG with Kotlin

📖 **Theoriedocument**: [62 - Introduction to RAG with Kotlin.md](62%20-%20Introduction%20to%20RAG%20with%20Kotlin.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 63 - Chunking strategies in RAG with Kotlin

📖 **Theoriedocument**: [63 - Chunking strategies in RAG with Kotlin.md](63%20-%20Chunking%20strategies%20in%20RAG%20with%20Kotlin.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 64 - Embeddings and vector stores in Spring AI

📖 **Theoriedocument**: [64 - Embeddings and vector stores in Spring AI.md](64%20-%20Embeddings%20and%20vector%20stores%20in%20Spring%20AI.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 65 - Qdrant fundamentals

📖 **Theoriedocument**: [65 - Qdrant fundamentals.md](65%20-%20Qdrant%20fundamentals.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 66 - Filtering in Qdrant with Kotlin

📖 **Theoriedocument**: [66 - Filtering in Qdrant with Kotlin.md](66%20-%20Filtering%20in%20Qdrant%20with%20Kotlin.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 67 - Building a RAG pipeline in Kotlin

📖 **Theoriedocument**: [67 - Building a RAG pipeline in Kotlin.md](67%20-%20Building%20a%20RAG%20pipeline%20in%20Kotlin.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 68 - Retrieval-augmented generation with Spring AI

📖 **Theoriedocument**: [68 - Retrieval-augmented generation with Spring AI.md](68%20-%20Retrieval-augmented%20generation%20with%20Spring%20AI.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 69 - Introduction to model fine-tuning

📖 **Theoriedocument**: [69 - Introduction to model fine-tuning.md](69%20-%20Introduction%20to%20model%20fine-tuning.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 70 - Starting fine-tuning in Kotlin

📖 **Theoriedocument**: [70 - Starting fine-tuning in Kotlin.md](70%20-%20Starting%20fine-tuning%20in%20Kotlin.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

## 71 - Deploying a fine-tuned model in Kotlin

📖 **Theoriedocument**: [71 - Deploying a fine-tuned model in Kotlin.md](71%20-%20Deploying%20a%20fine-tuned%20model%20in%20Kotlin.md)

*Nog geen opdrachten geregistreerd voor dit onderwerp. Gebruik het onderstaande sjabloon om nieuwe vragen toe te voegen:*

```markdown
### Vraag XX.Y: [Titel van de vraag]

- **Bron**: [Hyperskill Step ID of referentie]
- **Type**: Meerkeuze / Open vraag / Code-opdracht
- **Theorie-sectie**: [Titel van de paragraaf in het bijbehorende theoriebestand]

#### Vraagstelling
> [Beschrijving of vraag]

#### Keuzeopties / Opdracht
- [ ] Optie A
- [x] Optie B (Correct)
- [ ] Optie C

<details>
<summary>💡 <b>Klik hier voor het juiste antwoord en toelichting</b></summary>

**Correct Antwoord:** ...

**Toelichting:**
...
</details>
```

---

