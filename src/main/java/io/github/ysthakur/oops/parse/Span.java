package io.github.ysthakur.oops.parse;

import io.github.ysthakur.oops.Nats;
import io.github.ysthakur.oops.Value;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

/**
 * A reference to a span of text that a piece of code came from. Used for error messages.
 */
@lombok.Value
public class Span {
  public static final Span GARBAGE = new Span();

  int start;
  int end;
  SourceFile src;
  Value value;

  /**
   * @param start Start offset (0-indexed, inclusive)
   * @param end   End offset (0-indexed, exclusive)
   * @param src   Which file the code came from
   */
  public Span(int start, int end, @NotNull SourceFile src) {
    this.start = start;
    this.end = end;
    this.src = src;
    this.value = new Value(Map.of("start", Nats.fromInt(start), "end",
        Nats.fromInt(end), "src", src.getValue()));
  }

  /**
   * To be used only for garbage
   */
  private Span() {
    this.start = -1;
    this.end = -1;
    this.src = null;
    this.value = null;
  }

  public String toString() {
    if (this == Span.GARBAGE) return "GARBAGE";
    return src.getName() + ":" + start + ":" + end;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Span span)) return false;
    return start == span.start && end == span.end && Objects.equals(src, span.src);
  }

  @Override
  public int hashCode() {
    return Objects.hash(start, end, src);
  }
}
