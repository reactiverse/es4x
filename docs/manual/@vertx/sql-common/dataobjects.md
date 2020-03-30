# ResultSet

Represents the results of a SQL query.

It contains a list for the column names of the results, and a list of

JsonArray

\- one for each row of the results.

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td><p>Name</p></td>
<td><p>Type</p></td>
<td><p>Description</p></td>
</tr>
<tr class="even">
<td><p><code>@columnNames</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Get the column names</p></td>
</tr>
<tr class="odd">
<td><p><code>@next</code></p></td>
<td><p><code>ResultSet</code></p></td>
<td><p>Get the next result set</p></td>
</tr>
<tr class="even">
<td><p><code>@numColumns</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Return the number of columns in the result set</p></td>
</tr>
<tr class="odd">
<td><p><code>@numRows</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Return the number of rows in the result set</p></td>
</tr>
<tr class="even">
<td><p><code>@output</code></p></td>
<td><p><code>Json array</code></p></td>
<td><p>Get the registered outputs</p></td>
</tr>
<tr class="odd">
<td><p><code>@results</code></p></td>
<td><p><code>Array of Json array</code></p></td>
<td><p>Get the results</p></td>
</tr>
<tr class="even">
<td><p><code>@rows</code></p></td>
<td><p><code>Array of Json object</code></p></td>
<td><p>Get the rows - each row represented as a JsonObject where the keys are the column names and the values are the column values.</p>
<p>Beware that it's legal for a query result in SQL to contain duplicate column names, in which case one will overwrite the other if using this method. If that's the case use link instead.</p>
<p>Be aware that column names are defined as returned by the database, this means that even if your SQL statement is for example: SELECT a, b FROM table the column names are not required to be: a and b and could be in fact A and B.</p>
<p>For cases when there is the need for case insentivitity you should see link</p></td>
</tr>
</tbody>
</table>

# SQLOptions

Represents the options one can use to customize the unwrapped
connection/statement/resultset types

|                             |                        |             |
| --------------------------- | ---------------------- | ----------- |
| Name                        | Type                   | Description |
| `@autoGeneratedKeys`        | `Boolean`              | \-          |
| `@autoGeneratedKeysIndexes` | `Json array`           | \-          |
| `@catalog`                  | `String`               | \-          |
| `@fetchDirection`           | `FetchDirection`       | \-          |
| `@fetchSize`                | `Number (int)`         | \-          |
| `@queryTimeout`             | `Number (int)`         | \-          |
| `@readOnly`                 | `Boolean`              | \-          |
| `@resultSetConcurrency`     | `ResultSetConcurrency` | \-          |
| `@resultSetType`            | `ResultSetType`        | \-          |
| `@schema`                   | `String`               | \-          |
| `@transactionIsolation`     | `TransactionIsolation` | \-          |

# UpdateResult

Represents the result of an update/insert/delete operation on the
database.

The number of rows updated is available with link and any generated keys
are available with link.

|            |                |                                |
| ---------- | -------------- | ------------------------------ |
| Name       | Type           | Description                    |
| `@keys`    | `Json array`   | Get any generated keys         |
| `@updated` | `Number (int)` | Get the number of rows updated |