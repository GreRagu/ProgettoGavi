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

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


/**
 * Classe che implementa un Thread per la creazione dell'indice,
 * effettuando parsing di ogni documento presente nell'IndexPath,
 * riuscendo a ridurre le dimesioni iniziali del dataset 
 * di circa 1/4
 * @author
 *
 */
public class IndexWorker extends Thread {

	public static final String CONTENTS = "contents";
	public static final String FILE_NAME = "filename";
	public static final String FILE_PATH = "filepath";
	public static final int MAX_SEARCH = 10;
	private String indexDir;
	private boolean append;
	private Directory dir;
	private StandardAnalyzer analyzer;
	private IndexWriterConfig iwc;
	private IndexWriter writer;
	private JProgressBar pb;
	private JDialog parent;
	private JFrame parentFrame;
	private MyModel M;
	private String indexFile;
	private Integer Number;
	private Integer FileNotFound;
	private Document document;
	private BufferedReader reader;
	private TextField contentField;
	private TextField fileNameField;
	private TextField filePathField;
	
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
	
	public int getTotal() {
		return Number - FileNotFound;
	}
	
	/**
	 * Viene creato creato l'indice a partire dai path presenti nel file
	 * IndexPath, dove sono presenti i path.
	 * Viene inizializzato uno StandardAnalyzer e recuperato il modello selezionato per la creazione
	 * (modello di default BM25)
	 * Viene anche registrato il tempo di creazione dell'indice 
	 * @return
	 * @throws IOException
	 */
	private int indexCreator() throws IOException {
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
		// file e successivamente l'aggiunge all'indice, se trova path no esistenti vengono segnalati
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
			br.close();
		}
		writer.close();
		analyzer.close();
		dir.close();
		
		Date end = new Date();

		// formatta il tempo da millisecondi al formato HH:mm:ss
		Date date = new Date(end.getTime() - start.getTime());
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String formatted = formatter.format(date);

		parent.dispose();
		parentFrame.setEnabled(true);
		JOptionPane.showMessageDialog(parentFrame, "Loading completed in " + formatted + 
				"\n " + (Number - FileNotFound) + " of " + Number + " files found", "Complete", JOptionPane.PLAIN_MESSAGE);
		
		writer.close();
		
		
		File modelFile = new File(indexDir, "Model.ser");
		modelFile.createNewFile();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(modelFile));
		out.writeObject(M.getModel());
		out.writeObject(Number - FileNotFound);
		out.close();
		
		return 1;
	}
	
	/**
	 * Riceve in ingresso un file e per ogni file preleva body, name e path
	 * e crea un document con questi fields e lo ritorna
	 * @param file - File con path presente nel file IndexPath
	 * @return
	 * @throws IOException
	 */
	private Document getDocument(File file) throws IOException {
		document = new Document();

		// index file contents
		contentField = new TextField(CONTENTS, fileToBody(file), Field.Store.YES);
		

		// index file name
		fileNameField = new TextField(FILE_NAME, file.getName().substring(0, file.getName().length() - 5), Field.Store.YES);

		// index file path
		filePathField = new TextField(FILE_PATH, file.getCanonicalPath(), Field.Store.YES);

		//System.out.println(contentField + " " + fileNameField + " " + filePathField);
		document.add(contentField);
		document.add(fileNameField);
		document.add(filePathField);

		return document;
	}
	
	/**
	 * Funzione che estrapola dal file nxml il contenuto del body
	 * e attraverso la classe tokenizeStopStem effettua un parsing
	 * approfondito
	 * 
	 * @throws IOException
	 * @throws ParseException 
	 */
	public String fileToBody(File file) throws IOException {
		reader = new BufferedReader(new FileReader(file));
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
				tokenizeStopStem tSS = new tokenizeStopStem(result);
				result = tSS.output();
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
