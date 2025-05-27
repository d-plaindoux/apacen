-{
    Type
}-

-- nil |- type(1) !: type(1)
?- assertTrue(type(nil |- type(1) : type(1), proof(error,_)), "nil |- type(1) !: type(1)").

-- nil |- type(1) : type(2)
?- assertTrue(type(nil |- type(1) : type(2), proof(type(term))), "nil |- type(1) : type(2)").

-{
    Hypothesis
}-

-- x:T |- x : R
?- assertTrue(type((x:T)::nil |- x : R, proof(hypothesis)), "x:T |- x : R").

-- x:int |- x : R
?- assertTrue(type((x:int)::nil |- x:R, proof(hypothesis)), "x:int |- x : R").

-- X:int |- X : int
?- assertTrue(type((x:int)::nil |- X:int, proof(hypothesis)), "X:int |- X : int").

-{
    Native literals and types
}-

-- nil |- int : type(0)
?- assertTrue(type(nil |- int:type(0), proof(type(int))), "nil |- int : type(0)").

-- nil |- 1 : int
?- assertTrue(type(nil |- 1:int, proof(int)), "nil |- 1 : int").

-- nil |- 1 !: string
?- assertTrue(type(nil |- 1:string, proof(error,_)), "nil |- 1 !: string").

-- nil |- string : type(0)
?- assertTrue(type(nil |- string:type(0), proof(type(string))), "nil |- string : type(0)").

-- nil |- "1" : string
?- assertTrue(type(nil |- "1":string, proof(string)), "nil |- '1' : string").

-{
    Abstraction
}-

-- nil |- x => x : (y:int) -> int
?- assertTrue(type(nil |- x => x : (y:int) -> int, proof(arrow,subst(no_subst,no_red),proof(hypothesis))), "nil |- x => x : (y:int) -> int").

-- nil |- x => x : T
?- assertTrue(type(nil |- x => x : T, proof(arrow,subst(unbound,free),proof(hypothesis))), "nil |- x => x : T").

-- nil |- X : (x:t) -> t
?- assertTrue(type(nil |- X : (x:t) -> t, proof(arrow,subst(unbound,no_red),proof(hypothesis))), "nil |- X : (x:t) -> t").

-{
    Application
}-

-- f:(y:int) -> int |- f @ 1 : int
?- assertTrue(type((f:(y:int) -> int)::nil |- f @ 1 : int, proof(abstraction,proof(hypothesis),proof(int),subst(no_subst,no_red))), "f:(y:int) -> int |- f @ 1 : int").

-- f:(y:int) -> int |- f @ 1 : T
?- assertTrue(type((f:(y:int) -> int)::nil |- f @ 1 : T, proof(abstraction,proof(hypothesis),proof(int),subst(no_subst,no_red))), "f:(y:int) -> int |- f @ 1 : T").

-- f:(y:int) -> int |- f @ Y : int
-- infinite loop !?
-- ?- assertTrue(type((f:(y:int) -> int)::nil |- f @ Y : int, proof(abstraction,proof(hypothesis),proof(int),subst(no_subst,no_red))), "f:(y:int) -> int |- f @ Y : int").

-- nil |- F @ int : int
?- assertTrue(type(nil |- F @ 1 : int, proof(abstraction,proof(arrow,subst(unbound,free),proof(stopped,(((_ : int) :: nil) |- (_ : int)))),proof(int),subst(unbound,free))), "nil |- F @ int : int").

-{
    Left injection
}-

-- nil |- inl(1) : int | T
?- assertTrue(type(nil |- inl(1) : int | T, proof(inl,proof(int))), "nil |- inl(1) : int | T").

-- nil |- inl(1) : T
?- assertTrue(type(nil |- inl(1) : T, proof(inl,proof(int))), "nil |- inl(1) : T").

-- nil |- Y : int | T
?- assertTrue(type(nil |- Y : int | T, proof(inl,proof(stopped,(nil |- (X : int))))), "nil |- Y : int | T").

-{
    Right injection
}-

-- nil |- inl(1) : int | T
?- assertTrue(type(nil |- inr(1) : T | int, proof(inr,proof(int))), "nil |- inl(1) : int | T").

-- nil |- inl(1) : T
?- assertTrue(type(nil |- inr(1) : T, proof(inr,proof(int))), "nil |- inl(1) : T").

-- nil |- inl(1) : int | T
-- No solution !?
-- ?- assertTrue(type(nil |- Y : T | int, proof(inl,proof(int))), "nil |- inl(1) : int | T").

-{
    Case
}-

-- nil |- case(inl(1),x => x, y => y) : int
?- assertTrue(type(nil |- case(inl(1),x => x, y => y) : int, proof(case,proof(inl,proof(int)),proof(arrow,subst(unbound,no_red),proof(hypothesis)),proof(arrow,subst(unbound,no_red),proof(hypothesis)))), "nil |- case(inl(1),x => x, y => y) : int").

