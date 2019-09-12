package io.reactiverse.es4x;

import io.reactiverse.es4x.impl.ESModuleIO;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

public class ESModuleAdapterTest {

  @Test
  public void shouldAdaptWildcard() {
    assertEquals(
      "const myModule = require('/modules/my-module.js');",
      ESModuleIO.adapt("import * as myModule from '/modules/my-module.js'"));
  }

  @Test
  public void shouldAdaptSingleSelect() {
    assertEquals(
      "const myExport = require('/modules/my-module.js').myExport;",
      ESModuleIO.adapt("import {myExport} from '/modules/my-module.js'"));
  }

  @Test
  public void shouldAdaptMultiSelect() {
    assertEquals(
      "const foo = require('/modules/my-module.js').foo;const bar = require('/modules/my-module.js').bar;",
      ESModuleIO.adapt("import {foo, bar} from '/modules/my-module.js'"));
  }

  @Test
  public void shouldAdaptSelectAlias() {
    assertEquals(
      "const shortName = require('/modules/my-module.js').reallyReallyLongModuleExportName;",
      ESModuleIO.adapt("import {reallyReallyLongModuleExportName as shortName} from '/modules/my-module.js';"));
  }

  @Test
  public void shouldAdaptMultiSelectAlias() {
    assertEquals(
      "const shortName = require('/modules/my-module.js').reallyReallyLongModuleExportName;const short = require('/modules/my-module.js').anotherLongModuleName;\n\n\n",
      ESModuleIO.adapt(
        "import {\n" +
          "  reallyReallyLongModuleExportName as shortName,\n" +
          "  anotherLongModuleName as short\n" +
          "} from '/modules/my-module.js';"));
  }

  @Test
  public void adaptFullFileNoChange() {
    String file = "module.exports = {\n" +
      "  a: require('./a'),\n" +
      "  b: require('./b')\n" +
      "};\n" +
      "\n";

    assertEquals(file, ESModuleIO.adapt(file));
  }

  @Test
  public void shouldAdaptWithDoubleQuotes() {
    assertEquals(
      "const TestSuite = require('vertx-unit').TestSuite;\n" +
        "\n" +
        "const suite = TestSuite.create(\"the_test_suite\");\n" +
        "\n" +
        "suite.test(\"my_test_case\", function (context) {\n" +
        "  var s = \"value\";\n" +
        "  context.assertEquals(\"value\", s);\n" +
        "});\n" +
        "\n" +
        "suite.run();\n" +
        "\n",

      ESModuleIO.adapt(
        "import { TestSuite } from 'vertx-unit';\n" +
          "\n" +
          "const suite = TestSuite.create(\"the_test_suite\");\n" +
          "\n" +
          "suite.test(\"my_test_case\", function (context) {\n" +
          "  var s = \"value\";\n" +
          "  context.assertEquals(\"value\", s);\n" +
          "});\n" +
          "\n" +
          "suite.run();\n" +
          "\n"));
  }

  @Test
  public void shouldNotAdaptCode() {

    String file = "import */ var __WEBPACK_IMPORTED_MODULE_0_react__ = __webpack_require__(8);\n" +
      "/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_react__);\n" +
      "var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };\n" +
      "\n" +
      "\n" +
      "\n" +
      "  // Usually the current owner is the offender, but if it accepts children as a\n" +
      "  // property, it may be the creator of the child that's responsible for\n" +
      "  // assigning it a key.\n" +
      "  var childOwner = '';\n" +
      "  if (element && element._owner && element._owner !== ReactCurrentOwner.current) {\n" +
      "    // Give the component that originally created this child.\n" +
      "    childOwner = ' It was passed a child from ' + element._owner.getName() + '\n";

    assertEquals(file, ESModuleIO.adapt(file));
  }

