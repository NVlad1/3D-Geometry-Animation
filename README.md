# Function3DAnimator

An Android app for building and animating user-defined 3D mathematical surfaces.

The project lets you enter functions, render them with OpenGL ES 2.0, and animate them over time
when the formula depends on `t`. It is a personal project intended to become a polished open-source
portfolio piece and a future F-Droid candidate.

## What it does

- Plots one or more 3D surfaces on the same scene
- Supports time-dependent functions and live animation
- Supports multiple function definition modes:
    - `z(x, y)`
    - `z(r, phi)`
    - `r(theta, phi)`
    - parametric surfaces `x(u, v)`, `y(u, v)`, `z(u, v)`
- Includes built-in example scenes such as waves, a pulsating sphere, a cylinder, and a Moebius
  strip
- Stores user-defined functions locally with Room
- Provides a custom math keyboard for entering expressions more easily
- Lets the user control rendering options such as grid visibility, rotation sensitivity, and graph
  screen orientation

## Architecture

Function3DAnimator is built around a custom expression parser and evaluator that turns user-entered
math into renderable surface data, then generates OpenGL ES 2.0 vertex and normal buffers for
Cartesian, elliptical, spherical, and parametric surfaces. The rendering layer supports
time-dependent formulas through the `t` variable, so surfaces can be animated without changing the
stored function definition, while Room persistence keeps user functions and built-in examples
available offline. The UI combines the original Android Views flow with an ongoing Kotlin/Jetpack
Compose migration, making the project a compact example of math parsing, real-time 3D rendering,
local data storage, and incremental Android modernization.

## Example expressions

Non-parametric:

```text
x*x*cos(t)/3.0
exp(-(x^2+y^2)*(4+cos(t)))
sin(r+phi+t)
1+cos(t)
```

Parametric:

```text
x(u,v) = (1+0.5*v*cos(0.5*(u+t)))*cos(u+t)
y(u,v) = (1+0.5*v*cos(0.5*u+t))*sin(u+t)
z(u,v) = 0.5*v*sin(0.5*(u+t))
```

## Supported math

Operators:

- `+`
- `-`
- `*`
- `/`
- `^`
- `%`
- `!`

Functions:

- `sqrt`
- `exp`
- `sin`
- `cos`
- `tg`
- `ctg`
- `abs`
- `log`
- `ln`
- `asin`
- `acos`
- `atan`
- `sh`
- `ch`

Variables depend on the selected function type:

- Cartesian: `x`, `y`, `t`
- Elliptical: `r`, `phi`, `t`
- Spherical: `theta`, `phi`, `t`
- Parametric: `u`, `v`, `t`

## Tech stack

- Kotlin
- Android Views + Jetpack Compose
- OpenGL ES 2.0
- Room
- Hilt
- Gradle Kotlin DSL

Current Android configuration:

- `minSdk = 26`
- `targetSdk = 36`
- Java/Kotlin target: `17`

## Build and run

### Requirements

- Android Studio with a recent Android SDK installed
- JDK 17
- An Android device or emulator with OpenGL ES 2.0 support

### From Android Studio

1. Open the project in Android Studio.
2. Let Gradle sync.
3. Run the `app` configuration on a device or emulator.

### From the command line

```powershell
.\gradlew.bat assembleDebug
```

To run unit tests:

```powershell
.\gradlew.bat testDebugUnitTest
```

## Project structure

- `app/src/main/java/io/github/nvlad1/function3danimator/model` contains the function models,
  surface generation, and expression evaluation logic
- `app/src/main/java/io/github/nvlad1/function3danimator/openGLutils` contains the renderer and
  OpenGL helpers
- `app/src/main/java/io/github/nvlad1/function3danimator/database` contains local persistence with
  Room
- `app/src/main/java/io/github/nvlad1/function3danimator/ui` contains the app screens and UI logic

## Status

This is an actively maintained personal project. The app is fully functional and continues to be
improved, with ongoing refinements and documentation updates.

The app allows Android system backups. No data is transmitted by the app itself, but system-level
backup services (e.g., Google Drive) may store app data externally.

## Author

Vladislav Naboka

## License

This project is licensed under the MIT License - see the LICENSE file for details.
