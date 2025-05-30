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

type_system(Gamma |- E : T,proof(stopped,Gamma |- E : T)) :-
    unbound(E), unbound(T),
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
    member(Strategy,check::infer_type::nil),
    L1 >= 0, L2 = L1 + 1,
    !.

type_system(infer_term,Gamma |- type(L1) : type(L2),proof(type(type))) :-
    L2 >= 1, L2 = L1 + 1,
    !.

-{ Hypothesis }-

type_system(_,Gamma |- A : T,proof(hypothesis)) :-
    member(A : T,Gamma),
    !.

-{ Function types }-

type_system(_,Gamma |- ((X:T) -> M) : type_system(_),proof(arrow,LOG1,LOG2)) :-
    !,
    type_system(Gamma |- T : type_system(_),LOG1),
    type_system(Gamma |- M : type_system(_),LOG2).

type_system(_,Gamma |- (X => A) : ((Y:R) -> T1),proof(arrow,RED,LOG)) :-
    !,
    beta(T1[Y:=X],T2,RED),
    type_system(((X:R)::Gamma) |- A : T2,LOG).

type_system(Strategy,Gamma |- (A @ B) : T1,proof(abstraction,LOG1,LOG2,RED)) :-
    member(Strategy,check::infer_type::nil),
    !,
    type_system(Gamma |- A : (X:R) -> T2,LOG1),
    type_system(Gamma |- B : R,LOG2),
    beta(T2[X:=B],T1,RED).

-{ Product type }-

type_system(_,Gamma |- ((X:T) * M) : type_system(_),proof(type_product,LOG1,LOG2)) :-
    member(Strategy,check::infer_type::nil),
    !,
    type_system(Gamma |- T : type_system(_),LOG1),
    type_system(Gamma |- M : type_system(_),LOG2).

type_system(Strategy,Gamma |- fst(A) : L,proof(fst,LOG)) :-
    member(Strategy,check::infer_type::nil),
    !,
    type_system(Gamma |- A : ((X:L) * R),LOG).

type_system(Strategy,Gamma |- snd(A) : R,proof(snd,LOG,RED)) :-
    member(Strategy,check::infer_type::nil),
    !,
    type_system(Gamma |- A : ((X:L) * R1),LOG),
    beta(R1[X:=fst(A)],R,RED).

type_system(_,Gamma |- pair(A,B) : (X:L) * R1,proof(pair,LOG1,LOG2)) :-
    !,
    type_system(Gamma |- A : L,LOG1),
    type_system(Gamma |- B : R1[X:=A],LOG2).

-{ Sum type }-

type_system(_,Gamma |- inl(A) : (L | R),proof(inl,LOG)) :-
    !,
    type_system(Gamma |- A : L,LOG).

type_system(_,Gamma |- inr(A) : (L | R),proof(inr,LOG)) :-
    !,
    type_system(Gamma |- A : R,LOG).

type_system(Strategy,Gamma |- case(A,B,C) : T,proof(case,LOG1,LOG2,LOG3)) :-
    member(Strategy,check::infer_type::nil), eval(const0(A)),
    !,
    type_system(Gamma |- A : (L | R),LOG1),
    type_system(Gamma |- B : ((X:L) -> T[A:=inl(X)]),LOG2),
    type_system(Gamma |- C : ((X:R) -> T[A:=inr(X)]),LOG3).

type_system(Strategy,Gamma |- case(A,B,C) : T,proof(case,LOG1,LOG2,LOG3)) :-
    member(Strategy,check::infer_type::nil),
    !,
    type_system(Gamma |- A : (L | R),LOG1),
    type_system(Gamma |- B : ((X:L) -> T),LOG2),
    type_system(Gamma |- C : ((Y:R) -> T),LOG3).

-{ Ascription type }-

type_system(Strategy,Gamma |- (A:T) : T,proof(ascription,LOG)) :-
    member(Strategy,check::infer_type::nil),
    !,
    type_system(Gamma |- A : T,LOG).

-{ Propositional equality }-

type_system(Strategy,Gamma |- (A :=: B) : type(_),proof(equality,LOG1,LOG2)) :-
    member(Strategy,check::infer_type::nil),
    !,
    type_system(Gamma |- A : T,LOG1),
    type_system(Gamma |- B : T,LOG2).

type_system(Strategy,Gamma |- refl : (T :=: T),proof(reflexivity)) :-
    member(Strategy,check::infer_type::nil),
    !.

type_system(Strategy,Gamma |- subst_by(A,B) : TA,proof(subst_by,LOG1,LOG2)) :-
    member(Strategy,check::infer_type::nil),
    !,
    type_system(Gamma |- B : X :=: TB,LOG1),
    type_system(Gamma |- A : TA[X := TB],LOG2).

-{ Reduction stage,error and stopped }-

type_system(infer_type,Gamma |- A : T,proof(error,Gamma |- A : T)) :-
    !.

type_system(infer_term,Gamma |- A : T,proof(stopped,Gamma |- A : T)) :-
    !.

type_system(check,Gamma |- A : T,proof(error,Gamma |- A:T)) :-
    beta(T,T,_),
    !.

type_system(check,Gamma |- A : T,proof(beta,RED,LOG)) :-
    beta(T,R,RED),
    !,
    type_system(Gamma |- A : R,LOG).

type_system(check,Gamma |- A : T,proof(error,Gamma |- A : T)) :-
    !.
