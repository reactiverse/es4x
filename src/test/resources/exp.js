function a() {
  b();
}

function b() {
  c();
}

function c() {
  try {
    d();
  } catch (ex) {
    console.trace(ex);
    throw new Error('Error on c');
  }
}

function d() {
  try {
    e();
  } catch (ex) {
    console.trace(ex);
    throw new Error('Error on d');
  }
}

function e() {
  throw new Error('Error on e');
}

module.exports = {
  a: a
};
