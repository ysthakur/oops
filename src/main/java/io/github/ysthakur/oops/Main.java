package io.github.ysthakur.oops;

import io.github.ysthakur.oops.parse.Parser;
import io.github.ysthakur.oops.parse.SourceFile;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
  public static void main(String[] args) throws IOException {
    for (int i = 1; i < args.length; i++) {
      var file = SourceFile.fromFile(args[i]);
      var res = Parser.parse(file);
      if (res instanceof Parser.Result.Ok) {
        var ok = (Parser.Result.Ok) res;
        System.out.println(ok.parsed());
        // todo evaluate
      } else {
        var err = (Parser.Result.Err) res;
        var loc = err.location();
        System.err.println("Error at " + loc);
        System.err.println(err.msg());
        System.err.println(loc.src.text.substring(loc.start, loc.end));
        System.exit(1);
      }
    }
  }
}
