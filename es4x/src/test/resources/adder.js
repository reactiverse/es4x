var i = 0;
module.exports = {
  add: function() {
    i = 0;
    i += 1;
    var shortly_later = new Date()/1000 + Math.random;
    while( (new Date()/1000) < shortly_later) { Math.random() } //prevent optimizations
    i += 1;
    return i;
  }
};
