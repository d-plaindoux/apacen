?- assertTrue(subst(x[x:=y],y, _), "x[x:=y] = y").
?- assertTrue(subst((x @ z)[x:=y],y @ z, _), "(x @ z)[x:=y] = y @ z").
?- assertTrue(subst((z @ x)[x:=y],z @ y, _), "(z @ x)[x:=y] = z @ y").
?- assertTrue(subst((x => x)[x:=y],x => x, _), "(x => x)[x:=y] = x => x)").
?- assertTrue(subst((z => x)[x:=y],z => y, _), "(z => x)[x:=y] = z => y").
?- assertTrue(subst(((x:z) -> t)[x:=y],(x:z) -> t, _), "((x:z) -> t)[x:=y] = (x:z) -> t").
?- assertTrue(subst(((z:x) -> t)[x:=y],(z:y) -> t, _), "((z:x) -> t)[x:=y] = (z:y) -> t").
?- assertTrue(subst(((z:t) -> x)[x:=y],(z:t) -> y, _), "((z:t) -> x)[x:=y] = (z:t) -> y").
?- assertTrue(subst(inl(x)[x:=y],inl(y), _), "inl(x)[x:=y] = inl(y)").
?- assertTrue(subst(inl(x)[x:=y],inl(y), _), "inr(x)[x:=y] = inr(y)").
?- assertTrue(subst(case(x,z,t)[x:=y],case(y,z,t), _), "case(x,z,t)[x:=y] = case(y,z,t)").
?- assertTrue(subst(case(z,x,t)[x:=y],case(z,y,t), _), "case(z,x,t)[x:=y] = case(z,y,t)").
?- assertTrue(subst(case(z,t,y)[x:=y],case(z,t,y), _), "case(z,t,x)[x:=y] = case(z,t,x)").
?- assertTrue(subst((x | z)[x:=y],y | z, _), "(x | z)[x:=y] = y | z").
?- assertTrue(subst((z | x)[x:=y],z | y, _), "(z | x)[x:=y] = z | y").
?- assertTrue(subst(fst(x)[x:=y],fst(y), _), "fst(x)[x:=y] = fst(y)").
?- assertTrue(subst(snd(x)[x:=y],snd(y), _), "snd(x)[x:=y] = snd(y)").
?- assertTrue(subst(pair(x,t)[x:=y],pair(y,t), _), "pair(x,t)[x:=y] = pair(y,t)").
?- assertTrue(subst(pair(t,x)[x:=y],pair(t,y), _), "pair(t,x)[x:=y] = pair(t,y)").
?- assertTrue(subst(((x:z) * t)[x:=y],(x:z) * t, _), "((x:z) * t)[x:=y] = (x:z) * t").
?- assertTrue(subst(((z:x) * t)[x:=y],(z:y) * t, _), "((z:x) * t)[x:=y] = (z:y) * t").
?- assertTrue(subst(((z:t) * x)[x:=y],(z:t) * y, _), "((z:t) * x)[x:=y] = (z:t) * y").
?- assertTrue(subst((x:t)[x:=y],(y:t), _), "(x:t)[x:=y] = (y:t)").
?- assertTrue(subst((t:x)[x:=y],(t:y), _), "(t:x)[x:=y] = (y:t)").
?- assertTrue(subst(T[x:=y],T[x:=y], _), "T[x:=y] = T[x:=y]").
?- assertTrue(subst(x[X:=y],x, _), "x[X:=y] = x").
?- assertTrue(subst(x[x:=Y],x, _), "x[x:=Y] = x").