package net.objecthunter.exp4j;
import net.objecthunter.exp4j.function.Function;
public class ExpressionBuilder {
    public ExpressionBuilder(String expression) {}
    public ExpressionBuilder variables(String... names) { return this; }
    public ExpressionBuilder function(Function f) { return this; }
    public Expression build() { return new Expression(); }
    public static class Expression {
        public Expression setVariable(String name, double value) { return this; }
        public double evaluate() { return 0D; }
    }
}
