import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.Similarity;


/**
 * This class is the common ancestor of all Models implemented.
 */
public abstract class Model{
	
	/**
	 * This is how a model process query string to obtain a Query object suitable to its structure.
	 * @param query is the query string to be parsed
	 * @param fields fields on which search
	 * @param analyzer analyzer to use for parsing
	 * @return Query object for the index
	 */
	public abstract Query getQueryParsed(String query, String field, StandardAnalyzer analyzer);
	
	/**
	 * This is a way to provide to the index the similarity to use for a particular model. This element influences
	 * ranking function
	 * @return Similarity object, to be applied on index
	 */
	public abstract Similarity getSimilarity();
	
}
