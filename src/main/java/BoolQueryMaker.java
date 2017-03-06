import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;


/**
 *@author arman.piloyan@picsart.com
 */
public class BoolQueryMaker {

    private String queryString;
    private int size;


    public BoolQueryMaker(String queryString,  int size){
        this.queryString = queryString;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public QueryBuilder getBoolQuery(QueryBuilder rangeQuery){
        QueryBuilder query = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.queryStringQuery(queryString).analyzeWildcard(true))
                .must(rangeQuery);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);
        searchSourceBuilder.size(size);

        return searchSourceBuilder.query();

    }



}
