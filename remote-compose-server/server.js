const express = require('express')
const app = express()

// 靜態 .rc 檔案放在 static/ 目錄，由 CaptureTest instrumented test 產生
app.use('/ui', express.static('static'))

app.get('/health', (req, res) => res.send('OK'))

app.listen(8080, () => console.log('Server running on :8080'))
