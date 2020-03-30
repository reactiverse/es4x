# AggregateOptions

Options used to configure aggregate operations.

|                 |                 |                                                                                                            |
| --------------- | --------------- | ---------------------------------------------------------------------------------------------------------- |
| Name            | Type            | Description                                                                                                |
| `@allowDiskUse` | `Boolean`       | Set the flag if writing to temporary files is enabled.                                                     |
| `@batchSize`    | `Number (int)`  | Set the batch size for methods loading found data in batches.                                              |
| `@maxAwaitTime` | `Number (long)` | The maximum amount of time for the server to wait on new documents to satisfy a $changeStream aggregation. |
| `@maxTime`      | `Number (long)` | Set the time limit in milliseconds for processing operations on a cursor.                                  |

# BulkOperation

Contains all data needed for one operation of a bulk write operation.

|             |                     |                                                                          |
| ----------- | ------------------- | ------------------------------------------------------------------------ |
| Name        | Type                | Description                                                              |
| `@document` | `Json object`       | Sets the document, used by insert, replace, and update operations        |
| `@filter`   | `Json object`       | Sets the filter document, used by replace, update, and delete operations |
| `@multi`    | `Boolean`           | Sets the multi flag, used by update and delete operations                |
| `@type`     | `BulkOperationType` | Sets the operation type                                                  |
| `@upsert`   | `Boolean`           | Sets the upsert flag, used by update and replace operations              |

# BulkWriteOptions

Options for configuring bulk write operations.

|                |               |                        |
| -------------- | ------------- | ---------------------- |
| Name           | Type          | Description            |
| `@ordered`     | `Boolean`     | Set the ordered option |
| `@writeOption` | `WriteOption` | Set the write option   |

# FindOptions

Options used to configure find operations.

|              |                |                                                               |
| ------------ | -------------- | ------------------------------------------------------------- |
| Name         | Type           | Description                                                   |
| `@batchSize` | `Number (int)` | Set the batch size for methods loading found data in batches. |
| `@fields`    | `Json object`  | Set the fields                                                |
| `@limit`     | `Number (int)` | Set the limit                                                 |
| `@skip`      | `Number (int)` | Set the skip                                                  |
| `@sort`      | `Json object`  | Set the sort document                                         |

# IndexOptions

Options used to configure index.

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
<td><p><code>@background</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Create the index in the background</p></td>
</tr>
<tr class="odd">
<td><p><code>@bits</code></p></td>
<td><p><code>Number (Integer)</code></p></td>
<td><p>Gets the number of precision of the stored geohash value of the location data in 2d indexes.</p></td>
</tr>
<tr class="even">
<td><p><code>@bucketSize</code></p></td>
<td><p><code>Number (Double)</code></p></td>
<td><p>Gets the specified the number of units within which to group the location values for geoHaystack Indexes</p></td>
</tr>
<tr class="odd">
<td><p><code>@defaultLanguage</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Gets the language for a text index.</p>
<p>The language that determines the list of stop words and the rules for the stemmer and tokenizer.</p></td>
</tr>
<tr class="even">
<td><p><code>@languageOverride</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Gets the name of the field that contains the language string.</p>
<p>For text indexes, the name of the field, in the collection's documents, that contains the override language for the document.</p></td>
</tr>
<tr class="odd">
<td><p><code>@max</code></p></td>
<td><p><code>Number (Double)</code></p></td>
<td><p>Gets the upper inclusive boundary for the longitude and latitude values for 2d indexes..</p></td>
</tr>
<tr class="even">
<td><p><code>@min</code></p></td>
<td><p><code>Number (Double)</code></p></td>
<td><p>Gets the lower inclusive boundary for the longitude and latitude values for 2d indexes..</p></td>
</tr>
<tr class="odd">
<td><p><code>@name</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Gets the name of the index.</p></td>
</tr>
<tr class="even">
<td><p><code>@partialFilterExpression</code></p></td>
<td><p><code>Json object</code></p></td>
<td><p>Get the filter expression for the documents to be included in the index or null if not set</p></td>
</tr>
<tr class="odd">
<td><p><code>@sparse</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>If true, the index only references documents with the specified field</p></td>
</tr>
<tr class="even">
<td><p><code>@sphereVersion</code></p></td>
<td><p><code>Number (Integer)</code></p></td>
<td><p>Gets the 2dsphere index version number.</p></td>
</tr>
<tr class="odd">
<td><p><code>@storageEngine</code></p></td>
<td><p><code>Json object</code></p></td>
<td><p>Gets the storage engine options document for this index.</p></td>
</tr>
<tr class="even">
<td><p><code>@textVersion</code></p></td>
<td><p><code>Number (Integer)</code></p></td>
<td><p>The text index version number.</p></td>
</tr>
<tr class="odd">
<td><p><code>@unique</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Gets if the index should be unique.</p></td>
</tr>
<tr class="even">
<td><p><code>@version</code></p></td>
<td><p><code>Number (Integer)</code></p></td>
<td><p>Gets the index version number.</p></td>
</tr>
<tr class="odd">
<td><p><code>@weights</code></p></td>
<td><p><code>Json object</code></p></td>
<td><p>Gets the weighting object for use with a text index</p>
<p>A document that represents field and weight pairs. The weight is an integer ranging from 1 to 99,999 and denotes the significance of the field relative to the other indexed fields in terms of the score.</p></td>
</tr>
</tbody>
</table>

# MongoClientBulkWriteResult

Result propagated from mongodb driver bulk write result.

|                  |                        |                                                                                                                                           |
| ---------------- | ---------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| Name             | Type                   | Description                                                                                                                               |
| `@deletedCount`  | `Number (long)`        | Returns the number of deleted documents                                                                                                   |
| `@insertedCount` | `Number (long)`        | Returns the number of inserted documents                                                                                                  |
| `@matchedCount`  | `Number (long)`        | Returns the number of matched documents                                                                                                   |
| `@modifiedCount` | `Number (long)`        | Returns the number of modified documents                                                                                                  |
| `@upserts`       | `Array of Json object` | An unmodifiable list of upsert data. Each entry has the index of the request that lead to the upsert, and the generated ID of the upsert. |

# MongoClientDeleteResult

Result propagated from mongodb driver delete result.

|                 |                 |                                     |
| --------------- | --------------- | ----------------------------------- |
| Name            | Type            | Description                         |
| `@removedCount` | `Number (long)` | Get the number of removed documents |

# MongoClientUpdateResult

Result propagated from mongodb driver update result.

|                  |                 |                                              |
| ---------------- | --------------- | -------------------------------------------- |
| Name             | Type            | Description                                  |
| `@docMatched`    | `Number (long)` | Get the number of documents that're matched  |
| `@docModified`   | `Number (long)` | Get the number of documents that're modified |
| `@docUpsertedId` | `Json object`   | Get the document id that's upserted          |

# UpdateOptions

Options for configuring updates.

|                         |               |                                                                                   |
| ----------------------- | ------------- | --------------------------------------------------------------------------------- |
| Name                    | Type          | Description                                                                       |
| `@multi`                | `Boolean`     | Set whether multi is enabled                                                      |
| `@returningNewDocument` | `Boolean`     | Set whether new document property is enabled. Valid only on findOneAnd\* methods. |
| `@upsert`               | `Boolean`     | Set whether upsert is enabled                                                     |
| `@writeOption`          | `WriteOption` | Set the write option                                                              |
