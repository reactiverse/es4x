require('./b');
if (oops) {
  throw new Error('engine is tainted: ' + oops);
}
oops = 'a';
