-- native call/1
-- native println/N

assertTrue(P, M) :- call(P), !, println("✅", M).
assertTrue(P, M) :- println("❌", M), abort.

assertFalse(P, M) :- not(P), !, println("✅", M).
assertFalse(P, M) :- println("❌", M), abort.

-- TODO locate in another source file ...

success(T) :- type(T,L),assertFalse(has_proof(error,L),T),!.
success(T) :- println("Failure ", T), abort.
failure(T) :- type(T,L),assertTrue(has_proof(error,L),T),!.
failure(T) :- println("Failure ", T), abort.

