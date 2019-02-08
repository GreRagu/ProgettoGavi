import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SimpleSuggestionService {
	
	private String[] text;
	private Path dir;
	private Path dict;
	private Directory directory;
	private IndexWriterConfig iwc;
	private SpellChecker spellChecker;
	private PlainTextDictionary d;
	private String Correct = "";
	
    
    public SimpleSuggestionService(String[] text) {
    	this.text = text;
    }
    
    public String DidYouMean() throws IOException {
    	
	//per il servizio "Did you mean" è stato utilizzato un dizionario di lingua inglese specifico
	//per l'ambito medico
        dir = Paths.get("./dictionary");
        dict = Paths.get("./dictionary.txt");
        directory = FSDirectory.open(dir);
        spellChecker = new SpellChecker(directory);
        
        d = new PlainTextDictionary(dict);
        File dictdir = new File(dir.toString());
        
        if(dictdir.isDirectory() && dictdir.list().length > 0) {
        	iwc = new IndexWriterConfig();
        	spellChecker.indexDictionary(d, iwc, false);
        }
        
        int suggestionsNumber = 1;
        
	//per ogni stringa ne suggerisce una composta dalle parole del dizionario
        for(int i = 0; i < text.length; i++) {
        	String[] suggestions = spellChecker.suggestSimilar(text[i], suggestionsNumber);

            if (suggestions!=null && suggestions.length>0) {
                for (String word : suggestions) {
                    System.out.println("Did you mean:" + word);
                    Correct += word + " ";
                }
            }
            else {
                System.out.println("No suggestions found for word:" + text[i]);
                Correct += text[i] + " ";
            }
        }
        spellChecker.close();
		return Correct;
    }

}
