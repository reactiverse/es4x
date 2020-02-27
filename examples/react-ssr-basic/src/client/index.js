import React from "react";
import { hydrate } from "react-dom";
import App from "./app";
import { BrowserRouter as Router } from "react-router-dom";

hydrate(
  <Router>
    <App />
  </Router>,
  document.getElementById("app")
);
