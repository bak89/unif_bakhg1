package ch.bfh.ti.unif

sealed trait Theta //trait = interface

case class Substitutions(substitutions: Map[Variable, Expression] = Map()) extends Theta {
  override def toString: String =
    substitutions
      .map { case (key, value) => s"$key/$value" }
      .mkString(", ")
      .formatted("{%s}")
}

case object Failure extends Theta
