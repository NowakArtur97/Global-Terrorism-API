const express = require("express");
const path = require("path");
const app = express();
console.log(__dirname + "/dist/client");
app.use(express.static("/dist/client"));

app.get("*", (req, res) => {
  res.sendFile(path.join("/dist/client/index.html"));
});

app.listen(process.env.PORT || 8080);
