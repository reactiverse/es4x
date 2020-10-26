/// <reference types="@vertx/core" />
// @ts-check

import { TestSuite, TestContext } from '@vertx/unit';

const suite = TestSuite.create("the_test_suite");

suite.test("my_test_case", (should: TestContext) => {
  let s : string = "value";
  should.assertEquals("value", s);
});

suite.run();
