package net.objecthunter.exp4j.function;
public abstract class Function {
    public Function(String name, int argc) {}
    public Function(String name) {}
    public abstract double apply(double... args);
}
