-{ Type checking is okay }-

typeOkay(T) :- normalise(T,R),type(R,L),assertFalse(has_proof(error,L),R),!.
typeOkay(T) :- normalise(T,R),println("❌", R), abort.

-{ Type checking is a failure }-

typeFail(T) :- normalise(T,R),type(R,L),assertTrue(has_proof(error,L),R),!.
typeFail(T) :- normalise(T,R),println("❌", R), abort.

-{ Type checking is okay with an identified hole }-

typeHole(T)  :- normalise(T,R),type(R,L),assertFalse(has_proof(error,L),R),assertTrue(has_proof(hole,L),R),!.
typeHole(T)  :- normalise(T,R),println("❌", R), abort.

-{ Type }-

?- typeFail(nil |- type(1) : type(1)).
?- typeOkay(nil |- type(1) : type(2)).

-{ Hypothesis }-

?- typeOkay((x:T)::nil |- x:R).
?- typeOkay((x:int)::nil |- x:R).
?- typeOkay((x:int)::nil |- X:int).
?- typeFail((x:string)::nil |- x:int).

-{ Native literals and types }-

?- typeOkay(nil |- int:type(0)).
?- typeOkay(nil |- 1:int).
?- typeFail(nil |- 1:string).
?- typeOkay(nil |- string:type(0)).
?- typeOkay(nil |- "1":string).
?- typeFail(nil |- "1":int).

-{ Abstraction }-

?- typeOkay(nil |- x => x : (y:int) -> int).
?- typeOkay(nil |- x => x : T).
?- typeOkay(nil |- X : (x:t) -> t).
?- typeFail(nil |- 1 : (x:t) -> t).

-{ Application }-

?- typeOkay((f:(y:int) -> int)::nil |- f @ 1 : int).
?- typeOkay((f:(y:int) -> int)::nil |- f @ 1 : T).
?- typeOkay((f:(y:int) -> int)::nil |- f @ Y : int).
?- typeHole(nil |- F @ 1 : int).
?- typeFail((f:int -> int)::nil |- f @ "1" : _).

-{ Left injection }-

?- typeOkay(nil |- inl(1) : int | T).
?- typeOkay(nil |- inl(1) : T).
?- typeOkay(nil |- Y : int | T).
?- typeFail(nil |- inl(1) : int).

-{ Right injection }-

?- typeOkay(nil |- inr(1) : T | int).
?- typeOkay(nil |- inr(1) : T).
?- typeFail(nil |- inr(1) : int).

-{ Case }-

?- typeOkay(nil |- case(inl(1),x => x,y => y) : int).
?- typeOkay(nil |- case(inl(1),x => x,y => y) : R).
?- typeOkay(nil |- a => case(a,x => "string",y => 1): (x:int|string) -> case(x,y => string,y => int)).

-{ Pair }-

?- typeOkay(nil |- pair(1,"2") : (x:int) * string).
?- typeOkay(nil |- pair(int,1) : (x:type(0)) * x).
?- typeOkay(nil |- pair(1,"2") : T).
?- typeOkay(nil |- pair(string,"2") : T).

-{ First }-

?- typeOkay(nil |- fst(pair(1,"2")) : int).
?- typeHole(nil |- fst(P) : int).
?- typeFail(nil |- fst(pair(1,"2")) : string).

-{ Second }-

?- typeOkay(nil |- snd(pair(1,"2")) : string).
?- typeHole(nil |- snd(P) : string).
?- typeFail(nil |- snd(pair(1,"2")) : int).
?- typeHole(nil |- snd(pair(1,Y)) : int).

-{ Propositional equality }-

?- typeOkay(nil |- b => a => R : (t:type(0)) -> (a:t) -> a:=:a).
?- typeOkay(nil |- t => a => b => a_b => subst_by(refl,a_b) : (t:type(0)) -> (a:t) -> (b:t) -> (a:=:b) -> b:=:a).
?- typeOkay(nil |- t => a => b => c => a_b => b_c => subst_by(subst_by(refl,a_b),b_c) : (t:type(0)) -> (a:t) -> (b:t) -> (c:t) -> (a:=:b) -> (b:=:c) -> a:=:c).
?- typeOkay(nil |- ta => tb => f => a => b => a_b => subst_by(refl,a_b) : (ta:type(0)) -> (tb:type(0)) -> (f:ta -> tb) -> (a:ta) -> (b:ta) -> (a:=:b) -> (f@a):=:(f@b)).
?- typeOkay(nil |- ta => tb => f => g => f_g => a => subst_by(refl,f_g) : (ta:type(0)) -> (tb:type(0)) -> (f:ta -> tb) -> (g:ta -> tb) -> (f:=:g) -> (a:ta) -> (f@a):=:(g@a)).
