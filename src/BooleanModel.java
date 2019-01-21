

import java.util.LinkedList;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.Similarity;

public class BooleanModel extends Model{
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see irModels.Model#getQueryParsed(java.lang.String, java.util.LinkedList, org.apache.lucene.analysis.standard.StandardAnalyzer)
	 */
	public Query getQueryParsed(String query, LinkedList<String> fields, StandardAnalyzer analyzer) {
		
		StandardQueryParser queryParser = null;
		Query q = null;
		Builder finalQuery = new BooleanQuery.Builder();
		queryParser = new StandardQueryParser(analyzer);
		
		for(String field : fields) {
			try {
				q = queryParser.parse(query, field);
			} catch (QueryNodeException e) {
				e.printStackTrace();
			}
			
			/*
			 * Using all "MUST" occurs is equivalent to "AND" operator. Using SHOULD is equivalent to "OR" 
			 * operator between queries
			 */			
			finalQuery.add(q, BooleanClause.Occur.SHOULD); 
		}
		
		return finalQuery.build();
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see irModels.Model#getSimilarity()
	 */
	public Similarity getSimilarity() {
		return new BooleanSimilarity();
	}
}