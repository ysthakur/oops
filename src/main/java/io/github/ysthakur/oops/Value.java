package io.github.ysthakur.oops;

import java.util.Map;

public record Value(Map<String, Value> fields) {}
