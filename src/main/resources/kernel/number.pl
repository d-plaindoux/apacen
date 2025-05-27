-- Number predicates

zero(0).
positive(X) :- 0 < X.
negative(X) :- 0 > X.
positiveOrZero(X) :- 0 <= X.
negativeOrZero(X) :- 0 >= X.

in(P1,R,P2) :- P1 <= P2, P1 <= R, R <= P2.

fact(0,1).
fact(N,R*N) :-
    N > 0,
    fact(N-1,R).