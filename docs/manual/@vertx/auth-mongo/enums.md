# HashAlgorithm

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 75%" />
</colgroup>
<tbody>
<tr class="odd">
<td><p>Name</p></td>
<td><p>Description</p></td>
</tr>
<tr class="even">
<td><p><code>SHA512</code></p></td>
<td><p>The default algorithm for backward compatible systems.</p>
<p>Should not be used for new projects as OWASP recommends stronger hashing algorithms.</p></td>
</tr>
<tr class="odd">
<td><p><code>PBKDF2</code></p></td>
<td><p>Stronger hashing algorithm, recommended by OWASP as of 2018.</p></td>
</tr>
</tbody>
</table>

# HashSaltStyle

Password hash salt configuration.

|            |                                                                                                      |
| ---------- | ---------------------------------------------------------------------------------------------------- |
| Name       | Description                                                                                          |
| `NO_SALT`  | Password hashes are not salted                                                                       |
| `COLUMN`   | Salt is in a separate column for each user in the database                                           |
| `EXTERNAL` | Salt is NOT stored in the database, but defined as external value like application preferences or so |
