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
# GET /health       → 健康檢查
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
