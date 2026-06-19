package net.kdt.pojavlaunch.extra;

public interface ExtraListener<T> {
    /**
     * @return true to remove this listener after the callback.
     */
    boolean onValueSet(String key, T value);
}
