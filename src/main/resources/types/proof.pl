has_proof(K,proof(K))             :- !.
has_proof(K,proof(K,_))           :- !.
has_proof(K,proof(K,_,_))         :- !.
has_proof(K,proof(K,_,_,_))       :- !.
has_proof(K,proof(K,_,_,_,_))     :- !.
has_proof(K,proof(_,R))           :- !,has_proof(K,R).
has_proof(K,proof(_,R1,R2))       :- !,or(has_proof(K,R1),has_proof(K,R2)).
has_proof(K,proof(_,R1,R2,R3))    :- !,or(has_proof(K,R1),or(has_proof(K,R2),has_proof(K,R3))).
has_proof(K,proof(_,R1,R2,R3,R4)) :- !,or(has_proof(K,R1),or(has_proof(K,R2),or(has_proof(K,R3),has_proof(K,R4)))).

display_proofs(K,proof(K))             :- !.
display_proofs(K,proof(K,A))           :- !, println(A).
display_proofs(K,proof(K,A,B))         :- !, println(A,B).
display_proofs(K,proof(K,A,B,C))       :- !, println(A,B,C).
display_proofs(K,proof(K,A,B,C,D))     :- !, println(A,B,C,D).
display_proofs(K,proof(_,R))           :- !,display_proofs(K,R).
display_proofs(K,proof(_,R1,R2))       :- !,or(display_proofs(K,R1),display_proofs(K,R2)).
display_proofs(K,proof(_,R1,R2,R3))    :- !,or(display_proofs(K,R1),or(display_proofs(K,R2),display_proofs(K,R3))).
display_proofs(K,proof(_,R1,R2,R3,R4)) :- !,or(display_proofs(K,R1),or(display_proofs(K,R2),or(display_proofs(K,R3),display_proofs(K,R4)))).
