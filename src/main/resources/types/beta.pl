-{
    Term beta reduction

    (x => y) @ z        = y[x:=z]
    fst(pair(a,b))      = a
    snd(pair(a,b))      = b
    case(inl(a),x=>y,_) = y[x:=a]
    case(inr(a),_,x=>y) = y[x:=a]
    T[X:=Y] apply substitution 
}-

beta(A,B,proof(beta_free))                         :- unbound(A),!,equals(A,B).
beta(X @ Y,R,proof(beta_apply,RED1,RED2))          :- beta(X,A => B,RED1),!,beta(B[A:=Y],R,RED2).
beta(fst(X),R,proof(beta_fst,RED1,RED2))           :- beta(X,pair(Y,_),RED1),!,beta(Y,R,RED2).
beta(snd(X),R,proof(beta_snd,RED1,RED2))           :- beta(X,pair(_,Y),RED1),!,beta(Y,R,RED2).
beta(case(X,Y,Z),R,proof(beta_inl,RED1,RED2,RED3)) :- beta(X,inl(C),RED1),beta(Y,A => B,RED2),!,beta(B[A:=C],R,RED3).
beta(case(X,Y,Z),R,proof(beta_inr,RED1,RED2,RED3)) :- beta(X,inr(C),RED1),beta(Z,A => B,RED2),!,beta(B[A:=C],R,RED3).
beta(T[X:=Y],S,proof(beta_subst,SUBS,RED))         :- subst(T[X:=Y],R,SUBS),not(equals(T[X:=Y],R)),!,beta(R,S,RED).
beta(A,A,proof(beta_no_red))                       :- !.
