/// <reference types="es4x" />
// @ts-check

import { HttpMethod } from "@vertx/core/enums";

var client = vertx.createNetClient();

vertx.createHttpServer().requestHandler(req => {
  if (req.method() === HttpMethod.CONNECT) {

    // Determine proxied server address
    var proxyAddress = req.uri();
    var idx = proxyAddress.indexOf(':');
    var host = proxyAddress.substring(0, idx);
    var port = parseInt(proxyAddress.substring(idx + 1), 10);

    console.log("Connecting to proxy " + proxyAddress);

    client.connect(port, host, onConnect => {

      if (onConnect.succeeded()) {
        console.log("Connected to proxy");
        var clientSocket = req.netSocket();
        clientSocket.write("HTTP/1.0 200 Connection established\n\n");
        var serverSocket = onConnect.result();

        serverSocket.handler(buff => {
          console.log("Forwarding server packet to the client");
          clientSocket.write(buff);
        });
        serverSocket.closeHandler(v => {
          console.log("Server socket closed");
          clientSocket.close();
        });

        clientSocket.handler(buff => {
          console.log("Forwarding client packet to the server");
          serverSocket.write(buff);
        });
        clientSocket.closeHandler(v => {
          console.log("Client socket closed");
          serverSocket.close();
        });
      } else {
        console.log("Fail proxy connection");
        req.response().setStatusCode(403).end();
      }
    });
  } else {
    req.response().setStatusCode(405).end();
  }
}).listen(8080);
