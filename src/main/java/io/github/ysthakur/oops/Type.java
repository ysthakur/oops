package io.github.ysthakur.oops;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public sealed interface Type {
  Type Any = new Or(new HashSet<>());

  Type Str = new Type.Lst(Named.Nat);

  static Type or(@NotNull Iterable<Type> types) {
    var listElemTypes = new HashSet<Type>();
    var otherTypes = new HashSet<Type>();
    for (var type : types) {
      if (type instanceof Lst listType) {
        listElemTypes.add(listType.elem());
      } else {
        otherTypes.add(type);
      }
    }
    if (otherTypes.contains(Named.Empty) && !listElemTypes.isEmpty()) {
      otherTypes.remove(Named.Empty);
    }
    if (listElemTypes.isEmpty() && otherTypes.isEmpty()) {
      return Type.Any;
    }
    if (!listElemTypes.isEmpty()) {
      otherTypes.add(new Lst(or(listElemTypes)));
    }
    if (otherTypes.size() == 1) {
      return otherTypes.iterator().next();
    } else {
      return new Or(otherTypes);
    }
  }

  static Type or(Type... types) {
    return or(List.of(types));
  }

  static Type guessType(@NotNull Value value) {
    if (value.equals(Lists.NIL)) {
      return Type.Named.Empty;
    }

    if (Lists.isCons(value)) {
      var carType = guessType(Lists.car(value));
      var cdrType = guessType(Lists.cdr(value));
      if (cdrType.isList()) {
        return or(new Lst(carType), cdrType);
      } else {
        return new Pair(carType, cdrType);
      }
    }

    var prev = value.get("prev");
    if (prev != null && guessType(prev).isNat()) {
      return Type.Named.Nat;
    }

    return Type.Any;
  }

  default boolean subtypeOf(@NotNull Type superType) {
    if (this.equals(superType)) {
      return true;
    }
    if (Type.Any.equals(superType)) {
      return true;
    }
    if (superType instanceof Or orType) {
      for (var orSuperType : orType.types) {
        if (this.subtypeOf(orSuperType)) {
          return true;
        }
      }
    }

    return false;
  }

  default boolean isList() {
    return this instanceof Lst || this == Named.Empty;
  }

  default boolean isNat() {
    return this == Named.Nat || this == Named.Empty;
  }

  enum Named implements Type {
    Empty,
    Nat,
    Unknown;

    @Override
    public boolean subtypeOf(@NotNull Type superType) {
      return this == Empty && (superType instanceof Type.Lst || superType.subtypeOf(Nat));
    }
  }

  record Or(Set<Type> types) implements Type {
  }

  record And(Set<Type> types) implements Type {
    @Override
    public boolean subtypeOf(@NotNull Type superType) {
      for (var type : types) {
        if (type.subtypeOf(superType)) {
          return true;
        }
      }

      return false;
    }
  }

  record Lst(Type elem) implements Type {
    @Override
    public boolean subtypeOf(@NotNull Type superType) {
      if (Type.super.subtypeOf(superType)) {
        return true;
      }
      if (superType instanceof Lst listSuperType) {
        return elem.subtypeOf(listSuperType.elem());
      }
      return false;
    }
  }

  record Pair(Type fst, Type snd) implements Type {
  }
}
