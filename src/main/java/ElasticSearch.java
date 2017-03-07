import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;

import java.io.IOException;

/**
 *@author arman.piloyan@picsart.com
 *@author avetik.sarikyan@picsart.com
 *@author hrachya.yeghishyan@picsart.com
 */
public class ElasticSearch {


    public static void main(String[] args) throws IOException {

        //getting from and to values in epoch_millis
        long[] time = new SetTime("Mar 06 2017 15:57:00.000 ", null).time();


        //initializing client
        Client client = new ClientBuilder("PicsArtCluster", "ny-log1", "173.244.201.88", 9300).getClient();

        //Basic range query
        QueryBuilder query = new RangeQueryMaker(Long.toString(time[1]), Long.toString(time[0]), "epoch_millis").getQuery("@timestamp");

        //by os version
        SearchResponse response = new ResponseMaker(query, "crashlytics-2017.03", "by_os_version", "os_version", 5)
                .getResponseWithAggregation(client);

        System.out.println("By os version" + "\n" + response);


        //by language
        SearchResponse response1 = new ResponseMaker(query, "crashlytics-2017.03", "by_language", "language_code", 50)
                .getResponseWithAggregation(client);

        System.out.println("By language" + "\n" + response1);

        //by app_version
        SearchResponse response2 = new ResponseMaker(query, "crashlytics-2017.03", "by_app_version", "app_version", 20)
                .getResponseWithAggregation(client);

        System.out.println("By app_version" + "\n" + response2);


        //by manufacturer-model
        SearchResponse response3 = new ResponseMaker(query, "crashlytics-2017.03", "by_manufacturer", "phone_manufacturer", 5, "models_of_manufacturer", "phone_model", 50)
                .getResponseWithAggregationAndSub(client);

        Terms byManufacturer = response3.getAggregations().get("by_manufacturer");


        for(Terms.Bucket entry1 : byManufacturer.getBuckets()){
            System.out.println(entry1.getKey());
            System.out.println(entry1.getDocCount());
            System.out.println();
            Terms models = entry1.getAggregations().get("models_of_manufacturer");
            for(Terms.Bucket entry2 : models.getBuckets()){
                System.out.println(entry2.getKey());
                System.out.println(entry2.getDocCount());
            }
            System.out.println();
        }
        //System.out.println("By manufacturer-model" + "\n" + response3);


        //Crash Trend
        SearchResponse response4 = new ResponseMaker(query, "crashlytics-2017.03", "date_hist", "@timestamp", 0, "crash", "crash_case", 15)
                .getResponseWithAggregationAndSubInterval(client, 30, "Europe/Moscow", 1);


        Histogram dateHist = response4.getAggregations().get("date_hist");

        for(Histogram.Bucket entry1 : dateHist.getBuckets()){
            System.out.println(entry1.getKey());
            System.out.println(entry1.getDocCount());
            System.out.println();
            Terms crashCase = entry1.getAggregations().get("crash");
            for(Terms.Bucket entry2 : crashCase.getBuckets()){
                System.out.println(entry2.getKey());
                System.out.println(entry2.getDocCount());
            }
            System.out.println();
        }
        //System.out.println("Crash trend count" + "\n" + response4);


        //Basic boolean query
        QueryBuilder query1 = new BoolQueryMaker("*", 0).getBoolQuery(query);

        //Crash count
        SearchResponse response5 = new ResponseMaker(query1, "crashlytics-2017.03").getResponse(client);

        System.out.println("Crash count" + "\n" + response5.getHits().getTotalHits());


        //Crash count by device ID
        SearchResponse response6 = new ResponseMaker(query1, "crashlytics-2017.03", "agg", "device_id", 0)
                .getResponseWithCardAggregation(client);

        Cardinality agg = response6.getAggregations().get("agg");
        System.out.println("Devices affected, Unique count of device_id " + "\n" + agg.getValue());


        //unique count by phone model
        SearchResponse response7 = new ResponseMaker(query1, "crashlytics-2017.03", "agg", "phone_model", 0)
                .getResponseWithCardAggregation(client);

        Cardinality agg1 = response7.getAggregations().get("agg");
        System.out.println("Phone models affected, Unique count of phone_model" + "\n" + agg1.getValue());


        //unique count by user id
        SearchResponse response8 = new ResponseMaker(query1, "crashlytics-2017.03", "agg", "user_id", 0)
                .getResponseWithCardAggregation(client);

        Cardinality agg2 = response8.getAggregations().get("agg");
        System.out.println("Logged in users affected, Unique count of user_id" + "\n" + agg2.getValue());


       //by app version with filter
        SearchResponse response9 = new ResponseMaker(query, "crashlytics-2017.03", "by_app_version", "app_version", 20)
                .getResponseWithAggregationFilter(client, "crash_case", "com.picsart.analytics.services.PAanalyticsService$3.onReceive(ProGuard:152)");

        System.out.println("By app_version with filter" + "\n" + response9);


        client.close();


    }
}