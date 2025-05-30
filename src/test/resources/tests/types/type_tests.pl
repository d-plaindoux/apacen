typeSuccess(T) :- type(T,L),assertFalse(has_proof(error,L),T),!.
typeSuccess(T) :- println("❌", T), abort.
typeFailure(T) :- type(T,L),assertTrue(has_proof(error,L),T),!.
typeFailure(T) :- println("❌", T), abort.

-{ Type }-

?- typeFailure(nil |- type(1) : type(1)).
?- typeSuccess(nil |- type(1) : type(2)).

-{ Hypothesis }-

?- typeSuccess((x:T)::nil |- x:R).
?- typeSuccess((x:int)::nil |- x:R).
?- typeSuccess((x:int)::nil |- X:int).
?- typeFailure((x:string)::nil |- x:int).

-{ Native literals and types }-

?- typeSuccess(nil |- int:type(0)).
?- typeSuccess(nil |- 1:int).
?- typeFailure(nil |- 1:string).
?- typeSuccess(nil |- string:type(0)).
?- typeSuccess(nil |- "1":string).
?- typeFailure(nil |- "1":int).

-{ Abstraction }-

?- typeSuccess(nil |- x => x : (y:int) -> int).
?- typeSuccess(nil |- x => x : T).
?- typeSuccess(nil |- X : (x:t) -> t).
?- typeFailure(nil |- 1 : (x:t) -> t).

-{ Application }-

?- typeSuccess((f:(y:int) -> int)::nil |- f @ 1 : int).
?- typeSuccess((f:(y:int) -> int)::nil |- f @ 1 : T).
?- typeSuccess((f:(y:int) -> int)::nil |- f @ Y : int).
?- typeSuccess(nil |- F @ 1 : int).
?- typeFailure((f:(y:int) -> int)::nil |- f @ "1" : _).

-{ Left injection }-

?- typeSuccess(nil |- inl(1) : int | T).
?- typeSuccess(nil |- inl(1) : T).
?- typeSuccess(nil |- Y : int | T).
?- typeFailure(nil |- inl(1) : int).

-{ Right injection }-

?- typeSuccess(nil |- inr(1) : T | int).
?- typeSuccess(nil |- inr(1) : T).
?- typeFailure(nil |- inr(1) : int).

-{ Case }-

?- typeSuccess(nil |- case(inl(1),x => x,y => y) : int).
?- typeSuccess(nil |- case(inl(1),x => x,y => y) : R).
?- typeSuccess(nil |- a => case(a,x => "string",y => 1): (x:int|string) -> case(x,y => string,y => int)).

-{ Pair }-

?- typeSuccess(nil |- pair(1,"2") : (x:int) * string).
?- typeSuccess(nil |- pair(int,1) : (x:type(0)) * x).
?- typeSuccess(nil |- pair(1,"2") : T).
?- typeSuccess(nil |- pair(string,"2") : T).

-{ First }-

?- typeSuccess(nil |- fst(pair(1,"2")) : int).
?- typeSuccess(nil |- fst(P) : int).
?- typeFailure(nil |- fst(pair(1,"2")) : string).

-{ Second }-

?- typeSuccess(nil |- snd(pair(1,"2")) : string).
?- typeSuccess(nil |- snd(P) : string).
?- typeFailure(nil |- snd(pair(1,"2")) : int).

-{ Propositional equality }-

?- typeSuccess(nil |- b => a => refl : (t:type(0)) -> (a:t) -> a:=:a).
?- typeSuccess(nil |- (t => a => b => a_b => subst_by(refl,a_b)) : (t:type(0)) -> (a:t) -> (b:t) -> (_:a:=:b) -> b :=: a).
