
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

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
	private String ModelPath;
	
	public SearchFiles(String query, String indexDir, DefaultTableModel model, JFrame Parent, String ModelPath) {
		this.index = indexDir;
		this.queryString = query;
		this.model = model;
		this.Parent = Parent;
		this.ModelPath = ModelPath;
	}

	public int Search() throws Exception {

	/** Simple command-line based search demo. */

		// select index to use (index_txt index_xml)
		
		FileReader fr = new FileReader(ModelPath);
		MyModel M = new MyModel((int) fr.read());
		fr.close();
		
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		searcher.setSimilarity(M.getModelSymilarity());
		Analyzer analyzer = new StandardAnalyzer();

		BufferedReader in = null;				
		QueryParser parser = new QueryParser(field, analyzer);

		Query query = parser.parse(queryString);
		System.out.println(queryString);
		System.out.println("Searching for: " + query.toString(field));
	
		number = new JDialog(Parent, "Documenti desiderati", true);
		number.setSize(450, 150);
		number.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		number.setLayout(null);
		label = new JLabel("Numero massimo di documenti da mostrare? (Solo numeri)");
		label.setBounds(10, 10, 400, 20);
		number.add(label);
		docnumber = new JFormattedTextField(NumberFormat.getIntegerInstance());
		docnumber.setBounds(50, 50, 50, 20);
		number.add(docnumber);
		okbtn = new JButton("OK");
		okbtn.setBounds(120, 50, 70, 20);
		okbtn.addActionListener(this);
		number.add(okbtn);
		number.setVisible(true);
		
		
		
		total = doPagingSearch(in, searcher, query, model);//queries == null && queryString == null);
		
		reader.close();
		
		return total;
		
	}

	/**
	 * This demonstrates a typical paging search scenario, where the search engine
	 * presents pages of size n to the user. The user can then go to the next page
	 * if interested in the next hits.
	 * 
	 * When the query is executed for the first time, then only enough results are
	 * collected to fill 5 result pages. If the user wants to page beyond this
	 * limit, then the query is executed another time and all hits are collected.
	 * 
	 */
	private int doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query, DefaultTableModel model) throws IOException {
		
		// Collect enough docs to show 5 pages
		TopDocs results = searcher.search(query, Hits);
		ScoreDoc[] hits = results.scoreDocs;

		int numTotalHits = (int) results.totalHits;
		System.out.println(numTotalHits + " total matching documents");

		int start = 0;
		int end = Math.min(numTotalHits, Hits);

		hits = searcher.search(query, numTotalHits).scoreDocs;
		
		end = Math.min(hits.length, start + Hits);

		for (int i = start; i < end; i++) {
			
			Document doc = searcher.doc(hits[i].doc);
			path = doc.get("filepath");
			name = doc.get("filename");
			
			if(path == null) {
				path = "Nessun path per questo documento";
			}
			if(name == null) {
				name = "Nessun nome per questo documento";
			}
			
			path = "." + path.substring(ProgettoGaviMain.basePath.length());
			
			Object[] data = {String.valueOf(i + 1), name, path, hits[i].score};
			model.addRow(data);

		}
		
		return numTotalHits;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if ( e.getSource() == okbtn ) {
			Hits = (int) (long) docnumber.getValue();
			number.dispose();
		}
	}
}
