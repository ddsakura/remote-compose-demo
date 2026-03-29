# Remote Compose Notes

## Why this document exists
This document captures the current understanding and direction of this repository's Remote Compose spike.

The goal is to help future contributors and coding agents understand:
- what is already working
- what is still a proof of concept
- what may become the preferred direction later

## Repository goal
This repository explores Android Remote Compose as a server-driven UI approach.

The current spike validates an end-to-end flow:
1. generate `.rc` files
2. publish or host them on a server
3. download and render them in an Android app

## Confirmed current implementation
The current `.rc` generation flow is implemented and working as a proof of concept.

At the moment, the main generator code is here:

- `remote-compose-android/rc-generator/src/main/kotlin/com/example/rcgenerator/HomeScreenRc.kt`

This implementation uses a low-level writer-style API to build Remote Compose documents.
That is useful for validating the binary generation flow, but it is not ideal for normal UI authoring.

This repository also now contains a higher-level Remote Compose example here:

- `remote-compose-android/ui-remote/src/main/kotlin/com/example/uiremote/RemoteHomeScreen.kt`

That higher-level example can be captured into `.rc` through Android instrumentation here:

- `remote-compose-android/ui-remote/src/androidTest/kotlin/com/example/uiremote/CaptureTest.kt`
- `remote-compose-android/ui-remote/build.gradle.kts`

At the time of writing, this means the repo has:
- a confirmed low-level JVM generation path
- a confirmed higher-level instrumentation capture path

These are both real implementations in the repository, but they have different tradeoffs.

## Important distinction: current PoC vs future authoring model
There are two different concerns in this project:

### 1. End-to-end feasibility
This is already validated by the current spike:
- `.rc` can be generated
- `.rc` can be hosted
- Android app can download and render it

### 2. Authoring experience
This is the area under evaluation.

The current low-level writer-style approach is difficult to read and maintain.
The higher-level approach is easier to read, but its development workflow is still constrained.

Because of that, a higher-level Remote Compose authoring style is being evaluated.

## Direction under evaluation
A possible future direction is to move from low-level writer-style generation toward higher-level AndroidX Remote Compose APIs that are closer to Compose-style UI authoring.

Desired benefits:
- more readable UI code
- easier maintenance
- easier iteration
- better local development experience
- reduced need to maintain hand-written low-level document assembly code

However, based on the current repo state, this direction should still be treated as under evaluation for production use.

What is now confirmed in this repository:
- higher-level Remote Compose code can be authored
- that code can be captured into `.rc`
- the generated `.rc` can be published and rendered by the player flow

What is not confirmed in this repository:
- that Android Studio Preview is usable for higher-level Remote Compose preview in this repository
- that the higher-level capture path is the preferred long-term production pipeline
- that one higher-level source fully replaces every lower-level implementation need

## README guidance
The README should accurately distinguish between:

### Current implemented reality
- This repo already has a working spike
- The current generator uses a low-level API
- The current low-level approach is valid as a PoC
- The repo also contains a working higher-level capture example

### Future direction
- A higher-level authoring approach may be preferable
- That approach is not yet the fully adopted implementation in this repo unless code is added and validated
- Any wording should avoid overstating this as already complete

## Official references

These references are useful when evaluating whether this repository should stay with the current
low-level writer approach or gradually move toward higher-level Remote Compose authoring.

### 1. AndroidX Remote Compose release notes
Official artifact and release entry point:
https://developer.android.com/jetpack/androidx/releases/compose-remote?hl=zh-tw

Why it matters:
- confirms the existence of Remote Compose artifacts
- shows the official module/release surface for creation and player libraries

### 2. Official AndroidX DemoWeather example
Official sample source:
https://android.googlesource.com/platform/frameworks/support/+/425b5884319c22d902bf8fade9c9ec1829d81c03/compose/remote/integration-tests/player-view-demos/src/main/java/androidx/compose/remote/integration/view/demos/examples/DemoWeather.kt

Why it matters:
- shows higher-level Compose-like Remote Compose authoring
- uses `@Composable`, `@RemoteComposable`, and `@Preview`
- useful as evidence that higher-level Compose-like authoring exists in official examples
- not sufficient, by itself, to prove that Android Studio Preview is usable for Remote Compose preview in this repository

### 3. AndroidX source diff for higher-level APIs and capture flow
Relevant source diff:
https://android.googlesource.com/platform/frameworks/support/+/a215f3885469d9d3e7a147b6847ee8a01e409d2e^!/

Why it matters:
- shows the higher-level Remote Compose APIs being added/exposed
- useful when evaluating `captureSingleRemoteDocument(...)`
- useful for understanding the direction away from purely low-level writer-only authoring

## Wording guidance
Prefer wording like:
- "current PoC approach"
- "current implementation in this repository"
- "working spike"
- "under evaluation"
- "possible future direction"

Avoid wording like:
- "this is the only way to build Remote Compose"
- "Remote Compose always requires dual maintenance"
- "the higher-level approach is already fully proven in this repo" unless it has actually been implemented and validated here

## Suggested migration strategy
If we decide to evolve the project, prefer an incremental path:

1. keep the current low-level generator working
2. add a small higher-level Remote Compose example alongside it
3. validate `.rc` capture output
4. validate app rendering with the new output
5. validate whether the workflow is practical enough for ongoing development
6. update README after the new path is confirmed

## Practical development rule
Do not replace the current working PoC too early.

Prefer:
- additive changes
- side-by-side comparison
- minimal risk
- documentation that matches the exact repo state

## For coding agents
When asked to modify this repository:

1. understand the existing `.rc` generation pipeline first
2. treat `HomeScreenRc.kt` as the current PoC implementation
3. avoid presenting unverified API assumptions as facts
4. update README conservatively
5. prefer small, reviewable steps over broad rewrites

## Status summary
Current status:
- End-to-end spike: confirmed
- Low-level `.rc` generation: confirmed
- Higher-level Remote Compose authoring code in this repo: confirmed
- Higher-level `.rc` capture through instrumentation in this repo: confirmed
- Android Studio Preview for higher-level Remote Compose preview in this repo: not usable
- Higher-level path as the preferred long-term production pipeline: still under evaluation
