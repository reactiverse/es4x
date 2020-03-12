# ConfigChange

A structure representing a configuration change.

|                          |               |                                  |
| ------------------------ | ------------- | -------------------------------- |
| Name                     | Type          | Description                      |
| `@newConfiguration`      | `Json object` | Sets the new configuration.      |
| `@previousConfiguration` | `Json object` | Sets the previous configuration. |

# ConfigRetrieverOptions

Options to configure the

ConfigRetriever

.

|                         |                               |                                                                                                             |
| ----------------------- | ----------------------------- | ----------------------------------------------------------------------------------------------------------- |
| Name                    | Type                          | Description                                                                                                 |
| `@includeDefaultStores` | `Boolean`                     | Enables or disables the inclusion of the default stored in the configuration.                               |
| `@scanPeriod`           | `Number (long)`               | Configures the scan period, in ms. This is the time amount between two checks of the configuration updates. |
| `@stores`               | `Array of ConfigStoreOptions` | Sets the configuration stores.                                                                              |

# ConfigStoreOptions

Data object representing the configuration of a configuration store.
This object describes the configuration of a chunk of configuration that
you retrieve. It specifies its type (type of configuration store), the
format of the retrieved configuration chunk, and you can also configures
the store if it needs configuration to retrieve the configuration chunk.

|             |               |                                                                                                                                                                                                          |
| ----------- | ------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name        | Type          | Description                                                                                                                                                                                              |
| `@config`   | `Json object` | Sets the configuration of the store                                                                                                                                                                      |
| `@format`   | `String`      | Sets the format of the configuration that is retrieved from the store.                                                                                                                                   |
| `@optional` | `Boolean`     | Sets whether or not the store is optional. When the configuration is retrieve, if an optional store returns a failure, the failure is ignored and an empty json object is used instead (for this store). |
| `@type`     | `String`      | Sets the configuration type                                                                                                                                                                              |
