# AGENTS.md

## Repo purpose
This repository is a spike for Android Remote Compose.
It validates an end-to-end flow where:
1. a generator produces `.rc` files
2. a server hosts those files
3. an Android app downloads and renders them

## Current state
- The current `.rc` generation flow works.
- The current generator implementation is a proof of concept.
- `remote-compose-android/rc-generator/src/main/kotlin/com/example/rcgenerator/HomeScreenRc.kt`
  currently uses a low-level writer-style API.
- Treat that implementation as a working PoC, not automatically as the final recommended authoring model.

## Important architectural context
There are two different authoring directions in this repo/context:

1. Low-level RC generation
- Direct writer / command style generation
- Good for proving binary generation works
- Harder to maintain and not ideal for normal UI authoring

2. Higher-level Remote Compose authoring
- Prefer evaluating AndroidX Remote Compose higher-level Compose-like APIs when feasible
- Goal: author Remote Compose UI in a way closer to Compose, with better readability and possible Preview-driven development
- Any migration toward this direction should be incremental and low risk

## Guidance for edits
- Prefer small, reviewable changes
- Preserve the existing working spike unless the task explicitly asks for larger refactors
- Do not remove the current low-level generator flow unless explicitly requested
- If proposing a new higher-level Remote Compose path, add it alongside the current PoC first
- Clearly separate:
  - current implemented PoC
  - future or proposed direction
- Do not present unverified assumptions as facts
- If an AndroidX Remote Compose API is uncertain or not confirmed in this repo, call that out clearly

## README expectations
README content must distinguish between:
- what is already implemented in this repo
- what is experimental
- what is a possible future refactor direction

Avoid wording that implies:
- the current low-level writer flow is the only valid way to build Remote Compose
- dual maintenance is always required by Remote Compose itself

Prefer wording like:
- "current PoC approach"
- "current implementation in this repo"
- "possible future direction"
- "under evaluation"

## Where to look first
- `README.md`
- `remote-compose-android/rc-generator/src/main/kotlin/com/example/rcgenerator/HomeScreenRc.kt`
- any modules related to preview/reference UI
- any Gradle tasks that generate or publish `.rc`

## External references
Use these official references when evaluating Remote Compose direction:

- AndroidX Remote Compose release notes:
  https://developer.android.com/jetpack/androidx/releases/compose-remote?hl=zh-tw

- Official AndroidX demo example:
  https://android.googlesource.com/platform/frameworks/support/+/425b5884319c22d902bf8fade9c9ec1829d81c03/compose/remote/integration-tests/player-view-demos/src/main/java/androidx/compose/remote/integration/view/demos/examples/DemoWeather.kt

- AndroidX source diff showing higher-level Remote Compose compose APIs and capture flow:
  https://android.googlesource.com/platform/frameworks/support/+/a215f3885469d9d3e7a147b6847ee8a01e409d2e^!/

## Change strategy
When asked to improve the project, prefer this order:
1. understand the existing `.rc` generation pipeline
2. identify which parts are PoC-only
3. propose minimal additive changes
4. update docs so they match reality
5. only then consider broader refactors

## Done means
A task is complete only when:
- code changes match the current repo reality
- README claims are accurate and not overstated
- existing working spike behavior is preserved unless the task explicitly says otherwise
- any proposed future direction is clearly labeled as proposal, evaluation, or next step

## Communication style
When summarizing findings:
- be explicit about what is confirmed vs inferred
- mention tradeoffs
- prefer practical recommendations over abstract theory