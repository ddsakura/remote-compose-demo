# Remote Compose Spike

驗證「從 Server 動態推送 UI，Android App 不需要發版即可更新畫面」。

## 目的

利用 [AndroidX Remote Compose](https://developer.android.com/jetpack/androidx/releases/compose-remote) library，將 UI 定義序列化為 `.rc` binary 檔案，由 Server 提供給 Android App 渲染。UI 改版只需更新 Server，App 無需重新發版。

這個 repository 目前是用來驗證端到端流程可行性的 spike。現況已確認：
- `.rc` 檔案可以產生
- `.rc` 檔案可以被 server 發佈
- Android App 可以下載並渲染 `.rc`

目前這個 repository 內實際在運作的 `.rc` 產生方式，是 `rc-generator` 中用低階 API 直接組裝 `.rc` 的 PoC 實作；它是目前可用的實作方式，但不應被視為此 repository 已經確認的長期 UI 撰寫方式。

## 架構

```
remote-compose/
├── remote-compose-android/     ← Android 專案
│   ├── app/                    ← 播放端：從 Server 抓 .rc 並渲染
│   ├── ui-remote/              ← 較高階、較接近 Compose 的 UI 寫法範例 + `.rc` 擷取實驗
│   └── rc-generator/           ← 目前 PoC 產生端：用 `RemoteComposeWriter` 產生 `.rc`
│
└── remote-compose-server/      ← Node.js 靜態檔案 Server
    ├── server.js
    └── static/                 ← 存放 .rc 檔案（由 rc-generator 產生）
```

### 流程

目前這個 repository 已實作並驗證的是下面這條 PoC 流程：

```
1. 改 ui-remote/RemoteHomeScreen.kt
                                        ← 較高階、較接近 Compose 的 Remote Compose UI 寫法範例
       ↓
2. 同步改 rc-generator/HomeScreenRc.kt   ← 目前實際產生 `.rc` 的低階 API PoC
       ↓
3. ./gradlew :rc-generator:publishRc    ← 產生 .rc 並複製到 server/static/
       ↓
4. npm start（remote-compose-server/）  ← 啟動靜態 Server
       ↓
5. 跑 Android App（模擬器）            ← 抓 .rc 渲染，不需重裝
```

這代表目前 repo 內是用低階 writer API 驗證 `.rc` 產生流程；不代表這是唯一可行或最終建議的 Remote Compose UI 寫法。

另外，`ui-remote` 目前也包含一個較高階、較接近 Compose 的 UI 寫法範例，可透過 Android instrumentation 擷取成 `.rc`。這條路徑已加入範例，但仍應視為在此 repository 中持續評估的方向，而不是已完全取代目前低階 generator flow。
另外，Android Studio Preview 目前不可作為這條高階 Remote Compose 路徑的預覽方式。

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

**執行高階 `.rc` 擷取範例：**
```bash
cd remote-compose-android
./gradlew :ui-remote:captureRc
adb pull /sdcard/Download/remote-compose/home-v1.rc ../remote-compose-server/static/home-v1.rc
adb pull /sdcard/Download/remote-compose/home-v2.rc ../remote-compose-server/static/home-v2.rc
```
> 這條路徑使用 `ui-remote` 中較高階、較接近 Compose 的 Remote Compose 範例進行 `.rc` 擷取，目前作為並行範例與評估方向保留。

**一鍵發布高階擷取產物到 Server：**
```bash
cd remote-compose-android
./gradlew :ui-remote:publishCapturedRc
```
> 這個 task 會先跑 instrumentation 擷取，再透過 `adb pull` 從 `/sdcard/Download/remote-compose/` 把 `.rc` 拉回 `remote-compose-server/static/`
> 
> 如需指定裝置，可使用：
```bash
./gradlew :ui-remote:publishCapturedRc -PandroidSerial=<device-serial>
```
> 如需指定 `adb` 路徑，可使用：
```bash
./gradlew :ui-remote:publishCapturedRc -PadbPath=/path/to/adb
```

**Build App：**
```bash
./gradlew assembleDebug
```

App 跑在模擬器時，透過 `http://10.0.2.2:8080` 連到本機 Server。切換畫面左上角的 **V1 / V2** 可即時拉取不同版本的 UI。

## Dependencies

| 模組 | 用途 | 關鍵 Library |
|------|------|-------------|
| `app` | 渲染 .rc | `remote-player-core`, `remote-player-view` |
| `ui-remote` | 較高階、較接近 Compose 的 UI 寫法範例 + `.rc` 擷取 | `remote-creation-compose` |
| `rc-generator` | 產生 .rc（純 JVM） | `remote-creation`, `remote-creation-core` |
| `remote-compose-server` | Serve .rc 靜態檔案 | Express |

Remote Compose 版本：`1.0.0-alpha07`

---

## 已知限制與待解問題

### 1. `.rc` 產生的 DX 問題

在目前這個 repository 中，低階 API PoC 與高階 `.rc` 擷取範例是並存的：

| 檔案 | 用途 |
|------|------|
| `ui-remote/RemoteHomeScreen.kt` | 較高階、較接近 Compose 的 Remote Compose UI 寫法範例 |
| `rc-generator/HomeScreenRc.kt` | RemoteComposeWriter 低階 API，實際產生 `.rc` |

這是目前 repo 內用來對照與評估的實作方式，不代表 Remote Compose 本身一定要求永遠維護多份定義。

`HomeScreenRc.kt` 語法低階、難以直接閱讀和修改。對這個問題，下面幾個方向可視為可能的後續評估方案，而不是此 repo 已完成驗證的最終方案：

| 方案 | 優點 | 缺點 |
|------|------|------|
| **JSON → `.rc`**（參考 [armcha/remotecompose](https://github.com/armcha/remotecompose)） | 人可讀、可直接編輯、純 JVM | UI 描述能力有限，不如 Compose 靈活 |
| **`@RemoteComposable` + `captureSingleRemoteDocument`** | 語法接近 Compose、目前已可在此 repo 中擷取出 `.rc` | 目前仍需要 Android 模擬器或實機執行擷取，且是否適合作為長期 production 流程仍在評估 |
| **Gradle task 呼叫 LLM API 轉換** | 見下方說明 | 見下方說明 |

#### LLM 轉換（詳細分析）

概念：將較高階的 UI 撰寫來源當成候選主要輸入，Gradle task 讀取內容後呼叫 LLM API，自動產生對應的 `HomeScreenRc.kt`（RemoteComposeWriter）。

**優點**
- 只需維護一份較高階的 UI 撰寫來源，不需手動同步兩個檔案
- Compose 語法人可讀、可寫，DX 最好
- 對複雜 UI 也能處理（template 方案處理不了的巢狀結構、條件判斷等）

**缺點**
- 依賴外部 LLM API（需要 API Key、有 token 成本）
- 輸出不穩定：同樣的 input 不保證產生相同的 output，CI 環境難以保證一致性
- 有機率轉錯：需要人工 review 產出的程式碼，否則 `.rc` 可能有 bug
- 有 latency：每次 sync 都要等 API 回應
- RemoteComposeWriter API 是 alpha，LLM 的訓練資料中不一定有足夠的範例，轉換品質不穩定

**適合場景**：如果未來要嘗試，可先作為開發期輔助人工 review 的實驗工具，而不是直接當成全自動 pipeline。

---

### 2. `.rc` 的 density 問題

`HomeScreenRc.kt` 目前 hardcode `DENSITY = 2.625f`（xxhdpi）。不同 density 的裝置可能會有顯示比例不正確的問題。Remote Compose player 是否會自動處理 density scaling 尚待驗證（library 仍為 alpha）。

### 3. App 端 Cache 機制（尚未實作）

目前每次 cold start 都需要 Server 可用才能顯示 UI。建議的機制：

- **本地 file cache**：成功拿到 `.rc` 後存在 `filesDir`，Server 無法連線時 fallback 顯示 cache 版本
- **HTTP ETag revalidation**：Express `static` 原生支援 ETag，OkHttp 加上 `Cache` + `CacheControl.noCache()` interceptor 可實現「每次問 Server 有沒有新版，沒變就用 cache bytes」的效果，避免重複下載

實作順序建議：先做 file cache（保障離線體驗），再疊加 ETag（減少不必要的下載）。
