const express = require("express");
const path = require("path");
const app = express();
console.log(__dirname);
console.log(path.join(__dirname + "/dist/client/index.html"));
console.log(path.dirname);
app.use(express.static("/dist/client"));

app.get("/*", function (req, res) {
  res.sendFile("/dist/client/index.html");
});

app.listen(process.env.PORT || 3000);
