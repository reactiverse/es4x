# ReportOptions

Configures a reporter consisting in a name

to

, an address

at

and an optional

format

.

|           |          |                                  |
| --------- | -------- | -------------------------------- |
| Name      | Type     | Description                      |
| `@format` | `String` | Set the current reporter format. |
| `@to`     | `String` | Set the current reporter name.   |

# ReportingOptions

Reporting options:

the

reporters

is an array of reporter configurations

|              |                          |                                                       |
| ------------ | ------------------------ | ----------------------------------------------------- |
| Name         | Type                     | Description                                           |
| `@reporters` | `Array of ReportOptions` | Replace the current list of reporters with a new one. |

# TestOptions

Test execution options:

the

timeout

in milliseconds, the default value is 2 minutes

the

useEventLoop

configures the event loop usage

true

always runs with an event loop

false

never runs with an event loop

null

uses an event loop if there is one (provided by link) otherwise run
without

\</li\>

the

reporters

is an array of reporter configurations

|                 |                          |                                                                             |
| --------------- | ------------------------ | --------------------------------------------------------------------------- |
| Name            | Type                     | Description                                                                 |
| `@reporters`    | `Array of ReportOptions` | Replace the current list of reporters with a new one.                       |
| `@timeout`      | `Number (long)`          | Set the test timeout.                                                       |
| `@useEventLoop` | `Boolean`                | Configure the execution to use an event loop when there is no one existing. |
