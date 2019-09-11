package mx.sugus.json;

/**
 * Thrown by the lexer and parser.
 */
public class ParseException extends RuntimeException {

  public ParseException(String element, String expecting, int got) {
    super(String.format("Error while parsing '%s', expecting: '%s', got: '%s'",
        element, expecting, (char) got));
  }

  public ParseException(String element, String expecting, String got) {
    super(String.format("Error while parsing '%s', expecting: '%s', got: '%s'",
        element, expecting, got));
  }

  public ParseException(String element, String expecting) {
    super(String.format("End of file found while parsing '%s', expecting: '%s'",
        element, expecting));
  }

  public ParseException(String element, String expected, Token got) {
    this(element, expected, got.toString());
  }
}
