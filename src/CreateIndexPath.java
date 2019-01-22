
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class CreateIndexPath {
	private String file;
	private String ext;
	private Integer FileNumber = 0;
	private String docsPath;
	private Boolean append;
	
	public CreateIndexPath() {}
	
	public int CreateFile(JFrame Parent) throws IOException {

		file = "./dataset/clinical_dataset/IndexPath.txt";
		
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new java.io.File(".")); // start at application current directory
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(fc);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
		    File yourFolder = fc.getSelectedFile();
		    docsPath = yourFolder.getAbsolutePath();
		    
			docsPath = "." + docsPath.substring(ProgettoGaviMain.basePath.length());
			System.out.println(docsPath);
		}
		if (returnVal==JFileChooser.CANCEL_OPTION) return 0;

		ext = (String)JOptionPane.showInputDialog("Insert file extension", "nxml");
		
		while (ext != null) {
			ext = ext.toLowerCase();
			if (ext.matches(".[a-z]+")) {
				break;
			} else
				ext = (String)JOptionPane.showInputDialog("Wrong extension, reinsert: ");
		}
		if(ext!=null) {
		
			int dialogResult = JOptionPane.showConfirmDialog (null, "Would You Like to append on file" + file + " ? ","",JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION){
				append = true;
			}
			else {
				append = false;
				
			}
			
			JDialog dlgProgress = new JDialog(Parent, "Please wait...", false);
			dlgProgress.getContentPane();
		    BorderFactory.createTitledBorder("Loading file...");
		    dlgProgress.setSize(300, 100);
		    dlgProgress.setVisible(true);
		    Parent.setEnabled(false);	    
	
			FileNumber = writeDocPath(docsPath, ext, file, append);
			
			dlgProgress.dispose();
		    Parent.setEnabled(true);
		    JOptionPane.showMessageDialog(Parent, "Caricamento completato", "Completato", JOptionPane.INFORMATION_MESSAGE);
			return FileNumber;
		    
		}
		return 0;
	}

	// Write on IndexPath.txt the path of each document that is going to add at the
	// index
	private int writeDocPath(String docsPath, String ext, String file, Boolean append) throws FileNotFoundException {

	    PrintStream write = new PrintStream(new FileOutputStream(file, append));
		try {
			Files.walk(Paths.get(docsPath)).filter(p -> p.toString().endsWith(ext)).forEach(write::println);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		write.close();
		FileReader fr = new FileReader(file);
		LineNumberReader lnr = new LineNumberReader(fr);
	    
	    int linenumber = 0;
	    
        try {
			while (lnr.readLine() != null){
				linenumber++;
			}
			lnr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return linenumber;
	}

}
