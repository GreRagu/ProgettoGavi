import java.util.LinkedList;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.Similarity;


/**
 * This class is the common ancestor of all Models implemented.
 */
public abstract class Model{
	
	/**
	 * This method is a short call to method submitQuery of Index class. It allows a slim call, passing
	 * model to use (itself), fields on which apply the query, query string and verbose, that toggle if index
	 * has to print the parsed query and the results on terminal.
	 * @param query is the query string
	 * @param fields is a list of fields on which search (by default, title and content)
	 * @param verbose activate/disable verbose mode
	 */
	public void query(String query, LinkedList<String> fields, boolean verbose) {
		Index i = Index.getIndex();
		i.submitQuery(query, fields, this, verbose);
	}
	
	/**
	 * This is how a model process query string to obtain a Query object suitable to its structure.
	 * @param query is the query string to be parsed
	 * @param fields fields on which search
	 * @param analyzer analyzer to use for parsing
	 * @return Query object for the index
	 */
	public abstract Query getQueryParsed(String query, LinkedList<String> fields, StandardAnalyzer analyzer);
	
	/**
	 * This is a way to provide to the index the similarity to use for a particular model. This element influences
	 * ranking function
	 * @return Similarity object, to be applied on index
	 */
	public abstract Similarity getSimilarity();
	
}
