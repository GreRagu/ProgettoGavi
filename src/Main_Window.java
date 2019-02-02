

import java.awt.EventQueue;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

//import com.sun.scenario.effect.Filterable;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import java.awt.Toolkit;
import java.awt.Font;
import javax.swing.JScrollPane;


public class Main_Window {

	private JFrame frame = null;
	private JTextField textField = null;
	JScrollPane scrollPane = null;
	private JTable resultsTable;
	private JTable fileTable = null;
	private JTable chronologyTable=null;
	private LinkedList<String> chronology=new LinkedList<String>();
	private JTextField editDistanceText = null;
	private Index generalIndex = null;
	private static int editdistance=0;
	
	
	
	
	// Launch the application
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main_Window window = new Main_Window();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	 // Create the application.
	 
	public Main_Window() {
		initialize();
	}

	
	 // Initialize the contents of the frame.
	 
	private void initialize() {
		generalIndex = Index.getIndex();
		
		
		frame = new JFrame();
		
		int btnDocs_pos_x = 20;
		int btnDocs_pos_y = 50;
		
		int right_pos_x = 733;
		int right_pos_y = 600;

		//frame.getContentPane().setBackground(Color.GRAY);

		//frame.getContentPane().setBackground(Color.gray);
		Dimension screenResolution = Toolkit.getDefaultToolkit().getScreenSize();
		ImageIcon wallpaper  = new ImageIcon(new ImageIcon("media/icons/wallpaper.jpg").getImage().getScaledInstance(screenResolution.width, screenResolution.height, 0));
		frame.setContentPane(new JLabel(wallpaper));
		frame.setSize(1200, 800);
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("SEARCH ENGINE");	
		frame.getContentPane().setLayout(null);
		
		/*// label Project's name
		JLabel title = new JLabel("GOOD SEARCH");
		title.setForeground(Color.BLACK);
		title.setFont(new Font("Verdana", Font.BOLD, 19));
		title.setBounds(783, 25, 192, 25);
		frame.getContentPane().add(title);*/
		
		//text to search
		textField = new JTextField();
		textField.setBounds(right_pos_x, 30, 418, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		//box model 
		final JComboBox<String> modelbox = new JComboBox<String>();
		modelbox.setBounds(right_pos_x, right_pos_y, 156, 20);
		modelbox.setMaximumRowCount(5);
		modelbox.setModel(new DefaultComboBoxModel<String>(new String[] { "Vector Space Model", "Boolean Model", "Probabilistic(BM25) Model", "Fuzzy Model"}));
		modelbox.setSelectedIndex(0);
		modelbox.setToolTipText("Preferenze");
		frame.getContentPane().add(modelbox);
		
		//button to search
		JButton search = new JButton("Search");
		search.setBounds(1200, 30, 89, 23);
		frame.getContentPane().add(search);
		
		//button to delete all file add
		JButton delete = new JButton(" delete all file ");
		delete.setBounds(btnDocs_pos_x, btnDocs_pos_y, 100, 52);
		ImageIcon deleteIcon = new ImageIcon(new ImageIcon("media/icons/empty_index.png").getImage().getScaledInstance(35, 35, 0));
		delete.setIcon(deleteIcon);
		delete.setMargin (new Insets (0, 0, 0, 0));
		frame.getContentPane().add(delete);
		
		//button to add new files
		JButton add = new JButton(" add new files ");
		add.setBounds(btnDocs_pos_x, btnDocs_pos_y +80, 100, 52);
		//add.setIcon(addIcon);
		add.setMargin (new Insets (0, 0, 0, 0));
		frame.getContentPane().add(add);
		
		//button to remove all file selecting
		JButton remove = new JButton("remove all file");
		remove.setBounds(btnDocs_pos_x, btnDocs_pos_y + 160, 100, 52);
		ImageIcon removeIcon = new ImageIcon(new ImageIcon("media/icons/remove_file.png").getImage().getScaledInstance(35, 35, 0));
		remove.setIcon(removeIcon);
		remove.setMargin (new Insets (0, 0, 0, 0));
		frame.getContentPane().add(remove);
		
		//Button to save Index
		JButton btnSaveIndex = new JButton("Save Index");
		btnSaveIndex .setBounds(btnDocs_pos_x, btnDocs_pos_y + 240, 100, 52);
		ImageIcon saveIcon  = new ImageIcon(new ImageIcon("media/icons/save_index.png").getImage().getScaledInstance(35, 35, 0));
		btnSaveIndex.setIcon(saveIcon);
		btnSaveIndex.setMargin (new Insets (0, 0, 0, 0));
		frame.getContentPane().add(btnSaveIndex );
		
		//button to load Index
		JButton btnloadIndex = new JButton("Load Index");
		btnloadIndex.setBounds(btnDocs_pos_x, btnDocs_pos_y + 320, 100, 52);
		ImageIcon loadIcon  = new ImageIcon(new ImageIcon("media/icons/load_index.png").getImage().getScaledInstance(35, 35, 0));
		btnloadIndex.setIcon(loadIcon);
		btnloadIndex.setMargin (new Insets (0, 0, 0, 0));
		frame.getContentPane().add(btnloadIndex);
		
		//table for results 
		resultsTable = new JTable();
		resultsTable.setBounds(right_pos_x, 134, 418, 405);
        final DefaultTableModel resultsModel = (DefaultTableModel) resultsTable.getModel();
        resultsModel.addColumn("File");
        resultsModel.addColumn("Score");
        
       	frame.getContentPane().add(resultsTable);
	
		//table to view files adding
		fileTable = new JTable();
		fileTable.setBounds(140, btnDocs_pos_y, 359, 540);
		final DefaultTableModel tableModel=(DefaultTableModel) fileTable.getModel();
		frame.getContentPane().add(fileTable);
		
		//box of button to optimizations
		ButtonGroup opGroup = new ButtonGroup();		
		
		JRadioButton noOptimizations = new JRadioButton("No Optimizations");
		noOptimizations.setBounds(right_pos_x + 140*3, right_pos_y - 20, 170, 23);
		noOptimizations.setSelected(true);
		opGroup.add(noOptimizations);
		frame.getContentPane().add(noOptimizations);
		
		final JRadioButton editDistance = new JRadioButton("Edit Distance");
		editDistance.setBounds(right_pos_x + 140*3, right_pos_y, 170, 23);
		opGroup.add(editDistance);
		frame.getContentPane().add(editDistance);
		
		editDistanceText = new JTextField();
		editDistanceText.setBounds(right_pos_x + 130*4, right_pos_y, 39, 20);
		frame.getContentPane().add(editDistanceText);
		editDistanceText.setColumns(10);
		
		//button for user helping
		JButton Help = new JButton("HELP");
		Help.setBounds(right_pos_x + 160, right_pos_y, 89, 23);
		frame.getContentPane().add(Help);
		
		//some label to define tables
		JLabel lblResults = new JLabel("RESULTS");
		lblResults.setForeground(Color.BLACK);
		lblResults.setFont(new Font("Verdana", Font.BOLD, 17));
		lblResults.setBounds(940, 90, 108, 14);
		frame.getContentPane().add(lblResults);
		
		JLabel lblDocumnets = new JLabel("Documents");
		lblDocumnets.setForeground(Color.BLACK);
		lblDocumnets.setFont(new Font("Verdana", Font.BOLD, 17));
		lblDocumnets.setBounds(200, 25, 156, 25);
		frame.getContentPane().add(lblDocumnets);
		
		JButton btnChronology = new JButton("Chronology");
		btnChronology.setBounds(373, 10, 136, 23);
		//frame.getContentPane().add(btnChronology);
		
		JButton btnRecursiveAdd = new JButton("Recursive Add");
		btnRecursiveAdd.setBounds(btnDocs_pos_x, btnDocs_pos_y + 400, 100, 52);
		frame.getContentPane().add(btnRecursiveAdd);

		JButton btnBenchmark = new JButton("Benchmark");
		btnBenchmark.setBounds(right_pos_x + 130*2, right_pos_y, 130, 23);
		frame.getContentPane().add(btnBenchmark);
		
	
		//prova
		final JFrame waitPane=new JFrame("Please Wait --->  Benchmark is working...");
		waitPane.setSize(400,100);
		Dimension screenSize = Toolkit.getDefaultToolkit ( ).getScreenSize();

		waitPane.setLocation ( ( screenSize.width / 2 ) - ( waitPane.getWidth ( ) / 2 ), (screenSize.height / 2 ) - ( waitPane.getHeight ( ) / 2 ) );
		waitPane.getContentPane().setLayout(null);
		waitPane.setAlwaysOnTop(true);
		waitPane.setEnabled(false);
		
		
		JLabel lblBenchemarkIsWorking = new JLabel("Benchmark is working...");
		lblBenchemarkIsWorking.setBounds(89, 23, 205, 27);
		waitPane.getContentPane().add(lblBenchemarkIsWorking);
		
		//frame Chronology
		final JFrame ChronoPane=new JFrame("Chronology");
		ChronoPane.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		chronologyTable= new JTable();
		final DefaultTableModel chronologyTableModel=(DefaultTableModel) chronologyTable.getModel();
		 chronologyTableModel.setRowCount(10);
		 chronologyTableModel.setColumnCount(1);
		 tableModel.addColumn("Search");

		
	//Search: searching after press button "Search"
		
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String queryStr = textField.getText();
				
				if(editDistance.isSelected()==true) {
					
					editdistance=checkEditDistance(editDistanceText.getText());
					System.out.println(editdistance);
				}else {
					editdistance=0;
				}
	  
				if(queryStr.isEmpty()==false) {
					//LinkList to save Chronology
					chronology.addFirst(queryStr);

				
				System.out.println("Query string: " + queryStr);
				resultsModel.setRowCount(0);
				
				 
				 Model modelUsed=null; 
				if(modelbox.getSelectedItem()=="Boolean Model") {
					System.out.println("Boolean");
					modelUsed = new BooleanModel();
				}
				
				if(modelbox.getSelectedItem()=="Vector Space Model") {
					System.out.println("Vector Space");
					modelUsed = new VectorSpaceModel();
				}
				
				if(modelbox.getSelectedItem()=="Probabilistic(BM25) Model") {
					System.out.println("Probabilistic(BM25)");
					modelUsed = new BM25();
				}
				
				if(modelbox.getSelectedItem()=="Fuzzy Model") {
					System.out.println("Fuzzy");
					modelUsed = new FuzzyModel();
				}	
				
				LinkedList<String> fields = new LinkedList<String>();
				
				if(queryStr.contains("name:")) {
					fields.add("name");
				}
				if(queryStr.contains("content:")) {
					fields.add("content");
				}
				if(fields.size() == 0) {
					fields.add("name");
					fields.add("content");
				}

		        generalIndex.setSimilarity(modelUsed.getSimilarity(), true);
				LinkedList<Hit> results = generalIndex.submitQuery(queryStr, fields, modelUsed, false);
				if (results != null) {
					for(Hit result : results) {
						resultsModel.addRow(new Object[] {result.getDocName(), result.getScore()});
					}
				}
			}
				}
		});
		
	// help for users
		Help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 				   
				JOptionPane.showMessageDialog(frame,	"Boolean Model: Using of Logic Operators (AND, OR, NOT), boosts, ... (\"(Lucene OR (Information Retrieval)^1.5) AND Boolean Model\") \" \n"
													+	"Fuzzy Model: More flexible than Boolean Model (N.B. Edit distance is used activating \"Edit Distance\"\n"
													+	"Vector Space Model: Based on a Vector representation of query and documents, very elastic on natural language queries. Ranking based on tf-idf\n"
													+ 	"Probabilistic(BM25): Use of BM25 ranking function to retrieve documents given a query\n\n"
													+ 	"Query composition: You can declare in which fields searching (name or content) writing field name in query\n"
													+   "\"name:Salomè content:Give me Iokanaan's head\" --> Salomè (Oscar Wilde) \n"
													+   "Use of WildCards is allowed, but not as first characters (Lucene parsing)");
			}
		});
		
		

		
		
	// Add new file in the table Documents
	add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser fileC=new JFileChooser();
				fileC.setMultiSelectionEnabled(true);
				fileC.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fileC.showOpenDialog(frame);
				
							
				File filesSelected[] = fileC.getSelectedFiles();
				
				for (File doc : filesSelected) {
					
					if(doc.isDirectory()) {
						subfolders(doc,tableModel);
				    }
				        						
					// Check that file is not yet in index
					if(doc.getAbsolutePath().endsWith(".txt")) {
						checkFile(doc, tableModel);
					}
					
				}
			}
		});
	
	
	//remove files selected 
	remove.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
				
				
				for(int i=0;i<tableModel.getRowCount();i++) {
					
				for(int j=0;j<generalIndex.getSize();j++) {
					
				if( (generalIndex.getDocument(j).get("path")+generalIndex.getDocument(j).get("name")).equals(fileTable.getValueAt(fileTable.getSelectedRow(), 0))) {
					generalIndex.removeDocument(j);
				}
				
	}
				}
				tableModel.removeRow(fileTable.getSelectedRow());
			}
	});
		
	// Delete All Row in the table "Documents"
	delete.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			int reply =JOptionPane.showConfirmDialog(null,"Do you want to reset all indices?", "Attention", JOptionPane.YES_NO_OPTION);
			
			
			if(reply==JOptionPane.YES_OPTION) {
			
			generalIndex.resetIndex();
			tableModel.setRowCount(0);
			}
			}
		});
	
	btnChronology.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			ChronoPane.setVisible(true);
			ChronoPane.getContentPane().add(chronologyTable);
			ChronoPane.setAlwaysOnTop(true);

			//in the center
			 Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

			    int x = (int) ((dimension.getWidth() - 300) / 2);
			    int y = (int) ((dimension.getHeight() - 215) / 2);

			ChronoPane.setBounds(x, y, 300, 215);
			int i;
			for(i=0; i< chronologyTableModel.getRowCount();i++) {

				if(chronology.size()>i)
				chronologyTableModel.setValueAt(chronology.get(i),i,0);

			}
		}
	});
	
	//Recursive Add
	btnRecursiveAdd.addActionListener(new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
			//INSERIRE CODICE QUIIIIIIIII
			
		}
	});
	
	
	
	//load index
	btnloadIndex.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			
			JFileChooser fileC=new JFileChooser();			
			fileC.setMultiSelectionEnabled(true);
			fileC.setDialogTitle("Choose files to load ");
			fileC.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileC.showOpenDialog(frame);
			
			if(fileC.getSelectedFile() == null) {
				return ;
			}
			
			int reply =JOptionPane.showConfirmDialog(null,"Do you want to load :"+ fileC.getSelectedFile()+ "?", "Attention", JOptionPane.YES_NO_OPTION);
			//aggiungi il file nella tabella documenti
			
			if(reply==JOptionPane.YES_OPTION) {
			
				File file[]=fileC.getSelectedFiles();
				if(file.length==1) {
			       System.out.println(file[0].getAbsolutePath());
			       
			       BufferedReader reader = null;
					
					try {
						reader = new BufferedReader(new FileReader(file[0]));
					} catch (FileNotFoundException e) {
						
							e.printStackTrace();
						
					}
					String content = "";
					String line;
					
					int i=0;
					try {
						File f = null;
						while((line = reader.readLine()) != null) {
							System.out.println("Loading " + line);
							f = new File(line);
							checkFile(f, tableModel);
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
							       
			       	}		
				else {
					JOptionPane.showMessageDialog(frame,"Only one file can be load \n");
				}
					
				}
				}
		
	});
	
	//Save index
	btnSaveIndex.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			
			JFileChooser fileSave = new JFileChooser();
			
			 fileSave.setDialogTitle("Specify a file to save");   
			 FileNameExtensionFilter filter = new FileNameExtensionFilter("Text File","txt");
			 fileSave.setFileFilter(filter);
	        
			int userSelection =  fileSave.showSaveDialog(frame);
			
			if (userSelection == JFileChooser.APPROVE_OPTION) {
				
			    File fileToSave =  fileSave.getSelectedFile();
			    
			    try {
					fileToSave.createNewFile();
					generalIndex.saveIndex(fileToSave.getAbsolutePath());					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			    
			    System.out.println("Save as file: " + fileToSave.getAbsolutePath());
			}
		}
	});
	
	// Benchmark
	btnBenchmark.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			int reply =JOptionPane.showConfirmDialog(null,"Do you want to launch LISA Benchmark?", "Attention", JOptionPane.YES_NO_OPTION);
			
			
			if(reply==JOptionPane.YES_OPTION) {
				
				// Before loading docs of benchmark, index is erased
				generalIndex.resetIndex();
				
				waitPane.setVisible(true);
				
				Model modelUsed=null;
				Benchmark benchmark=null;
				 
				if(modelbox.getSelectedItem()=="Boolean Model") {
					System.out.println("Boolean");
					modelUsed = new BooleanModel();
				}
					
				if(modelbox.getSelectedItem()=="Vector Space Model") {
					System.out.println("Vector Space");
					modelUsed = new VectorSpaceModel();
				}
					
				if(modelbox.getSelectedItem()=="Probabilistic(BM25) Model") {
					System.out.println("Probabilistic(BM25)");
					modelUsed = new BM25();
				}
					
				if(modelbox.getSelectedItem()=="Fuzzy Model") {
					System.out.println("Fuzzy");
					modelUsed = new FuzzyModel();
				}	
					
				
				//benchmark=new Benchmark(modelUsed,"benchmarkDocs.ser", "benchmark/lisa/LISA.QUE", "benchmark/lisa/LISA.REL");
				

				benchmark=new Benchmark(modelUsed,"megaIndice", "topics2014.xml", "qrels2015.txt");
				
				benchmark.executeBenchmark();
					
				waitPane.setVisible(false);
				int reply1 = JOptionPane.showConfirmDialog(null,"Do you want to see the results of Benchmark?", "Attention", JOptionPane.YES_NO_OPTION);
					
					
					if(reply1==JOptionPane.YES_OPTION) {
						// Grafici
						System.out.println("Starting plotting...");
						waitPane.setVisible(true);
						
						benchmark.doGraph();
						
						waitPane.setVisible(false);
						
						File files[];
						File Directory = null;
						try {
							Directory = new File(new java.io.File( "." ).getCanonicalPath()+"\\results\\");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						files=Directory.listFiles();
						Arrays.sort(files);

					}
				//After benchmark, index is erased
				generalIndex.resetIndex();
			}
			
		}
	});
	
	}
	
	public int checkEditDistance(String text) {
		int number=0;
		char n;
		if(text.length()==1) {
			n=text.charAt(0);
			
				
			if(Character.isDigit(n)==true)
			number= Integer.parseInt(String.valueOf(n));
			else {
				JOptionPane.showMessageDialog(frame,"Error in edit distance text box \n"
                                                  + "Only 1 or 2 are accepted ");
				return 2;
				}
			if(number<3 && number>0)
				return number;
			else {
				JOptionPane.showMessageDialog(frame,"Error in edit distance text box \n"
                                                   + "Only 1 or 2 are accepted ");
				
				return 2;
		}
			}
			
		JOptionPane.showMessageDialog(frame,"Error in edit distance text box \n"
                                          + "Only 1 or 2 are accepted ");
			return 2;
		
	}
	
	
	public static int getEditdistance() {
		return editdistance;
	}


	// function to decide if add all or some or no subfolders
	public void subfolders (File Directory,DefaultTableModel tableModel) {
		
		File files[];
		
		files=Directory.listFiles();
		
			for (File f : files) {
				if(f.isDirectory())	{
					int reply = JOptionPane.showConfirmDialog(null,"Do you want add all subfolders of :  "+ f.getPath() + "?", "Attention", JOptionPane.YES_NO_OPTION);
				    if (reply == JOptionPane.YES_OPTION) {
				    	subfolders(f, tableModel);
					}
				}else if(f.getAbsolutePath().endsWith(".txt")) {
					checkFile(f, tableModel);
				}
			}
				
			if(Directory.getAbsolutePath().endsWith(".txt")) {
				checkFile(Directory, tableModel);
			}
	}
	
	public void checkFile(File f, DefaultTableModel tableModel) {
		boolean inIndex = false;
		for(int i=0; i<generalIndex.getSize(); i++) {
			if( (generalIndex.getDocument(i).get("path")+generalIndex.getDocument(i).get("name")).equals(f.getAbsolutePath())){
				inIndex=true;
				break;
			}
		}
		// If inIndex is false, means that the file is not in index, so it is added to index
		if(!inIndex) {
			generalIndex.addDocument(f.getAbsolutePath());
			tableModel.setRowCount(tableModel.getRowCount()+1);
			tableModel.setValueAt(f.getAbsolutePath(), tableModel.getRowCount()-1, 0);
		}
	}
}
