-{ Program validation }-

valid(R, Diagnostic) :-
    valid(R,Proof,Diagnostic).

valid(R,Proof,Diagnostic) :-
    program(R,Program),
    valid(nil,Program,_,Proof),
    diagnostic(Proof,Diagnostic).

valid(Gamma,P1 ; P2,Gamma2,proofs(Proof1,Proof2)) :-
    !,
    valid(Gamma, P1, Gamma1, Proof1),
    valid(Gamma1, P2, Gamma2, Proof2).

valid(Gamma,P,Gamma2,Proof) :-
    time_in_millis(T0),
    valid_(Gamma,P,Gamma2,Proof),
    time_in_millis(T1),
    println("[", T1-T0, "ms ]", P).

valid_(Gamma,Id :: Type,(Id : Type)::Gamma,Proof) :-
    !,
    const0(Id),
    type(Gamma |- Type : type(_),Proof).

valid_(Gamma,Id := Term,(Id := Term)::Gamma,Proof) :-
    const0(Id),
    type(Gamma |- Id : T, IdProof),
    not(has_proof(error,IdProof)),
    !,
    type(Gamma |- Term : T,Proof).

valid_(Gamma,ID := Term,(Id := Term)::Gamma,IdProof) :-
    !,
    type(Gamma |- Id : T, IdProof).

diagnostic(Proof,"Program is valid") :-
    not(has_proofs(error,Proof)),
    !.

diagnostic(Proof,"program has errors").

has_proofs(K,P) :-
    unbound(P),
    !,
    has_proof(K,P).

has_proofs(K,proofs(P1,P2)) :-
    or(has_proof(K,P1),has_proof(K,P2)).

display_proofs(K,proofs(P1,P2)) :-
    or(display_proof(K,P1),display_proofs(K,P2)).

