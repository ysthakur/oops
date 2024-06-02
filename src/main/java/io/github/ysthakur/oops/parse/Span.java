package io.github.ysthakur.oops.parse;

import io.github.ysthakur.oops.Nats;
import io.github.ysthakur.oops.Value;

import java.util.Map;
import java.util.Objects;

/**
 * A reference to a span of text that a piece of code came from. Used for error messages.
 */
public class Span {
  public final int start;
  public final int end;
  public final SourceFile src;
  public final Value asValue;

  /**
   * @param start Start offset (0-indexed, inclusive)
   * @param end   End offset (0-indexed, exclusive)
   * @param src   Which file the code came from
   */
  public Span(int start, int end, SourceFile src) {
    this.start = start;
    this.end = end;
    this.src = src;
    this.asValue = new Value(Map.of("start", Nats.fromInt(start), "end", Nats.fromInt(end), "src", src.asValue));
  }

  public String toString() {
    return src.name + ":" + start + ":" + end;
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
