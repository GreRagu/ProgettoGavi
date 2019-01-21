

import java.util.LinkedList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;

public class FuzzyModel extends Model {

	@Override
	public Query getQueryParsed(String query, LinkedList<String> fields, StandardAnalyzer analyzer) {
		
		int maxEdits = 0;
		/*
		 * First parsing is to remove stopwords, make stemming, removing digits, ...
		 */
		StandardQueryParser queryParser = new StandardQueryParser(analyzer);
		try {
			query = queryParser.parse(query, "").toString();
		} catch (Exception e) {
			
		}
		
		//From the query parsed, i remove all special letters used by query parsed, and eventual exceeding whitespaces
		query = query.replaceAll("[()+-:]", "");
		query = query.trim().replaceAll(" +", " "); // Removes first and last white spaces, and substitute multiple white spaces with only one white space
		
		String[] terms = query.split(" ");
		
		query = "";
		//For each "Token", this is followed by a ~ and a value representing maxEdits
		for (String term : terms) {
			term = term.replaceAll("~", "") + "~" + maxEdits;
			query += term + " ";
		}
		
		//Query is re-parsed, this time on each field, and builded using a BooleanQuery.Builder
		String query_parsed = "";
		Query q = null;
			try {
				for (String field : fields) {
					//b.add(queryParser.parse(query, field), BooleanClause.Occur.SHOULD);
					query_parsed += queryParser.parse(query, field).toString() + " ";
				}
				q = queryParser.parse(query_parsed, "");
			} catch (Exception e) {
				e.printStackTrace();
			}
        
		return q;
		//return b.build();
	}

	@Override
	public Similarity getSimilarity() {
		System.out.println("Creating a Fuzzy Model Similarity");
		return new ClassicSimilarity();
	}

}
