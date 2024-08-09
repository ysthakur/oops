package io.github.ysthakur.oops.parse;

import io.github.ysthakur.oops.Strings;
import io.github.ysthakur.oops.Value;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@lombok.Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SourceFile {
  @NonNull @EqualsAndHashCode.Include
  String name;
  @NonNull String text;
  @NonNull Value value;

  public SourceFile(@NotNull String name, @NotNull String text) {
    this.name = name;
    this.text = text;
    this.value = new Value(Map.of("name", Strings.from(name), "text", Strings.from(text)));
  }

  public static @NotNull SourceFile fromFile(@NotNull String filePath) throws IOException {
    var text = Files.readString(Paths.get(filePath));
    return new SourceFile(filePath, text);
  }
}
