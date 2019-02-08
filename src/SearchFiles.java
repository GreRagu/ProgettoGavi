
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;


/**
 * Classe che effettua la ricerca della query all'interno dell'indice
 * @author
 *
 */
public class SearchFiles implements ActionListener{
	
	private String queryString;
	private String index;
	private String field = "contents";
	private Integer Hits;
	private DefaultTableModel model;
	private Integer total;
	private String path;
	private String name;
	private JFrame Parent;
	private JDialog number;
	private JLabel label;
	private JButton okbtn;
	private JFormattedTextField docnumber;
	private MyModel M;
	
	public SearchFiles(String query, String indexDir, DefaultTableModel model, JFrame Parent, MyModel M) {
		this.index = indexDir;
		this.queryString = query;
		this.model = model;
		this.Parent = Parent;
		this.M = M;
	}

	/**
	 * Viene chiesto quanti documenti collezzionare per la ricerca e successivamente
	 * esegue il parse della query a seconda del modello usato per creare l'indice,
	 * ricerca poi la query all'interno del dataset
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public int Search() throws Exception {
		
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		searcher.setSimilarity(M.getModelSymilarity());
		StandardAnalyzer analyzer = new StandardAnalyzer();
		
		tokenizeStopStem tSS = new tokenizeStopStem(queryString);
		queryString = tSS.output();
		
		Query query = M.getQuery(queryString, field, analyzer);

		
		System.out.println(queryString);
		System.out.println("Searching for: " + query.toString(field));
	
		number = new JDialog(Parent, "Desired documents", true);
		number.setSize(450, 150);
		number.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		number.setLayout(null);
		label = new JLabel("How many documets do you want to collect? (only number, default 100)");
		label.setBounds(10, 10, 400, 20);
		number.add(label);
		docnumber = new JFormattedTextField(NumberFormat.getIntegerInstance());
		docnumber.setBounds(50, 50, 50, 20);
		docnumber.setValue(new Long("100"));
		number.add(docnumber);
		okbtn = new JButton("OK");
		okbtn.setBounds(120, 50, 70, 20);
		okbtn.addActionListener(this);
		number.add(okbtn);
		number.setVisible(true);
		
		
		
		total = doSearch(searcher, query, model);//queries == null && queryString == null);
		
		reader.close();
		
		return total;
		
	}

	/**
	 * Funzione che effettua la ricerca nel' indice e aggiunge alla tabella tante righe quanti
	 * sono i documenti che l'utente ha deciso di collezzionare per la sua ricerca(default 100)
	 * @param searcher
	 * @param query
	 * @param model
	 * @return
	 * @throws IOException
	 */
	private int doSearch(IndexSearcher searcher, Query query, DefaultTableModel model) throws IOException {
		
		TopDocs results = searcher.search(query, Hits);
		ScoreDoc[] hits = results.scoreDocs;

		int numTotalHits = (int) results.totalHits;
		System.out.println(numTotalHits + " total matching documents");

		if(numTotalHits > 0) {
			int start = 0;
			int end = Math.min(numTotalHits, Hits);
			hits = searcher.search(query, numTotalHits).scoreDocs;
			
			end = Math.min(hits.length, start + Hits);

			for (int i = start; i < end; i++) {
				
				Document doc = searcher.doc(hits[i].doc);
				path = doc.get("filepath");
				name = doc.get("filename");
				
				if(path == null) {
					path = "No path for this document";
				}
				if(name == null) {
					name = "No name for this document";
				}
				
				path = "." + path.substring(ProgettoGaviMain.basePath.length());
				
				Object[] data = {String.valueOf(i + 1), name, hits[i].score, doc.get("filepath")};
				model.addRow(data);
			}
		}
		else {
			JOptionPane.showMessageDialog(Parent,"No results for this search", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		return numTotalHits;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if ( e.getSource() == okbtn ) {
			if(!docnumber.getValue().equals("0")) {
				Hits = (int) (long) docnumber.getValue();
				number.dispose();
			}
			else {
				Hits = 100;
			}
		}
	}
}
