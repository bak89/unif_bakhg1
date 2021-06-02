package ch.bfh.ti.unif

object Robinson {


  /*
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
   */

  /*
  Unify(knows(X, John), knows(Beppe, Y), [])
    -> isCompound
       Unify([X, John], [Beppe, Y], Unify(knows, knows, []))
         -> Unify([X, John], [Beppe, Y], [])
         -> isList
            Unify([John], [Y], Unify(X, Beppe, []))
            | UnifyVar(X, Beppe, []) -> theta = [{X/Beppe}]
            | Unify([John], [Y], [{X/Beppe}])
              -> isList
                 Unify([], [], Unify(John, Y, [{X/Beppe}]))
                 | UnifyVar(Y, John, [{X/Beppe}]) -> theta = [{X/Beppe}, {Y/John}]
                 | Unify([], [], [{X/Beppe}, {Y/John}])
              [{X/Beppe}, {Y/John}]
            [{X/Beppe}, {Y/John}]
         [{X/Beppe}, {Y/John}]
       [{X/Beppe}, {Y/John}]
    [{X/Beppe}, {Y/John}]
   */

  /**
   * Robinson's unification algorithm
   *
   * @param x     first expression to unify
   * @param y     second expression to unify
   * @param theta set of substitutions built up
   * @return set of substitutions orfFailure
   */
  def unify(x: Expression, y: Expression, theta: Theta = Substitutions()): Theta = {
    Logger.debug(s"*** Entry unify: \nx: $x\ny: $y\ntheta: $theta\n")

    // =========================================================================
    // if theta = failure then return failure
    // =========================================================================
    if (theta == Failure) return Failure
    // =========================================================================
    //  else if x = y then return theta
    // =========================================================================
    if (x == y) return theta

    (x, y) match {
      // =========================================================================
      //  else if isVariable(x) then return UnifyVar(x, y, theta)
      // =========================================================================
      case (x@Variable(_), _) => unifyVar(x, y, theta)
      // =========================================================================
      //  else if isVariable(y) then return UnifyVar(y, x, theta)
      // =========================================================================
      case (_, y@Variable(_)) => unifyVar(y, x, theta)
      // =========================================================================
      //  else if isCompound(x) and isCompound(y) then
      //    return Unify(Args[x], Args[y], Unify(OP[x], OP[y], theta))
      // =========================================================================
      case (Compound(x_op, x), Compound(y_op, y)) =>
        unify(x, y, unify(x_op, y_op, theta))
      // =========================================================================
      //  else if isList(x) and isList(y) then
      //    return Unify(Rest[x], Rest[y], Unify(First[x], First[y], theta))
      // =========================================================================
      case (Args(x_first :: x_rest), Args(y_first :: y_rest)) =>
        unify(Args(x_rest), Args(y_rest), unify(x_first, y_first, theta))
      // =========================================================================
      //  else return failure
      // =========================================================================
      case _ => Failure
    }
  }

  /**
   * Unify Variable Function of the Robinson's unification algorithm
   *
   * @param variable of Term
   * @param x        expression to unify with the variable
   * @param theta    set of substitutions built up so far
   * @return set of substitutions or failure
   */
  def unifyVar(variable: Variable, x: Expression, theta: Theta): Theta = {
    Logger.debug(s"*** Entry unifyvar: \nvariable: $variable, \nx: $x\n")

    theta match {
      case Failure => Failure
      case Substitutions(map) =>
        // =================================================================
        // if {var/val} in theta then return Unify(val, x, theta)
        // =================================================================
        map.get(variable) match {
          case Some(value) => return unify(value, x, theta)
          case _ => ()
        }
        // =================================================================
        // else if {x/val} in theta then return Unify(var, val, theta)
        // =================================================================
        x match {
          case x@Variable(_) =>
            map.get(x) match {
              case Some(value) => return unify(variable, value, theta)
              case _ => ()
            }
          case _ => ()
        }
        // =================================================================
        // else if OccurCheck(var, x) then return failure
        // =================================================================
        if (occurCheck(variable, x)) {
          Logger.debug(s"Occurrence check success.. for $variable and $x\n")
          Failure
        } else {
          // =================================================================
          // else return add {var/x} to theta
          // =================================================================
          Substitutions(map + (variable -> x))
        }
    }
  }

  /**
   * Occurrence Check Function of the Robinson's unification algorithm
   *
   * @param v variable
   * @param x expression to check with the variable
   * @return Occurrence Check positive
   */
  def occurCheck(v: Variable, x: Expression): Boolean = {
    Logger.debug(s"*** Occurrence check for: \nv: $v, \nx: $x\n")
    x match {
      case Constant(_) => false
      case Variable(_) => false
      case Compound(_, args) => occurCheck(v, args)
      case Args(args) => args.exists(arg => occurCheck(v, arg))
    }
  }
}
