import org.elasticsearch.index.query.MatchPhraseQueryBuilder;

/**
 *@author hrachya.yeghishyan@picsart.com
 *@author avetik.sarikyan@picsart.com
 */
public class MatchPhraseQueryMaker {
    private String filterName;
    private String filterValue;

    public MatchPhraseQueryMaker(String filterName, String filterValue) {
        this.filterName = filterName;
        this.filterValue = filterValue;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public MatchPhraseQueryBuilder getMatchQuery(){
        MatchPhraseQueryBuilder matchQuery = new MatchPhraseQueryBuilder(filterName, filterValue);
        return matchQuery;
    }



}
