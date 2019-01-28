import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ProgettoGaviMain implements ActionListener {

	private JFrame frmHegregio;
	private JTextField txtSearch;
	private JTable table;
	private JMenuItem mntmCreateIndexpath;
	private Integer filenumber;
	private JLabel lblRicercaSuN;
	public JMenuItem mntmLoadIndex;
	public JMenuItem mntmCreateIndex;
	private String indexPath;
	private JMenuItem mntmVectorSpaceModel;
	private JMenuItem mntmBooleanModel;
	private JMenuItem mntmFuzzyModel;
	private JMenuItem mntmProbabilisticModel;
	private Model modelUsed = null;
	public static String basePath = new File("").getAbsolutePath();
	private JButton btnHelp;
	private JButton btnSearch;

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
		frmHegregio.setBounds(100, 100, 450, 300);
		frmHegregio.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
		
		btnHelp = new JButton("Help");
		menuBar.add(btnHelp);
		frmHegregio.getContentPane().setLayout(null);
		btnHelp.addActionListener(this);
		
		btnSearch = new JButton("search");
		btnSearch.setBounds(89, 11, 57, 21);
		frmHegregio.getContentPane().add(btnSearch);
		btnSearch.addActionListener(this);
		
		txtSearch = new JTextField();
		txtSearch.setText("Inserire testo da cercare");
		txtSearch.setBounds(156, 12, 206, 20);
		frmHegregio.getContentPane().add(txtSearch);
		txtSearch.setColumns(15);
		
		
		table = new JTable();
		table.setBounds(62, 79, 300, 150);
		frmHegregio.getContentPane().add(table);
		
		lblRicercaSuN = new JLabel("Ricerca su " + filenumber + " file con modello");
		lblRicercaSuN.setHorizontalAlignment(SwingConstants.CENTER);
		lblRicercaSuN.setBounds(61, 54, 301, 14);
		frmHegregio.getContentPane().add(lblRicercaSuN);
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
			Index ind = new Index(frmHegregio, filenumber);
			try {
				ind.CreateGUI();
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
				System.out.println("docPath :"+ indexPath);
			}
			if (returnVal==JFileChooser.CANCEL_OPTION) return;
			
			JOptionPane.showMessageDialog(frmHegregio, "Cartella selezionata per l'indice: " + yourFolder.getAbsolutePath(), "Completato", JOptionPane.INFORMATION_MESSAGE);
			System.out.println("docPath :"+ indexPath);
			
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
			
			
			if ( !txtSearch.getText().equals("") ) {
				try {
					SearchFiles sf = new SearchFiles( txtSearch.getText() );
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else {
				
				JOptionPane.showMessageDialog(frmHegregio,	"Inserire i testo nella barra di ricerca");
	
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