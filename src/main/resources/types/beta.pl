-{
    Term betaRed betaRed

    (x => y) @ z        = y[x:=z]
    fst(pair(a,b))      = a
    snd(pair(a,b))      = b
    case(inl(a),x=>y,_) = y[x:=a]
    case(inr(a),_,x=>y) = y[x:=a]
    T[X:=Y] apply substitution
}-

beta(Gamma,A,B,L)                                           :- betaRed(Gamma,A,B,L),!.
beta(Gamma,A,B,proof(error))                                :- !.

-{ Internal beta reduction }-

betaRed(Gamma,A,B,L)                                         :- -- println(beta, gamma |- A ~> B),
                                                                betaRed1(Gamma,A,B,L).

betaRed1(Gamma,A,B,proof(beta_free))                         :- unbound(A),!,equals(A,B).
betaRed1(Gamma,X @ Y,R,proof(beta_apply,RED1,RED2))          :- betaRed(Gamma,X,A => B,RED1),!,betaRed(Gamma,B[A:=Y],R,RED2).
betaRed1(Gamma,fst(X),R,proof(beta_fst,RED1,RED2))           :- betaRed(Gamma,X,pair(Y,_),RED1),!,betaRed(Gamma,Y,R,RED2).
betaRed1(Gamma,snd(X),R,proof(beta_snd,RED1,RED2))           :- betaRed(Gamma,X,pair(_,Y),RED1),!,betaRed(Gamma,Y,R,RED2).
betaRed1(Gamma,case(X,Y,Z),R,proof(beta_inl,RED1,RED2,RED3)) :- betaRed(Gamma,X,inl(C),RED1),betaRed(Gamma,Y,A => B,RED2),!,betaRed(Gamma,B[A:=C],R,RED3).
betaRed1(Gamma,case(X,Y,Z),R,proof(beta_inr,RED1,RED2,RED3)) :- betaRed(Gamma,X,inr(C),RED1),betaRed(Gamma,Z,A => B,RED2),!,betaRed(Gamma,B[A:=C],R,RED3).
betaRed1(Gamma,T[X:=Y],S,proof(beta_subst,SUBS,RED))         :- subst(T[X:=Y],R,SUBS),not(equals(T[X:=Y],R)),!,betaRed(Gamma,R,S,RED).
betaRed1(Gamma,rec(X:T,A),B,proof(rec,SUBS))                 :- bound(B),!,subst(A[X:=rec(X:T,A)],B,SUBS).
betaRed1(Gamma,X,A,RED)                                      :- const0(X),member(X:=M,Gamma),!,betaRed(Gamma,M,A,RED).
betaRed1(Gamma,A,A,proof(beta_no_red))                       :- !.

