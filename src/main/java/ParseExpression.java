public interface ParseExpression {
    ConditionNode getCondition(String expression);
    String getString(String expression);
}
