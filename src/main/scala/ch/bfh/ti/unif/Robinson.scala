package ch.bfh.ti.unif

object Robinson {
  /**
   * Robinson's unification algorithm
   *
   * @param x     first expression to unify
   * @param y     second expression to unify
   * @param theta set of substitutions built up
   * @return set of substitutions orfFailure
   */

  def unify(x: Term, y: Term, theta: Theta = Substitutions()): Theta = {
    Logger.debug(s"*** Entry unify: \nx: $x\ny: $y\ntheta: $theta\n")

    // =========================================================================
    //  else if x = y then return theta
    // =========================================================================
    if (x == y) return theta
    // =========================================================================
    // if theta = failure then return failure
    // =========================================================================
    theta match {
      case Failure => Failure
      case subst@Substitutions(_) => (x, y) match {
        // =========================================================================
        //  else if isVariable(x) then return UnifyVar(x, y, theta)
        // =========================================================================
        case (x@Variable(_), _) => unifyVar(x, y, subst)
        // =========================================================================
        //  else if isVariable(y) then return UnifyVar(y, x, theta)
        // =========================================================================
        case (_, y@Variable(_)) => unifyVar(y, x, subst)
        // =========================================================================
        //  else if isCompound(x) and isCompound(y) then
        //    return Unify(Args[x], Args[y], Unify(OP[x], OP[y], theta))
        // =========================================================================
        case (Compound(x_op, x), Compound(y_op, y)) =>
          unify(x, y, unify(x_op, y_op, subst))
        // =========================================================================
        //  else if isList(x) and isList(y) then
        //    return Unify(Rest[x], Rest[y], Unify(First[x], First[y], theta))
        // =========================================================================
        case (Arguments(x_first :: x_rest), Arguments(y_first :: y_rest)) =>
          unify(Arguments(x_rest), Arguments(y_rest), unify(x_first, y_first, subst))
        // =========================================================================
        //  else return failure
        // =========================================================================
        case _ => Failure
      }
    }
  }

  /**
   * Unify Variable Function of the Robinson's unification algorithm
   *
   * @param v     of Term
   * @param t     term to unify with the variable
   * @param subst set of substitutions built up so far
   * @return set of substitutions or failure
   */
  def unifyVar(v: Variable, t: Term, subst: Substitutions): Theta = {
    Logger.debug(s"*** Entry unifyvar: \nvariable: $v, \nx: $t\n")

    if (v == t) return subst
    // =================================================================
    // if {var/val} in theta then return Unify(val, x, theta)
    // =================================================================
    subst.substitutions.get(v) match {
      case Some(value) => return unify(value, t, subst)
      case None => ()
    }
    // =================================================================
    // else if {x/val} in theta then return Unify(var, val, theta)
    // =================================================================
    t match {
      case t@Variable(_) => subst.substitutions.get(t) match {
        case Some(value) => return unify(v, value, subst)
        case None => ()
      }
      case _ => ()
    }
    // =================================================================
    // else if OccurCheck(var, x) then return failure
    // =================================================================
    if (occurCheck(v, t, subst)) {
      Logger.debug(s"Occurrence check success.. for $v and $t\n")
      Failure
    } else {
      // =================================================================
      // else return add {var/x} to theta
      // =================================================================
      Substitutions(subst.substitutions + (v -> t))
    }
  }


  /**
   * Occurrence Check Function of the Robinson's unification algorithm
   *
   * @param v variable
   * @param t expression to check with the variable
   * @return occurrence check positive
   */
  def occurCheck(v: Variable, t: Term, subst: Substitutions): Boolean = {
    Logger.debug(s"*** Occurrence check for: \nv: $v, \nx: $t\n")
    if (v == t) return true
    t match {
      //senza case l'ultimo test fail
      case t@Variable(_) => subst.substitutions.get(t) match {
        case Some(x) => occurCheck(v, x, subst)
        case None => false
      }
      case Compound(op, args) => occurCheck(v, op, subst) || occurCheck(v, args, subst)
      case Arguments(first :: rest) => occurCheck(v, first, subst) || occurCheck(v, Arguments(rest), subst)
      case _ => false
    }
  }
}
