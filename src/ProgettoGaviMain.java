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
	public JMenuItem mntmLoadFiles;
	private String indexPath;
	public static String basePath = new File("").getAbsolutePath();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
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
		
		JMenu mnIndexpath = new JMenu("IndexPath");
		menuBar.add(mnIndexpath);
		
		mntmCreateIndexpath = new JMenuItem("Create Indexpath");
		mnIndexpath.add(mntmCreateIndexpath);
		mntmCreateIndexpath.addActionListener(this);
		
		mntmLoadFiles = new JMenuItem("Load index");
		mnIndexpath.add(mntmLoadFiles);
		mntmLoadFiles.addActionListener(this);
		
		JMenu mnModels = new JMenu("Models");
		menuBar.add(mnModels);
		
		JMenuItem mntmVectorSpaceModel = new JMenuItem("Vector Space Model");
		mnModels.add(mntmVectorSpaceModel);
		
		JMenuItem mntmBooleanModel = new JMenuItem("Boolean Model");
		mnModels.add(mntmBooleanModel);
		
		JMenu mnTolerantRetriaval = new JMenu("Tolerant retriaval");
		menuBar.add(mnTolerantRetriaval);
		
		JMenu mnEfficiency = new JMenu("Efficiency");
		menuBar.add(mnEfficiency);
		
		JMenuItem mntmCalculateAndPlot = new JMenuItem("Calculate and plot");
		mnEfficiency.add(mntmCalculateAndPlot);
		frmHegregio.getContentPane().setLayout(null);
		
		JButton btnSearch = new JButton("search");
		btnSearch.setBounds(89, 11, 57, 21);
		frmHegregio.getContentPane().add(btnSearch);
		
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
		
		if ( e.getSource() == mntmLoadFiles ) {
			
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new java.io.File(".")); // start at application current directory
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fc.showOpenDialog(fc);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
			    File yourFolder = fc.getSelectedFile();
			    indexPath = yourFolder.getAbsolutePath();
			    
				indexPath = "." + indexPath.substring(basePath.length());
				System.out.println("docPath :"+ indexPath);
			}
			if (returnVal==JFileChooser.CANCEL_OPTION) return;
				
			System.out.println("docPath :"+ indexPath);
			
		}
		
		
	}
}