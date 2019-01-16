import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

public class ProgettoGaviMain {

	private JFrame frmHegregio;
	private JTextField txtSearch;
	private JTable table;

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
		frmHegregio = new JFrame();
		frmHegregio.setTitle("Hegregio");
		frmHegregio.setBounds(100, 100, 450, 300);
		frmHegregio.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmHegregio.setJMenuBar(menuBar);
		
		JMenu mnIndexpath = new JMenu("IndexPath");
		menuBar.add(mnIndexpath);
		
		JMenuItem mntmCreateIndexpath = new JMenuItem("Create Indexpath");
		mnIndexpath.add(mntmCreateIndexpath);
		
		JMenuItem mntmLoadFiles = new JMenuItem("Load files");
		mnIndexpath.add(mntmLoadFiles);
		
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
		
		JLabel lblRicercaSuN = new JLabel("Ricerca su N file con modello");
		lblRicercaSuN.setHorizontalAlignment(SwingConstants.CENTER);
		lblRicercaSuN.setBounds(61, 54, 301, 14);
		frmHegregio.getContentPane().add(lblRicercaSuN);
	}
}