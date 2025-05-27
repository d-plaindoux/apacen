-{
    First Order Logic
}-

true.

not(P) :- call(P), ! -{ red cut}- , fail.
not(P).
or(P1,_) :- call(P1), !.
or(_,P2) :- call(P2).
and(P1,P2) :- !, call(P1), call(P2).
imply(P1,P2) :- or(not(P1), P2).

eval(A)        :- unbound(A), !, fail.
eval(P1 || _ ) :- eval(P1), !.
eval(_  || P2) :- !, eval(P2).
eval(P1 && P2) :- !, eval(P1), eval(P2).
eval(P1 => P2) :- !, eval(not(P1) || P2).
eval(P)        :- call(P).