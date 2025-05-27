package org.smallibs.parsec.parser

import org.smallibs.parsec.io.Reader

typealias Parser<I, A> = (Reader<I>) -> Response<I, A>
