import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

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

public class IndexWorker extends Thread {

	public static final String CONTENTS = "contents";
	public static final String FILE_NAME = "filename";
	public static final String FILE_PATH = "filepath";
	public static final int MAX_SEARCH = 10;
	private String indexDir;
	private boolean append;
	private Directory dir;
	private Analyzer analyzer;
	private IndexWriterConfig iwc;
	private IndexWriter writer;
	private JProgressBar pb;
	private JDialog parent;
	private JFrame parentFrame;
	private MyModel M;
	private String indexFile;
	private Integer Number;
	private Integer FileNotFound;
	
	public IndexWorker(String indexDir, boolean append, JProgressBar pb, JDialog parent, JFrame parentFrame, MyModel M, String indexFile, Integer Number) {
		this.indexDir = indexDir;
		this.append = append;
		this.pb = pb;
		this.parent = parent;
		this.parentFrame = parentFrame;
		this.M = M;
		this.indexFile = indexFile;
		this.Number = Number;
		this.FileNotFound = 0;
	}
	
	private void indexCreator() throws IOException {
		dir = FSDirectory.open(Paths.get(indexDir));
		analyzer = new StandardAnalyzer();
		iwc = new IndexWriterConfig(analyzer);
		iwc.setSimilarity(M.getModelSymilarity());
		
		System.out.println(iwc.getSimilarity());

		if (!append) {
			// Create a new index in the directory, removing any
			// previously indexed documents:
			iwc.setOpenMode(OpenMode.CREATE);
		} else {
			// Add new documents to an existing index:
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		}

		// buffer ram
		iwc.setRAMBufferSizeMB(512.0);

		writer = new IndexWriter(dir, iwc);

		// apre il file indexFile e per ogni riga(path di un 
		// file da aggiungere all'indice)
		// effettua parsing attraveso la funzione getDocument passandogli il path del
		// file e successivamente l'aggiunge all'indice
		Date start = new Date();
		try (BufferedReader br = new BufferedReader(new FileReader(indexFile))) {
			String line;
			File file;

			while ((line = br.readLine()) != null) {			
				file = new File(line);
				if (file.isFile() && file.canRead()) {
					writer.addDocument(getDocument(new File(line)));
				}
				else {
					FileNotFound++;
				}
				pb.setValue(pb.getValue() + 1);
			}
		}

		writer.close();
		Date end = new Date();

		// formatta il tempo da millisecondi al formato HH:mm:ss
		Date date = new Date(end.getTime() - start.getTime());
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String formatted = formatter.format(date);

		parent.dispose();
		parentFrame.setEnabled(true);
		JOptionPane.showMessageDialog(parentFrame, "Loading completed in " + formatted, "Complete",
				JOptionPane.PLAIN_MESSAGE);
		
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(M.getPaht()));
		out.writeObject(M.getModel());
		out.writeObject(Number - FileNotFound);
		out.close();
	}
	
	private Document getDocument(File file) throws IOException {
		Document document = new Document();

		// index file contents
		TextField contentField = new TextField(CONTENTS, fileToBody(file), Field.Store.YES);

		// index file name
		TextField fileNameField = new TextField(FILE_NAME, file.getName(), Field.Store.YES);

		// index file path
		TextField filePathField = new TextField(FILE_PATH, file.getCanonicalPath(), Field.Store.YES);

		// System.out.println(contentField + " " + fileNameField + " " + filePathField);
		document.add(contentField);
		document.add(fileNameField);
		document.add(filePathField);

		return document;
	}
	
	/**
	 * Funzione che estrapola dal file xml il contenuto del body
	 * 
	 * @throws IOException
	 */
	public static String fileToBody(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		try {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}

			String result = stringBuilder.toString();

			// restituisce tutto il contenuto del tag body.
			// se non è presente restituisce stringa nulla
			if (result.indexOf("<body>") != -1) {
				result = result.substring(result.indexOf("<body>") + 6, result.indexOf("</body>"));
				return result;
			}
			return " ";
		} finally {
			reader.close();
		}

	}
	
	public void run() {
		try {
			this.indexCreator();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
