package ch.bfh.ti.unif


object Parser {
  // Regex tokenizer: input is split into parsable tokens
  private val RxTokenizer = "\\w+|[,()]".r
  // Regex if current token is a variable (starting Uppercase)
  private val RxIsVariable = "[A-Z]\\w*".r
  // Regex if current token is a name
  private val RxIsName = "\\w+".r

  /**
   * Parses user Input and returns the parsed Term
   *
   * @param input The user Input
   */
  def parseInput(input: String): Option[Term] = {
    val tokens = RxTokenizer.findAllIn(input).toList
    Logger.debug("Tokens: " + tokens)
    parseAllTokens(tokens)
  }

  /**
   * Recursive Functions to parse all Input tokens
   *
   * @param tokens list of strings created by the tokenizer
   * @return
   */
  private def parseAllTokens(tokens: List[String]): Option[Term] = {
    Logger.debug("*** Entry parseTokens")
    parseToken(tokens) match {
      case Some((expr, Nil)) => Some(expr)
      case Some((_, tokens)) =>
        Logger.debug("Unparsed tokens: " + tokens)
        None
      case None =>
        Logger.debug("Parser failed")
        None
    }
  }

  private def parseToken(tokens: List[String]): Option[(Term, List[String])] = {
    tokens match {
      case head :: tail => head match {
        case "(" =>
          Logger.debug("Enter list")
          parseArgs(tail).map { case (args, tokens) => (Arguments(args), tokens) }
        case _ if RxIsVariable.matches(head) =>
          Logger.debug("Variable: " + head)
          Some((Variable(head), tail))
        case _ if RxIsName.matches(head) => tail match {
          case "(" :: tail =>
            Logger.debug("Enter compound: " + head)
            parseArgs(tail)
              .map { case (args, tokens) => (Compound(Constant(head), Arguments(args)), tokens) }
          case _ =>
            Logger.debug("Constant: " + head)
            Some((Constant(head), tail))
        }
        case _ =>
          Logger.debug("Invalid token: " + head)
          None
      }
    }
  }

  private def parseArgs(tokens: List[String]): Option[(List[Term], List[String])] = {
    parseToken(tokens) match {
      case Some((expr, tokens)) =>
        tokens match {
          case "," :: tokens =>
            Logger.debug("Continue list")
            parseArgs(tokens) match {
              case Some((args, tokens)) => Some((expr :: args, tokens))
              case _ => None
            }
          case ")" :: tail =>
            Logger.debug("Exit list")
            Some((List(expr), tail))
          case _ =>
            Logger.debug("Invalid list")
            None
        }
      case _ => None
    }
  }
}
