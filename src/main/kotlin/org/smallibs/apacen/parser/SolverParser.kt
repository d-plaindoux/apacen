package org.smallibs.apacen.parser

import org.smallibs.apacen.data.CompoundTerm
import org.smallibs.apacen.data.CompoundTerm.Comparator.EQ
import org.smallibs.apacen.data.CompoundTerm.Comparator.GT
import org.smallibs.apacen.data.CompoundTerm.Comparator.GTE
import org.smallibs.apacen.data.CompoundTerm.Comparator.LT
import org.smallibs.apacen.data.CompoundTerm.Comparator.LTE
import org.smallibs.apacen.data.CompoundTerm.Cut
import org.smallibs.apacen.data.CompoundTerm.Functor
import org.smallibs.apacen.data.CompoundTerm.Relation
import org.smallibs.apacen.data.Declaration
import org.smallibs.apacen.data.Declaration.Fact
import org.smallibs.apacen.data.Declaration.Rule
import org.smallibs.apacen.data.Term
import org.smallibs.apacen.data.Term.BinOp
import org.smallibs.apacen.data.Term.Constructor
import org.smallibs.apacen.data.Term.Kind.ADD
import org.smallibs.apacen.data.Term.Kind.DIV
import org.smallibs.apacen.data.Term.Kind.GEN
import org.smallibs.apacen.data.Term.Kind.MAX
import org.smallibs.apacen.data.Term.Kind.MIN
import org.smallibs.apacen.data.Term.Kind.MUL
import org.smallibs.apacen.data.Term.Kind.SUB
import org.smallibs.apacen.data.Term.NumberLiteral
import org.smallibs.apacen.data.Term.StringLiteral
import org.smallibs.apacen.data.Term.Variable
import org.smallibs.core.IList
import org.smallibs.core.IList.Empty
import org.smallibs.parsec.parser.Parser
import org.smallibs.parsec.parser.alphaNum
import org.smallibs.parsec.parser.char
import org.smallibs.parsec.parser.charIn
import org.smallibs.parsec.parser.digit
import org.smallibs.parsec.parser.eos
import org.smallibs.parsec.parser.lazy
import org.smallibs.parsec.parser.lowerLetter
import org.smallibs.parsec.parser.map
import org.smallibs.parsec.parser.not
import org.smallibs.parsec.parser.opt
import org.smallibs.parsec.parser.optrep
import org.smallibs.parsec.parser.or
import org.smallibs.parsec.parser.rep
import org.smallibs.parsec.parser.returns
import org.smallibs.parsec.parser.satisfy
import org.smallibs.parsec.parser.string
import org.smallibs.parsec.parser.then
import org.smallibs.parsec.parser.thenLeft
import org.smallibs.parsec.parser.thenRight
import org.smallibs.parsec.parser.trace
import org.smallibs.parsec.parser.upperLetter

object SolverParser {

    private fun skip(): Parser<Char, Unit> =
        charIn(' ', '\n', '\t', '\r').optrep.thenRight(returns(Unit))

    private fun <A> skipped(p: Parser<Char, A>): Parser<Char, A> =
        skip().thenRight(p).thenLeft(skip())

    private fun string(): Parser<Char, String> =
        char('"').thenRight(not(char('"')).optrep).thenLeft(char('"')) map { it.joinToString("") }

    private fun natural(): Parser<Char, String> =
        digit().then(digit().or(char('_')).optrep) map {
            (listOf(it.first) + it.second).joinToString("").replace("_", "")
        }

    private fun integer(): Parser<Char, String> =
        (charIn('+', '-').opt map { it ?: '+' }).then(natural()) map { it.first + it.second }

    private fun double(): Parser<Char, Double> =
        integer().then(char('.').thenRight(natural()).opt map {
            it ?: ""
        }) map { (it.first + '.' + it.second).toDouble() }

    private fun token(c: Char): Parser<Char, Char> =
        trace(skipped(char(c)), "$c")

    private fun token(s: String): Parser<Char, String> =
        trace(
            skipped(string(s)),
            s
        )

    private fun ident(): Parser<Char, String> =
        trace(
            skipped(lowerLetter().then(alphaNum().or(charIn('_')).optrep)).map {
                it.first + it.second.joinToString("")
            }.satisfy { listOf("min", "max").contains(it).not() },
            "ident"
        )

    private fun special(): Parser<Char, Char> =
        charIn('@', '&', ':', '|', '$', '+', '-', '*', '/', '%', '?', '>', '<', '=', '~', ';')

    private fun operator(vararg rejected: String): Parser<Char, String> =
        trace(
            skipped(special().rep).map {
                it.joinToString("")
            }.satisfy {
                !rejected.contains(it)
            },
            "operator except {${rejected.joinToString(",")}}"
        )

    private fun variableName(): Parser<Char, String> =
        trace(
            skipped(upperLetter().then(alphaNum().or(charIn('_')).optrep)) map {
                it.first + it.second.joinToString("")
            },
            "variable name"
        )

    private fun anonymous(): Parser<Char, String?> =
        trace(
            skipped(char('_') map { null }
            ),
            "variable name"
        )

