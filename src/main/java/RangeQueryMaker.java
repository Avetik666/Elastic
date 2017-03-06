import org.elasticsearch.index.query.QueryBuilder;

import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;


/**
 *@author arman.piloyan@picsart.com
 */
public class RangeQueryMaker {

    private String from;
    private String to;
    private String format;
    private String name;

    public RangeQueryMaker(String from, String to, String format){
        this.from = from;
        this.to = to;
        this.format = format;
    }

    public String getType() {
        return name;
    }

    public void setType(String type) {
        this.name = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public QueryBuilder getQuery(String name){
        this.name = name;

        QueryBuilder query = rangeQuery(name)
                .gte(from)
                .lte(to)
                .includeLower(true)
                .includeUpper(true)
                .format(format);

        return query;
    }



}
