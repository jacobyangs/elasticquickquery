import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Jacob on 2017/7/17.
 */
public class ConditionNode {
    //与
    public static final String AND = "and";
    //或
    public static final String OR = "or";
    //区间
    public static final String RANGE = "range";
    //非
    public static final String NOT = "not";
    //小于
    public static final String LT = "lt";
    //小于等于
    public static final String LTE = "lte";
    //大于
    public static final String GT = "gte";
    //大于等于
    public static final String GTE = "gte";
    //等于
    public static final String EQ = "eq";

    //普通
    public static final String NORMAL = "normal";
    //内嵌
    public static final String NESTED = "nested";
    //字段名称
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String field;
    //字段值
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String value;
    //条件类型
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String op = EQ;
    //字段类型
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String type = NORMAL;
    //左子节点
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ConditionNode left;
    //右子节点
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ConditionNode right;
    //子节点关系
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String relation;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public ConditionNode getLeft() {
        return left;
    }

    public void setLeft(ConditionNode left) {
        this.left = left;
    }

    public ConditionNode getRight() {
        return right;
    }

    public void setRight(ConditionNode right) {
        this.right = right;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
