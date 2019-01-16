import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class ProgettoGaviMain {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProgettoGaviMain window = new ProgettoGaviMain();
					window.frame.setVisible(true);
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
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
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
	}

}
