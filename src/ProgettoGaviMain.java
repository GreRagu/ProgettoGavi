import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class ProgettoGaviMain implements ActionListener {

	private JFrame frmHegregio;
	private JTextField txtSearch;
	private JTable table;
	private JMenuItem mntmCreateIndexpath;
	private Integer filenumber;
	private JLabel lblRicercaSuN;
	private JLabel totalFound;
	public JMenuItem mntmLoadIndex;
	public JMenuItem mntmCreateIndex;
	private String indexPath;
	private String IndexFile = "./dataset/clinical_dataset/IndexPath.txt";
	private JMenuItem mntmVectorSpaceModel;
	private JMenuItem mntmBooleanModel;
	private JMenuItem mntmFuzzyModel;
	private JMenuItem mntmProbabilisticModel;
	private Model modelUsed = null;
	private String indexDir = null;
	public static String basePath = new File("").getAbsolutePath();
	private JButton btnHelp;
	private JButton btnSearch;
	private Vector<String> columnNames;
	private DefaultTableModel model;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
					ProgettoGaviMain window = new ProgettoGaviMain();
					window.frmHegregio.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

	}

	/**
	 * Create the application.
	 */
	public ProgettoGaviMain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		filenumber = 0;
		
		frmHegregio = new JFrame();
		frmHegregio.setTitle("Hegregio");
		frmHegregio.setBounds(100, 100, 625, 500);
		frmHegregio.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmHegregio.setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		frmHegregio.setJMenuBar(menuBar);
		
		JMenu mnIndexpath = new JMenu("Index");
		menuBar.add(mnIndexpath);
		
		mntmCreateIndexpath = new JMenuItem("Create IndexPath");
		mnIndexpath.add(mntmCreateIndexpath);
		mntmCreateIndexpath.addActionListener(this);
		
		mntmLoadIndex = new JMenuItem("Load index");
		mnIndexpath.add(mntmLoadIndex);
		mntmLoadIndex.addActionListener(this);
		
		mntmCreateIndex = new JMenuItem("Create index");
		mnIndexpath.add(mntmCreateIndex);
		mntmCreateIndex.addActionListener(this);
		
		JMenu mnModels = new JMenu("Models");
		menuBar.add(mnModels);
		
		mntmVectorSpaceModel = new JMenuItem("Vector Space Model");
		mnModels.add(mntmVectorSpaceModel);
		mntmVectorSpaceModel.addActionListener(this);
		
		mntmBooleanModel = new JMenuItem("Boolean Model");
		mnModels.add(mntmBooleanModel);
		mntmBooleanModel.addActionListener(this);
		
		mntmFuzzyModel = new JMenuItem("Fuzzy Model");
		mnModels.add(mntmFuzzyModel);
		mntmFuzzyModel.addActionListener(this);
		
		mntmProbabilisticModel = new JMenuItem("Probabilistic(BM25) Model");
		mnModels.add(mntmProbabilisticModel);
		mntmProbabilisticModel.addActionListener(this);
		
		JMenu mnTolerantRetriaval = new JMenu("Tolerant retriaval");
		menuBar.add(mnTolerantRetriaval);
		
		JMenu mnEfficiency = new JMenu("Efficiency");
		menuBar.add(mnEfficiency);
		
		JMenuItem mntmCalculateAndPlot = new JMenuItem("Calculate and plot");
		mnEfficiency.add(mntmCalculateAndPlot);
		
		btnHelp = new JButton("? Help");
		btnHelp.setBounds(540, 0, 70, 20);
		frmHegregio.add(btnHelp, null);
		btnHelp.addActionListener(this);
		
		btnSearch = new JButton("Search");
		btnSearch.setBounds(230, 20, 75, 20);
		frmHegregio.add(btnSearch, null);
		btnSearch.addActionListener(this);
		
		txtSearch = new JTextField();
		txtSearch.setText("Inserire testo da cercare");
		txtSearch.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent evt) {
				if (txtSearch.getText().toLowerCase().equals("inserire testo da cercare")) {
					txtSearch.setText("");
				}
			}
			
			public void focusLost(FocusEvent evt) {
				if (txtSearch.getText().trim().equals("")
						|| txtSearch.getText().toLowerCase().equals("inserire testo da cercare")) {
					txtSearch.setText("Inserire testo da cercare");
				}
			}
		});
		txtSearch.setBounds(15, 20, 200, 20);
		txtSearch.setColumns(15);
		frmHegregio.add(txtSearch, null);
		
		totalFound = new JLabel("File trovati: ");
		totalFound.setBounds(15, 60, 300, 15);
		frmHegregio.add(totalFound, null);
		
		lblRicercaSuN = new JLabel("Ricerca su " + filenumber + " file con modello");
		lblRicercaSuN.setBounds(15, 45, 300, 15);
		frmHegregio.add(lblRicercaSuN, null);
		
		columnNames = new Vector<>();
		columnNames.addElement("#");
		columnNames.addElement("Docnos");
		columnNames.addElement("Path");
		columnNames.addElement("Score");
		model = new DefaultTableModel(columnNames, 0);
		table = new JTable(model);
		TableColumn a = table.getColumnModel().getColumn(0);
	    a.setPreferredWidth(25);
	    a = table.getColumnModel().getColumn(1);
	    a.setPreferredWidth(100);
	    a = table.getColumnModel().getColumn(2);
	    a.setPreferredWidth(350);
	    a = table.getColumnModel().getColumn(3);
	    a.setPreferredWidth(102);
	    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(15, 110, 580, 300);
		frmHegregio.add(scrollPane, BorderLayout.CENTER);
		
		frmHegregio.setResizable(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if ( e.getSource() == mntmCreateIndexpath ) {
			try {
				CreateIndexPath CIP = new CreateIndexPath();
				filenumber += CIP.CreateFile(frmHegregio);
				lblRicercaSuN.setText("Ricerca su " + filenumber + " file con modello");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
		if ( e.getSource() == mntmCreateIndex) {
			Index ind = new Index(frmHegregio, filenumber, IndexFile);
			try {
				indexDir = ind.CreateGUI();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		if ( e.getSource() == mntmLoadIndex ) {
			
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new java.io.File(".")); // start at application current directory
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fc.showOpenDialog(fc);
			File yourFolder = null;
			if(returnVal == JFileChooser.APPROVE_OPTION) {
			    yourFolder = fc.getSelectedFile();
			    indexPath = yourFolder.getAbsolutePath();
			    indexPath = "." + indexPath.substring(basePath.length());
			    
				try {
					FileReader fr = new FileReader(IndexFile);
					LineNumberReader lnr = new LineNumberReader(fr);
					while ((lnr.readLine()) != null) {
						filenumber++;
					}
					lnr.close();
					fr.close();
					lblRicercaSuN.setText("Ricerca su " + filenumber + " file con modello");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (returnVal==JFileChooser.CANCEL_OPTION) return;
			
			JOptionPane.showMessageDialog(frmHegregio, "Cartella selezionata per l'indice: " + yourFolder.getAbsolutePath(), "Completato", JOptionPane.INFORMATION_MESSAGE);
			System.out.println("docPath :"+ indexPath);
			indexDir = indexPath;
			
		}
		
		
		if ( e.getSource() == btnHelp ) {   
			JOptionPane.showMessageDialog(frmHegregio,	"Programma che si occupa di information retrieval\" \n"
								+	"Procedimento: scrivere nel text field il testo che verrà cercato nel dataset preimpostato\n"
								+	" e premere il tasto 'cerca', nella tabella sottostante verrà restituito il risultato della ricerca \n"
								+ 	"\n\n"
								+ 	"\n"
								+   " \n"
								+   "");
			
		}
		
		
		//BUTTON SEARCH
		if ( e.getSource() == btnSearch ) {
			if (!txtSearch.getText().equals("") &&  !txtSearch.getText().equals("Inserire testo da cercare")) {
				if(indexDir != null) {
					try {
						
						SearchFiles sf = new SearchFiles(txtSearch.getText(), indexDir, model, frmHegregio);
						totalFound.setText(totalFound.getText() + " " + sf.Search());
						
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else {
					JOptionPane.showMessageDialog(frmHegregio,	"Selezionare la cartella dell'indice o creane uno", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			else {
				JOptionPane.showMessageDialog(frmHegregio,	"Inserire il testo nella barra di ricerca");
			}
			
		}
			
		
		
		
		//------------Model Selection--------------
		
		if ( e.getSource()  == mntmVectorSpaceModel ) {
			
			modelUsed  = new VectorSpaceModel();
			
		}
		
		if ( e.getSource()  == mntmBooleanModel ) {
			
			modelUsed = new BooleanModel();
			
		}
		
		if ( e.getSource()  == mntmFuzzyModel ) {
			
			modelUsed = new FuzzyModel();
			
		}
		
		
		if ( e.getSource()  == mntmProbabilisticModel ) {
			
			modelUsed = new BM25();
			
		}
		
		
		
	}
}