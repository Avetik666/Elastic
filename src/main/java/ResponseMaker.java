import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.joda.time.DateTimeZone;

/**
 *@author arman.piloyan@picsart.com
 */
public class ResponseMaker {

    private QueryBuilder query;
    private String index;
    private String aggName;
    private String aggField;
    private int size;
    private String subaggName;
    private String subaggField;
    private int subsize;
    private MatchPhraseQueryMaker matchPhraseQueryBuilder;

    public ResponseMaker(QueryBuilder query, String index){
        this.query = query;
        this.index = index;
    }

    public ResponseMaker(QueryBuilder query, String index, String aggName, String aggField, int size){
        this.query = query;
        this.index = index;
        this.aggName = aggName;
        this.aggField = aggField;
        this.size = size;
    }

    public ResponseMaker(QueryBuilder query, String index, String aggName, String aggField, int size, String subaggName, String subaggField, int subsize){
        this.query = query;
        this.index = index;
        this.aggName = aggName;
        this.aggField = aggField;
        this.size = size;
        this.subaggName = subaggName;
        this.subaggField = subaggField;
        this.subsize = subsize;
    }

    public String getSubaggName() {
        return subaggName;
    }

    public void setSubaggName(String subaggName) {
        this.subaggName = subaggName;
    }

    public String getSubaggField() {
        return subaggField;
    }

    public void setSubaggField(String subaggField) {
        this.subaggField = subaggField;
    }

    public int getSubsize() {
        return subsize;
    }

    public void setSubsize(int subsize) {
        this.subsize = subsize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public QueryBuilder getQuery() {
        return query;
    }

    public void setQuery(QueryBuilder query) {
        this.query = query;
    }

    public String getAggName() {
        return aggName;
    }

    public void setAggName(String aggName) {
        this.aggName = aggName;
    }

    public String getAggField() {
        return aggField;
    }

    public void setAggField(String aggField) {
        this.aggField = aggField;
    }

    public SearchResponse getResponse(Client client){
        SearchResponse response = client.prepareSearch(index)
                .setFetchSource(true)
                .setQuery(query)
                .get();
        return response;
    }

    public SearchResponse getResponseWithAggregation(Client client){
        SearchResponse response = client.prepareSearch(index)
                .setQuery(query)
                .addAggregation(
                        AggregationBuilders.terms(aggName).field(aggField).size(size)
                )
                .execute().actionGet();
        return response;
    }

    public SearchResponse getResponseWithCardAggregation(Client client){
        SearchResponse response = client.prepareSearch(index)
                .setQuery(query)
                .addAggregation(
                        AggregationBuilders.cardinality(aggName).field(aggField)
                )
                .execute().actionGet();
        return response;
    }

    public SearchResponse getResponseWithAggregationAndSub(Client client){

        SearchResponse response = client.prepareSearch(index)
                .setQuery(query)
                .addAggregation(
                        AggregationBuilders.terms(aggName).field(aggField).size(size)
                                .subAggregation(
                                        AggregationBuilders.terms(subaggName).field(subaggField).size(subsize)
                                )
                )

                .execute().actionGet();
        return response;
    }

    public SearchResponse getResponseWithAggregationAndSubInterval(Client client, int intervals, String timezone, int minCount){

        SearchResponse response = client.prepareSearch(index)
                .setQuery(query)
                .addAggregation(
                        AggregationBuilders.dateHistogram(aggName).field(aggField).dateHistogramInterval(DateHistogramInterval.seconds(intervals)).timeZone(DateTimeZone.forID(timezone)).minDocCount(minCount)
                                .subAggregation(
                                        AggregationBuilders.terms(subaggName).field(subaggField).size(15)
                                )
                )
                .execute().actionGet();
        return response;
    }

    public SearchResponse getResponseFilter(Client client, String filterName, String filterValue){
        this.matchPhraseQueryBuilder = new MatchPhraseQueryMaker(filterName, filterValue);
        SearchResponse response = client.prepareSearch(index)
                .setFetchSource(true)
                .setQuery(query)
                .setQuery(matchPhraseQueryBuilder.getMatchQuery())
                .get();
        return response;
    }

    public SearchResponse getResponseWithAggregationFilter(Client client,String filterName, String filterValue){
        this.matchPhraseQueryBuilder = new MatchPhraseQueryMaker(filterName, filterValue);
        SearchResponse response = client.prepareSearch(index)
                .setQuery(matchPhraseQueryBuilder.getMatchQuery())
                .setQuery(query)
                .addAggregation(
                        AggregationBuilders.terms(aggName).field(aggField).size(size)
                )
                .execute().actionGet();
        return response;
    }

    public SearchResponse getResponseWithCardAggregationFilter(Client client,String filterName, String filterValue){
        this.matchPhraseQueryBuilder = new MatchPhraseQueryMaker(filterName, filterValue);
        SearchResponse response = client.prepareSearch(index)
                .setQuery(query)
                .setQuery(matchPhraseQueryBuilder.getMatchQuery())
                .addAggregation(
                        AggregationBuilders.cardinality(aggName).field(aggField)
                )
                .execute().actionGet();
        return response;
    }

    public SearchResponse getResponseWithAggregationAndSubFilter(Client client,String filterName, String filterValue){
        this.matchPhraseQueryBuilder = new MatchPhraseQueryMaker(filterName, filterValue);
        SearchResponse response = client.prepareSearch(index)
                .setQuery(query)
                .setQuery(matchPhraseQueryBuilder.getMatchQuery())
                .addAggregation(
                        AggregationBuilders.terms(aggName).field(aggField).size(size)
                                .subAggregation(
                                        AggregationBuilders.terms(subaggName).field(subaggField).size(subsize)
                                )
                )

                .execute().actionGet();
        return response;
    }

    public SearchResponse getResponseWithAggregationAndSubIntervalFilter(Client client, int intervals, String timezone, int minCount,String filterName, String filterValue){
        this.matchPhraseQueryBuilder = new MatchPhraseQueryMaker(filterName, filterValue);
        SearchResponse response = client.prepareSearch(index)
                .setQuery(query)
                .setQuery(matchPhraseQueryBuilder.getMatchQuery())
                .addAggregation(
                        AggregationBuilders.dateHistogram(aggName).field(aggField).dateHistogramInterval(DateHistogramInterval.seconds(intervals)).timeZone(DateTimeZone.forID(timezone)).minDocCount(minCount)
                                .subAggregation(
                                        AggregationBuilders.terms(subaggName).field(subaggField).size(15)
                                )
                )
                .execute().actionGet();
        return response;
    }







}
