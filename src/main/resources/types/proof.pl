has_proof(K,proof(K))             :- !.
has_proof(K,proof(K,_))           :- !.
has_proof(K,proof(K,_,_))         :- !.
has_proof(K,proof(K,_,_,_))       :- !.
has_proof(K,proof(K,_,_,_,_))     :- !.
has_proof(K,proof(_,R))           :- !,has_proof(K,R).
has_proof(K,proof(_,R1,R2))       :- !,or(has_proof(K,R1),has_proof(K,R2)).
has_proof(K,proof(_,R1,R2,R3))    :- !,or(has_proof(K,R1),or(has_proof(K,R2),has_proof(K,R3))).
has_proof(K,proof(_,R1,R2,R3,R4)) :- !,or(has_proof(K,R1),or(has_proof(K,R2),or(has_proof(K,R3),has_proof(K,R4)))).

