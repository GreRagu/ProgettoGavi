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
    	
        dir = Paths.get("./dictionary");
        dict = Paths.get("./dictionary.txt");
        directory = FSDirectory.open(dir);
        iwc = new IndexWriterConfig();
        spellChecker = new SpellChecker(directory);
        
        d = new PlainTextDictionary(dict);
        spellChecker.indexDictionary(d, iwc, false);
        
        int suggestionsNumber = 1;
        
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
        System.out.println(Correct);
		return Correct;
    }

}