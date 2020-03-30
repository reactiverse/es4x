# HttpLocation

Represents the location of a HTTP endpoint. This object (its json
representation) will be used as "location" in a service record.

|             |                |                                                                                                                                    |
| ----------- | -------------- | ---------------------------------------------------------------------------------------------------------------------------------- |
| Name        | Type           | Description                                                                                                                        |
| `@endpoint` | `String`       | Sets the endpoint, which is the URL of the service. The endpoint is automatically computed when you use the other \`setX\` method. |
| `@host`     | `String`       | Sets the host.                                                                                                                     |
| `@port`     | `Number (int)` | Sets the port                                                                                                                      |
| `@root`     | `String`       | Sets the path of the service (root)                                                                                                |
| `@ssl`      | `Boolean`      | Sets whether or not the HTTP service is using `https`.                                                                             |

# Record

Describes a \`service\`. The record is the only piece of information
shared between consumer and provider. It should contains enough metadata
to let consumer find the service they want.

|                 |               |                                                                                                                             |
| --------------- | ------------- | --------------------------------------------------------------------------------------------------------------------------- |
| Name            | Type          | Description                                                                                                                 |
| `@location`     | `Json object` | Sets the json object describing the location of the service. By convention, this json object should contain the link entry. |
| `@metadata`     | `Json object` | Gets the metadata attached to the record.                                                                                   |
| `@name`         | `String`      | Sets the name of the service. It can reflect the service name of the name of the provider.                                  |
| `@registration` | `String`      | Sets the registration id. This method is called when the service is published.                                              |
| `@status`       | `Status`      | Sets the status of the service. When published, the status is set to. When withdrawn, the status is set to .                |
| `@type`         | `String`      | Sets the type of service.                                                                                                   |

# ServiceDiscoveryOptions

Options to configure the service discovery.

|                                |               |                                                                                                                                                                         |
| ------------------------------ | ------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name                           | Type          | Description                                                                                                                                                             |
| `@announceAddress`             | `String`      | Sets the event bus address on which the service arrivals, departures and modifications are announced. This address must be consistent in the whole application.         |
| `@autoRegistrationOfImporters` | `Boolean`     | Sets whether or not the registration of importers declared as SPI is enabled.                                                                                           |
| `@backendConfiguration`        | `Json object` | Sets the configuration passed to the . Refer to the backend documentation to get more details on the requirements. The default backend does not need any configuration. |
| `@name`                        | `String`      | Sets the service discovery name used in the service usage events. If not set, the node id is used.                                                                      |
| `@usageAddress`                | `String`      | Sets the usage address: the event bus address on which are sent the service usage events (bind / release).                                                              |
