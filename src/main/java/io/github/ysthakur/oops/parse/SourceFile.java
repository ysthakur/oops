package io.github.ysthakur.oops.parse;

import io.github.ysthakur.oops.Strings;
import io.github.ysthakur.oops.Value;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

public final class SourceFile {
  public final String name;
  public final String text;
  public final Value asValue;

  public SourceFile(String name, String text) {
    this.name = name;
    this.text = text;
    this.asValue = new Value(Map.of("name", Strings.fromJavaStr(name), "text", Strings.fromJavaStr(text)));
  }

  public static @NotNull SourceFile fromFile(String filePath) throws IOException {
    var text = Files.readString(Paths.get(filePath));
    return new SourceFile(filePath, text);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SourceFile that)) return false;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }
}
