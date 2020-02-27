const path = require("path");

module.exports = [
  {
    entry: {
      client: "./src/client/index.js"
    },
    output: {
      path: path.resolve(__dirname, "dist"),
      filename: "[name].js"
    },
    module: {
      rules: [
        {
          test: /\.(js|jsx)$/,
          exclude: /node_modules/,
          use: {
            loader: "babel-loader"
          }
        }
      ]
    }
  },
  {
    entry: {
      server: "./src/server.js"
    },
    output: {
      path: path.resolve(__dirname),
      filename: "[name].js"
    },
    module: {
      rules: [
        {
          test: /\.(js|jsx)$/,
          exclude: /node_modules/,
          use: {
            loader: "babel-loader"
          }
        }
      ]
    }
  }
];
