package mx.sugus.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import mx.sugus.json.Token.Type;
import org.junit.Test;

public class TokenizerTest {

  @Test
  public void testTrueLiteral() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("true");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.TRUE, result.getType());
  }

  @Test
  public void testFalseLiteral() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("false");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.FALSE, result.getType());
  }

  @Test
  public void testNullLiteral() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("null");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.NULL, result.getType());
  }

  @Test(expected = ParseException.class)
  public void testNilLiteral() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("nil");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertTrue(false);
  }

  @Test
  public void testZeroInteger() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("0");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.LONG, result.getType());
    assertEquals("0", result.getValue());
    assertEquals(Token.EOF, tokenizer.next());
  }

  @Test(expected = ParseException.class)
  public void testZeroFollowByDot() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("0.");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertTrue(false);
  }

  @Test
  public void testZeroFloat() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("0.0");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.DOUBLE, result.getType());
    assertEquals("0.0", result.getValue());
    assertEquals(Token.EOF, tokenizer.next());
  }

  @Test
  public void testZeroWithExponent() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("0e-20");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.DOUBLE, result.getType());
    assertEquals("0e-20", result.getValue());
    assertEquals(Token.EOF, tokenizer.next());
  }

  @Test
  public void testZeroFloatWithExponent() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("0.0e20");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.DOUBLE, result.getType());
    assertEquals("0.0e20", result.getValue());
    assertEquals(Token.EOF, tokenizer.next());
  }

  @Test
  public void testIntegerOneDigit() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("1");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.LONG, result.getType());
    assertEquals("1", result.getValue());
    assertEquals(Token.EOF, tokenizer.next());
  }

  @Test
  public void testIntegerTwoDigits() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("12");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.LONG, result.getType());
    assertEquals("12", result.getValue());
    assertEquals(Token.EOF, tokenizer.next());
  }

  @Test
  public void testIntegerThreeDigits() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("123");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.LONG, result.getType());
    assertEquals("123", result.getValue());
    assertEquals(Token.EOF, tokenizer.next());
  }

  @Test
  public void testFloatWithDecimalPoint() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("123.456");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.DOUBLE, result.getType());
    assertEquals("123.456", result.getValue());
    assertEquals(Token.EOF, tokenizer.next());
  }

  @Test(expected = ParseException.class)
  public void testFloatWithDecimalPointWithoutDigitsAfter() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("123.");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertTrue(false);
  }

  @Test
  public void testNegativeFloatWithDecimalPoint() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("-123.456");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.DOUBLE, result.getType());
    assertEquals("-123.456", result.getValue());
    assertEquals(Token.EOF, tokenizer.next());
  }

  @Test(expected = ParseException.class)
  public void testInvalidNegative() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("-NaN");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertTrue(false);
  }

  @Test
  public void testFloatWithExponentPart() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("123e12");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.DOUBLE, result.getType());
    assertEquals("123e12", result.getValue());
    assertEquals(Token.EOF, tokenizer.next());
  }

  @Test
  public void testFloatWithCapitalExponentPart() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("123E-12");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.DOUBLE, result.getType());
    assertEquals("123e-12", result.getValue());
    assertEquals(Token.EOF, tokenizer.next());
  }

  @Test(expected = ParseException.class)
  public void testFloatWithInvalidExponentPart() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("123e");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertTrue(false);
  }

  @Test(expected = ParseException.class)
  public void testFloatWithInvalidNegativeExponentPart() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("123e-NaN");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertTrue(false);
  }

  @Test
  public void testFloatWithPositiveExponentPart() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("123e+12");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.DOUBLE, result.getType());
    assertEquals("123e+12", result.getValue());
    assertEquals(Token.EOF, tokenizer.next());
  }

  @Test
  public void testFloatWithNegativeExponentPart() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("123e-12");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.DOUBLE, result.getType());
    assertEquals("123e-12", result.getValue());
    assertEquals(Token.EOF, tokenizer.next());
  }

  @Test(expected = ParseException.class)
  public void testNumberStartsWithDot() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer(".32");

    // Act
    Token result = tokenizer.next();

    // Assert not reached
    assertTrue(false);
  }

  @Test
  public void testEmptyString() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("\"\"");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.STRING, result.getType());
    assertEquals("", result.getValue());
    assertEquals(Token.EOF, tokenizer.next());
  }

  @Test
  public void testNonEmptyString() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("\"foo bar\"");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.STRING, result.getType());
    assertEquals("foo bar", result.getValue());
    assertEquals(Token.EOF, tokenizer.next());
  }

  @Test
  public void testControlEscapeString() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("\"\\r\\n\\b\\f\\t\\\"\\\\\\/\"");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.STRING, result.getType());
    assertEquals("\r\n\b\f\t\"\\/", result.getValue());
    assertEquals(Token.EOF, tokenizer.next());
  }


  @Test(expected = ParseException.class)
  public void testInvalidEscape() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("\"Hello \\q World\"");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertTrue(false);
  }

  @Test
  public void testUnicodeEscapeString() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("\"32\\u00b0F \\u2192 0\\u00b0C\"");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertEquals(Type.STRING, result.getType());
    assertEquals("32°F → 0°C", result.getValue());
    assertEquals(Token.EOF, tokenizer.next());
  }

  @Test
  public void testUnicodeEscapeSurrogatePair() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("\"\\uD83D\\uDE02\"");

    // Act
    Token result = tokenizer.next();
    // Assert
    assertEquals(Type.STRING, result.getType());
    assertEquals("\uD83D\uDE02", result.getValue());
    assertEquals(Token.EOF, tokenizer.next());
  }

  @Test(expected = ParseException.class)
  public void testInvalidUnicodeEscape() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("\"\\uD83Z\"");

    // Act
    Token result = tokenizer.next();

    // Assert
    assertTrue(false);
  }

  @Test(expected = ParseException.class)
  public void testControlCharacterWithinString() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("\"foo \n bar\"");

    // Act
    Token result = tokenizer.next();

    // Assert not reached
    assertTrue(false);
  }

  @Test(expected = ParseException.class)
  public void testUnterminatedString() {
    // Arrange
    Tokenizer tokenizer = new Tokenizer("\"foo");

    // Act
    Token result = tokenizer.next();

    // Assert not reached
    assertTrue(false);
  }
}
