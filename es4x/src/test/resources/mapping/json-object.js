const ByteBuffer = Java.type('java.nio.ByteBuffer');

const JsonObject = Java.type('io.vertx.core.json.JsonObject');
const JsonArray = Java.type('io.vertx.core.json.JsonArray');

try {
  mapping.map(undefined);
  should.fail('Should have failed [undefined]');
} catch (e) {
}

try {
  mapping.mapBuffer(undefined);
} catch (e) {
  should.fail('Should have not failed [undefined]');
}

try {
  mapping.mapJsonObject(null);
} catch (e) {
  should.fail('Should have not failed [null]');
}

try {
  mapping.map(null);
  should.fail('Should have failed [null]');
} catch (e) {
}

try {
  mapping.map(123);
} catch (e) {
  should.fail('Should have not failed [123]');
}

try {
  mapping.mapByte(123);
} catch (e) {
  should.fail('Should have not failed [123]');
}

try {
  mapping.map(456.789);
} catch (e) {
  should.fail('Should have not failed [456.789], even though it looks strange, the java API allows it');
}

try {
  mapping.mapByte(456.789);
} catch (e) {
  should.fail('Should have not failed [456.789], even though it looks strange, the java API allows it');
}

try {
  mapping.map(false);
  should.fail('Should have failed [false]');
} catch (e) {
}

try {
  mapping.map('string');
  should.fail('Should have failed [string]');
} catch (e) {
}

try {
  mapping.map([1, 2, 3]);
} catch (e) {
  should.fail('Should have not failed [[1,2,3]]');
}

try {
  mapping.map([]);
} catch (e) {
  should.fail('Should have not failed [[]]');
}

try {
  mapping.mapSet([1, 2, 3]);
} catch (e) {
  should.fail('Should have not failed [[1,2,3]]');
}

try {
  mapping.mapSet([]);
} catch (e) {
  should.fail('Should have not failed [[]]');
}

try {
  mapping.mapJsonArray([1, 2, 3]);
} catch (e) {
  should.fail('Should have not failed [[1,2,3]]');
}

try {
  mapping.mapJsonArray([]);
} catch (e) {
  should.fail('Should have not failed [[]]');
}

try {
  mapping.map([1, 2, 3]);
} catch (e) {
  print(e)
  should.fail('Should have not failed [[1,2,3]]');
}

try {
  mapping.map([]);
} catch (e) {
  should.fail('Should have not failed [[]]');
}

try {
  mapping.map({key:1, value:'2'});
} catch (e) {
  print(e)
  should.fail('Should have not failed [{key:1, value:\'2\'}]');
}

try {
  mapping.map({});
} catch (e) {
  should.fail('Should have not failed [{}]');
}

try {
  mapping.mapJsonObject({key:1, value:'2'});
} catch (e) {
  print(e)
  should.fail('Should have not failed [{key:1, value:\'2\'}]');
}

try {
  mapping.mapJsonObject({});
} catch (e) {
  should.fail('Should have not failed [{}]');
}

try {
  mapping.map(new ArrayBuffer(5));
} catch (e) {
  should.fail('Should have not failed [new ArrayBuffer(5)]');
}

try {
  mapping.map(new ArrayBuffer(ByteBuffer.allocateDirect(16)));
} catch (e) {
  print(e)
  should.fail('Should have not failed [new ArrayBuffer(ByteBuffer.allocateDirect(16))]');
}

try {
  mapping.mapBuffer(new ArrayBuffer(5));
} catch (e) {
  should.fail('Should have not failed [new ArrayBuffer(5)]');
}

try {
  mapping.mapBuffer(new ArrayBuffer(ByteBuffer.allocateDirect(16)));
} catch (e) {
  should.fail('Should have not failed [new ArrayBuffer(ByteBuffer.allocateDirect(16))]');
}

try {
  mapping.mapFunction((ctx) => Promise.resolve(ctx));
} catch (e) {
  should.fail('Should have not failed [(ctx) => Promise.resolve(ctx)]');
}

try {
  mapping.map((ctx) => Promise.resolve(ctx));
} catch (e) {
  should.fail('Should have not failed [(ctx) => Promise.resolve(ctx)]');
}

try {
  mapping.mapObject([]);
} catch (e) {
  should.fail('Should have not failed [[]]');
}

try {
  mapping.map(new JsonObject());
} catch (e) {
  should.fail('Should have not failed [JsonObject]');
}

try {
  let arr = new JsonArray('[1,2,3,4,5]');
  arr[5] = 6;
  arr[6] = undefined;
  mapping.map(arr);
} catch (e) {
  should.fail('Should have not failed [JsonArray]');
}

try {
  mapping.mapErrFunction((err) => Promise.reject(err));
} catch (e) {
  should.fail('Should have not failed [(ctx) => Promise.reject(ctx)]');
}

try {
  mapping.mapErrFunction((err) => Promise.reject(new TypeError(err)));
} catch (e) {
  should.fail('Should have not failed [(ctx) => Promise.reject(ctx)]');
}
