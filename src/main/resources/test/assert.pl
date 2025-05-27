-- native call/1
-- native println/N

assertTrue(P, M) :- call(P), !, println("✅", M).
assertTrue(P, M) :- println("❌", M), abort.

assertFalse(P, M) :- not(P), !, println("✅", M).
assertFalse(P, M) :- println("❌", M), abort.