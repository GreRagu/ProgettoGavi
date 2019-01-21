
/**
 * Classe che rappresenta un singolo documento recuperato dall'indice (hit).
 * Il documento è rappresentato da un path, nome, e un punteggio.
 * Un indice ritorna una lista di hits
 * Questa classe è necassaria solo per linkare un documento al suo punteggio per una determinata query
 */
public class Hit {
	private String docPath;
	private String docName;  
    private float score;  
    
    Hit(String docPath, String docName, float score){ 
    	this.docPath = docPath;
        this.docName = docName;
        this.score = score;
    }  
    
    public String getDocPath() {
    	return docPath;
    }
    
    public String getDocName() {
    	return docName;
    }
    
    public float getScore() {
    	return score;
    }
}
