package ch.bfh.ti.unif

import scala.annotation.tailrec
import scala.io.StdIn._

/**
 * @author ${user.name}
 */
object App {

  def main(args: Array[String]) {

    //! Enable Debugger: //////////////////////////
    Logger.DebugEnabled = true
    //! ///////////////////////////////////////////

    println()
    println("===========================================================")
    println("Robinson's unifier by Aleistar Markoczy")
    println("===========================================================")
    println()
    println("The Input String must respect the following rules:")
    println()
    println("- A Variable must always start with an Uppercase letter.")
    println("  e.g. 'MyVariable'")
    println("- A Constant must always start with a lowercase letter.")
    println("  e.g. 'adaLovelace'")
    println("- A Compound Term must always start with a lowercase letter")
    println("  and use ( and ) brackets. e.g. 'human(X)'")
    println()
    println("Here are a few examples of valid input Strings:")
    println()
    println("- Example 1: knows(john, X)")
    println("- Example 2: knows(Y, mother(Y))")
    println("- Example 3: food(X, fruit(apple, X))")
    println()
    println("===========================================================")
    println()

    val term1 = requireInput("Enter Term 1: ")
    pauseIfDebug()

    val term2 = requireInput("Enter Term 2: ")
    pauseIfDebug()

    val mgu = Robinson.unify(term1, term2)
    println(s"Resulting MGU: $mgu\n")
  }

  def pauseIfDebug(): Any = {
    if (Logger.DebugEnabled) {
      readLine("Continue with <Enter>")
    }
  }

  @tailrec
  def requireInput(text: String): Expression = {
    val str = readLine(text)
    Parser.parseInput(str) match {
      case Some(expr) => expr
      case _ =>
        println("> Error while parsing\n")
        requireInput(text)
    }
  }
}
