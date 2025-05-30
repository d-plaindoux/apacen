?- assertTrue(beta(x[x:=y], y, _), "x[x:=y] = y").
?- assertFalse(beta(x[z:=y], y, _), "x[z:=y] != y").
?- assertTrue(beta((x => x) @ y, y, _), "(x => x) @ y = y").
?- assertTrue(beta(fst(pair(x,y)),x, _), "fst(pair(x,y)) = x").
?- assertTrue(beta(snd(pair(x,y)),y, _), "snd(pair(x,y)) = y").
?- assertTrue(beta(case(inl(x), x => y, z), y, _), "case(inl(x), x => y, z) = y").
?- assertTrue(beta(case(inr(x), z, x => y), y, _), "case(inr(x), z, x => y) = y").
?- assertTrue(beta((x => case(inr(x), z, x => x)) @ 1, 1, _), "(x => case(inr(x), z, x => x)) @ 1 = 1").

-{ Cases with unbound variables }-

?- assertTrue(beta(T[x:=y], T[x:=y], _), "T[x:=y] = T[x:=y]").
?- assertTrue(beta(t[X:=y], t, _), "t[X:=y] = y").
?- assertTrue(beta(t[x:=Y], t, _), "t[X:=y] = y").