-- Strategy: l2, core
-- All = core + commutativity etc.

-{
    Inequations rules
        X + Y <: Z ~~> X <: Z - Y
        X - Y <: Z ~~> X <: Z + Y
        X * Y <: Z ~~> (X <: Z / Y /\ Y > 0) \/ (X :> Z / Y /\ Y < 0)
        X / Y <: Z ~~> (X <: Z * Y /\ Y > 0) \/ (X :> Z * Y /\ Y < 0)

    Expression rules:
        X + Y ~~> Y + X                                 -- Commutativity
        X * Y ~~> Y * X                                 -- Commutativity

        X * (Y + Z) ~~> (X * Y) + (X * Z)               -- Distribution
        X * (Y - Z) ~~> (X * Y) - (X * Z)               -- Distribution

        (X + Y) + Z ~~> X + (Y + Z)                     -- Associativity
        (X * Y) * Z ~~> X * (Y * Z)                     -- Associativity

        0 + X == Z ~~> X == Z                           -- Simplification neutral-+)
        1 * X == Z ~~> X == Z                           -- Simplification neutral-*)
        X / 1 == Z ~~> X == Z                           -- Simplification neutral-/)
        0 * X == X ~~> 0 == Z                           -- Simplification greedy-*)
}-

-{
    Logical rules (intuitionist or classical? )
        A \/ B        ~~> B \/ A
        A /\ B        ~~> B /\ A
        A /\ (B /\ C) ~~> (A /\ B) /\ C
        A \/ (B \/ C) ~~> (A \/ B) \/ C
        A /\ (B \/ C) ~~> (A /\ B) \/ (A /\ C)
        A \/ (B /\ C) ~~> (A \/ B) /\ (A \/ C)
        true /\ A     ~~> A
        false /\ A    ~~> false
        true \/ A     ~~> true
        false \/ A    ~~> A
}-

rewrite(l2, A \/ B, B \/ A).
rewrite(l2, A /\ B, B /\ A).
rewrite(l2, A /\ (B /\ C), (A /\ B) /\ C).
rewrite(l2, A \/ (B \/ C), (A \/ B) \/ C).
rewrite(l2, A /\ (B \/ C), (A /\ B) \/ (A /\ C)).
rewrite(l2, A \/ (B /\ C), (A \/ B) /\ (A \/ C)).

rewrite(l1, true /\ A, A).
rewrite(l1, false /\ A, false).
rewrite(l1, true \/ A, true).
rewrite(l1, false \/ A, A).

-{
    Equations rules
        X + Y == Z ~~> X == Z - Y
        X - Y == Z ~~> X == Z + Y
        X * Y == Z ~~> X == Z / Y /\ Y <> 0
        X / Y == Z ~~> X == Z * Y
}-

rewrite(l2, (X + Y) == Z, X == (Z - Y)).
rewrite(l2, (X - Y) == Z, X == (Z + Y)).
rewrite(l2, (X * Y) == Z, (X == (Z / Y)) /\ (Y <> 0)).
rewrite(l2, (X / Y) == Z, X == (Z * Y)).

rewrite(X,Z) :-
    rewrite(l1,X,Y),
    rewrite(l2,Y,T),
    rewrite(T,Z).
