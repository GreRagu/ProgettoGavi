import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
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
	private JRadioButtonMenuItem mntmVectorSpaceModel;
	private JRadioButtonMenuItem mntmBooleanModel;
	private JRadioButtonMenuItem mntmFuzzyModel;
	private JRadioButtonMenuItem mntmProbabilisticModel;
	private Integer modelUsed = 0; //Default BM25 Model
	private String indexDir = null;
	public static String basePath = new File("").getAbsolutePath();
	private JButton btnHelp;
	private JButton btnSearch;
	private DefaultTableModel model;
	private MyModel M;
	private Vector<String> columnNames;
	private JMenuItem mntmCalculateAndPlot;
	private Integer futureModel = 0;

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
		M = new MyModel(modelUsed);
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
		
		ButtonGroup group = new ButtonGroup();
		mntmProbabilisticModel = new JRadioButtonMenuItem("Probabilistic(BM25) Model");
		mnModels.add(mntmProbabilisticModel);
		mntmProbabilisticModel.addActionListener(this);
		group.add(mntmProbabilisticModel);
		mnModels.addSeparator();
		
		mntmVectorSpaceModel = new JRadioButtonMenuItem("Vector Space Model (TFIDF)");
		mnModels.add(mntmVectorSpaceModel);
		mntmVectorSpaceModel.addActionListener(this);
		group.add(mntmVectorSpaceModel);
		mnModels.addSeparator();
		
		mntmBooleanModel = new JRadioButtonMenuItem("Boolean Model");
		mnModels.add(mntmBooleanModel);
		mntmBooleanModel.addActionListener(this);
		group.add(mntmBooleanModel);
		mnModels.addSeparator();
		
		mntmFuzzyModel = new JRadioButtonMenuItem("Fuzzy Model");
		mnModels.add(mntmFuzzyModel);
		mntmFuzzyModel.addActionListener(this);
		group.add(mntmFuzzyModel);
		mnModels.addSeparator();
		
		JMenu mnEfficiency = new JMenu("Efficiency");
		menuBar.add(mnEfficiency);
		
		mntmCalculateAndPlot = new JMenuItem("Calculate and plot");
		mntmCalculateAndPlot.addActionListener(this);
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
		txtSearch.setText("Enter the search text");
		txtSearch.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent evt) {
				if (txtSearch.getText().toLowerCase().equals("enter the search text")) {
					txtSearch.setText("");
				}
			}
			
			public void focusLost(FocusEvent evt) {
				if (txtSearch.getText().trim().equals("")
						|| txtSearch.getText().toLowerCase().equals("enter the search text")) {
					txtSearch.setText("Enter the search text");
				}
			}
		});
		txtSearch.setBounds(15, 20, 200, 20);
		txtSearch.setColumns(15);
		frmHegregio.add(txtSearch, null);
		
		totalFound = new JLabel("Files found: ");
		totalFound.setBounds(15, 60, 400, 15);
		frmHegregio.add(totalFound, null);
		
		lblRicercaSuN = new JLabel("Search on " + filenumber + " files with model: ");
		lblRicercaSuN.setBounds(14, 45, 400, 15);
		frmHegregio.add(lblRicercaSuN, null);

		columnNames = new Vector<>();
		columnNames.addElement("#");
		columnNames.addElement("Docnos");
		columnNames.addElement("Path");
		columnNames.addElement("Score");
		model = new DefaultTableModel(columnNames, 0);
		table = new JTable(model);
		TableColumn col = table.getColumnModel().getColumn(0);
		col.setPreferredWidth(40);
		col = table.getColumnModel().getColumn(1);
		col.setPreferredWidth(200);
		col = table.getColumnModel().getColumn(2);
		col.setPreferredWidth(150);
		col = table.getColumnModel().getColumn(3);
		col.setPreferredWidth(165);
		col.setCellRenderer(new TableButton());
		col.setCellEditor(new ButtonEditor(new JCheckBox()));
		table.setRowHeight(20);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(15, 110, 580, 300);
		frmHegregio.add(scrollPane, BorderLayout.CENTER);
		
		
		frmHegregio.getRootPane().setDefaultButton(btnSearch);
		btnSearch.requestFocus();
		frmHegregio.setResizable(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if ( e.getSource() == mntmCreateIndexpath ) {
			try {
				CreateIndexPath CIP = new CreateIndexPath();
				filenumber += CIP.CreateFile(frmHegregio);
				lblRicercaSuN.setText("Search on " + filenumber + " files with model: ");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
		if ( e.getSource() == mntmCreateIndex) {
			M = new MyModel(futureModel);
			Index ind = new Index(frmHegregio, filenumber, IndexFile, M);
			try {
				String[] app = ind.CreateGUI().split(" ");
				if(!app[0].equals("null") && !app[1].equals("0") ) {
					indexDir = app[0];
					filenumber = Integer.parseInt(app[1]);
				}
				if(indexDir != null && filenumber != 0) {
					lblRicercaSuN.setText("Search on " + filenumber + " files with model: " + M.getModelString());
				}
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
						ObjectInputStream in = new ObjectInputStream(new FileInputStream(M.getPaht()));
						modelUsed = (Integer) in.readObject();
						filenumber = (Integer) in.readObject();
						in.close();
						System.out.println(modelUsed);
						File dir = new File(indexPath);
						String[] file = dir.list();
						Boolean cond = false;
						for(int i = 0; i < file.length; i++) {
							if(file[i].endsWith(".cfs")) {
								cond = true;
							}
						}
						if(dir.isDirectory() && dir.list().length > 0 && cond) {
							if(modelUsed >= 0 && modelUsed < 4) {
								M = new MyModel(modelUsed);
								JOptionPane.showMessageDialog(frmHegregio, "Folder selected for index: " + yourFolder.getAbsolutePath(), "Complete", JOptionPane.INFORMATION_MESSAGE);
								lblRicercaSuN.setText("Search on " + filenumber + " files with model: " + M.getModelString());
								System.out.println("indexPath :"+ indexPath);
								indexDir = indexPath;
							}
							else {
								JOptionPane.showMessageDialog(frmHegregio,	"Impossible to load index, unknown model", "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
						else {
							JOptionPane.showMessageDialog(frmHegregio,	"Not index in selected folder!", "Error", JOptionPane.ERROR_MESSAGE);
						}
					} catch (ClassNotFoundException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}
			else return;
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
			if (!txtSearch.getText().equals("") &&  !txtSearch.getText().equals("Enter the search text")) {
				if(indexDir != null) {
					try {
						for(int k = 0; k < model.getRowCount(); k++) {
							model.removeRow(k);
						}
						model.setRowCount(0);
						SimpleSuggestionService SSS = new SimpleSuggestionService(txtSearch.getText().split(" "));
						String CorrectQuery = SSS.DidYouMean();
						System.out.println("corretta: " + CorrectQuery);
						
						int result = JOptionPane.showConfirmDialog(frmHegregio, "Did you mean: " + CorrectQuery + "?", "", JOptionPane.YES_NO_OPTION);
						SearchFiles sf;
						if (result == JOptionPane.YES_OPTION) {
							sf = new SearchFiles(CorrectQuery, indexDir, model, frmHegregio, M);
						} else {
							sf = new SearchFiles(txtSearch.getText(), indexDir, model, frmHegregio, M);
						}
						
						Integer totalFile = sf.Search();
						totalFound.setText("Files found: ");
						totalFound.setText(totalFound.getText() + " " + totalFile + " for query: " + CorrectQuery);
						
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else {
					JOptionPane.showMessageDialog(frmHegregio,	"Select the index folder or create one", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			else {
				JOptionPane.showMessageDialog(frmHegregio,	"Enter text on search bar");
			}
			
		}
		
		
		//------------Model Selection--------------
		
		if ( e.getSource()  == mntmVectorSpaceModel ) {
			futureModel = 1;
			MyModel temp = new MyModel(futureModel);
			JOptionPane.showMessageDialog(frmHegregio, "Future index will be create with model: " + temp.getModelString(), "Loaded", JOptionPane.INFORMATION_MESSAGE);
			temp = null;
		}
		
		if ( e.getSource()  == mntmBooleanModel ) {
			futureModel = 2;
			MyModel temp = new MyModel(futureModel);
			JOptionPane.showMessageDialog(frmHegregio, "Future index will be create with model: " + temp.getModelString(), "Loaded", JOptionPane.INFORMATION_MESSAGE);
			temp = null;
		}
		
		if ( e.getSource()  == mntmFuzzyModel ) {
			futureModel = 3;
			MyModel temp = new MyModel(futureModel);
			JOptionPane.showMessageDialog(frmHegregio, "Future index will be create with model: " + temp.getModelString(), "Loaded", JOptionPane.INFORMATION_MESSAGE);
			temp = null;
		}
		
		if ( e.getSource()  == mntmProbabilisticModel ) {
			futureModel = 0;
			MyModel temp = new MyModel(futureModel);
			JOptionPane.showMessageDialog(frmHegregio, "Future index will be create with model: " + temp.getModelString(), "Loaded", JOptionPane.INFORMATION_MESSAGE);
			temp = null;
		}
		
		if( e.getSource() == mntmCalculateAndPlot) {
			if(indexDir != null) {
				PrecisionRecall pr = new PrecisionRecall(indexDir);
				try {
					pr.start();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else {
				JOptionPane.showMessageDialog(frmHegregio,	"Select the index folder or create one", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
}