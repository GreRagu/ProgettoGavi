
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.demo.IndexFiles;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;



public class Index {

	public static void Main() {
		File toIndexFile = new File("../dataset/clinical_dataset/pmc-text-00/01/2668905.nxml");
		IndexFile(toIndexFile);
	}
	
	   public static final String CONTENTS = "contents";
	   public static final String FILE_NAME = "filename";
	   public static final String FILE_PATH = "filepath";
	   public static final int MAX_SEARCH = 10;
	
	private Document getDocument(File file) throws IOException {
		   Document document = new Document();
		   
		   //index file contents
		   TextField contentField = new TextField(CONTENTS, "TESTO DA RICERCARE", Field.Store.YES);
		   
		   //index file name
		   TextField fileNameField = new TextField(FILE_NAME, file.getName(), Field.Store.NO);
		   
		   //index file path
		   TextField filePathField = new TextField(FILE_PATH, file.getCanonicalPath(), Field.Store.NO);

		   document.add(contentField);
		   document.add(fileNameField);
		   document.add(filePathField);

		   return document;
		}
	
	private IndexWriter writer;

	public void Indexer(String indexDirectoryPath) throws IOException {
	   //this directory will contain the indexes
	   Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
	   
	   Analyzer analyzer = new StandardAnalyzer();
	   IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
	   
	   //create the indexer
	   writer =new IndexWriter(	indexDirectory, iwc);
	}
	
	private void indexFile(File file) throws IOException {
		   System.out.println("Indexing "+file.getCanonicalPath());
		   Document document = getDocument(file);
		   writer.addDocument(document);
		   
		}
	
	
	
	
	
}
