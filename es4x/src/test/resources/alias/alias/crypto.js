const SecureRandom = Java.type('java.security.SecureRandom');

const rnd = new SecureRandom();

module.exports = {
  randomBytes: function (len) {
    let bytes = new Array(len);

    for (let i = 0; i < len; i++) {
      // this is not the best performance alternative!
      let r = rnd.nextInt();
      bytes[i] = r & 0xff;
    }

    return bytes;
  }
};
