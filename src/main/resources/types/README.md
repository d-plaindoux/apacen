# Dependent type system

In this module we propose a type system dedicated to a dependant type programming language.

## Grammar

### Premise

```
goal ::=
    G |- E : E
```    

### Hypothesis

```
G ::=
    nil
    (ident:E) :: G
```

### Term

```
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

    E :=: E
    refl
    subst_by(E,E)

    (*TODO*)
    { ident : E, ... }
    { ident = E, ... }

    mu(ident,E)
```
