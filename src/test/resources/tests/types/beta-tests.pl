betaSuccess(A,B) :- betaSuccess(nil,A,B).
betaFailure(A,B) :- betaFailure(nil,A,B).

betaSuccess(Gamma,A,B) :- beta(Gamma,A,B,L),assertFalse(has_proof(error,L),eq(A,B)),!.
betaSuccess(Gamma,A,B) :- println("❌", A, "=", B), abort.
betaFailure(Gamma,A,B) :- beta(Gamma,A,B,L),assertTrue(has_proof(error,L),neq(A,B)),!.
betaFailure(Gamma,A,B) :- println("❌", A, "!=", B), abort.

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

?- betaSuccess((f:=(x=>x))::nil,f @ 1, 1).

-- BUG in the reduction

?- betaSuccess(
        (((_:int) * (x :=: int)) | ((_:(e @ int)) * ((_:(e @ int)) * (x :=: int))))[x := t],
        (((_:int) * (t :=: int)) | ((_:(e @ int)) * ((_:(e @ int)) * (t :=: int))))
   ).