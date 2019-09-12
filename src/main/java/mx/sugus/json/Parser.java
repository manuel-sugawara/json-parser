package mx.sugus.json;

import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.sugus.json.Token.Type;

/**
 * Parses a JSON string and return its corresponding java data structure.
 */
public class Parser {

  private final Tokenizer tokenizer;

  public Parser(Reader reader) {
    this.tokenizer = new Tokenizer(reader);
  }

  public Parser(String json) {
    this.tokenizer = new Tokenizer(json);
  }

  public Object parse() {
    Object value = parseOneValue(next());
    consume("json value", Type.EOF);
    return value;
  }

  private Object parseOneValue(Token token) {
    Object value;
    switch (token.getType()) {
      case DOUBLE:
      case LONG:
        value = toValue(token);
        break;
      case STRING:
        value = token.getValue();
        break;
      case NULL:
        value = null;
        break;
      case TRUE:
        value = true;
        break;
      case FALSE:
        value = false;
        break;
      case START_LIST:
        value = parseList();
        break;
      case START_MAP:
        value = parseMap();
        break;
      default:
        throw parseError("json value", "value", token);
    }
    return value;
  }

  private Object toValue(Token token) {
    String value = token.getValue();
    if (token.getType() == Type.LONG) {
      try {
        return Long.parseLong(value);
      } catch (NumberFormatException e) {
        return new BigInteger(value);
      }
    }
    if (token.getType() == Type.DOUBLE) {
      Double result = Double.parseDouble(token.getValue());
      if (!Double.isInfinite(result)) {
        return result;
      }
      return new BigDecimal(value);
    }
    throw new IllegalStateException("not reached");
  }

  private List<?> parseList() {
    Token token = next();
    List<Object> result = new ArrayList<>();
    Token.Type type;
    while ((type = token.getType()) != Type.END_LIST && type != Type.EOF) {
      result.add(parseOneValue(token));
      token = next();
      if (token.getType() != Type.COMMA) {
        break;
      }
      token = next();
    }
    consume(token,"list", Type.END_LIST);
    return result;
  }

  private Map<String, ?> parseMap() {
    Token token = next();
    Map<String, Object> result = new HashMap<>();
    Token.Type type;
    while ((type = token.getType()) != Type.END_MAP && type != Type.EOF) {
      Token key = consume(token, "map", Type.STRING);
      consume(next(), "map", Type.COLON);
      result.put(key.getValue(), parseOneValue(next()));
      token = next();
      if (token.getType() != Type.COMMA) {
        break;
      }
      token = next();
    }
    consume(token, "map", Type.END_MAP);
    return result;
  }

  private Token consume(String element, Token.Type type) {
    Token token = next();
    if (token.getType() != type) {
      throw parseError(element, type, token);
    }
    return token;
  }

  private Token consume(Token token, String element, Token.Type type) {
    if (token.getType() != type) {
      throw parseError(element, type, token);
    }
    return token;
  }

  private Token next() {
    return tokenizer.next();
  }

  private ParseException parseError(String element, Token.Type expected, Token got) {
    return new ParseException(element, expected.toString(), got);
  }

  private ParseException parseError(String element, String expected, Token got) {
    return new ParseException(element, expected, got);
  }
}
