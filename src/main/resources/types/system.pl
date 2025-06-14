-{
    Dependant type checker implementation
}-

type(G |- E : T, LOG1) :-
    !,
    type_system(G |- E : T, LOG2),
    equals(LOG1, LOG2).

type(R, LOG) :-
    !,
    normalise(R,G |- E : T),
    type(G |- E : T, LOG).

-{ Type system }-

type_system(Gamma |- E : T,proof(hole,Gamma |- E : T)) :-
    unbound(E), unbound(T),
    -- println(hole,Gamma |- E : T),
    !.

type_system(Gamma |- E : T,LOG) :-
    bound(E), bound(T),
    !,
    type_system(check,Gamma |- E : T,LOG).

type_system(Gamma |- E : T,LOG) :-
    bound(E),
    !,
    type_system(infer_type,Gamma |- E : T,LOG).

type_system(Gamma |- E : T,LOG) :-
    bound(T),
    !,
    type_system(infer_term,Gamma |- E : T,LOG).

-{ Native types }-

type_system(_,Gamma |- int : type(0),proof(type(int))) :-
    !.

type_system(_,Gamma |- A : int,proof(int)) :-
    number(A),
    !.

type_system(_,Gamma |- string : type(0),proof(type(string))) :-
    !.

type_system(_,Gamma |- A : string,proof(string)) :-
    string(A),
    !.

-{ Type type }-

type_system(Strategy,Gamma |- type(L1) : type(L2),proof(type(term))) :-
    L1 >= 0, L2 = L1 + 1,
    !.

-{ Hypothesis }-

type_system(_,Gamma |- A : T1,proof(hypothesis)) :-
    member(A : T2,Gamma),
    beta(Gamma,T1,R1,_),
    beta(Gamma,T2,R2,_),
    equals(R1,R2),
    !.

-{ Function types }-

type_system(_,Gamma |- ((X:T) -> M) : type(_),proof(arrow,LOG1,LOG2)) :-
    !,
    type_system(Gamma |- T : type(_),LOG1),
    type_system(((X:T)::Gamma) |- M : type(_),LOG2).

type_system(_,Gamma |- (X => A) : ((Y:R) -> T1),proof(arrow,RED,LOG)) :-
    !,
    beta(Gamma,T1[Y:=X],T2,RED),
    type_system(((X:R)::Gamma) |- A : T2,LOG).

type_system(Strategy,Gamma |- (A @ B) : T1,proof(abstraction,LOG1,LOG2,RED)) :-
    member(Strategy,check::infer_type::nil),
    !,
    type_system(Gamma |- A : (X:R) -> T2,LOG1),
    type_system(Gamma |- B : R,LOG2),
    beta(Gamma,T2[X:=B],T1,RED).

-{ Product type }-

type_system(_,Gamma |- ((X:T) * M) : type(_),proof(type_product,LOG1,LOG2)) :-
    !,
    type_system(Gamma |- T : type(_),LOG1),
    type_system(((X:T)::Gamma) |- M : type(_),LOG2).

type_system(Strategy,Gamma |- fst(A) : L,proof(fst,LOG)) :-
    member(Strategy,check::infer_type::nil),
    !,
    type_system(Gamma |- A : ((X:L) * R),LOG).

type_system(Strategy,Gamma |- snd(A) : R,proof(snd,LOG,RED)) :-
    member(Strategy,check::infer_type::nil),
    !,
    type_system(Gamma |- A : ((X:L) * R1),LOG),
    beta(Gamma,R1[X:=fst(A)],R,RED).

type_system(_,Gamma |- pair(A,B) : (X:L) * R1,proof(pair,LOG1,RED,LOG2)) :-
    !,
    type_system(Gamma |- A : L,LOG1),
    beta(Gamma,R1[X:=A],R,RED), -- Force the beta reduction / should be transparent
    type_system(Gamma |- B : R,LOG2).

-{ Sum type }-

type_system(_,Gamma |- (T1 | T2) : type(_),proof(type_sum,LOG1,LOG2)) :-
    member(Strategy,check::infer_type::nil),
    !,
    type_system(Gamma |- T1 : type(_),LOG1),
    type_system(Gamma |- T2 : type(_),LOG2).

