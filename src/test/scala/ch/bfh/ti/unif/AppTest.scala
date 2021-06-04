package ch.bfh.ti.unif

/*
ScalaTest facilitates different styles of testing by providing traits you can mix
together to get the behavior and syntax you prefer.  A few examples are
included here.  For more information, visit:

http://www.scalatest.org/

One way to use ScalaTest is to help make JUnit or TestNG tests more
clear and concise. Here's an example:
 */

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class AppTest extends AnyFlatSpec with should.Matchers {
  "The parser" should "create a constant from a lowercase string" in {
    val input = "myConst"
    val term = Parser.parseInput(input)

    // Test by TypeCheck
    term should be(Some(Constant("myConst")))

    // Test by toString()
    s"${term.get}" should be(input)
  }

  "The parser" should "create a variable from an uppercase string" in {
    val input = "MyVar"
    val term = Parser.parseInput(input)

    // Test by TypeCheck
    term should be(Some(Variable("MyVar")))

    // Test by toString()
    s"${term.get}" should be(input)
  }

  "The parser" should "create a compound from a lowercase string with brackets" in {
    val input = "knows(peter, X)"
    val term = Parser.parseInput(input)

    // Test by TypeCheck
    term should be(Some(
      Compound(Constant("knows"), Arguments(List(
        Constant("peter"),
        Variable("X"))))
    ))

    // Test by toString()
    s"${term.get}" should be(input)
  }

  "The parser" should "parse a complex expression" in {
    val input = "knows(Y, mother(Y))"
    val term = Parser.parseInput(input)

    // Test by TypeCheck
    term should be(Some(
      Compound(Constant("knows"), Arguments(List(
        Variable("Y"),
        Compound(Constant("mother"), Arguments(List(Variable("Y"))))
      ))))
    )

    // Test by toString()
    s"${term.get}" should be(input)
  }

  "The parser" should "not parse an invalid expression" in {
    val input = "knows X Y Z"
    val term = Parser.parseInput(input)

    // Test by TypeCheck
    term should be(None)
  }

  "The solver" should "unify 'knows(john, X)' and 'knows(john, jane)'" in {
    // Unification
    val term1 = Parser.parseInput("knows(john, X)")
    val term2 = Parser.parseInput("knows(john, jane)")
    val theta = Robinson.unify(term1.get, term2.get)

    // Test by TypeCheck
    theta should be(
      Substitutions(Map(
        Variable("X") -> Constant("jane")
      ))
    )

    // Test by toString()
    s"$theta" should be("{X/jane}")
  }

  "The solver" should "unify 'knows(john, X)' and 'knows(Y, bill)'" in {
    // Unification
    val term1 = Parser.parseInput("knows(john, X)").get
    val term2 = Parser.parseInput("knows(Y, bill)").get
    val theta = Robinson.unify(term1, term2)

    // Test by TypeCheck
    theta should be(
      Substitutions(Map[Variable, Term](
        Variable("Y") -> Constant("john"),
        Variable("X") -> Constant("bill")
      ))
    )

    // Test by toString()
    s"$theta" should (
      equal("{Y/john, X/bill}") or
        equal("{X/bill, Y/john}")
      )
  }

  "The solver" should "unify 'knows(john, X)' and 'knows(Y, mother(Y))'" in {
    // Unification
    val term1 = Parser.parseInput("knows(john, X)").get
    val term2 = Parser.parseInput("knows(Y, mother(Y))").get
    val theta = Robinson.unify(term1, term2)

    // Test by TypeCheck
    theta should be(
      Substitutions(Map[Variable, Term](
        Variable("Y") -> Constant("john"),
        Variable("X") -> Compound(Constant("mother"), Arguments(List(
          Variable("Y")
        )))
      ))
    )

    // Test by toString()
    s"$theta" should (
      equal("{Y/john, X/mother(Y)}") or
        equal("{X/mother(Y), Y/john}")
      )
  }

  "The solver" should "fail unifying 'knows(john, X)' and 'knows(X, elizabeth)'" in {
    // Unification
    val term1 = Parser.parseInput("knows(john, X)")
    val term2 = Parser.parseInput("knows(X, elizabeth)")
    val theta = Robinson.unify(term1.get, term2.get)

    // Test by TypeCheck
    theta should be(Failure)
  }

  "The solver" should "fail unifying compound occur checks" in {
    // Unification
    val term1 = Parser.parseInput("X")
    val term2 = Parser.parseInput("mother(X)")
    val theta = Robinson.unify(term1.get, term2.get)

    // Test by TypeCheck
    theta should be(Failure)
  }

  "The solver" should "fail unifying substituted occur checks" in {
    // Unification
    val term1 = Parser.parseInput("f(X, Y)")
    val term2 = Parser.parseInput("f(Y, g(X))")
    val theta = Robinson.unify(term1.get, term2.get)

    // Test by TypeCheck
    theta should be(Failure)
  }
}