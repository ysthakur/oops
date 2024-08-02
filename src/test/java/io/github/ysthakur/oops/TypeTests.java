package io.github.ysthakur.oops;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeTests {
  @Test
  public void empty() {
    assertEquals(Type.Named.Empty, Type.guessType(Lists.NIL));
  }

  @Test
  public void cons() {
    assertEquals(new Type.Lst(Type.Named.Empty), Type.guessType(Lists.from(Lists.NIL)));
  }
}
