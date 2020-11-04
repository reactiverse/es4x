const StructuredClone = Java.type('io.reactiverse.es4x.impl.StructuredClone');

var a = { };
a.b = a;
a.c = { $ref: '$' };
a.d = new Uint8Array(2);
a.d[0] = 0xde;
a.d[1] = 0xad;
a.e = [ a, a.b ];
a.f = new Date();
a.g = /ab+a/i;

a = StructuredClone.cloneObject(a);

// check circular
should.assertTrue(a.b == a);
// check json
should.assertTrue(a.c.$ref == '$');
// check buffers
should.assertTrue(a.d instanceof Java.type('io.vertx.core.buffer.Buffer'));
should.assertNotNull(a.d);
should.assertTrue(a.d.length() == 2);
should.assertTrue(a.d.getUnsignedByte(0) == 0xde);
should.assertTrue(a.d.getUnsignedByte(1) == 0xad);
// check arrays
should.assertTrue(Array.isArray(a.e));
should.assertTrue(a.e.length == 2);
should.assertTrue(a.e[0] == a);
// arrays with circular dependencies
should.assertTrue(a.e[1] == a.b);
should.assertTrue(a.f instanceof Java.type('java.time.LocalDate'));
should.assertTrue(a.g instanceof Java.type('java.util.regex.Pattern'));

test.complete();
