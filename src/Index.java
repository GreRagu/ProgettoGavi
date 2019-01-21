
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;



public class Index {
	
	public static final String CONTENTS = "contents";
	public static final String FILE_NAME = "filename";
	public static final String FILE_PATH = "filepath";
	public static final int MAX_SEARCH = 10;
	private IndexWriter writer;

	public static void main(String[] args) throws IOException {
		Index i = new Index();
	}
	
	public Index() throws IOException {
		Indexer("./index_pmc");
		File toIndexFile = new File("../dataset/clinical_dataset/pmc-text-00/01/2668905.nxml");
		IndexFile(toIndexFile);
	}
	
	private Document getDocument(File file) throws IOException {
	   Document document = new Document();
	   
	   //index file contents
	   TextField contentField = new TextField(CONTENTS, "TESTO DA RICERCARE", Field.Store.YES);
	   
	   //index file name
	   TextField fileNameField = new TextField(FILE_NAME, file.getName(), Field.Store.NO);
	   
	   //index file path
	   TextField filePathField = new TextField(FILE_PATH, file.getCanonicalPath(), Field.Store.NO);
	
	   System.out.println(contentField + " " + fileNameField + " " + filePathField);
	   document.add(contentField);
	   document.add(fileNameField);
	   document.add(filePathField);
	
	   return document;
	}

	public void Indexer(String indexDirectoryPath) throws IOException {
	   //this directory will contain the indexes
	   Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
	   
	   Analyzer analyzer = new StandardAnalyzer();
	   IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
	   
	   //create the indexer
	   writer = new IndexWriter(indexDirectory, iwc);
	}
	
	private void IndexFile(File file) throws IOException {
	   System.out.println("Indexing "+ file.getCanonicalPath());
	   
	   Document doc = new Document();
	   
	   doc = getDocument(file);
	   
	   System.out.println(doc);
	   
	   writer.addDocument(doc);
	   writer.close();
	   
	}
}
