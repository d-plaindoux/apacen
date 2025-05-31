-{
    Term betaRed betaRed

    (x => y) @ z        = y[x:=z]
    fst(pair(a,b))      = a
    snd(pair(a,b))      = b
    case(inl(a),x=>y,_) = y[x:=a]
    case(inr(a),_,x=>y) = y[x:=a]
    T[X:=Y] apply substitution
}-

beta(A,B,L)                                           :- betaRed(A,B,L),!.
beta(A,B,proof(error))                                :- !.

-{ Internal beta reduction }-

betaRed(A,B,proof(beta_free))                         :- unbound(A),!,equals(A,B).
betaRed(X @ Y,R,proof(beta_apply,RED1,RED2))          :- betaRed(X,A => B,RED1),!,betaRed(B[A:=Y],R,RED2).
betaRed(fst(X),R,proof(beta_fst,RED1,RED2))           :- betaRed(X,pair(Y,_),RED1),!,betaRed(Y,R,RED2).
betaRed(snd(X),R,proof(beta_snd,RED1,RED2))           :- betaRed(X,pair(_,Y),RED1),!,betaRed(Y,R,RED2).
betaRed(case(X,Y,Z),R,proof(beta_inl,RED1,RED2,RED3)) :- betaRed(X,inl(C),RED1),betaRed(Y,A => B,RED2),!,betaRed(B[A:=C],R,RED3).
betaRed(case(X,Y,Z),R,proof(beta_inr,RED1,RED2,RED3)) :- betaRed(X,inr(C),RED1),betaRed(Z,A => B,RED2),!,betaRed(B[A:=C],R,RED3).
betaRed(T[X:=Y],S,proof(beta_subst,SUBS,RED))         :- subst(T[X:=Y],R,SUBS),not(equals(T[X:=Y],R)),!,betaRed(R,S,RED).
betaRed(A,A,proof(beta_no_red))                       :- !.

