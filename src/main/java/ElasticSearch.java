

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

/**
 * Created by armanmac on 2/16/17.
 */
public class ElasticSearch {
    public static void main(String[] args) throws IOException {
        Settings settings = Settings.builder()
                .put("cluster.name", "PicsArtCluster")
                .put("node.name", "ny-log1").build();
        TransportAddress address = new InetSocketTransportAddress(InetAddress.getByName("173.244.201.88"), 9300);
        Client client = new PreBuiltTransportClient(settings).
                addTransportAddress(address);
        QueryBuilder qb = matchAllQuery();


        //query: Crash count
        QueryBuilder test_query = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.queryStringQuery("*").analyzeWildcard(true))
                .must(rangeQuery("@timestamp").gte("1488449959307" ).lte("1488450859307").format("epoch_millis"));

        SearchSourceBuilder searchSourceBuilder1 = new SearchSourceBuilder();
        searchSourceBuilder1.query(test_query);
        searchSourceBuilder1.size(0);


        //sending search query for Crash count
        SearchResponse response1 = client.prepareSearch("crashlytics-2017.03")
                .setFetchSource(true)
                .setQuery(searchSourceBuilder1.query())
                .get();

        System.out.println(response1.getHits().totalHits());




        //sending search query for unique device ID Crash count
        CardinalityAggregationBuilder aggregation = AggregationBuilders
                .cardinality("agg")
                .field("device_id");


        SearchResponse response2 = client.prepareSearch("crashlytics-2017.03")
                .setFetchSource(true)
                .setQuery(searchSourceBuilder1.query())
                .addAggregation(aggregation)
                .get();

        Cardinality agg = response2.getAggregations().get("agg");
        System.out.println(agg.getValue());


        //by os version
        QueryBuilder query1= rangeQuery("@timestamp")
                .from("1488286689614")  //specific numbers, to compare the results with the one in logging.picsart.tools
                .to("1488287589614")
                .includeLower(true)
                .includeUpper(true)
                .format("epoch_millis");

        SearchResponse response3 = client.prepareSearch("crashlytics-2017.02")
                .setQuery(qb)
                .addAggregation(
                        AggregationBuilders.terms("by_os_version").field("os_version").size(5)
                )
                .execute().actionGet();

        System.out.print(response3.toString());

//




        client.close();














    }
}