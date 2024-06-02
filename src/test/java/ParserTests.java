import io.github.ysthakur.oops.parse.Parser;
import io.github.ysthakur.oops.parse.Parser.Result;
import io.github.ysthakur.oops.parse.SourceFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTests {
  Result parse(String str) {
    return Parser.parse(new SourceFile("test", str));
  }

  @Test
  void unclosed() {
    var res = parse("{ foo");
    assertInstanceOf(Result.Err.class, res, res.toString());
    var err = (Result.Err) res;
    assertTrue(err.msg().contains("Unexpected EOF"), err.toString());
  }

  @ParameterizedTest
  @ValueSource(strings = {"foo", " foo", "foo ", " ? "})
  void singleAtom() {
//    assertEquals();
  }
}
