package io.github.ysthakur.oops;

import io.github.ysthakur.oops.eval.Eval;
import io.github.ysthakur.oops.parse.Parser;
import io.github.ysthakur.oops.parse.SourceFile;

import java.io.IOException;

public class Main {
  public static void main(String[] args) throws IOException {
    for (int i = 1; i < args.length; i++) {
      var file = SourceFile.fromFile(args[i]);
      try {
        var parsed = Parser.parse(file);
        System.out.println("Parsed " + parsed);
        var res = new Eval().eval(parsed);
        System.out.println("Result " + res);
      } catch (Parser.ParseError err) {
        var loc = err.location();
        System.err.println("Error at " + loc);
        System.err.println(err.msg());
        System.err.println(loc.getSrc().getText().substring(loc.getStart(), loc.getEnd()));
        System.exit(1);
      }
    }
  }
}

