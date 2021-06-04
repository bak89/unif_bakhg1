package ch.bfh.ti.unif

/**
 * Interface for all Term
 */
sealed trait Term

/**
 * Term that represent a variable
 * @param name of the variable
 */
case class Variable(name: String) extends Term {
  override def toString: String = name
}

/**
 * Term that represent a constant
 * @param name of the constant
 */
case class Constant(name: String) extends Term {
  override def toString: String = name
}

/**
 * Term that represent a compound
 * @param operator identifier of the operation
 * @param args list of terms
 */
case class Compound(operator: Constant, args: Arguments) extends Term {
  override def toString: String = s"$operator$args"
}

/**
 * Arguments to create compound
 * @param args list of term
 */
case class Arguments(args: List[Term]) extends Term {
  override def toString: String = args.mkString(", ").formatted("(%s)")
}