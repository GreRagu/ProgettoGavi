import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.hunspell.Dictionary;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SimpleSuggestionService {
    
    public static void main(String[] args) throws Exception {
        
        Path dir = Paths.get("./dictionary");
        Path dict = Paths.get("./dictionary.txt");
        Directory directory = FSDirectory.open(dir);
        IndexWriterConfig iwc = new IndexWriterConfig();
        SpellChecker spellChecker = new SpellChecker(directory);
        
        PlainTextDictionary d = new PlainTextDictionary(dict);
        spellChecker.indexDictionary(d, iwc, false);
        
        String wordForSuggestions = "diseast"; //instead of heart
        
        int suggestionsNumber = 1;

        String[] suggestions = spellChecker.
            suggestSimilar(wordForSuggestions, suggestionsNumber);

        if (suggestions!=null && suggestions.length>0) {
            for (String word : suggestions) {
                System.out.println("Did you mean:" + word);
            }
        }
        else {
            System.out.println("No suggestions found for word:"+wordForSuggestions);
        }
            
    }

}