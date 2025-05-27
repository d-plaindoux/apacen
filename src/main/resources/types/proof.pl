has_proof(K, proof(K,_))        :- !.
has_proof(K, proof(K,_,_))      :- !.
has_proof(K, proof(K,_,_,_))    :- !.
has_proof(K,proof(_,R))         :- !, has_error(K,R).
has_proof(K,proof(_,R1,R2))     :- !, has_error(K,R1,R2).
has_proof(K,proof(_,R1,R2,R3))  :- !, has_error(K,R1,R2,R3).

