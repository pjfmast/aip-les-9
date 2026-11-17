---
sessionId: session-261115-204751-1wlm
---

# Requirements

### Overview & Goals
Provide a clear, practical, step-by-step workflow for utilizing the Valkyrie IntelliJ / Android Studio plugin to convert vector assets (`SVG` / Android Vector XML) into optimized, standalone Kotlin `ImageVector` code. This eliminates dependency bloat from extended icon libraries and maintains fast build times in this Compose Multiplatform project.

### Scope
- **In Scope:**
  - Step-by-step instructions for configuring and invoking the installed Valkyrie plugin within IntelliJ IDEA / Android Studio.
  - Recommended source folder layout for raw vector assets and destination package structure for generated Kotlin `ImageVector` files in the `shared` module.
  - Refactoring pattern for `AccountSpinnerExample.kt` to migrate from `androidx.compose.material.icons.*` to the local generated icon accessors.
  - Best practices for icon naming, organization (`AppIcons` object namespace), and multiplatform compatibility.
- **Out of Scope:**
  - Plugin installation (Valkyrie is already installed).
  - Runtime SVG parsing libraries (e.g., Coil SVG decoder at runtime).
  - Modifying external Gradle / Maven / Amper dependencies beyond icon libraries.

### User Stories
- **As a developer**, I want step-by-step instructions on converting SVG/XML icons using Valkyrie so that I can generate type-safe `ImageVector` code directly in my Kotlin Multiplatform codebase.
- **As a developer**, I want to replace `androidx.compose.material:material-icons-extended` references in `AccountSpinnerExample.kt` with local vector definitions so that my project compiles faster without unnecessary class bloat.

### Functional Requirements
- Step-by-step Valkyrie usage guide covering file preparation, plugin invocation, configuration settings, and output generation.
- Clear file and package organization conventions within `shared/src/com/example/aip_les_9/icons/`.
- Concrete code examples showing generated `ImageVector` structure and migration in composables.

# Technical Design

### Current Implementation
- `AccountSpinnerExample.kt` in `shared/src/` currently imports icons from `androidx.compose.material.icons.Icons` (`Icons.Default.Email`, `Icons.Default.Phone`, `Icons.Default.ArrowDropDown`).
- The project is a Compose Multiplatform project configured with Amper (`shared/module.yaml`), targeting JVM and Android.

### Key Decisions
1. **Source Vector Location:** Raw `.svg` or `.xml` assets should be kept in a designated source directory (e.g., `design/icons/` or `shared/composeResources/icons/`) as the source of truth.
2. **Generated Code Destination:** Generated Kotlin `ImageVector` files reside in `shared/src/com/example/aip_les_9/icons/` under the package `com.example.aip_les_9.icons`.
3. **Namespace Organization:** Use an `AppIcons` container object or extension properties on a shared object (e.g., `AppIcons.Email`, `AppIcons.Phone`, `AppIcons.ArrowDropDown`) to avoid top-level namespace pollution and enable IDE autocomplete.

### Valkyrie Step-by-Step Workflow Guide

#### Step 1: Prepare Source Vectors
1. Download or export the required icon files in `.svg` format (or Android Vector XML format).
2. For `AccountSpinnerExample.kt`, obtain:
   - `email.svg` / `ic_email.xml`
   - `phone.svg` / `ic_phone.xml`
   - `arrow_drop_down.svg` / `ic_arrow_drop_down.xml`
3. Place them in a project asset directory, for example `design/icons/`.

#### Step 2: Open Valkyrie in IntelliJ IDEA / Android Studio
1. Right-click on the SVG file (or vector folder) in the Project tool window.
2. Select **Valkyrie** -> **Convert to ImageVector** (or press the Valkyrie shortcut / action `Ctrl+Shift+V` / `Cmd+Shift+V` if configured).
3. Alternatively, open the **Valkyrie Tool Window** from the right tool window bar or via **View -> Tool Windows -> Valkyrie**.

#### Step 3: Configure Conversion Settings in Valkyrie Dialog
In the Valkyrie conversion popup/settings:
- **Output Directory:** Set to `shared/src/com/example/aip_les_9/icons/` (or relative path to your shared source folder).
- **Package Name:** Set to `com.example.aip_les_9.icons`.
- **Icon Accessor / Nesting Type:**
  - Choose `Nested in object` with object name `AppIcons`, or `Extension property` on `AppIcons`.
- **Vector Name:** Use PascalCase (e.g., `Email`, `Phone`, `ArrowDropDown`).
- **All / Batch Mode:** If converting a folder, select all icons to generate in one batch.
- Click **Generate / Convert**.

