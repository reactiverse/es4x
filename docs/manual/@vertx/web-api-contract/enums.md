# ContainerSerializationStyle

This enum contains supported object and arrays serialization styles.
Every style has a enum value, and an array of strings to refeer to it.

|                          |                                                                                                   |
| ------------------------ | ------------------------------------------------------------------------------------------------- |
| Name                     | Description                                                                                       |
| `csv`                    | Comma separated values: "value1,value2,value3" aliases: "csv", "commaDelimited", "form", "simple" |
| `ssv`                    | Space separated values: "value1 value2 value3" aliases: "ssv", "spaceDelimited"                   |
| `psv`                    | \+ Pipe separated values: "value1                                                                 |
| value2                   | value3" aliases: "psv", "pipeDelimited" +                                                         |
| `dsv`                    | Dot delimited values: "value1.value2.value3" aliases: "dsv", "dotDelimited", "label"              |
| `simple_exploded_object` | For internal usage, don't use it                                                                  |
| `matrix_exploded_array`  | For internal usage, don't use it                                                                  |

# ParameterLocation

ParameterLocation describe the location of parameter inside HTTP Request

|             |             |
| ----------- | ----------- |
| Name        | Description |
| `HEADER`    | \-          |
| `QUERY`     | \-          |
| `PATH`      | \-          |
| `FILE`      | \-          |
| `BODY_FORM` | \-          |
| `BODY`      | \-          |
| `BODY_JSON` | \-          |
| `BODY_XML`  | \-          |
| `COOKIE`    | \-          |

# ParameterType

ParameterType contains prebuilt type validators. To access to
ParameterTypeValidator of every ParameterType, use link

|                  |                                                                |
| ---------------- | -------------------------------------------------------------- |
| Name             | Description                                                    |
| `GENERIC_STRING` | STRING Type accept every string                                |
| `EMAIL`          | \-                                                             |
| `URI`            | \-                                                             |
| `BOOL`           | It allows true, false, t, f, 1, 0                              |
| `INT`            | INT type does the validation with Integer.parseInt(value)      |
| `FLOAT`          | FLOAT type does the validation with Float.parseFloat(value)    |
| `DOUBLE`         | DOUBLE type does the validation with Double.parseDouble(value) |
| `DATE`           | DATE as defined by full-date - RFC3339                         |
| `DATETIME`       | DATETIME as defined by date-time - RFC3339                     |
| `TIME`           | TIME as defined by partial-time - RFC3339                      |
| `BASE64`         | \-                                                             |
| `IPV4`           | \-                                                             |
| `IPV6`           | \-                                                             |
| `HOSTNAME`       | \-                                                             |
| `UUID`           | UUID as defined by RFC4122                                     |
