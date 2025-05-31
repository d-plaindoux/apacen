betaSuccess(A,B) :- beta(A,B,L),assertFalse(has_proof(error,L),eq(A,B)),!.
betaSuccess(A,B) :- println("❌", A, "=", B), abort.
betaFailure(A,B) :- beta(A,B,L),assertTrue(has_proof(error,L),neq(A,B)),!.
betaFailure(A,B) :- println("❌", A, "!=", B), abort.


?- betaSuccess(x[x:=y], y).
?- betaFailure(x[z:=y], y).
?- betaSuccess((x => x) @ y, y).
?- betaSuccess(fst(pair(x,y)),x).
?- betaSuccess(snd(pair(x,y)),y).
?- betaSuccess(case(inl(x), x => y, z), y).
?- betaSuccess(case(inr(x), z, x => y), y).
?- betaSuccess((x => case(inr(x), z, x => x)) @ 1, 1).

-{ Cases with unbound variables }-

?- betaSuccess(T[x:=y], T[x:=y]).
?- betaSuccess(t[X:=y], t).
?- betaSuccess(t[x:=Y], t).