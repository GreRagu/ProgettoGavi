
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class Index implements ActionListener{

	//----------------------
	private static Index uniqueIndex;
	private static Similarity simUsed = null;
	private static StandardAnalyzer stdAnalyzer = null; 
	private static Directory dirIndex = null;
	private static IndexWriterConfig iwConfig = null; 
	private static IndexWriter inWriter = null; 
	private static IndexReader inReader = null;
	private static IndexSearcher inSearcher = null;
	//----------------------
	
	private String indexFile;
	private String indexDir;
	private Boolean append;
	private JProgressBar progressBar;
	private Integer Number;
	private JFrame ParentFrame;
	private File yourFolder;
	public String timeused = null;
	private JButton start;
	private JDialog dlgProgress;
	private MyModel M;
	private String ModelPath;
	
	
	private Index() {
		startIndex();
	}
	

	public Index(JFrame Parent, Integer FileCount, String IndexFile, MyModel M, String ModelPath) {
		this.ParentFrame = Parent;
		this.Number = FileCount;
		this.indexFile = IndexFile;
		this.M = M;
		this.ModelPath = ModelPath;
	}
	
	//-------------------
	
	/**
	 * alloca tutti gli strumenti dell'Index. per cambiare la similarità, l'indice deve essere riinizializzato
	 */
	private void startIndex() {
		stdAnalyzer = new StandardAnalyzer();
		dirIndex = new RAMDirectory();
		iwConfig = new IndexWriterConfig();
		iwConfig.setSimilarity(simUsed);
		
		try {
			inWriter = new IndexWriter(dirIndex, iwConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			inReader = DirectoryReader.open(inWriter);
			inSearcher = new IndexSearcher(inReader);
			inSearcher.setSimilarity(simUsed);			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Per cambiare la similarità dell'indice questo deve essere ri inizializzato e perdere tutti i contenuti.
	 * Per prevenire ciò si può settare a true il reload,salvando il contenuto dell'indice inun indice temporaneo(tempIndex)
	 * e ricaricandolo dopo la re-initializzazione.
	 * Questo avviene solo se la similarità dell'indice è diversa da quella passata.
	 * @param sim is the similarity to set
	 * @param reload is a boolean to save index in a temporary save and reload it's content after reset
	 */
	public void setSimilarity(Similarity sim, boolean reload) {
		if(simUsed.getClass() != sim.getClass()) {
			simUsed = sim;
			if(reload) {
				saveIndex("tempIndex.ser");
			}
			resetIndex();
			if(reload) {
				loadIndex("tempIndex.ser");
			}
		}
	}
	
	/**
	 * This method removes the previous index and closes its tools. 
	 * Then it makes a new Index, reallocating new tools.
	 * This is the fastest and easiest way to "clear" totally an index from its entries.
	 */	
	public void resetIndex() {
		closeIndex();
		startIndex();
	}
	
	
	/**
	 * This method close tools that are closable.
	 */
	private void closeIndex() {
		if(stdAnalyzer != null){
			stdAnalyzer.close();
		}
		if(inWriter != null) {
			try {
				inWriter.deleteAll();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(dirIndex != null) {
			try {
				dirIndex.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * This method write all documents path to the target file, as clear text.
	 * @param saveFile is the path of the saveFile (plain text) to load (each line is the path to the document)
	 */
	public void saveIndex(String saveFile) {
		if (getSize() == 0 && !saveFile.equals("tempIndex.ser")) {
			System.err.println("This index is empty, saving it is useless");
			return ;
		}
		
		PrintWriter fileWriter = null;
		
		try {
			fileWriter = new PrintWriter(saveFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("File " + saveFile + " doesn't exist");
		}
		
		for (int k = 0 ; k < this.getSize() ; k++) {
			fileWriter.println(getDocument(k).get("path") + getDocument(k).get("name"));
		}
		
		fileWriter.close();
		System.out.println("Saving successful to " + saveFile + "!");
	}
	
	/**
	 * This method, contrary to saveIndex, load documents in Index by a save file that contains a list of them.
	 * It doesn't overwrite index content, only adds documents to index.
	 * @param saveFile is the file containing documents to be loaded.
	 */
	public void loadIndex(String saveFile) {
		System.out.println("Loading from " + saveFile);
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(new File(saveFile)));
		} catch (FileNotFoundException e) {
			if(!saveFile.equals("tempIndex.ser")) {
				e.printStackTrace();
			}
			System.err.println("File " + saveFile + " doesn't seem to exist, or some else error showed up. Loading aborted.");
			return ;
		}
		
		String line = "";
		try {
			while ( (line = reader.readLine()) != null) {
				addDocument(line);
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return ;
		}
		
		System.out.println("Loading successful from " + saveFile + "!");
	}
	
	/**
	 * Crea e aggiunge un documento all'index. 
	 * @param docPath path e nome di un documento singolo (es. "doc/Lucene.txt")
	 */
	public void addDocument(String docPath) {
		Document doc = new Document();
		
		BufferedReader buffer = null;
		try{
			buffer = new BufferedReader(new FileReader(docPath));
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		/*
		 * Legge il contenuto di un documento e lo conserva in una String, usando la stringa line  per controllare 
		 * la consistenza di ogni riga letta dal BufferedReader
		 */
		String content = "";
		String line;
		try {
			while((line = buffer.readLine()) != null) {
				content += line + "\n";
			}
		}catch(Exception e) {
			System.out.println("End of file");
		}
		
		int separatorIndex = docPath.lastIndexOf("/");
		
		String path = "";
		
		/*
		 * Relative path could be used in tests to add documents to index, so the "/" could be missing. Using
		 * gui, absolute path will be always declared, ignoring this problem
		 */
		if (separatorIndex != -1) {
			path = docPath.substring(0, separatorIndex+1);
		}
		
		String name = docPath.substring(separatorIndex+1, docPath.length());
		
		/*
		 * Document properties are stored into Document type.
		 * @warning path field is not intended to be used for queries
		 */
		doc.add(new TextField("path", path, Field.Store.YES));
		doc.add(new TextField("name", name, Field.Store.YES));
		doc.add(new TextField("content", content, Field.Store.YES));
		
		try {
			inWriter.addDocument(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		/*
		 * This updates indexReader because index has been modified (a new document has been added to it)
		 */
		try {
			inReader = DirectoryReader.openIfChanged((DirectoryReader) inReader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a document giving corresponding index.
	 * @param index of the document to return
	 * @return document object from index
	 */
	public Document getDocument(int index) {
		Document doc = null;
		try {
			doc = inReader.document(index);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	/**
	 * This method removes a document from the index, given its position into the index
	 * @param index is index of document to remove
	 */
	public void removeDocument(int index) {
		try {
			inWriter.tryDeleteDocument(inReader, index);
			inReader = DirectoryReader.openIfChanged((DirectoryReader)inReader);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the number of documents stored in index.
	 * @return size of the index
	 */
	public int getSize() {
		return inWriter.numDocs();
	}
	
	/**
	 * This method requires a string representing user query, a LinkedList of Strings containing fields
	 * in which searching, the Model instance used to parse query, a boolean print to get query and results to be printed or not
	 * @param query is the query String
	 * @param fields are fields on which search
	 * @param m is the model to use for parsing query
	 * @param print allows query and results printing
	 * @return a list of "Hit", where Hit is a custom class that contains a document and its score for that query	 
	 */
	public LinkedList<Hit> submitQuery(String query, LinkedList<String> fields, Model m, boolean print) {
		
		LinkedList<Hit> queryResults = new LinkedList<Hit>();
		
		if(getSize() == 0) {
			System.err.println("No documents in index!");
			return null;
		}
		
		Query q = m.getQueryParsed(query, fields, stdAnalyzer);
		
		TopDocs results = null;
		ScoreDoc[] hits = null;
		
		if (print) {
			System.out.println("Printing query: " + q.toString() + "\n");
		}
		
		/* Updating of IndexSearcher. The only way to update a searcher is to
		 * create a new searcher on the current reader. This is cheap if we already have a reader
		 * available (as we have)
		 */		
		try {
			inSearcher = new IndexSearcher(inReader);
			inSearcher.setSimilarity(simUsed);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			results = inSearcher.search(q, getSize());
			hits = results.scoreDocs;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("\nSomething goes wrong with your query... Quitting...");
			return null;
		}
		
		System.out.println(results.totalHits + " total matching documents");
		
		
		Document doc = null;
		try {
			for (int k=0 ; k < hits.length ; k++) {
					doc = inSearcher.doc(hits[k].doc);
					queryResults.add(new Hit(doc.get("path"), doc.get("name"), hits[k].score));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return queryResults;
	}

	//-------------------
	
	
	

	public void setValue(final int j) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progressBar.setValue(j);
			}
		});
	}

	public String CreateGUI() throws IOException {

		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new java.io.File(".")); // start at application current directory
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			yourFolder = fc.getSelectedFile();
			indexDir = yourFolder.getAbsolutePath();

			indexDir = "." + indexDir.substring(ProgettoGaviMain.basePath.length());
			System.out.println(indexDir);
		}
		if (returnVal == JFileChooser.CANCEL_OPTION)
			return null;

		int dialogResult = JOptionPane.showConfirmDialog(null, "Would You Like to append to the index folder?", "",
				JOptionPane.YES_NO_OPTION);
		if (dialogResult == JOptionPane.YES_OPTION) {
			append = true;
		} else {
			append = false;
		}
		

		// Usato per ottenere il numero di di path per inizializzare la progressBar
		if (Number == 0) {
			FileReader fr = new FileReader(indexFile);
			LineNumberReader lnr = new LineNumberReader(fr);
			try {
				while ((lnr.readLine()) != null) {
					Number++;
				}
				lnr.close();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		int dResult = JOptionPane.showConfirmDialog(null, "Do you want to create index with model: " + M.getModelString() 
						+ " end " + Number + " of file?", "", JOptionPane.YES_NO_OPTION);
		if (dResult == JOptionPane.NO_OPTION) return null;

		System.out.println(Number);

		BufferedWriter writer = new BufferedWriter(new FileWriter(ModelPath));
	    writer.write(M.getModel());
	    writer.close();
	    
		dlgProgress = new JDialog(ParentFrame, "Please wait...", false);
		dlgProgress.setLocationRelativeTo(ParentFrame);
		dlgProgress.setSize(500, 110);
		dlgProgress.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		Container content = dlgProgress.getContentPane();
		Border border = BorderFactory
				.createTitledBorder("Indexing to directory " + yourFolder.getAbsolutePath() + "...");
		progressBar = new JProgressBar(0, Number);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setBorder(border);
		start = new JButton("Start");
		start.setSize(70, 50);
		start.addActionListener(this);
		content.add(start, BorderLayout.EAST);
		content.add(progressBar, BorderLayout.NORTH);
		dlgProgress.setVisible(true);
		dlgProgress.setResizable(false);
		

		return indexDir;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == start) {
			ParentFrame.setEnabled(false);
			IndexWorker c = new IndexWorker(indexDir, append, progressBar, dlgProgress, ParentFrame, M, indexFile);
			c.start();
		}
	}
	
	/**
	 * Questo metodo rende questa classe un singleton, allocando uniqueIndex come unicca istanza di questa classe.
	 * Di default, è applicato il VectorSpaceModel.
	 * @return Index as a uniqueIndex
	 */
	public static Index getIndex() {
		if(uniqueIndex == null) {
			return getIndex(new VectorSpaceModel().getSimilarity());
		}
		return uniqueIndex;
	}
	
	
	/**
	 * Crea un index specificando la similarity. 
	 * Quando viene chiamato il costruttore simUsed definisc il modello da usare per la similarity.
	 * @param sim similarità da applicare, non applicata se uniqueIndex non è ancora stato creato
	 * @return Index 
	 */
	public static Index getIndex(Similarity sim) {
		if(uniqueIndex == null) {
			simUsed = sim;
			uniqueIndex = new Index();
		}
		return uniqueIndex;
	}
	
}