#### Step 4: Verify Generated Kotlin Code
Valkyrie generates standalone Kotlin files like `AppIcons.kt` or individual files (`Email.kt`, `Phone.kt`, `ArrowDropDown.kt`) with the following pattern:

```kotlin
package com.example.aip_les_9.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object AppIcons

val AppIcons.Email: ImageVector
    get() {
        if (_email != null) return _email!!
        _email = ImageVector.Builder(
            name = "Email",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                // Vector path commands
            }
        }.build()
        return _email!!
    }

private var _email: ImageVector? = null
```

#### Step 5: Update Imports and Usages in Composable Files
Update `AccountSpinnerExample.kt`:
```kotlin
// Remove:
// import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.filled.Email
// import androidx.compose.material.icons.filled.Phone

// Add:
import com.example.aip_les_9.icons.AppIcons
import com.example.aip_les_9.icons.Email
import com.example.aip_les_9.icons.Phone
import com.example.aip_les_9.icons.ArrowDropDown

// In EmailAccountItem:
Icon(
    imageVector = AppIcons.Email,
    contentDescription = "Email Account",
    tint = MaterialTheme.colorScheme.primary
)

// In PhoneAccountItem:
Icon(
    imageVector = AppIcons.Phone,
    contentDescription = "Phone Account",
    tint = MaterialTheme.colorScheme.secondary
)
```

### File Structure
```
aip-les-9/
├── design/
│   └── icons/
│       ├── email.svg
│       ├── phone.svg
│       └── arrow_drop_down.svg
└── shared/
    └── src/
        ├── AccountSpinnerExample.kt
        └── com/
            └── example/
                └── aip_les_9/
                    └── icons/
                        ├── AppIcons.kt
                        ├── Email.kt
                        ├── Phone.kt
                        └── ArrowDropDown.kt
```

# Testing

### Validation Approach
Verify that the generated icons compile cleanly across all Kotlin Multiplatform targets and render properly in UI composables.

### Key Scenarios
1. **Compilation Check:** Run compilation on JVM and Android targets (`shared` module) to ensure all generated `ImageVector` files compile with no unresolved references.
2. **UI Visual Rendering:** Verify that `AccountSpinnerExample` renders the Email icon, Phone icon, and dropdown arrow with proper color tinting and sizing.
3. **No Extended Library Overhead:** Confirm that the project builds without requiring `material-icons-extended` dependency.

### Edge Cases
- **Multi-path SVGs:** Verify that SVGs with multiple layers/paths convert into multi-path `ImageVector.Builder` calls.
- **Custom Viewport Dimensions:** Check that non-standard viewports (e.g. 20x20 or 48x48) are preserved with matching `dp` sizes in the generated builder.

# Delivery Steps

### ✓ Step 1: Configure asset storage and Valkyrie output settings
A dedicated asset folder and target package are established in the project structure for Valkyrie icon exports.

- Create or designate an icon vector asset folder (such as `shared/design/icons/` or project assets) for storing source `.svg` and `.xml` vector files.
- Configure Valkyrie's output directory in IntelliJ IDEA/Android Studio to target `shared/src/com/example/aip_les_9/icons/`.
- Set the target package name in Valkyrie preferences to `com.example.aip_les_9.icons` and select the preferred accessor nesting pattern (e.g., `AppIcons.<IconName>`).

### ✓ Step 2: Convert vector assets to Kotlin ImageVector source files
Kotlin source files containing standalone ImageVector definitions are generated from vector files using Valkyrie.

- Obtain the required SVG or Android Vector XML files (for `Email`, `Phone`, `ArrowDropDown`).
- Run the Valkyrie conversion tool on the vector files to produce individual Kotlin `ImageVector` property files (e.g., `Email.kt`, `Phone.kt`, `ArrowDropDown.kt`).
- Ensure generated files compile cleanly as pure Kotlin multiplatform `ImageVector`s without requiring Android-specific dependencies.

### ✓ Step 3: Integrate generated icons into AccountSpinnerExample and project UI
AccountSpinnerExample.kt and other components use the generated local ImageVectors instead of broad library imports.

- Update imports in `AccountSpinnerExample.kt` from `androidx.compose.material.icons.filled.*` to `com.example.aip_les_9.icons.AppIcons.*` (or generated icon accessors).
- Replace `Icons.Default.Email`, `Icons.Default.Phone`, and dropdown icons with the newly generated `ImageVector` properties.
- Verify compilation and UI preview rendering across desktop and Android targets to ensure visual parity.