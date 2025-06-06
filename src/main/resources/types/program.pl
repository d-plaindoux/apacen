-{ Program validation }-

valid(R, Diagnostic) :-
    valid(R,Proof,Diagnostic).

valid(R,Proof,Diagnostic) :-
    program(R,Program),
    valid(nil,Program,_,Proof),
    diagnostic(Proof,Diagnostic).

valid(Gamma,P1 ; P2,Gamma2,proofs(Proof1,Proof2)) :-
    valid(Gamma, P1, Gamma1, Proof1),
    valid(Gamma1, P2, Gamma2, Proof2).

valid(Gamma,Id :: Type,(Id : Type)::Gamma,Proof) :-
    !,
    const0(Id),
    type(Gamma |- Type : type(_),Proof).

valid(Gamma,Id := Term,(Id := Term)::Gamma,Proof) :-
    const0(Id),
    type(Gamma |- Id : T, IdProof),
    not(has_proof(error,IdProof)),
    !,
    type(Gamma |- Term : T,Proof).

valid(Gamma,ID := Term,(Id := Term)::Gamma,IdProof) :-
    !,
    type(Gamma |- Id : T, IdProof).

diagnostic(Proof,"Program is valid") :-
    not(has_proof(error,Proof)),
    !.

has_proof(K,proofs(P1,P2)) :-
    or(has_proof(K,P1),has_proof(K,P2)).

diagnostic(Proof,"program has errors").

display_proofs(K,proofs(P1,P2)) :-
    or(display_proof(K,P1),display_proof(K,P2)).

-{

    # Examples

    ```
    ?- Program =
        proof :: 1 :=: "1",
        valid(Program).
    ```


    ```
    ?- Program =
        id :: ((x:type(0)) -> x -> x);
        id := (t => x => t),
        valid(Program).
    ```

    ```
    ?- Program =
        p :: ((x:type(0)) * x);
        p := pair(int, 1),
        valid(Program,Proof).
    ```

    ## Boolean

    ```
    ?- Program =
        unit :: type(0);
        one  :: unit;
        bool :: type(0);
        bool := (unit | unit);
        true :: bool;
        true := inl(one),
        valid(Program).
    ```

    ## Recursive list data type

    ```
    ?- Program =
        unit :: type(0);
        one  :: unit;
        list :: ((x:type(0)) -> type(0));
        list := (x => rec(l:type(0),unit | ((e:x) * l)));
        nil  :: ((x:type(0)) -> (list @ x));
        nil  := (x => inl(one));
        cons :: ((x:type(0)) -> x -> (list @ x) -> (list @ x));
        cons := (x => h => t => inr(pair(h,t))),
        valid(Program, Proof, Diagnostic).
    ```

    ## Recursive list data type v2

    ```
    ?- Program =
        unit :: type(0);
        one  :: unit;
        list :: ((x:type(0)) -> type(0));
        list := rec(l:type(0) -> type(0),x => (unit | (x * (l @ x))));
        nil  :: ((x:type(0)) -> (list @ x));
        nil  := (x => inl(one));
        cons :: ((x:type(0)) -> x -> (list @ x) -> (list @ x));
        cons := (x => h => t => inr(pair(1,t))),
        valid(Program, Proof, Diagnostic).
    ```

    ## GADT

    ```
    ?- Program =
        expr :: (type(0) -> type(0));
        expr := rec(e:type(0) -> type(0),x => (int * (x :=: int)) | ((e @ int) * (e @ int) * (x :=: int)));
        num  :: ((t:type) -> (t:=:int) -> int -> (expr @ t));
        num  := (t => p => a => inl(pair(a,p))),
        valid(Program, Proof, Diagnostic).
    ```

    ```
    ?- Program =
        expr :: (type(0) -> type(0));
        expr := rec(e:type(0) -> type(0),x => (int * (x :=: int)) | ((e @ int) * (e @ int) * (x :=: int)));
        num  :: ((t:type) -> (t:=:int) -> int -> (expr @ t));
        num  := (t => p => a => inl(pair(a,p)));
        add  :: ((t:type) -> (t:=:int) -> (expr @ int) -> (expr @ int) -> (expr @ t));
        add  := (t => p => a => b => inr(pair(a,pair(b,P)))),
        valid(Program, Proof, Diagnostic).
    ```

}-