## Project Overview

**Function3DAnimator** is an Android application for building and animating user-defined 3D
mathematical surfaces. It allows users to enter mathematical expressions, render them with OpenGL ES
2.0, and animate them over time. The app supports multiple function definition modes (Cartesian,
Elliptical, Spherical, and parametric) and includes built-in example scenes.

Key features:

- Plots one or more 3D surfaces on the same scene
- Time-dependent functions with live animation
- Multiple function definition modes: `z(x, y)`, `z(r, phi)`, `r(theta, phi)`, parametric surfaces
- Built-in example scenes (waves, pulsating sphere, cylinder, Moebius strip)
- Local function storage with Room
- Custom math keyboard for expression entry
- User-controllable rendering options (grid visibility, rotation sensitivity, orientation)

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Android Views + Jetpack Compose
- **Rendering**: OpenGL ES 2.0
- **Database**: Room
- **Dependency Injection**: Hilt + Dagger
- **Build System**: Gradle Kotlin DSL
- **Android SDK**: minSdk 26, targetSdk 36
- **JVM Target**: Java/Kotlin 17

## Code Architecture

### Project Structure

```
app/src/main/java/io/github/nvlad1/function3danimator/
├── model/              # Math models and surface rendering
│   ├── calculationModel/   # Expression parsing and evaluation
│   ├── FunctionModel.kt    # Core function data class
│   ├── FunctionSurfaceBuilder.kt
│   ├── ColorManager.kt     # Color assignment logic
│   ├── DefaultFunctionSet.kt
│   └── EnumColor.kt
├── openGLutils/        # OpenGL rendering pipeline
│   ├── OpenGLRenderer.kt
│   ├── GLSurfaceViewWithRotation.kt
│   ├── FunctionRenderState.kt
│   └── ShaderUtils.kt
├── database/           # Room persistence layer
│   ├── FunctionRepository.kt    # Main data access layer
│   ├── FunctionDatabase.kt      # Room database
│   ├── FunctionDao.kt
│   └── FunctionDbEntity.kt
├── ui/                 # UI screens and components
│   ├── function_screen/        # Main 3D function editor/viewer
│   ├── functions_list/         # Function management
│   ├── settings/               # App preferences
│   ├── help/
│   ├── theme/
│   ├── MainActivity.kt          # Entry point
│   └── MainViewModel.kt         # Main VM with app state
├── di/                 # Hilt dependency injection
│   └── ProductionModule.kt      # Singleton bindings and custom qualifiers
├── keyboard/           # Custom math expression keyboard
├── exception/          # Custom exception types
└── utils/              # Utility classes
```

### Dependency Injection Pattern

The project uses **Hilt** for dependency injection with custom qualifiers for coroutine scopes and
dispatchers:

- **Custom Qualifiers**: `@ApplicationScope`, `@IoScope`, `@DefaultScope`, `@MainDispatcher`,
  `@IoDispatcher`, `@DefaultDispatcher`
- **Scopes**: All major dependencies are `@Singleton`
- **Module**: `ProductionModule.kt` provides all singletons including:
    - `SharedPreferences` for settings
    - Room `FunctionDatabase` and `FunctionDao`
    - `FunctionRepository` as the main data access layer
    - `ColorManager` for color assignment
    - Coroutine scopes with appropriate dispatchers

### Data Flow

1. **App Initialization** (`AnimatorApplication`):
    - Launches `FunctionRepository.loadFunctions()` in app scope (IO dispatcher)
    - Loads functions from Room database or populates with defaults

2. **Function Management** (`FunctionRepository`):
    - Singleton managing in-memory `ArrayList<FunctionModel>`
    - Emits `StateFlow<Boolean>` via `initComplete` for initialization synchronization
    - Handles function creation, deletion, filtering, and color assignment

3. **UI State** (`MainViewModel`):
    - `@HiltViewModel` managing main app state
    - Uses `viewModelScope` for lifecycle-aware coroutines
    - Composed state object (`MainUiState`) for UI rendering

4. **Rendering** (`FunctionSurfaceBuilder`):
    - Creates vertex/normal arrays from `FunctionRenderState`
    - Generates surface geometry based on function type

### Key Patterns

**Function Models**: Immutable `data class FunctionModel` with:

- Properties for bounds (xmin, xmax, ymin, ymax)
- Expression strings (single or parametric: strX, strY, strZ)
- `FunctionDefinitionType` enum (Cartesian, elliptical, spherical, parametric)
- `TimeUnit` for animation (seconds or milliseconds)
- UUID and timestamp for identity/sorting

**Expression Evaluation**:

- Custom parser in `calculationModel/` package
- Supports operators: `+`, `-`, `*`, `/`, `^`, `%`, `!`
- Math functions: `sqrt`, `sin`, `cos`, `exp`, `log`, `abs`, `asin`, `acos`, `atg`, `sh`, `ch`,
  `tg`, `ctg`
- Variables depend on function type: `x, y, t` (Cartesian), `r, phi, t` (elliptical),
  `theta, phi, t` (spherical), `u, v, t` (parametric)

**Color Management**: `ColorManager` automatically assigns distinct colors to new functions using
`EnumColor` enum

**Persistence**:

- `FunctionDbEntity` maps to/from `FunctionModel` for serialization
- Room handles schema migrations
- Database file: `"functions"`

## Build and Test

### Build Commands

From command line (PowerShell):

```powershell
# Build debug APK
.\gradlew.bat assembleDebug

# Run unit tests
.\gradlew.bat testDebugUnitTest
```

From Android Studio:

1. Open project and let Gradle sync
2. Run `app` configuration on device or emulator

### Testing

- Unit tests use **JUnit 4** (configured in `libs.versions.toml`)
- Test location: `app/src/test/java/`
- Instrumented tests use **Espresso** for UI testing
- No existing test examples in codebase — follow standard Android test conventions

### Build Configuration

- **minSdk**: 26 (Android 8.0)
- **targetSdk**: 36 (Android 15)
- **compileSdk**: 36
- **JVM Target**: 17
- **Lint**: `abortOnError = true` (strict build validation)
- **Release**: ProGuard minification enabled

## Repository Analysis Scope

Treat files and directories matched by `.gitignore` as out of scope for routine analysis.

- Prefer repository exploration commands that respect `.gitignore` by default, such as `rg` and
  `rg --files`.
- Do not open, summarize, search, or mention ignored files unless the user explicitly asks about
  them or the task clearly requires it.
- If an ignored file appears relevant to the task, ask for confirmation before analyzing it.
- This is a workflow preference to reduce noise, not a security boundary.

Out-of-scope directories: `.gradle/`, `build/`, `.idea/`, `local.properties`, `app/release/`,
`*.docx`, `*.iml`

