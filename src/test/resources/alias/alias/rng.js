const SecureRandom = Java.type('java.security.SecureRandom');

const rnd = new SecureRandom();

module.exports = function jvmRNG() {
  let bytes = new Array(16);

  for (let i = 0; i< 4; i++) {
    let r = rnd.nextInt();
    let idx = i * 4;
    bytes[idx] = r & 0xff;
    bytes[idx + 1] = (r >> 8) & 0xff;
    bytes[idx + 2] = (r >> 16) & 0xff;
    bytes[idx + 3] = (r >> 24) & 0xff;
  }

  return bytes;
};
