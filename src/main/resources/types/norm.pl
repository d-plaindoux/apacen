-{
    Set of predicates used for term normalisation dedicated to flat premise definition
}-

normalise(R1,G |- E : T)                        :- !,hypothesis(R1,G |- R2),ascription(R2,E : T).

hypothesis(nil |- R,nil |- R)                   :- !.
hypothesis(G |- R, G |- R)                      :- !.
hypothesis((X : R1)::R2,((X : T1)::G) |- R3)    :- !,const0(X),term(R1,T1),hypothesis(R2,G |- R3).

ascription(R1 : R2,E : T)                       :- !,term(R1,E),term(R2,T).
ascription((X : R1) -> R2,((X : T1) -> E) : T2) :- !,const0(X),term(R1,T1),ascription(R2,E : T2).
ascription(R1 -> R2,((_ : T1) -> E) : T2)       :- !,term(R1,T1),ascription(R2,E : T2).
ascription((X : R1) * R,((X : T1) * E) : T2)    :- !,const0(X),term(R1,T1),ascription(R2,E : T2).
ascription(R1 * R2,((_ : T1) * E) : T2)         :- !,term(R1,T1),ascription(R2,E : T2).
ascription(X => R,(X => E) : T)                 :- !,const0(X),ascription(R,E : T).
ascription(R1 @ R2,E : T)                       :- !,term(R1,E1),ascription(R2,E2 : T),leftAssoc(E2,E1,E).
ascription(R1 | R2,(E1 | E2) : T)               :- !,term(R1,E1),ascription(R2,E2 : T).
ascription(R1 :=: R2,(E1 :=: E2) : T)           :- !,term(R1,E1),ascription(R2,E2 : T).

term(E,E)                                       :- unbound(E),!. -- holes are allowed
term(E,E)                                       :- const0(E),!.
term(E,E)                                       :- number(E),!.
term(E,E)                                       :- string(E),!.
term(type(E),type(E))                           :- number(E),!.
term(type(E),type(E))                           :- unbound(E),!.
term((X : R1) -> R2,(X : T1) -> E)              :- !,const0(X),term(R1,T1),term(R2,E).
term(R1 -> R2,(_ : T1) -> E)                    :- !,term(R1,T1),term(R2,E).
term((X : R1) * R2,(X : T1) * E)                :- !,const0(X),term(R1,T1),term(R2,E).
term(R1 * R2,(_ : T1) * E)                      :- !,term(R1,T1),term(R2,E).
term(X => R,X => E)                             :- !,const0(X),term(R,E).
term(R1 @ R2,E)                                 :- !,term(R1,E1),term(R2,E2),leftAssoc(E2,E1,E).
term(R1 | R2,E1 | E2)                           :- !,term(R1,E1),term(R2,E2).
term(inl(R),inl(E))                             :- !,term(R,E).
term(inr(R),inr(E))                             :- !,term(R,E).
term(case(R1,R2,R3),case(E1,E2,E3))             :- !,term(R1,E1),term(R2,E2),term(R3,E3).
term(fst(R),fst(E))                             :- !,term(R,E).
term(snd(R),snd(E))                             :- !,term(R,E).
term(pair(R1,R2),pair(E1,E2))                   :- !,term(R1,E1),term(R2,E2).
term(R1 : R2,E : T)                             :- !,term(R1,E),term(R2,T).
term(R1 :=: R2,E1 :=: E2)                       :- !,term(R1,E1),term(R2,E2).
term(subst_by(R1,R2),subst_by(E1,E2))           :- !,term(R1,E1),term(R2,E2).

-- do not work for term like a @ (b @ c). The last group is not preserved (@ should be left associative)

leftAssoc(B,A,A @ B)                            :- unbound(B),!.
leftAssoc(B @ C,A,R)                            :- !,leftAssoc(C,A @ B,R).
leftAssoc(B,A,A @ B)                            :- !.