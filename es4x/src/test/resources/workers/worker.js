const Thread = Java.type('java.lang.Thread');

onmessage = function (e) {
  console.log('Message received from main script, will sleep 5 seconds...');
  Thread.sleep(5 * 1000);
  var workerResult = 'Result: ' + (e.data[0] * e.data[1]);
  console.log('Posting message back to main script');
  postMessage(workerResult);
};
