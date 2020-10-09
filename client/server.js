const express = require("express");
const path = require("path");
const app = express();
console.log(__dirname);
console.log(path.join(__dirname + "/dist/client/index.html"));
app.use(express.static(__dirname + "/dist/client"));

app.get("/*", function (req, res) {
  res.sendFile(path.join(__dirname + "/dist/client/index.html"));
});

app.listen(process.env.PORT || 3000);
