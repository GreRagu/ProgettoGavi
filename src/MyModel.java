import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.Similarity;

public class MyModel {

	private String[] Model = {"Probabilistic(BM25) Model", "Vector Space Model (TFIDF)", "Boolean Model", "Fuzzy Model"};
	private Integer modelUsed;
	private Similarity Sim;
	private String Path = "./dataset/clinical_dataset/Model.ser";
	private Query Q;
	
	public MyModel(Integer modelUsed) {
		this.modelUsed = modelUsed;
	}
	
	public String getPaht() {
		return Path;
	}
	
	public Query getQuery(String query, String field, StandardAnalyzer analyzer) {
		if(modelUsed == 1) {
			VectorSpaceModel VSM = new VectorSpaceModel();
			Q = VSM.getQueryParsed(query, field, analyzer);
		}
		else if(modelUsed == 2) {
			BooleanModel BM = new BooleanModel();
			Q = BM.getQueryParsed(query, field, analyzer);
		}
		else if(modelUsed == 3) {
			FuzzyModel FM = new FuzzyModel();
			Q = FM.getQueryParsed(query, field, analyzer);
		}
		else {
			BM25 BM25 = new BM25();
			Q = BM25.getQueryParsed(query, field, analyzer);
		}
			
		return Q;
	}
	
	public Integer getModel() {
		return modelUsed;
	}
	
	public String getModelString() {
		return Model[modelUsed];
	}
	
	public Similarity getModelSymilarity() {
		if(modelUsed == 1) {
			VectorSpaceModel VSM = new VectorSpaceModel();
			Sim = VSM.getSimilarity();
		}
		else if(modelUsed == 2) {
			BooleanModel BM = new BooleanModel();
			Sim = BM.getSimilarity();
		}
		else if(modelUsed == 3) {
			FuzzyModel FM = new FuzzyModel();
			Sim = FM.getSimilarity();
		}
		else {
			BM25 BM25 = new BM25();
			Sim = BM25.getSimilarity();
		}
			
		return Sim;
	}
	
}
