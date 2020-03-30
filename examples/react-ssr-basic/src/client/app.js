import React, { Component, Fragment } from "react";
import { Switch, Route } from "react-router-dom";
import Routes from "../routes";
import Header from "./components/header/header";

export default class App extends Component {
  render() {
    return (
      <Fragment>
        <Header />
        <Switch>
          {Routes.map((c, index) => (
            <Route key={index} path={c.url} exact component={c.component} />
          ))}
        </Switch>
      </Fragment>
    );
  }
}
