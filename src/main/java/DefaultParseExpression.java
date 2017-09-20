import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultParseExpression implements ParseExpression{
    public static final String SPLIT_KEY = "((==|>|<|!=|>=|<=)\\s*(\\d+|[a-aA-Z]+))| and | or";
    public static final String MATCH_VALUE = "\\s*((==|>|<|!=|>=|<=)\\s*(\\d+|[a-aA-Z]+))| and | or";
    public static final String OPER_FLAG = "==|>|<|!=|>=|<=";
    public static final String REPLACE_STR = "REPLACE_STR";
    public static final String SPLIT_SINGLE_EXP = " and | or";
    public static final Map<String,String> operaReplaceMap = new HashMap();
    static {
        operaReplaceMap.put("==",ConditionNode.AND);
        operaReplaceMap.put("!=",ConditionNode.NOT);
        operaReplaceMap.put(">",ConditionNode.GT);
        operaReplaceMap.put(">=",ConditionNode.GTE);
        operaReplaceMap.put("<",ConditionNode.LT);
        operaReplaceMap.put("<=",ConditionNode.LTE);
    }
    //提取表达式中的括号内容 同时记录括号位置信息等
    private void parse(String str ,Record record) throws Exception{
        int size = 0;
        int num = record.getNum();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i)=='('||str.charAt(i)==')'){
                size++;
            }
        }
        if (str.indexOf('(')!=-1){
            int left = 0;
            int right = str.length();
            for (int i = 0; i < str.length(); i++) {
                if (record.getList().contains(i)) continue;
                if (str.charAt(i)=='(') left=i;
                if (str.charAt(i)==')') right=i;
            }
            String t = str.substring(left+1,right);
            if (t.split(SPLIT_SINGLE_EXP).length == 1){
                str = str.replace("(" + t + ")" ,t);
            }else {
                record.setNum(num++);
                String toreplace = getEqualStr2Replace(str,num);
                str = str.replace("(" + t + ")",toreplace);
                ConditionNode result = setExpressionNode(t,record);
                record.getToReplaceMap().put(toreplace,result);
            }
            if (size != record.getList().size()){
                parse(str,record);
            }
        }
    }
    //将记录好的recode进行递归解析  循环设置condition
    private ConditionNode setLoopExpressionNode(String single ,ConditionNode result ,Record record) throws Exception{
        ConditionNode thefinalExpression = new ConditionNode();
        if (result == null){
            String[] temp = single.split(SPLIT_SINGLE_EXP,3);
            String oper = getOperBystr(single);
            String newSingle = "tempstr "+ single.replaceFirst(temp[0]+oper+temp[1],"");
            thefinalExpression.setLeft(parseNode(temp[0],record));
            thefinalExpression.setRight(parseNode(temp[1],record));
            thefinalExpression.setRelation(oper.trim());
            if (temp.length>2){
                thefinalExpression = setLoopExpressionNode(newSingle,thefinalExpression,record);
            }
        }else {
            String[] temp = single.split(SPLIT_SINGLE_EXP,3);
            if (temp[0].equals("tempstr ")){
                String oper = getOperBystr(single);
                String newSingle = "tempstr "+single.replace(temp[0] + oper + temp[1],"");
                thefinalExpression.setLeft(result);
                thefinalExpression.setRight(parseNode(temp[1] ,record));
                thefinalExpression.setRelation(oper.trim());
                if (temp.length > 2){
                    thefinalExpression = setLoopExpressionNode(newSingle ,thefinalExpression, record);
                }
            }
        }
        return thefinalExpression;
    }
    //解析单个无括号的表达式
    private ConditionNode parseNode(String expr,Record record) throws Exception{
        String [] condition = expr.split(SPLIT_SINGLE_EXP,2);
        ConditionNode node = new ConditionNode();
        if (condition.length==2){
            node.setLeft(parseNode(condition[0],record));
            node.setRight(parseNode(condition[1],record));
            node.setRelation(getOper(expr));
            return node;
        }
        if (expr.contains(REPLACE_STR)){
            return record.getToReplaceMap().get(expr);
        }
        condition = expr.split(OPER_FLAG);
        node.setField(condition[0].trim());
        node.setOp(operaReplaceMap.get(expr.replace(condition[0],"").replace(condition[1],"")));
        node.setValue(condition[1].trim());
        return node;
    }
    //获取单个表达式的操作符
    private static String getOper(String str) throws Exception{
        if (str.contains("and")) return "and";
        else if(str.contains("or")) return "or";
        else throw new Exception("not find opera flag");
    }
    //获取单个表达式的操作符
    private String getOperBystr(String str){
        String[] temp = str.split(SPLIT_SINGLE_EXP ,2);
        return str.replaceFirst(temp[0],"").replaceFirst(temp[1],"");
    }

    //如果表达式有超过两个and或者or 继续递归解析
    private ConditionNode setExpressionNode(String single, Record record) throws Exception{
        String [] condition = single.split(SPLIT_SINGLE_EXP);
        if (condition.length > 2){
            return  setLoopExpressionNode(single , null , record);
        }else {
            return parseNode(single,record);
        }
    }
    //在parse()方法中将括号内的内容进行替换  并记录替换的表达式到recode 的map中  方便后续解析时使用之
    private String getEqualStr2Replace(String str, int num) {
        StringBuffer sb = new StringBuffer();
        int last_length = str.length()-String.valueOf(num).length()-REPLACE_STR.length();
        for (int i = 0; i < last_length - 1; i++) {
            sb.append("-");
        }
        return String.valueOf(num)+sb.toString()+REPLACE_STR;
    }

    public ConditionNode getCondition(String expression) {
        return null;
    }

    public String getString(String expression) {
        return null;
    }
    private class Record{
        //装载被替换字符的Condition
        private Map<String,ConditionNode> toReplaceMap;
        //记录括号存在的位置
        private List list;
        //几率是第几个被替换的括号内容
        private int num;

        Record(){
            toReplaceMap = new HashMap<String, ConditionNode>();
            list = new ArrayList();
            num = 0;
        }

        public Map<String, ConditionNode> getToReplaceMap() {
            return toReplaceMap;
        }

        public void setToReplaceMap(Map<String, ConditionNode> toReplaceMap) {
            this.toReplaceMap = toReplaceMap;
        }

        public List getList() {
            return list;
        }

        public void setList(List list) {
            this.list = list;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }
}