    private fun <A> list(p: Parser<Char, A>, sep: Char = ',', strict: Boolean = false): Parser<Char, IList<A>> =
        p.then(token(sep).thenRight(p).optrep(strict)).opt map {
            it?.let { IList.from(listOf(it.first) + it.second) } ?: Empty
        }

    // Argument

    fun variable(): Parser<Char, Term> =
        trace(
            variableName().or(anonymous()) map { Variable(it) },
            "variable"
        )

    fun number(): Parser<Char, Term> =
        trace(
            skipped(double()).map { NumberLiteral(it) },
            "number"
        )

    fun stringLiteral(): Parser<Char, Term> =
        trace(
            skipped(string()).map { StringLiteral(it) },
            "string"
        )

    private fun group(): Parser<Char, Term> =
        trace(
            token('(').thenRight(lazy { term() }).thenLeft(token(')')),
            "group"
        )

    fun function(): Parser<Char, BinOp> =
        trace(
            (token("min").or(token("max"))).then(
                token('(').thenRight(
                    term().thenLeft(token(',')).then(term()).thenLeft(token(')'))
                )
            ) map {
                BinOp(if (it.first == "min") MIN else MAX, it.second.first, it.second.second)
            },
            "function"
        )

    fun binop(vararg rejected: String): Parser<Char, Pair<Pair<Term, String>, Term>> =
        (token('(').thenRight(lazy { binop(*rejected) }).thenLeft(token(')'))).or(
            term().then(operator(*rejected)).then(term())
        )

    fun expr(): Parser<Char, Term> =
        trace(
            function().or(constructor()).or(number()).or(stringLiteral()).or(variable()).or(group())
                .then(operator("=", "<", "<=", ">", ">=").then(lazy { term() }).opt) map {
                it.second?.let { second ->
                    when (second.first) {
                        "+" -> BinOp(ADD, it.first, second.second)
                        "-" -> BinOp(SUB, it.first, second.second)
                        "*" -> BinOp(MUL, it.first, second.second)
                        "/" -> BinOp(DIV, it.first, second.second)
                        else -> BinOp(GEN(second.first), it.first, second.second)
                    }

                } ?: it.first
            },
            "expression"
        )

    fun constructor(): Parser<Char, Term> =
        trace(
            ident().then(token('(').thenRight(list(term(), strict = true).thenLeft(token(')'))).opt) map {
                Constructor(it.first, it.second ?: Empty)
            },
            "constructor"
        )

    fun block(): Parser<Char, (Term) -> Term> =
        trace(
            (token('[').thenRight(term()).thenLeft(token(']')) map {
                { t: Term -> Constructor("[]", IList.of(t, it)) }
            }),
            "block"
        )

    fun term(): Parser<Char, Term> =
        trace(
            (lazy { expr() }.or(lazy { constructor() })).then((lazy { block() }).opt) map {
                when (val f = it.second) {
                    null -> it.first
                    else -> f(it.first)
                }
            },
            "term"
        )

    fun functor(): Parser<Char, Functor> =
        trace(
            (ident().then(token('(').thenRight(list(term(), strict = true).thenLeft(token(')'))).opt) map {
                Functor(it.first, it.second ?: Empty)
            }),
            "functor"
        )

    fun equation(): Parser<Char, CompoundTerm> =
        trace(
            binop("+", "-", "*", "/").map {
                when (it.first.second) {
                    "=" -> Relation(EQ, it.first.first, it.second)
                    "<" -> Relation(LT, it.first.first, it.second)
                    ">" -> Relation(GT, it.first.first, it.second)
                    "<=" -> Relation(LTE, it.first.first, it.second)
                    ">=" -> Relation(GTE, it.first.first, it.second)
                    else -> Functor(it.first.second, IList.of(it.first.first, it.second))
                }
            },
            "equation"
        )

    fun cut(): Parser<Char, Cut> =
        trace(token('!').map { Cut() }, "cut")

    fun compound(): Parser<Char, CompoundTerm> =
        trace(
            comments().thenRight(functor().or(equation()).or(cut())).thenLeft(comments()),
            "compound term"
        )

    // Comments

    private fun commentBloc(): Parser<Char, Unit> =
        string("-{").then(not(string("}-")).optrep).then(string("}-")) map { }

    private fun commentLine(): Parser<Char, Unit> =
        string("--").then(not(char('\n')).optrep) map { }

    private fun comment(): Parser<Char, Unit> =
        skipped(commentBloc().or(commentLine()))

    private fun comments(): Parser<Char, Unit> =
        comment().optrep map { }

    // rule

    fun rule(): Parser<Char, Rule> =
        trace(
            functor().then(token(":-").thenRight(list(compound(), strict = true)).opt)
                .thenLeft(token('.')) map {
                Rule(it.first, it.second ?: Empty)
            },
            "rule"
        )

    fun goal(): Parser<Char, Fact> =
        trace(
            token("?-").thenRight(list(compound(), strict = true)).thenLeft(token('.')) map {
                Fact(it)
            },
            "fact"
        )

    fun program(): Parser<Char, IList<Declaration>> =
        trace(
            skipped((comment() map { null }).or(goal()).or(rule()).optrep(true)).thenLeft(eos()).map {
                IList.from(it.filterNotNull())
            },
            "program"
        )

}