print('deployed (4)!');

process.on('undeploy', function (fut) {
  print('onStop');
  fut.complete();
});
