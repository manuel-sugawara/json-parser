package mx.sugus.json;

import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;

/**
 * Tokenizes a JSON string.
 * See https://tools.ietf.org/html/rfc7159.html
 */
public class Tokenizer {

  private final Reader reader;
  private final String source;
  private int lookahead = -2;

  public Tokenizer(String source, Reader reader) {
    this.source = source;
    this.reader = reader;
  }

  public Tokenizer(String json) {
    this("<user input>", new StringReader(json));
  }

  public Token next() {
    int ch = skipWhitespace();
    switch (ch) {
      case -1:
        return Token.EOF;
      case '[':
        return Token.START_LIST;
      case ']':
        return Token.END_LIST;
      case '{':
        return Token.START_MAP;
      case '}':
        return Token.END_MAP;
      case ',':
        return Token.COMMA;
      case ':':
        return Token.COLON;
      case '"':
        return readString();
      case '-':
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        return readNumber(ch);
      case 'f':
        return readFalse();
      case 't':
        return readTrue();
      case 'n':
        return readNull();
      default:
        throw parseError("JSON", "Valid char", ch);
    }
  }

  private int skipWhitespace() {
    int ch = read();
    while (isWhitespace(ch)) {
      ch = read();
    }
    return ch;
  }

  private boolean isWhitespace(int ch) {
    switch (ch) {
      case ' ':
      case '\t':
      case '\r':
      case '\n':
        return true;
      default:
        return false;
    }
  }

  private Token readString() {
    StringBuilder buf = new StringBuilder();
    int ch;
    do {
      ch = read();
      if (ch == -1) {
        throw parseError("string", "\"", ch);
      }
      if (ch == '\\') {
        consumeEscape(buf);
        continue;
      }
      if (ch != '"') {
        if (ch >= '\u0000' && ch <= '\u001F') {
          throw parseError("string", "non-control character", ch);
        }
        buf.append((char) ch);
      }
    } while (ch != '"');
    return newToken(Token.Type.STRING, buf.toString());
  }

  private void consumeEscape(StringBuilder buf) {
    int ch = read();
    switch (ch) {
      case '"':
        buf.append('"');
        break;
      case '\\':
        buf.append('\\');
        break;
      case '/':
        buf.append('/');
        break;
      case 'b':
        buf.append('\b');
        break;
      case 'f':
        buf.append('\f');
        break;
      case 'n':
        buf.append('\n');
        break;
      case 'r':
        buf.append('\r');
        break;
      case 't':
        buf.append('\t');
        break;
      case 'u':
        int value = hexDigitValue();
        value = (value << 4) + hexDigitValue();
        value = (value << 4) + hexDigitValue();
        value = (value << 4) + hexDigitValue();
        buf.append((char) value);
        break;
      default:
        throw parseError("string escape", "\"\\/bfnrt or unicode escape", ch);
    }
  }

  private int hexDigitValue() {
    int ch = read();
    if (ch >= '0' && ch <= '9') {
      return ch - '0';
    }
    if (ch >= 'a' && ch <= 'f') {
      return (ch - 'a') + 10;
    }
    if (ch >= 'A' && ch <= 'F') {
      return (ch - 'A') + 10;
    }
    throw parseError("Unicode escape", "0-9 or A-Z", ch);
  }

  private Token readNumber(int ch) {
    StringBuilder buf = new StringBuilder();
    if (ch == '-') {
      buf.append((char) ch);
    } else if (ch == '0') {
      buf.append((char) ch);
      return consumeNumberStartsWithZero(buf);
    } else {
      unread(ch);
    }
    if (!consumeDigits(buf)) {
      ch = read();
      throw parseError("number", "[0-9]", ch);
    }
    boolean isFloat = false;
    if (match('.')) {
      isFloat = true;
      buf.append('.');
      if (!consumeDigits(buf)) {
        throw parseError("number", "[0-9] after dot", -1);
      }
    }
    if (consumeExponent(buf) || isFloat) {
      return newToken(Token.Type.DOUBLE, buf.toString());
    }
    return newToken(Token.Type.LONG, buf.toString());
  }

  private Token consumeNumberStartsWithZero(StringBuilder buf) {
    if (match('.')) {
      buf.append('.');
      if (!consumeDigits(buf)) {
        throw parseError("number", "[0-9] after dot", read());
      }
      consumeExponent(buf);
      return newToken(Token.Type.DOUBLE, buf.toString());
    } else if (consumeExponent(buf)) {
      return newToken(Token.Type.DOUBLE, buf.toString());
    }
    return newToken(Token.Type.LONG, buf.toString());
  }

  private boolean consumeExponent(StringBuilder buf) {
    if (!match('e') && !match('E')) {
      return false;
    }
    buf.append('e');
    if (match('-')) {
      buf.append('-');
    } else if (match('+')) {
      buf.append('+');
    }
    if (!consumeDigits(buf)) {
      throw parseError("number", "[0-9] after exponent start", -1);
    }
    return true;
  }

  private boolean consumeDigits(StringBuilder buf) {
    int ch = read();
    boolean success = false;
    if (ch >= '0' && ch <= '9') {
      success = true;
      do {
        buf.append((char) ch);
        ch = directRead();
      } while (ch >= '0' && ch <= '9');
    }
    unread(ch);
    return success;
  }

  private Token readFalse() {
    consume("false literal", 'a', 'l', 's', 'e');
    return Token.FALSE;
  }

  private Token readTrue() {
    consume("true literal", 'r', 'u', 'e');
    return Token.TRUE;
  }

  private Token readNull() {
    consume("null literal", 'u', 'l', 'l');
    return Token.NULL;
  }

  private boolean match(int expected) {
    int ch = read();
    if (ch == expected) {
      return true;
    }
    unread(ch);
    return false;
  }

  private void consume(String parsing, int... chars) {
    for (int c : chars) {
      int ch = read();
      if (ch != c) {
        throw parseError(parsing, Character.toString((char) c), ch);
      }
    }
  }

  private void unread(int ch) {
    lookahead = ch;
  }

  private int directRead() {
    try {
      return reader.read();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private int read() {
    try {
      if (lookahead != -2) {
        int tmp = lookahead;
        lookahead = -2;
        return tmp;
      }
      return reader.read();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Token newToken(Token.Type type, String value) {
    return new Token(type, value);
  }

  private ParseException parseError(String expected, String got, int ch) {
    if (ch == -1) {
      return new ParseException(expected, got);
    }
    return new ParseException(expected, got, ch);
  }
}
