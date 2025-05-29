-{ Type }-

?- failure(nil |- type(1) : type(1)).
?- success(nil |- type(1) : type(2)).

-{ Hypothesis }-

?- success((x:T)::nil |- x:R).
?- success((x:int)::nil |- x:R).
?- success((x:int)::nil |- X:int).
?- failure((x:string)::nil |- x:int).

-{ Native literals and types }-

?- success(nil |- int:type(0)).
?- success(nil |- 1:int).
?- failure(nil |- 1:string).
?- success(nil |- string:type(0)).
?- success(nil |- "1":string).
?- failure(nil |- "1":int).

-{ Abstraction }-

?- success(nil |- x => x : (y:int) -> int).
?- success(nil |- x => x : T).
?- success(nil |- X : (x:t) -> t).
?- failure(nil |- 1 : (x:t) -> t).

-{ Application }-

?- success((f:(y:int) -> int)::nil |- f @ 1 : int).
?- success((f:(y:int) -> int)::nil |- f @ 1 : T).
?- success((f:(y:int) -> int)::nil |- f @ Y : int).
?- success(nil |- F @ 1 : int).
?- failure((f:(y:int) -> int)::nil |- f @ "1" : _).

-{ Left injection }-

?- success(nil |- inl(1) : int | T).
?- success(nil |- inl(1) : T).
?- success(nil |- Y : int | T).
?- failure(nil |- inl(1) : int).

-{ Right injection }-

?- success(nil |- inr(1) : T | int).
?- success(nil |- inr(1) : T).
?- failure(nil |- inr(1) : int).

-{ Case }-

?- success(nil |- case(inl(1),x => x,y => y) : int).
?- success(nil |- case(inl(1),x => x,y => y) : R).
?- success(nil |- a => case(a,x => "string",y => 1): (x:int|string) -> case(x,y => string,y => int)).

-{ Pair }-

?- success(nil |- pair(1,"2") : (x:int) * string).
?- success(nil |- pair(int,1) : (x:type(0)) * x).
?- success(nil |- pair(1,"2") : T).
?- success(nil |- pair(string,"2") : T).

-{ First }-

?- success(nil |- fst(pair(1,"2")) : int).
?- success(nil |- fst(P) : int).
?- failure(nil |- fst(pair(1,"2")) : string).

-{ Second }-

?- success(nil |- snd(pair(1,"2")) : string).
?- success(nil |- snd(P) : string).
?- failure(nil |- snd(pair(1,"2")) : int).

-{ Propositional equality }-

?- success(nil |- b => a => refl : (t:type(0)) -> (a:t) -> a:=:a).
?- success(nil |- (t => a => b => a_b => subst_by(refl,a_b)) : (t:type(0)) -> (a:t) -> (b:t) -> (_:a:=:b) -> b :=: a).
