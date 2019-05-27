print('deployed (2)!');

process.on('undeploy', function () {
  print('onStop');
});
