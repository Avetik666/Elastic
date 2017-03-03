

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

/**
 * Created by armanmac on 2/16/17.
 */
public class ElasticSearch {
    public static long[] time(String from, String to) {
        long date[] = new long[2];
        SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS ");
        df.setTimeZone(TimeZone.getTimeZone("GMT+4"));
        Date dateto = null;
        Date fromtime = null;
        try {
            fromtime = df.parse(from);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calobj = Calendar.getInstance();

        if (to == null) {
            try {
                String current = df.format(calobj.getTime());
                dateto = df.parse(current);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                dateto = df.parse(to);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        long epochto = dateto.getTime();
        long epochfrom = fromtime.getTime();
        date[0] = epochto;
        date[1] = epochfrom;
        return date;
    }

    public static void main(String[] args) throws IOException {
        long[] time = time("Mar 03 2017 15:14:00.000 ", null);

        Settings settings = Settings.builder()
                .put("cluster.name", "PicsArtCluster")
                .put("node.name", "ny-log1").build();
        TransportAddress address = new InetSocketTransportAddress(InetAddress.getByName("173.244.201.88"), 9300);
        Client client = new PreBuiltTransportClient(settings).
                addTransportAddress(address);
        QueryBuilder qb = matchAllQuery();


       /* Hrach's methods */


        //by os version
        QueryBuilder query1 = rangeQuery("@timestamp")
                .from("1488286689614")
                .to("1488287589614")
                .includeLower(true)
                .includeUpper(true)
                .format("epoch_millis");

        SearchResponse response3 = client.prepareSearch("crashlytics-2017.02")
                .setQuery(query1)
                .addAggregation(
                        AggregationBuilders.terms("by_os_version").field("os_version").size(5)
                )
                .execute().actionGet();

        System.out.print("\n" + response3.toString());


        //by language
        QueryBuilder qry1 = rangeQuery("@timestamp")
                .gte("1488544479895")
                .lte("1488545379895")
                .format("epoch_millis");

        SearchResponse resp1 = client.prepareSearch("crashlytics-2017.03")
                .setQuery(qry1)
                .addAggregation(
                        AggregationBuilders.terms("by_language").field("language_code").size(50)
                )
                .execute().actionGet();

        System.out.print("\n" + resp1.toString());


        //by manufacturer-model
        QueryBuilder query2 = rangeQuery("@timestamp")
                .from("1488375445536")
                .to("1488376345536")
                .includeLower(true)
                .includeUpper(true)
                .format("epoch_millis");

        SearchResponse response4 = client.prepareSearch("crashlytics-2017.03")
                .setQuery(query2)
                .addAggregation(
                        AggregationBuilders.terms("by_manufacturer").field("phone_manufacturer").size(5)
                                .subAggregation(
                                        AggregationBuilders.terms("models_of_manufacturer").field("phone_model").size(50)
                                )
                )

                .execute().actionGet();
        System.out.print("\n" + response4.toString());


        //Crash Trend
        QueryBuilder queryBuilder = rangeQuery("@timestamp")
                .gte("1488450868673")
                .lte("1488451768673")
                .format("epoch_millis");

        SearchResponse searchResponse = client.prepareSearch("crashlytics-2017.03")
                .setQuery(queryBuilder)
                .addAggregation(
                        AggregationBuilders.dateHistogram("date_hist").field("@timestamp").dateHistogramInterval(DateHistogramInterval.seconds(30)).timeZone(DateTimeZone.forID("Europe/Moscow")).minDocCount(1)
                                .subAggregation(
                                        AggregationBuilders.terms("crash").field("crash_case").size(15)
                                )
                )
                .execute().actionGet();


        System.out.print("\n" + searchResponse.toString());


        //end of Hrach's queries



        //Arman's queries

        //query: Crash count
        QueryBuilder test_query = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.queryStringQuery("*").analyzeWildcard(true))
                .must(rangeQuery("@timestamp").gte(Long.toString(time[1])).lte(Long.toString(time[0])).format("epoch_millis"));

        SearchSourceBuilder searchSourceBuilder1 = new SearchSourceBuilder();
        searchSourceBuilder1.query(test_query);
        searchSourceBuilder1.size(0);


        //sending search query for Crash count
        SearchResponse response1 = client.prepareSearch("crashlytics-2017.03")
                .setFetchSource(true)
                .setQuery(searchSourceBuilder1.query())
                .get();

        System.out.println("\n"+"Crash Count" + ":" + " " + response1.getHits().totalHits());


        //sending search query for unique device ID Crash count
        CardinalityAggregationBuilder aggregation = AggregationBuilders
                .cardinality("agg")
                .field("device_id");


        SearchResponse response_2 = client.prepareSearch("crashlytics-2017.03")
                .setFetchSource(true)
                .setQuery(searchSourceBuilder1.query())
                .addAggregation(aggregation)
                .get();

        Cardinality agg = response_2.getAggregations().get("agg");
        System.out.println("Devices affected, Unique count of device_id " + ":" + " " + agg.getValue());

        //sending search query for unique count for phone model

        CardinalityAggregationBuilder aggregation_1 = AggregationBuilders
                .cardinality("agg")
                .field("phone_model");

        SearchResponse response_3 = client.prepareSearch("crashlytics-2017.03")
                .setFetchSource(true)
                .setQuery(searchSourceBuilder1.query())
                .addAggregation(aggregation_1)
                .get();

        Cardinality agg1 = response_3.getAggregations().get("agg");
        System.out.println("Phone models affected, Unique count of phone_model" + ":" + " " + agg1.getValue());


        //sending search query for unique count for user id

        CardinalityAggregationBuilder aggregation_2 = AggregationBuilders
                .cardinality("agg")
                .field("user_id");

        SearchResponse response_4 = client.prepareSearch("crashlytics-2017.03")
                .setFetchSource(true)
                .setQuery(searchSourceBuilder1.query())
                .addAggregation(aggregation_2)
                .get();

        Cardinality agg2 = response_4.getAggregations().get("agg");
        System.out.println("Logged in users affected, Unique count of user_id" + ":" + " " + agg2.getValue());


        //end of Arman's methods





        client.close();


    }
}