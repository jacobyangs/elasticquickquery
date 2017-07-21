import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

/**
 * Created by Jacob on 2017/7/17.
 */
public class ElasticUtil {
    public static TransportClient client;

    private static BoolQueryBuilder getBoolQueryBuilder(ConditionNode conditionNode){
        BoolQueryBuilder bqb = QueryBuilders.boolQuery();
        if (conditionNode==null){
            return bqb;
        }
        if (StringUtils.isNotBlank(conditionNode.getField())){
            System.out.println(conditionNode.getField());
            if (ConditionNode.NESTED.equals(conditionNode.getType())){
                String path = conditionNode.getField().substring(0,conditionNode.getField().lastIndexOf("."));
                InnerHitBuilder innerHitBuilder = new InnerHitBuilder();
                innerHitBuilder.setSize(5000);
                if (ConditionNode.NOT.equalsIgnoreCase(conditionNode.getOp())){
                    bqb.mustNot(QueryBuilders.nestedQuery(path,QueryBuilders.queryStringQuery(conditionNode.getField()+":\""+conditionNode.getValue()+"\""), ScoreMode.Min).innerHit(innerHitBuilder,false));
                }else if (ConditionNode.LT.equalsIgnoreCase(conditionNode.getOp())){
                    RangeQueryBuilder rq = QueryBuilders.rangeQuery(conditionNode.getField());
                    rq.lt(conditionNode.getValue());
                    bqb.must(QueryBuilders.nestedQuery(path,rq,ScoreMode.Min).innerHit(innerHitBuilder,false));
                }else if (ConditionNode.LTE.equalsIgnoreCase(conditionNode.getOp())){
                    RangeQueryBuilder rq = QueryBuilders.rangeQuery(conditionNode.getField());
                    rq.lte(conditionNode.getValue());
                    bqb.must(QueryBuilders.nestedQuery(path,rq,ScoreMode.Min).innerHit(innerHitBuilder,false));
                }else if (ConditionNode.GT.equalsIgnoreCase(conditionNode.getOp())){
                    RangeQueryBuilder rq = QueryBuilders.rangeQuery(conditionNode.getField());
                    rq.gt(conditionNode.getValue());
                    bqb.must(QueryBuilders.nestedQuery(path,rq,ScoreMode.Min).innerHit(innerHitBuilder,false));
                }else if (ConditionNode.GTE.equalsIgnoreCase(conditionNode.getOp())){
                    RangeQueryBuilder rq = QueryBuilders.rangeQuery(conditionNode.getField());
                    rq.gte(conditionNode.getValue());
                    bqb.must(QueryBuilders.nestedQuery(path,rq,ScoreMode.Min).innerHit(innerHitBuilder,false));
                }else {
                    bqb.must(QueryBuilders.nestedQuery(path,QueryBuilders.queryStringQuery(conditionNode.getField()+":\""+conditionNode.getValue()+"\""), ScoreMode.Min));
                }
            }else {
                if (ConditionNode.NOT.equalsIgnoreCase(conditionNode.getOp())){
                    bqb.mustNot(QueryBuilders.queryStringQuery(conditionNode.getField()+":\""+conditionNode.getValue()+"\""));
                }else if (ConditionNode.LT.equalsIgnoreCase(conditionNode.getOp())){
                    RangeQueryBuilder rq = QueryBuilders.rangeQuery(conditionNode.getField());
                    rq.lt(conditionNode.getValue());
                    bqb.must(rq);
                }
                else if (ConditionNode.LTE.equalsIgnoreCase(conditionNode.getOp())){
                    RangeQueryBuilder rq = QueryBuilders.rangeQuery(conditionNode.getField());
                    rq.lte(conditionNode.getValue());
                    bqb.must(rq);
                }
                else if (ConditionNode.GT.equalsIgnoreCase(conditionNode.getOp())){
                    RangeQueryBuilder rq = QueryBuilders.rangeQuery(conditionNode.getField());
                    rq.gt(conditionNode.getValue());
                    bqb.must(rq);
                }
                else if (ConditionNode.GTE.equalsIgnoreCase(conditionNode.getOp())){
                    RangeQueryBuilder rq = QueryBuilders.rangeQuery(conditionNode.getField());
                    rq.gte(conditionNode.getValue());
                    bqb.must(rq);
                }else {
                    bqb.must(QueryBuilders.queryStringQuery(conditionNode.getField()+":\""+conditionNode.getValue()+"\""));
                }
            }
            return bqb;
        }
        if (ConditionNode.AND.equalsIgnoreCase(conditionNode.getRelation())){
            bqb.must(getBoolQueryBuilder(conditionNode.getLeft()));
            bqb.must(getBoolQueryBuilder(conditionNode.getRight()));
        }else if (ConditionNode.OR.equalsIgnoreCase(conditionNode.getRelation())){
            bqb.should(getBoolQueryBuilder(conditionNode.getRight()));
            bqb.should(getBoolQueryBuilder(conditionNode.getLeft()));
        }
        return bqb;
    }

    public static void main(String[] args) {
        ConditionNode commons = new ConditionNode();
        ConditionNode conditionNode = new ConditionNode();
        conditionNode.setField("name");
        conditionNode.setOp("eq");
        conditionNode.setValue("yangsong");
        ConditionNode conditionNode2 = new ConditionNode();
        conditionNode2.setField("name");
        conditionNode2.setOp("eq");
        conditionNode2.setValue("wangjing");
        commons.setLeft(conditionNode);
        commons.setRight(conditionNode2);
        commons.setRelation(ConditionNode.AND);
        System.out.println(getBoolQueryBuilder(commons));
    }
}
