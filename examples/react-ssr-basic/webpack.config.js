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
    // target: "node",
    entry: {
      server: "./src/server.js"
    },
    // node: {
    //   dns: "mock",
    //   fs: "empty",
    //   path: true,
    //   url: false,
    //   net: "empty"
    // },
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
