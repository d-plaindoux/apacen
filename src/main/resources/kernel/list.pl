-- List := nil | e::List

concat(nil,L2,L2).
concat(H::L1,L2,H::L3) :- concat(L1,L2,L3).

reverse(nil, nil).
reverse(X::L, R) :- reverse(L,RL), concat(RL, X::nil, R).

member(X, X::_).
member(X, _::H::T) :- member(X, H::T).
