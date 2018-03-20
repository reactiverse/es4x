print('deployed (2)!');

process.onStop(function (f) {
  print('onStop');
  f.complete();
});
