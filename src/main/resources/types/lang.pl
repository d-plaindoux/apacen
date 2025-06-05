-{

    program ::=
        declaration (";" declaration)*

    declaration ::=
        ident :: E
        ident := E

    E ::=
        ident

        ident => E
        E @ E
        (ident:T) -> T

        inl(E)
        inr(E)
        case(E,E,E)
        E | E

        fst(E)
        snd(E)
        pair(E,E)
        (ident:E) * E

        (E:E)
        type(N)

        E = E
        refl
        subst(E,E)

        rec(ident:E,E)

(*TODO*)
        sig[ ident :: E; ... ]
        val[ ident := E; ... ]

    ---------------------------

    goal ::=
        G |- E : E

    G ::=
        nil
        (ident::E)::G
        (ident:=E)::G
}-

premise(G |- E : T) :- goal(G),term(E),term(T).

goal(nil)         :- !.
goal((X:T)::G)    :- const0(X),term(T),goal(G).

term(X)             :- unbound(X),!. -- holes are allowed
term(X)             :- const0(X),!.
term(X)             :- number(X),!.
term(X)             :- string(X),!.
term(X => E)        :- !,const0(X),term(E).
term((X:T) -> E)    :- !,const0(X),term(T),term(E).
term(T -> E)        :- !,term(T),term(E).
term(E @ F)         :- !,term(E),term(F).
term(inl(E))        :- !,term(E).
term(inr(E))        :- !,term(E).
term(case(E,F,G))   :- !,term(E),term(F),term(G).
term(E | F)         :- !,term(E),term(F).
term(fst(E))        :- !,term(E).
term(snd(E))        :- !,term(E).
term(pair(E,F))     :- !,term(E),term(F).
term((X:T) * E)     :- !,const0(X),term(T),term(E).
term(T * E)         :- !,term(T),term(E).
term(E : T)         :- !,term(E),term(T).
term(type(N))       :- !,number(N).
term(E :=: F)       :- !,term(E),term(F).
term(refl)          :- !.
term(subst_by(E,F)) :- !,term(E),term(F).
term(rec(X:E,F))    :- !,const0(X),term(E),term(F).