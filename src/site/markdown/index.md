## Project: Unification

A unification algorithm takes two terms with variables and tries to find substitutions for the variables that makes both the terms equal.

```
Term 1: food(X, fruit(apple, X))
Term 2: food(kiwi, fruit(Y, X))
Substitutions : {X/kiwi, Y/apple}
```
The objective of this project is to implement in Scala the Robinson unification algorithm, which is widely used in artificial intelligence, logic programming (Prolog), typing systems, first-order inference systems.  For more information consult [Unification](https://en.wikipedia.org/wiki/Unification_\(computer_science\))

**Robinson Unification Algorithm**

Let us look at some examples to understand how the unification process works. This first example concerns knowledge inference in first-order logic. Suppose we have a query `Knows(John, X)`: whom does John know? Some answers to this query can be found by finding all sentences in a knowledge base that unify with `Knows(John, X)`. Here are the results of unification with four different sentences that might be in the knowledge base.

The knowledge base might as follows:

```
Knows(John, Jane)
Knows(Y, Bill)
Knows(Y, Mother(Y))
Knows(X, Elizabeth)
```
The result of the query `Knows(John, X)` is the unification with all the entries of the knowledge base.
```
UNIFY(Knows(John, X), Knows(John, Jane)) = {X/Jane}
UNIFY(Knows(John, X), Knows(Y, Bill)) = {X/Bill, Y/John}
UNIFY(Knows(John, X), Knows(Y, Mother(Y))) = {Y/John, X/Mother(John)}
UNIFY(Knows(John, X), Knows(X, Elizabeth)) = fail
```
The last unification fails because`X` cannot take  the values `John` and `Elizabeth` at the same time. Now, remember that `Knows(X, Elizabeth)` means "Everyone knows Elizabeth", so we should be able to infer that `John` knows `Elizabeth`. The problem arises only because the two sentences happen to use the same variable name, `X`. The problem can be avoided using a different name, e.g. `Z`.

```
UNIFY(Knows(John, X), Knows(Z, Elizabeth)) = {X/Elizabeth, Z/John}
```
There is one more complication: we said that UNIFY should return a substitution that makes the two arguments look the same. But there could be more than one single unifier. For example, `UNIFY(Knows(John, X) , Knows(Y, Z))` could return `{Y/John, X/Z}` or `{Y/John, X/John, Z/John}`. The first unifier gives `Knows(John, Z)` as the result of unification, whereas the second gives `Knows(John, John)`. The second result could be obtained from the first by an additional substitution `{Z/John}`; we say that the first unifier is more general than the second one, because it places fewer restrictions on the values of the variables. It turns out that, for every unifiable pair of expressions, there is a single **most general unifier** (or MGU) that is unique up to renaming of variables. In this case it is `{Y/John, X/Z}`.

Another area where the Unification algorithm is used is type checking/inference. For example, assume that we want to type check the following expression and infer the type of `lst`:

```
val lst = map(even, List(1,2,3))
```
We know the types

```
even:Int => Boolean
map:(A => B)(List[A])(List[B])
List(1,2,3):List[Int]
```
From the unification of the two types (these type expressions are translated for the unification algorithm)
```
(A => B)(List[A])(List[B])         ---->  Fun(Fun(Fun(A, B), Lst(A)), Lst(B))
```
and
```
(Int => Boolean)(List[Int])(List[B])  ---->  Fun(Fun(Fun(A, B), Lst(A)), Lst(B))
```
we deduce that the type variable `B` is of type `Boolean`. Moreover, we can conclude that the expression `val lst = map(even, List(1,2,3)) is well typed and the type of `lst` is `List[Boolean]`. Obviously, doing the same job with the expression `val lst = map(even, List(List(1,2,3))`` will fail.

An algorithm for computing the most general unifier is shown below. The process is simple: recursively explore the two expressions simultaneously "side by side", building up a unifier along the way, but failing if two corresponding points in the structures do not match. There is one expensive step: when matching a variable against a complex term, one must check whether the variable itself occurs inside the term; if it does, the match fails because no consistent unifier can be constructed. This step is called **occur check**.

```
Unify(x, y, theta) returns a substitution to make x and y identical
  input x: a Variable, Constant, List, or Compound
        y: a Variable, Constant, List, or Compound
        theta: the substitutions build up so far (optional, defaults empty)
  if theta = failure then return failure
  else if x = y then return theta
  else if isVariable(x) then return UnifyVar(x, y, theta)
  else if isVariable(y) then return UnifyVar(y, x, theta)
  else if isCompound(x) and isCompound(y) then
    return Unify(Args[x], Args[y], Unify(OP[x], OP[y], theta))
  else if isList(x) and isList(y) then
    return Unify(Rest[x], Rest[y], Unify(First[x], First[y], theta))
  else return failure

UnifyVar(var, x, theta) returns a substitution
  input var: a Variable,
        x: any expression
        theta: the substitutions build up so far
  if {var/val} in theta then return Unify(val, x, theta)
  else if {x/val} in theta then return Unify(var, val, theta)
  else if OccurCheck(var, x) then return failure
  else return add {var/x} to theta 
```
In a Compound expression, such as `F(A, B)`, the function `OP` picks out the function symbol and the function `Args` picks out the argument list `(A,B)`. `First` takes the first element of an argument list and `Rest` takes the argument list without the first element.

## Download and Installation

1. Install the last version of "Maven" (`https://maven.apache.org/`)

2. Download the file `unif.zip` and unzip somewhere.

3. Go into the created directory `unif` where the `pom.xml` file is.

4. Into a shell type `mvn clean package site`

5. With your browser visualize the file `./target/site/index.html` to see this documentation.

6. Do not forget to add you names, abbreviations and email address in the `pom.xml` under the tag `developper`


## How to proceed

It is important to divide the work in three steps:

1. The algorithm which can be developed without any mutable elements
	 * Use the above pseudo-code.
2. The parsing of terms
   1. Define the lexical elements of the language of such terms
	 1. Develop the grammar for the syntactic aspects of the language
	 3. Use the grammar to develop the parser (kind of recursive descent parser)
3. The user interface to input the two terms and display the MGU, command line user interface is enough

Try to develop each step independently.  First understand the algorithm, then define how you want to represent the different components of the language of terms in Scala.  Then tackle the development of the algorithm. Afterward think about the parser and the user interface.

## Deliverables

The deliverable of this project consists in the implementation of the Robinson unification and a parser for the terms.  Your implementation have to provide a simple user interface, which allows a user to enter two terms. The two terms have to be parsed and then provided to your implementation of the Robinson unification algorithm. The *most general unifier*, computed by the algorithm, has to be displayed in a nicely way. Here is a very simple example:
```
Term 1: food(X, fruit(apple, X))
Term 2: food(kiwi, fruit(Y, X))
MGU   : {X/kiwi, Y/apple}
```
It is important to use as much as possible immutable elements, adopt an elegant and *functional*  programming style.

Do not forget to develop unit tests with Scalatest, and write Scaladoc.

A report written in Markdown stored at the end of this file has to be provided.  Your report will describe briefly the approach of your parser and the tests that have been conducted.

Do no forget to provide the grammar that descripbes the syntax of your system.

You must use GitLab at the BFH (gitlab.ti.bfh.ch). Create a project with the name `unif_yourabbreviation1_yourabbreviation2` (for example: `unif_gautx1_bobsp2` and give me the same rights as you. An invitation email will be automatically sent to me. Copy the downloaded files of `unif` and commit them.

## Organization

* This project is obligatory for every student who attends the course.
* The project is realized by small groups of maximum two persons.
* You must use *Gitlab* and *Maven* and obviously *Scala*
* Let me remind you that such a project is not a collaborative work. Each team must provide its own solution.
* Deadline for the deliverable is **6th June 2021 (24:00)**


## REPORT

Add your report below. The source of this file is located in `./src/site/markdown/index.md`.
