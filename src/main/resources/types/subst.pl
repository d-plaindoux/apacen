-{
    Term substitution
}-

subst(T[X:=Y],T,unbound)                                        :- unbound(X),const0(Y),!,equals(X,Y).
subst(T[X:=Y],T,unbound)                                        :- unbound(X),!.
subst(T[X:=Y],T,unbound)                                        :- unbound(Y),!,equals(X,Y).
subst(T[X:=Y],R,unbound)                                        :- unbound(T),!,equals(T[X:=Y],R).
subst(X[X:=Y],Y,subst)                                          :- bound(X),!.

subst((E1 @ E2)[X:=Y],E3 @ E4,L1 @ L2)                          :- !,subst(E1[X:=Y],E3,L1),subst(E2[X:=Y],E4,L2).
subst((X1 => E)[X:=_],X => E,arrow(hidden))                     :- bound(X1),equals(X1,X),!.
subst((X1 => E1)[X:=Y],X1 => E2,arrow(L))                       :- !,subst(E1[X:=Y],E2,L).
subst(((X1:E1) -> E2)[X:=_],(X:E1) -> E2,forall(hidden))        :- bound(X1),equals(X1,X),!.
subst(((X1:E1) -> E2)[X:=Y],(X1:E3) -> E4,forall(L1,L2))        :- !,subst(E1[X:=Y],E3,L1),subst(E2[X:=Y],E4,L2).
subst(inl(E1)[X:=Y],inl(E2),inl(L))                             :- !,subst(E1[X:=Y],E2,L).
subst(inr(E1)[X:=Y],inr(E2),inr(L))                             :- !,subst(E1[X:=Y],E2,L).
subst(case(E1,E2,E3)[X:=Y],case(E4,E5,E6),case(L1,L2,L3))       :- !,subst(E1[X:=Y],E4,L1),subst(E2[X:=Y],E5,L2),subst(E3[X:=Y],E6,L3).
subst((E1 | E2)[X:=Y],E3 | E4,L1 | L2)                          :- !,subst(E1[X:=Y],E3,L1),subst(E2[X:=Y],E4,L2).
subst(fst(E1)[X:=Y],fst(E2),fst(L))                             :- !,subst(E1[X:=Y],E2,L).
subst(snd(E1)[X:=Y],snd(E2),snd(L))                             :- !,subst(E1[X:=Y],E2,L).
subst(pair(E1,E2)[X:=Y],pair(E3,E4),pair(L1,L2))                :- !,subst(E1[X:=Y],E3,L1),subst(E2[X:=Y],E4,L2).
subst(((X1:R) * E)[X:=Y],(X:R) * E,exist(hidden))               :- bound(X1),equals(X1,X),!.
subst(((X1:E1) * E2)[X:=Y],(X1:E3) * E4,exist(L1,L2))           :- !,subst(E1[X:=Y],E3,L1),subst(E2[X:=Y],E4,L2).
subst((E1:E2)[X:=Y],(E3:E4),(L1:L2))                            :- !,subst(E1[X:=Y],E3,L1),subst(E2[X:=Y],E4,L2).
subst((E1 :=: E2)[X:=Y],E3 :=: E4,L1 :=: L2)                    :- !,subst(E1[X:=Y],E3,L1),subst(E2[X:=Y],E4,L2).
subst(subst_by(E1,E2)[X:=Y],subst_by(E3,E4),subst_by(L1,L2))    :- !,subst(E1[X:=Y],E3,L1),subst(E2[X:=Y],E4,L2).
subst(rec(X1:E1,E2)[X:=_],rec(X:E1,E2),rec(hidden))             :- bound(X1),equals(X1,X),!.
subst(rec(X1:E1,E2)[X:=Y],rec(X1:E3,E4),rec(L1,L2))             :- !,subst(E1[X:=Y],E3,L1),subst(E2[X:=Y],E4,L2).

subst(A[_:=_],A,no_subst)                                       :- !.
