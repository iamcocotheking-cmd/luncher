package net.kdt.pojavlaunch.extra;

@FunctionalInterface
public interface ExtraListener<T> {
    boolean onValueSet(String key, T value);
}
