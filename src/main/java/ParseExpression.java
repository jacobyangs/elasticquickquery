



public interface ParseExpression {
    ConditionNode getCondition(String expression) throws Exception;
    String getString(String expression) throws Exception;
}
