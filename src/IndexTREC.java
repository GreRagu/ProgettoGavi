
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexTREC {

	public IndexTREC() {
	}

	public static void main(String[] args) throws IOException {

		String indexDir = null;
		String indexFile = null;
		Boolean create = true;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
		String app = null;

		// Per velocizzare ho commentato la parte in cui si inseriscono i path via
		// terminale dovrebbe essere funzionante
		System.out.println("Path to the file: ");
		indexFile = "./dataset/clinical_dataset/IndexPath.txt";
		
		/*while ((indexFile = in.readLine()) != null) { 
			Path file = Paths.get(indexFile); 
			if (!Files.exists(file))
				System.out.println("File doesn't exists or empty, \n reinsert: "); 
			else
				break; 
		}*/
		

		System.out.println("Path to the index dir: ");
		indexDir = "./index_pmc";
		
		/*while ((indexDir = in.readLine()) != null) { 
			Path dir = Paths.get(indexDir);
			if (Files.isDirectory(dir))
				System.out.println("File doesn't exists, \n reinsert: ");
			else
				break;
		} */
		

		System.out.println("Do you want to append on index [y/n]: ");
		
		/*while((app = in.readLine()) != null) { 
			if(app.equals("y")) { 
				 create = true;
				 break;
				}
			else if(app.equals("n")){ 
				create = false; 
				break;
			} 
			else
				System.out.println("Wrong answer, reinsert [y/n]: "); 
		}*/

		Date start = new Date();
		try {
			System.out.println("Indexing to directory '" + indexDir + "'...");

			Directory dir = FSDirectory.open(Paths.get(indexDir));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			if (create) {
				// Create a new index in the directory, removing any
				// previously indexed documents:
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// Add new documents to an existing index:
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}

			// Optional: for better indexing performance, if you
			// are indexing many documents, increase the RAM
			// buffer. But if you do this, increase the max heap
			// size to the JVM (eg add -Xmx512m or -Xmx1g):
			//
			iwc.setRAMBufferSizeMB(512.0);

			IndexWriter writer = new IndexWriter(dir, iwc);

			// read line by line(path) indexFile and add document at the index by using the
			// path of single file
			// ----------------------------------------------------------------
			try (BufferedReader br = new BufferedReader(new FileReader(indexFile))) {
				String line;
				while ((line = br.readLine()) != null) {
					indexDoc(writer, Paths.get(line), Files.getLastModifiedTime(Paths.get(line)).toMillis());
				}
			}
			// ----------------------------------------------------------------

			// NOTE: if you want to maximize search performance,
			// you can optionally call forceMerge here. This can be
			// a terribly costly operation, so generally it's only
			// worth it when your index is relatively static (ie
			// you're done adding documents to it):
			//
			// writer.forceMerge(1);

			writer.close();

			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}

	/** Indexes a single document */
	static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {

			// make a new, empty document
			Document doc = new Document();

			// Add the path of the file as a field named "path". Use a
			// field that is indexed (i.e. searchable), but don't tokenize
			// the field into separate words and don't index term frequency
			// or positional information:
			Field pathField = new StringField("path", file.toString(), Field.Store.YES);
			doc.add(pathField);
			System.out.println("field: "+ pathField);
			System.out.println("path: "+ file.toString());
			// Add the last modified date of the file a field named "modified".
			// Use a LongPoint that is indexed (i.e. efficiently filterable with
			// NumericRangeFilter). This indexes to milli-second resolution, which
			// is often too fine. You could instead create a number based on
			// year/month/day/hour/minutes/seconds, down the resolution you require.
			// For example the long value 2011021714 would mean
			// February 17, 2011, 2-3 PM.
			// doc.add(new LongPoint("modified", lastModified, Field.Store.NO));
			doc.add(new LongPoint("modified", lastModified));

			// Add the contents of the file to a field named "contents". Specify a Reader,
			// so that the text of the file is tokenized and indexed, but not stored.
			// Note that FileReader expects the file to be in UTF-8 encoding.
			// If that's not the case searching for special characters will fail.
			doc.add(new TextField("contents",
					new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				// New index, so we just add the document (no old document can be there):
				// System.out.println("adding " + file);
				writer.addDocument(doc);
				
			} else {
				// Existing index (an old copy of this document may have been indexed) so
				// we use updateDocument instead to replace the old one matching the exact
				// path, if present:
				System.out.println("updating " + file);
				writer.updateDocument(new Term("path", file.toString()), doc);
			}
		}
	}
}
