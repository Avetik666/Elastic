import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
/**
 * Created by armanmac on 2/16/17.
 */
public class ElasticSearch {
    public static long[] time(String from, String to) {
        long date[] = new long[2];
        SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
        Date datefrom = null;
        Date dateto = null;
        if (to == null) {
            Calendar calobj = Calendar.getInstance();
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
        try {
            datefrom = df.parse(from);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long epochfrom = datefrom.getTime();
        long epochto = dateto.getTime();
        date[0] = epochfrom;
        date[1] = epochto;
        return date;

    }
    public static void main(String[] args) throws IOException {
        long[] time = time("Mar 01 2017 15:53:00.000 UTC", null);
        //System.out.println(time[0]+ "\n" +  time[1]);


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
                .must(rangeQuery("@timestamp").gte(Long.toString(time[0])).lte(Long.toString(time[1])).format("epoch_millis"));
        SearchSourceBuilder searchSourceBuilder1 = new SearchSourceBuilder();
        searchSourceBuilder1.query(test_query);
        searchSourceBuilder1.size(0);
        //sending search query for Crash count
        SearchResponse response1 = client.prepareSearch("crashlytics-2017.03")
                .setFetchSource(true)
                .setQuery(searchSourceBuilder1.query())
                .get();
        System.out.println(response1.getHits().totalHits());
        //query:

    }
}