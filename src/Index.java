
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class Index implements ActionListener{

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

	public Index(JFrame Parent, Integer FileCount, String IndexFile, MyModel M) {
		this.ParentFrame = Parent;
		this.Number = FileCount;
		this.indexFile = IndexFile;
		this.M = M;
	}

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
		if (dResult == JOptionPane.NO_OPTION) {
			Number = 0;
			return null;
		}

		System.out.println(Number);
	    
		dlgProgress = new JDialog(ParentFrame, "Please wait...", false);
		dlgProgress.setLocationRelativeTo(ParentFrame);
		dlgProgress.setSize(500, 110);
		dlgProgress.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		Container content = dlgProgress.getContentPane();
		Border border = BorderFactory
				.createTitledBorder("Indexing to directory " + indexDir + "...");
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
		
		return indexDir + " " + Number;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == start) {
			ParentFrame.setEnabled(false);
			dlgProgress.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			start.setEnabled(false);
			IndexWorker c = new IndexWorker(indexDir, append, progressBar, dlgProgress, ParentFrame, M, indexFile, Number);
			c.start();
		}
	}
}
