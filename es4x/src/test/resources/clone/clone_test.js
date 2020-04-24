const StructuredClone = Java.type('io.reactiverse.es4x.impl.StructuredClone');

var a = { };
a.b = a;
a.c = { $ref: '$' };
// a.d = new Buffer([0xde, 0xad]);
a.e = [ a, a.b ];
a.f = new Date();
a.g = /ab+a/i;

var a = StructuredClone.cloneObject(a);

should.assertTrue(a.b == a);
should.assertTrue(a.c.$ref == '$');

// should.assertTrue(Buffer.isBuffer(a.d));
// should.assertTrue(a.d[0] == 0xde);
// should.assertTrue(a.d[1] == 0xad);
// should.assertTrue(a.d.length == 2);

should.assertTrue(Array.isArray(a.e));
should.assertTrue(a.e.length == 2);
should.assertTrue(a.e[0] == a);
should.assertTrue(a.e[1] == a.b);
should.assertTrue(a.f instanceof Date);
should.assertTrue(a.g instanceof RegExp);
test.complete();