-- nil |- case(inl(1),x => x, y => y) : R
?- assertTrue(type(nil |- case(inl(1),x => x, y => y) : R, proof(case,proof(inl,proof(int)),proof(arrow,subst(unbound,free),proof(hypothesis)),proof(arrow,subst(unbound,no_red),proof(hypothesis)))), "nil |- case(inl(1),x => x, y => y) : R").

-- nil |- a => case(a,x => "string", y => 1): (x:int|string) -> case(x, y => string, y => int)
?- assertTrue(type(nil |- a => case(a,x => "string", y => 1): (x:int|string) -> case(x, y => string, y => int), proof(arrow,subst(case(subst(x,a),arrow(no_subst),arrow(no_subst)),no_red),proof(case,proof(hypothesis),proof(arrow,subst(unbound,subst(case(subst(a,inl(y)),arrow(no_subst),arrow(no_subst)),inl(no_red,no_red,subst(unbound,no_red)))),proof(string)),proof(arrow,subst(no_subst,subst(case(subst(a,inr(y)),arrow(no_subst),arrow(no_subst)),inr(no_red,no_red,subst(no_subst,no_red)))),proof(int))))), "nil |- a => case(a,x => 'string', y => 1): (x:int|string) -> case(x, y => string, y => int)").
-{
    Pair
}-

-- nil |- pair(1,"2") : (x:int) * string
?- assertTrue(type(nil |- pair(1,"2") : (x:int) * string, proof(pair,proof(int),proof(beta,subst(no_subst,no_red),proof(string)))), "nil |- pair(1,'2') : (x:int) * string").

-- nil |- pair(int,1) : (x:type(0)) * x
?- assertTrue(type(nil |- pair(int,1) : (x:type(0)) * x, proof(pair,proof(type(int)),proof(beta,subst(subst(x,int),no_red),proof(int)))), "nil |- pair(int,1) : (x:type(0)) * x").

-- nil |- pair(1,"2") : T
?- assertTrue(type(nil |- pair(1,"2") : T, proof(pair,proof(int),proof(beta,subst(unbound,free),proof(string)))), "nil |- pair(1,'2') : T").

-- nil |- pair(string,"2") : T
?- assertTrue(type(nil |- pair(string,"2") : T, proof(pair,proof(type(string)),proof(beta,subst(unbound,free),proof(string)))), "nil |- pair(string,'2') : T").

-{
    First
}-

-- nil |- fst(pair(1,"2")) : int
?- assertTrue(type(nil |- fst(pair(1,"2")) : int, proof(fst,proof(pair,proof(int),proof(beta,subst(unbound,free),proof(string))))), "nil |- fst(pair(1,'2')) : int").

-- nil |- fst(P) : int
?- assertTrue(type(nil |- fst(P) : int, proof(fst,proof(pair,proof(stopped,(nil |- (_ : int))),proof(stopped,(nil |- (_ : _[(_ := _)])))))), "nil |- fst(P) : int").

-{
    Second
}-

-- nil |- snd(pair(1,"2")) : string
?- assertTrue(type(nil |- snd(pair(1,"2")) : string, proof(snd,proof(pair,proof(int),proof(beta,subst(unbound,free),proof(string))),subst(unbound,no_red))), "nil |- snd(pair(1,'2')) : string").


-- nil |- snd(P) : string
?- assertTrue(type(nil |- snd(P) : string, proof(snd,proof(pair,proof(stopped,(nil |- (_ : _))),proof(stopped,(nil |- (_ : string[(_ := _)])))),subst(unbound,free))), "nil |- snd(P) : string").

-{
    Propositional equality
}-

-- nil |- b => a => refl : (t:type(0)) -> (a:t) -> a:=:a
?- assertTrue(type(nil |- b => a => refl : (t:type0) -> (a:t) -> a:=:a, proof(arrow,subst(forall(subst(t,b),(no_subst :=: no_subst)),no_red),proof(arrow,subst((subst(a,a) :=: subst(a,a)),no_red),proof(reflexivity)))), "nil |- b => a => refl : (t:type(0)) -> (a:t) -> a:=:a").

-- nil |- (t => a => b => a_b => subst_by(refl,a_b)) : (t:type) -> (a:t) -> (b:t) -> (_:a:=:b) -> b:=:a
?- assertTrue(type(nil |- (t => a => b => a_b => subst_by(refl,a_b)) : (t:type) -> (a:t) -> (b:t) -> (_:a:=:b) -> b :=: a, proof(arrow,subst(forall(subst(t,t),forall(subst(t,t),forall(hidden))),no_red),proof(arrow,subst(forall(no_subst,forall(hidden)),no_red),proof(arrow,subst(forall(hidden),no_red),proof(arrow,subst(unbound,no_red),proof(subst_by,proof(hypothesis),proof(beta,subst((no_subst :=: subst(a,b)),no_red),proof(reflexivity)))))))), "nil |- t => a => b => a_b => subst_by(refl,a_b) : (t:type) -> (a:t) -> (b:t) -> (a :=: b) -> b :=: a").
