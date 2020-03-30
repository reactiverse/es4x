# Status

Represents the outcome of a health check procedure. Each procedure
produces a link indicating either OK or KO. Optionally, it can also
provide additional data.

|                     |               |                                                                                           |
| ------------------- | ------------- | ----------------------------------------------------------------------------------------- |
| Name                | Type          | Description                                                                               |
| `@data`             | `Json object` | Sets the metadata.                                                                        |
| `@ok`               | `Boolean`     | Sets whether or not the current status is positive (UP) or negative (DOWN).               |
| `@procedureInError` | `Boolean`     | Sets whether or not the procedure attached to this status has failed (timeout, error...). |