-{

    # Examples

    ```
    ?- Program =
        proof :: 1 :=: "1",
        valid(Program, Proof, "program has errors").
    ```


    ```
    ?- Program =
        id :: ((x:type) -> x -> x);
        id := (t => x => x),
        valid(Program, Proof, Diagnostic).
    ```

    ```
    ?- Program =
        p :: ((x:type) * x);
        p := pair(int, 1),
        valid(Program, Proof, Diagnostic).
    ```

    ## Boolean

    ```
    ?- Program =
        unit :: type;
        one  :: unit;
        bool :: type;
        bool := (unit | unit);
        true :: bool;
        true := inl(one);
        false :: bool;
        false := inl(one),
        valid(Program, Proof, Diagnostic).
    ```

    ## Recursive list data type

    ```
    ?- Program =
        unit :: type;
        one  :: unit;
        list :: ((x:type) -> type);
        list := (x => rec(l:type,unit | ((e:x) * l)));
        nil  :: ((x:type) -> (list @ x));
        nil  := (x => inl(one));
        cons :: ((x:type) -> x -> (list @ x) -> (list @ x));
        cons := (x => h => t => inr(pair(h,t))),
        valid(Program, Proof, Diagnostic).
    ```

    ## Recursive list data type v2

    ```
    ?- Program =
        unit :: type;
        one  :: unit;
        list :: ((x:type) -> type);
        list := rec(l:type -> type,x => (unit | (x * (l @ x))));
        nil  :: ((x:type) -> (list @ x));
        nil  := (x => inl(one));
        cons :: ((x:type) -> x -> (list @ x) -> (list @ x));
        cons := (x => h => t => inr(pair(h,t))),
        valid(Program, Proof, Diagnostic).
    ```

    ## GADT

    ```
    ?- Program =
        expr :: (type -> type);
        expr := rec(e:type -> type,x =>
                    (int * (x :=: int))
                  | ((e @ x))
                );
        num  :: ((t:type) -> (t:=:int) -> int -> (expr @ t));
        num  := (t => p => a => inl(pair(a,p)));
        eval :: ((t:type) -> (expr @ t) -> t);
        eval := (t => e => case(e,i => subst_by(fst(i),snd(i)),e => eval @ t @ e)),
        valid(Program, Diagnostic).
    ```

    ```
    ?- Program =
        expr :: (type -> type);
        expr := rec(e:type -> type,x =>
                    (int * (x :=: int))
                  | ((e @ int) * (e @ int) * (x :=: int))
                );
        num  :: ((t:type) -> (t:=:int) -> int -> (expr @ t));
        num  := (t => p => a => inl(pair(a,p)));
        add  :: ((t:type) -> (t:=:int) -> (expr @ int) -> (expr @ int) -> (expr @ t));
        add  := (t => p => a => b => inr(pair(a,pair(b,P)))),
        valid(Program, Proof, Diagnostic).
    ```

    ```
    ?- Program =
        unit :: type;
        one  :: unit;
        bool :: type;
        bool := (unit | unit);
        expr :: (type -> type);
        expr := rec(e:type -> type,x =>
                    (int * (x :=: int))
                  | ((e @ int) * (e @ int) * (x :=: int))
                  | ((e @ bool) * (e @ x) * (e @ x))
                );
        num  :: ((t:type) -> (t:=:int) -> int -> (expr @ t));
        add  :: ((t:type) -> (t:=:int) -> (expr @ int) -> (expr @ int) -> (expr @ t));
        iff  :: ((t:type) -> (expr @ bool) -> (expr @ t) -> (expr @ t) -> (expr @ t));
        num  := (t => p => a => inl(pair(a,p)));
        add  := (t => p => a => b => inr(inl(pair(a,pair(b,p)))));
        iff  := (t => b => t => f => inr(inr(pair(b,pair(t,f))))),
        valid(Program, Diagnostic).
    ```

    ```
    ?- Program =
        unit :: type;
        one  :: unit;
        bool :: type;
        bool := (unit | unit);
        expr :: (type -> type);
        expr := rec(e:type -> type,x =>
                    (int * (x :=: int))
                  | ((e @ int) * (e @ int) * (x :=: int))
                  | ((e @ bool) * (e @ x) * (e @ x))
                );
        num  :: ((t:type) -> (t:=:int) -> int -> (expr @ t));
        num  := (t => p => a => inl(pair(a,p)));
        add  :: ((t:type) -> (t:=:int) -> (expr @ int) -> (expr @ int) -> (expr @ t));
        add  := (t => p => a => b => inr(inl(pair(a,pair(b,p)))));
        iff  :: ((t:type) -> (expr @ bool) -> (expr @ t) -> (expr @ t) -> (expr @ t));
        iff  := (t => b => t => f => inr(inr(pair(b,pair(t,f))))),
        valid(Program, Proof, Diagnostic).
    ```

    ```
    ?- Program =
        unit :: type;
        one  :: unit;
        bool :: type;
        bool := (unit | unit);
        expr :: (type -> type);
        expr := rec(e:type -> type,x =>
                    (int * (x :=: int))
                  | ((e @ int) * (e @ int) * (x :=: int))
                  | ((e @ bool) * (e @ x) * (e @ x))
                  | (bool * (x :=: bool))
                );
        num  :: ((t:type) -> (t:=:int) -> int -> (expr @ t));
        add  :: ((t:type) -> (t:=:int) -> (expr @ int) -> (expr @ int) -> (expr @ t));
        iff  :: ((t:type) -> (expr @ bool) -> (expr @ t) -> (expr @ t) -> (expr @ t));
        cond :: ((t:type) -> (t:=:bool) -> bool -> (expr @ t));
        num  := (t => p => a => inl(pair(a,p)));
        add  := (t => p => a => b => inr(inl(pair(a,pair(b,p)))));
        iff  := (t => b => t => f => inr(inr(inl(pair(b,pair(t,f))))));
        cond := (t => p => a => inr(inr(inr(pair(a,p))))),
        valid(Program, Diagnostic).
    ```
}-