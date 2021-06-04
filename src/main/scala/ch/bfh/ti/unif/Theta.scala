package ch.bfh.ti.unif

/**
 * Interface Theta
 */
sealed trait Theta

/**
 * Substitutions
 *
 * @param substitutions map created with variable and term return to string
 */
case class Substitutions(substitutions: Map[Variable, Term] = Map()) extends Theta {
  override def toString: String =
    substitutions
      .map { case (key, value) => s"$key/$value" }
      .mkString(", ")
      .formatted("{%s}")
}

case object Failure extends Theta
