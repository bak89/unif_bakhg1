package ch.bfh.ti.unif

/**
 * Interface for all Expression
 */
sealed trait Expression

/**
 * Term that represent a constant
 * @param value of the constant
 */
case class Constant(value: String) extends Expression{
  override def toString: String = value
}

/**
 * Term that represent a variable
 * @param name of the variable
 */
case class Variable(name: String) extends Expression {
  override def toString: String = name
}

/**
 * Term that represent a compound
 * @param operator identifier of the operation
 * @param args list of terms
 */
case class Compound(operator: Constant, args: Args) extends Expression {
  override def toString: String = s"$operator$args"
}

/**
 * Arguments to create compound
 * @param args list of expression
 */
case class Args(args: List[Expression]) extends Expression {
  override def toString: String = args.mkString(", ").formatted("(%s)")
}