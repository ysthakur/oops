package io.github.ysthakur.oops.eval;

import io.github.ysthakur.oops.parse.Span;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class EvalError extends RuntimeException {
  private final String msg;
  /**
   * Where the error occurred
   */
  private final Span loc;

  public EvalError(String msg, Span loc) {
    super(loc + ": " + msg);
    this.msg = msg;
    this.loc = loc;
  }
}
