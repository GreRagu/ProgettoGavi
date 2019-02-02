import org.apache.lucene.search.similarities.Similarity;

public class MyModel {

	private String[] Model = {"Probabilistic(BM25) Model", "Vector Space Model (TFIDF)", "Boolean Model", "Fuzzy Model"};
	private Integer modelUsed;
	private Similarity Sim;
	private String Path = "./dataset/clinical_dataset/Model.ser";
	
	public MyModel(Integer modelUsed) {
		this.modelUsed = modelUsed;
	}
	
	public String getPaht() {
		return Path;
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
