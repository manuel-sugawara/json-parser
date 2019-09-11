package mx.sugus.json;

/**
 * JSON tokens.
 */
public class Token {

  enum Type {
    EOF,
    START_LIST,
    END_LIST,
    START_MAP,
    END_MAP,
    COMMA,
    COLON,
    STRING,
    DOUBLE,
    LONG,
    FALSE,
    TRUE,
    NULL
  }

  public static final Token EOF = new Token(Type.EOF);
  public static final Token START_LIST = new Token(Type.START_LIST);
  public static final Token END_LIST = new Token(Type.END_LIST);
  public static final Token START_MAP = new Token(Type.START_MAP);
  public static final Token END_MAP = new Token(Type.END_MAP);
  public static final Token COMMA = new Token(Type.COMMA);
  public static final Token COLON = new Token(Type.COLON);
  public static final Token FALSE = new Token(Type.FALSE);
  public static final Token TRUE = new Token(Type.TRUE);
  public static final Token NULL = new Token(Type.NULL);

  private final Type type;
  private final String value;

  public Token(Type type, String value) {
    this.type = type;
    this.value = value;
  }

  public Token(Type type) {
    this(type, null);
  }

  public Type getType() {
    return type;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    if (value == null) {
      return "[" + type.toString() + "]";
    }
    return "[" + type.toString() + ", \"" + value + "\"]";
  }
}