  @Test
  public void shouldNotAdaptReactJSCode() {

    assertEquals("import */ var __WEBPACK_IMPORTED_MODULE_0_react__ = __webpack_require__(8);\n" +
      "/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_react__);\n" +
      "var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };\n" +
      "\n" +
      "\n" +
      "\n" +
      "function isValidChild(object) {\n" +
      "  return object == null || __WEBPACK_IMPORTED_MODULE_0_react___default.a.isValidElement(object);\n" +
      "}\n" +
      "\n" +
      "function isReactChildren(object) {\n" +
      "  return isValidChild(object) || Array.isArray(object) && object.every(isValidChild);\n" +
      "}\n" +
      "\n" +
      "function createRoute(defaultProps, props) {\n" +
      "  return _extends({}, defaultProps, props);\n" +
      "}\n" +
      "\n" +
      "function createRouteFromReactElement(element) {\n" +
      "  var type = element.type;\n" +
      "  var route = createRoute(type.defaultProps, element.props);\n" +
      "\n" +
      "  if (route.children) {\n" +
      "    var childRoutes = createRoutesFromReactChildren(route.children, route);\n" +
      "\n" +
      "    if (childRoutes.length) route.childRoutes = childRoutes;\n" +
      "\n" +
      "    delete route.children;\n" +
      "  }\n" +
      "\n" +
      "  return route;\n" +
      "}\n" +
      "\n" +
      "/**\n" +
      " * Creates and returns a routes object from the given ReactChildren. JSX\n" +
      " * provides a convenient way to visualize how routes in the hierarchy are\n" +
      " * nested.\n" +
      " *\n" +
      " *   const Route = require('react-router').Route;const createRoutesFromReactChildren = require('react-router').createRoutesFromReactChildren;\n",

      ESModuleIO.adapt("import */ var __WEBPACK_IMPORTED_MODULE_0_react__ = __webpack_require__(8);\n" +
        "/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_react__);\n" +
        "var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };\n" +
        "\n" +
        "\n" +
        "\n" +
        "function isValidChild(object) {\n" +
        "  return object == null || __WEBPACK_IMPORTED_MODULE_0_react___default.a.isValidElement(object);\n" +
        "}\n" +
        "\n" +
        "function isReactChildren(object) {\n" +
        "  return isValidChild(object) || Array.isArray(object) && object.every(isValidChild);\n" +
        "}\n" +
        "\n" +
        "function createRoute(defaultProps, props) {\n" +
        "  return _extends({}, defaultProps, props);\n" +
        "}\n" +
        "\n" +
        "function createRouteFromReactElement(element) {\n" +
        "  var type = element.type;\n" +
        "  var route = createRoute(type.defaultProps, element.props);\n" +
        "\n" +
        "  if (route.children) {\n" +
        "    var childRoutes = createRoutesFromReactChildren(route.children, route);\n" +
        "\n" +
        "    if (childRoutes.length) route.childRoutes = childRoutes;\n" +
        "\n" +
        "    delete route.children;\n" +
        "  }\n" +
        "\n" +
        "  return route;\n" +
        "}\n" +
        "\n" +
        "/**\n" +
        " * Creates and returns a routes object from the given ReactChildren. JSX\n" +
        " * provides a convenient way to visualize how routes in the hierarchy are\n" +
        " * nested.\n" +
        " *\n" +
        " *   import { Route, createRoutesFromReactChildren } from 'react-router'\n"));
  }

  @Test
  public void shouldAdapt() {
    String file =
      "/// <reference types=\"@vertx/core/runtime\" />\n"+
      "// @ts-check\n"+
      "\n"+
      "import { Router } from '@vertx/web';\n"+
      "\n"+
      "const router = Router.router(vertx);\n"+
      "\n"+
      "router.route().handler(function (ctx) {\n"+
      "    ctx.response().end('Hello!');\n"+
      "})\n"+
      "\n"+
      "vertx.createHttpServer().requestHandler(function (req) {\n"+
      "    router.accept(req);\n"+
      "}).listen(8080);";

    assertNotEquals(ESModuleIO.adapt(file), file);
  }

  @Test
  public void shouldRemoveShebang() {
    String file = "#!/usr/bin/env es4x\n" +
      "console.log('HELLO');\n";

    assertFalse(ESModuleIO.stripShebang(file).startsWith("#!/usr/bin/env es4x\n"));
  }

  @Test
  public void testStripBOM() throws UnsupportedEncodingException {
    String file = new String(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF, ' ' }, "UTF-8");
    assertEquals(" ", ESModuleIO.stripBOM(file));
  }
}
