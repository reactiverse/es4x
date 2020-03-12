# CircuitBreakerOptions

Circuit breaker configuration options. All time are given in
milliseconds.

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
<td><p><code>@failuresRollingWindow</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Sets the rolling window used for metrics.</p></td>
</tr>
<tr class="odd">
<td><p><code>@fallbackOnFailure</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Sets whether or not the fallback is executed on failure, even when the circuit is closed.</p></td>
</tr>
<tr class="even">
<td><p><code>@maxFailures</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Sets the maximum number of failures before opening the circuit.</p></td>
</tr>
<tr class="odd">
<td><p><code>@maxRetries</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Configures the number of times the circuit breaker tries to redo the operation before failing.</p></td>
</tr>
<tr class="even">
<td><p><code>@metricsRollingBuckets</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Sets the configured number of buckets the rolling window is divided into.</p>
<p>The following must be true - metrics.rollingStats.timeInMilliseconds % metrics.rollingStats.numBuckets == 0 - otherwise it will throw an exception.</p>
<p>In other words, 10000/10 is okay, so is 10000/20 but 10000/7 is not.</p></td>
</tr>
<tr class="odd">
<td><p><code>@metricsRollingWindow</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Sets the rolling window used for metrics.</p></td>
</tr>
<tr class="even">
<td><p><code>@notificationAddress</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Sets the event bus address on which the circuit breaker publish its state change.</p></td>
</tr>
<tr class="odd">
<td><p><code>@notificationPeriod</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Configures the period in milliseconds where the circuit breaker send a notification on the event bus with its current state.</p></td>
</tr>
<tr class="even">
<td><p><code>@resetTimeout</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Sets the time in ms before it attempts to re-close the circuit (by going to the half-open state). If the circuit is closed when the timeout is reached, nothing happens. <code>-1</code> disables this feature.</p></td>
</tr>
<tr class="odd">
<td><p><code>@timeout</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Sets the timeout in milliseconds. If an action is not completed before this timeout, the action is considered as a failure.</p></td>
</tr>
</tbody>
</table>
