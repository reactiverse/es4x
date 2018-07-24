# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).


## [Unreleased]

## [0.5.3] - 2018-07-20
- Added docs about Worker and Graal
- Code clean ups
- `vertx-scripts` will install `mvnw` by default (for Graal users, this means there are no extra
  dependencies needed to run or develop ES4X)
- `Worker` API following the Ecma Interface will off load to worker verticles.
- Bump Vert.x dependencies to 3.5.3
- Bump `pg-reactive-client` to 0.9.0
- Fix build to always target `JDK8` making it possible to use on `JDK>=8` and `GraalVM 1.0.0`

## [0.5.2] - 2018-07-13
- `vertx-scripts` can generate basic native images
- Added `pg-reactive-client@0.8.0`
- Bump Vert.x dependencies to 3.5.2

## [0.5.1] - 2018-07-03
- REPL support for both Nashorn and Graal
- Added `Dynalink` to any `DataObject`
- Type definition for `process`
- Loader updated to switch from `Nashorn`/`GraalVM` depending on the runtime
- Fix Console `format` to not crash when handling unsupported types
- Added a Changelog
- Implemented `util/promisify` similar to node
- Added `process.nextTick` wrapper
- Added support to mix manual content to the typedoc/npm main page
- Codecov support to the build system


## [0.5.1] - 2018-03-22
- Initial Release
