
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;



public class Index {
	
	public static final String CONTENTS = "contents";
	public static final String FILE_NAME = "filename";
	public static final String FILE_PATH = "filepath";
	public static final int MAX_SEARCH = 10;
	private IndexWriter writer;
	private String indexFile;
	private String indexDir;
	private Boolean append;
	private Directory dir;
	private Analyzer analyzer;
	private IndexWriterConfig iwc;

	public static void main(String[] args) throws IOException {
		Index i = new Index();
	}
	
	public Index() throws IOException {
		IndexFile();
	}
	
	private Document getDocument(File file) throws IOException {
	   Document document = new Document();
	   
	   //index file contents
	   TextField contentField = new TextField(CONTENTS, "TESTO DA RICERCARE", Field.Store.YES);
	   
	   //index file name
	   TextField fileNameField = new TextField(FILE_NAME, file.getName(), Field.Store.NO);
	   
	   //index file path
	   TextField filePathField = new TextField(FILE_PATH, file.getCanonicalPath(), Field.Store.NO);
	
	   //System.out.println(contentField + " " + fileNameField + " " + filePathField);
	   document.add(contentField);
	   document.add(fileNameField);
	   document.add(filePathField);
	
	   return document;
	}
	
	private void IndexFile() throws IOException {
		indexFile = "./dataset/clinical_dataset/IndexPath.txt";
		indexDir = "./index_pmc";
		append = false;
		Date start = new Date();
		try {
			System.out.println("Indexing to directory '" + indexDir + "'...");

			dir = FSDirectory.open(Paths.get(indexDir));
			analyzer = new StandardAnalyzer();
			iwc = new IndexWriterConfig(analyzer);

			if (!append) {
				// Create a new index in the directory, removing any
				// previously indexed documents:
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// Add new documents to an existing index:
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}

			//buffer ram
			iwc.setRAMBufferSizeMB(512.0);

			writer = new IndexWriter(dir, iwc);

			// read line by line(path) indexFile and add document at the index by using the
			// path of single file
			try (BufferedReader br = new BufferedReader(new FileReader(indexFile))) {
				String line;
				while ((line = br.readLine()) != null) {
					writer.addDocument(getDocument(new File(line)));
				}
			}

			writer.close();

			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}
}