type_system(_,Gamma |- inl(A) : (L | R),proof(inl,LOG)) :-
    !,
    type_system(Gamma |- A : L,LOG).

type_system(_,Gamma |- inr(A) : (L | R),proof(inr,LOG)) :-
    !,
    type_system(Gamma |- A : R,LOG).

type_system(Strategy,Gamma |- case(A,B,C) : T,proof(case,LOG1,LOG2,LOG3)) :-
    member(Strategy,check::infer_type::nil),
    const0(A),
    !,
    type_system(Gamma |- A : (L | R),LOG1),
    type_system(Gamma |- B : ((X:L) -> T[A:=inl(X)]),LOG2),
    type_system(Gamma |- C : ((X:R) -> T[A:=inr(X)]),LOG3).

type_system(Strategy,Gamma |- case(A,B,C) : T,proof(case,LOG1,LOG2,LOG3)) :-
    member(Strategy,check::infer_type::nil),
    !,
    type_system(Gamma |- A : (L | R),LOG1),
    type_system(Gamma |- B : ((X:L) -> T),LOG2),
    type_system(Gamma |- C : ((X:R) -> T),LOG3).

-{ Ascription type }-

type_system(Strategy,Gamma |- (A:T) : T,proof(ascription,LOG)) :-
    member(Strategy,check::infer_type::nil),
    !,
    type_system(Gamma |- A : T,LOG).

-{ Propositional equality }-

type_system(_,Gamma |- (A :=: B) : type(_),proof(equality,LOG1,LOG2)) :-
    !,
    type_system(Gamma |- A : T,LOG1),
    type_system(Gamma |- B : T,LOG2).

type_system(_,Gamma |- refl : T:=:T,proof(reflexivity)) :-
    !.

type_system(Strategy,Gamma |- subst_by(A,B) : TA,proof(subst_by,LOG1,RED,LOG2)) :-
    member(Strategy,check::infer_type::nil),
    type_system(Gamma |- B : X:=:TB,LOG1),
    const0(X),
    beta(Gamma,TA[X := TB], TAB, RED),
    not(equals(TA,TAB)),
    !,
    type_system(Gamma |- A : TAB,LOG2).

type_system(Strategy,Gamma |- subst_by(A,B) : TA,proof(subst_by,LOG1,RED,LOG2)) :-
    member(Strategy,check::infer_type::nil),
    type_system(Gamma |- B : TB:=:X,LOG1),
    const0(X),
    !,
    beta(Gamma,TA[X := TB], TAB, RED),
    type_system(Gamma |- A : TAB,LOG2).

-{ Recursive type }-

type_system(Strategy,Gamma |- rec(X:T1,M):T2,proof(type_rec,LOG1,LOG2)) :-
    member(Strategy,check::infer_type::nil),
    !,
    type_system(Gamma |- T1 : type(_),LOG1),
    type_system(((X:T1)::Gamma) |- M : T2,LOG2).

type_system(Strategy,Gamma |- A : rec(X:T,M),proof(fold,RED,LOG)) :-
    member(Strategy,check::infer_term::nil),
    !,
    beta(Gamma,M[X:=rec(X:T,M)],R,RED),
    type_system(Strategy,Gamma |- A : R,LOG).

-{ Reduction stage,error and hole }-

type_system(infer_type,Gamma |- A : T,proof(error,Gamma |- A : T)) :-
    !.

type_system(infer_term,Gamma |- A : T,proof(hole,Gamma |- A : T)) :-
    !.

type_system(check,Gamma |- A : T,proof(error,RED,Gamma |- A : T)) :-
    beta(Gamma,T,T,RED),
    not(has_proof(error,RED)),
    !.

type_system(check,Gamma |- A : T,proof(beta,RED,LOG)) :-
    beta(Gamma,T,R,RED),
    !,
    type_system(Gamma |- A : R,LOG).

type_system(check,Gamma |- A : T,proof(error,Gamma |- A : T)) :-
    !.
