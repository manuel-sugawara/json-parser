package mx.sugus.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class ParserTest {

  @Test
  public void testParseString() {
    // Arrange
    Parser parser = new Parser("  \"foobar\"  ");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof String);
    assertEquals("foobar", value);
  }

  @Test
  public void testParseLong() {
    // Arrange
    Parser parser = new Parser("123");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof Long);
    assertEquals(123L, value);
  }

  @Test
  public void testParseDouble() {
    // Arrange
    Parser parser = new Parser("123.456");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof Double);
    assertEquals(123.456, value);
  }

  @Test
  public void testParseFalse() {
    // Arrange
    Parser parser = new Parser("false");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof Boolean);
    assertEquals(false, value);
  }

  @Test
  public void testParseTrue() {
    // Arrange
    Parser parser = new Parser("true");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof Boolean);
    assertEquals(true, value);
  }

  @Test
  public void testParseNull() {
    // Arrange
    Parser parser = new Parser("null");

    // Act
    Object value = parser.parse();

    // Assert
    assertEquals(null, value);
  }

  @Test
  public void testParseEmptyList() {
    // Arrange
    Parser parser = new Parser("[]");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof List);
    assertEquals(0, ((List) value).size());
  }

  @Test
  public void testParseOneValueList() {
    // Arrange
    Parser parser = new Parser("[123]");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof List);
    assertEquals(1, ((List) value).size());
    assertEquals(123L, ((List) value).get(0));
  }

  @Test
  public void testParseTwoValueList() {
    // Arrange
    Parser parser = new Parser("[123, true]");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof List);
    assertEquals(2, ((List) value).size());
    assertEquals(123L, ((List) value).get(0));
    assertEquals(true, ((List) value).get(1));
  }

  @Test(expected = ParseException.class)
  public void testParseTwoValueInvalidList() {
    // Arrange
    Parser parser = new Parser("[123 456]");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(false);
  }

  @Test(expected = ParseException.class)
  public void testParseNonTerminatedList() {
    // Arrange
    Parser parser = new Parser("[123");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(false);
  }

  @Test(expected = ParseException.class)
  public void testParseMalformedList() {
    // Arrange
    Parser parser = new Parser("[123, :]");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(false);
  }

  @Test
  public void testParseEmptyMap() {
    // Arrange
    Parser parser = new Parser("{}");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof Map);
    assertEquals(0, ((Map) value).size());
  }

  @Test
  public void testParseOnePairMap() {
    // Arrange
    Parser parser = new Parser("{\"foo\": 123}");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof Map);
    assertEquals(1, ((Map) value).size());
    assertEquals(123L, ((Map) value).get("foo"));
  }

  @Test
  public void testParseTwoPairsMap() {
    // Arrange
    Parser parser = new Parser("{\"foo\": 123, \"bar\": \"Hello World\"}");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof Map);
    assertEquals(2, ((Map) value).size());
    assertEquals(123L, ((Map) value).get("foo"));
    assertEquals("Hello World", ((Map) value).get("bar"));
  }

  @Test
  public void testParseMapWithNestedList() {
    // Arrange
    Parser parser = new Parser("{\"zero\": [], \"one\": [1], \"two\": [1, 2]}");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof Map);
    assertEquals(3, ((Map) value).size());
    assertTrue(((Map) value).get("zero") instanceof List);
    assertEquals(0, ((List)((Map) value).get("zero")).size());
    assertTrue(((Map) value).get("one") instanceof List);
    assertEquals(1, ((List)((Map) value).get("one")).size());
    assertTrue(((Map) value).get("two") instanceof List);
    assertEquals(2, ((List)((Map) value).get("two")).size());
  }

  @Test
  public void testParseListWithOneNestedEmptyMap() {
    // Arrange
    Parser parser = new Parser("[{}]");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof List);
    assertEquals(1, ((List) value).size());
    assertTrue(((List) value).get(0) instanceof Map);
    assertEquals(0, ((Map) ((List) value).get(0)).size());
  }

  @Test
  public void testParseListWithTwoNestedEmptyMap() {
    // Arrange
    Parser parser = new Parser("[{},{}]");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof List);
    assertEquals(2, ((List) value).size());
    assertTrue(((List) value).get(0) instanceof Map);
    assertEquals(0, ((Map) ((List) value).get(0)).size());
    assertTrue(((List) value).get(1) instanceof Map);
    assertEquals(0, ((Map) ((List) value).get(1)).size());
  }

  @Test
  public void testParseListWithOneNestedOnePairMap() {
    // Arrange
    Parser parser = new Parser("[{\"foo\": 123}]");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof List);
    assertEquals(1, ((List) value).size());
    assertTrue(((List) value).get(0) instanceof Map);
    assertEquals(1, ((Map) ((List) value).get(0)).size());
    assertEquals(123L, ((Map) ((List) value).get(0)).get("foo"));
  }

  @Test
  public void testParseListWithOneNestedTwoPairsMap() {
    // Arrange
    Parser parser = new Parser("[{\"foo\": 123, \"bar\": \"Hello World\"}]");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof List);
    assertEquals(1, ((List) value).size());
    assertTrue(((List) value).get(0) instanceof Map);
    assertEquals(2, ((Map) ((List) value).get(0)).size());
    assertEquals(123L, ((Map) ((List) value).get(0)).get("foo"));
    assertEquals("Hello World", ((Map) ((List) value).get(0)).get("bar"));
  }

  @Test
  public void testParseListWithTwoNestedTwoPairsMap() {
    // Arrange
    Parser parser = new Parser("[{\"foo\": 123, \"bar\": \"Hello World\", \"baz\": false}, "
        + "{\"foo\": 456, \"baz\": null}]");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof List);
    assertEquals(2, ((List) value).size());
    assertTrue(((List) value).get(0) instanceof Map);
    assertEquals(3, ((Map) ((List) value).get(0)).size());
    assertEquals(123L, ((Map) ((List) value).get(0)).get("foo"));
    assertEquals("Hello World", ((Map) ((List) value).get(0)).get("bar"));
    assertEquals(false, ((Map) ((List) value).get(0)).get("baz"));
    assertEquals(2, ((Map) ((List) value).get(1)).size());
    assertEquals(456L, ((Map) ((List) value).get(1)).get("foo"));
    assertEquals(null, ((Map) ((List) value).get(1)).get("baz"));
  }

  @Test(expected = ParseException.class)
  public void testSingleComma() {
    // Arrange
    Parser parser = new Parser(",");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(false);
  }


  @Test(expected = ParseException.class)
  public void testTwoValues() {
    // Arrange
    Parser parser = new Parser("123 456");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(false);
  }

  @Test
  public void testBigInteger() {
    // Arrange
    Parser parser = new Parser(Long.MAX_VALUE + "000");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof BigInteger);
  }

  @Test
  public void testBigDecimal() {
    // Arrange
    Parser parser = new Parser("2.5e308");

    // Act
    Object value = parser.parse();

    // Assert
    assertTrue(value instanceof BigDecimal);
  }
}