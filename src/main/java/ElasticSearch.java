import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * @author arman.piloyan@picsart.com
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
        SearchResponse response = client.prepareSearch("crashlytics-2017.02")
                .setFetchSource(true)
                .setQuery(qb)
                .get();
        System.out.println(response.getHits().totalHits());
        for( SearchHit hit : response.getHits()) {
            System.out.println(hit.getSource().get("platform"));
        }
        QueryBuilder qb1;
        qb1 = boolQuery()
                .must(new QueryStringQueryBuilder("*").analyzeWildcard(true))
                .must(rangeQuery("@timestamp").gte(26/02/2017).lte(28/02/2017).format("dd/mm/yyyy"))
                .mustNot( null );
        System.out.println(qb1);
        client.close();
    }
}