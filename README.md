# Remote Compose Spike

驗證「從 Server 動態推送 UI，Android App 不需要發版即可更新畫面」。

## 目的

利用 [AndroidX Remote Compose](https://developer.android.com/jetpack/androidx/releases/compose-remote) library，將 UI 定義序列化為 `.rc` binary 檔案，由 Server 提供給 Android App 渲染。UI 改版只需更新 Server，App 無需重新發版。

## 架構

```
remote-compose/
├── remote-compose-android/     ← Android 專案
│   ├── app/                    ← 播放端：從 Server 抓 .rc 並渲染
│   ├── ui-remote/              ← UI 定義：Composable + @Preview（設計參考）
│   └── rc-generator/           ← 產生端：RemoteComposeWriter → .rc 檔案
│
└── remote-compose-server/      ← Node.js 靜態檔案 Server
    ├── server.js
    └── static/                 ← 存放 .rc 檔案（由 rc-generator 產生）
```

### 流程

```
1. 改 ui-remote/HomeScreen.kt       ← @Preview 確認 UI 長相
       ↓
2. 同步改 rc-generator/HomeScreenRc.kt  ← RemoteComposeWriter 版本
       ↓
3. ./gradlew :rc-generator:publishRc    ← 產生 .rc 並複製到 server/static/
       ↓
4. npm start（remote-compose-server/）  ← 啟動靜態 Server
       ↓
5. 跑 Android App（模擬器）            ← 抓 .rc 渲染，不需重裝
```

## 操作說明

### Server 端

**第一次：**
```bash
cd remote-compose-server
npm install
```

**每次啟動：**
```bash
npm start
# Server 跑在 http://localhost:8080
# GET /health        → 健康檢查
# GET /ui/home-v1.rc → V1 UI
# GET /ui/home-v2.rc → V2 UI
```

### Android 端

**更新 UI 並推到 Server：**
```bash
cd remote-compose-android
./gradlew :rc-generator:publishRc
```
> 產生 `home-v1.rc`、`home-v2.rc` 並自動複製到 `remote-compose-server/static/`

**Build App：**
```bash
./gradlew assembleDebug
```

App 跑在模擬器時，透過 `http://10.0.2.2:8080` 連到本機 Server。切換畫面左上角的 **V1 / V2** 可即時拉取不同版本的 UI。

## Dependencies

| 模組 | 用途 | 關鍵 Library |
|------|------|-------------|
| `app` | 渲染 .rc | `remote-player-core`, `remote-player-view` |
| `ui-remote` | UI 設計 + @Preview | `remote-creation-compose` |
| `rc-generator` | 產生 .rc（純 JVM） | `remote-creation`, `remote-creation-core` |
| `remote-compose-server` | Serve .rc 靜態檔案 | Express |

Remote Compose 版本：`1.0.0-alpha07`

---

## 已知限制與待解問題

### 1. `.rc` 產生的 DX 問題

目前改 UI 需要維護兩份檔案：

| 檔案 | 用途 |
|------|------|
| `ui-remote/HomeScreen.kt` | Composable，給 `@Preview` 看 UI 長相 |
| `rc-generator/HomeScreenRc.kt` | RemoteComposeWriter 低階 API，實際產生 `.rc` |

`HomeScreenRc.kt` 語法低階、難以直接閱讀和修改。可能的解法：

| 方案 | 優點 | 缺點 |
|------|------|------|
| **JSON → `.rc`**（參考 [armcha/remotecompose](https://github.com/armcha/remotecompose)） | 人可讀、可直接編輯、純 JVM | UI 描述能力有限，不如 Compose 靈活 |
| **`@RemoteComposable` + `captureSingleRemoteDocument`** | 語法接近 Compose、單一來源 | 需要 Android 模擬器或實機執行 capture |
| **Gradle task 呼叫 LLM API 轉換** | 見下方說明 | 見下方說明 |

#### LLM 轉換（詳細分析）

概念：`HomeScreen.kt`（Composable）作為唯一來源，Gradle task 讀取檔案內容後呼叫 LLM API，自動產生對應的 `HomeScreenRc.kt`（RemoteComposeWriter）。

**優點**
- 只需維護一份 `HomeScreen.kt`，不需手動同步兩個檔案
- Compose 語法人可讀、可寫，DX 最好
- 對複雜 UI 也能處理（template 方案處理不了的巢狀結構、條件判斷等）

**缺點**
- 依賴外部 LLM API（需要 API Key、有 token 成本）
- 輸出不穩定：同樣的 input 不保證產生相同的 output，CI 環境難以保證一致性
- 有機率轉錯：需要人工 review 產出的程式碼，否則 `.rc` 可能有 bug
- 有 latency：每次 sync 都要等 API 回應
- RemoteComposeWriter API 是 alpha，LLM 的訓練資料中不一定有足夠的範例，轉換品質不穩定

**適合場景**：在開發時輔助人工 review，而不是全自動 pipeline。

---

### 2. `.rc` 的 density 問題

`HomeScreenRc.kt` 目前 hardcode `DENSITY = 2.625f`（xxhdpi）。不同 density 的裝置可能會有顯示比例不正確的問題。Remote Compose player 是否會自動處理 density scaling 尚待驗證（library 仍為 alpha）。

### 3. App 端 Cache 機制（尚未實作）

目前每次 cold start 都需要 Server 可用才能顯示 UI。建議的機制：

- **本地 file cache**：成功拿到 `.rc` 後存在 `filesDir`，Server 無法連線時 fallback 顯示 cache 版本
- **HTTP ETag revalidation**：Express `static` 原生支援 ETag，OkHttp 加上 `Cache` + `CacheControl.noCache()` interceptor 可實現「每次問 Server 有沒有新版，沒變就用 cache bytes」的效果，避免重複下載

實作順序建議：先做 file cache（保障離線體驗），再疊加 ETag（減少不必要的下載）。
