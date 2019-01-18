
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class CreateIndexPath {
	public CreateIndexPath() throws IOException {
		String docsPath = "";
		String file = "";
		String ext = "";
		Boolean append = null;

		file = "./dataset/clinical_dataset/IndexPath.txt";
		
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new java.io.File(".")); // start at application current directory
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(fc);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
		    File yourFolder = fc.getSelectedFile();
			docsPath = yourFolder.getAbsolutePath();
			
			String basePath = new File("").getAbsolutePath();
			docsPath = "." + docsPath.substring(basePath.length());
			System.out.println("docPath :"+ docsPath);
		}

		ext = (String)JOptionPane.showInputDialog("Insert file extension", "nxml");
		while ( ext != null) {
			ext = ext.toLowerCase();
			if (ext.matches(".[a-z]+")) {
				break;
			} else
				ext = (String)JOptionPane.showInputDialog("Wrong extension, reinsert: ");
		}
		
		int dialogResult = JOptionPane.showConfirmDialog (null, "Would You Like to append on file" + file + " ? ","",JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){
			append = true;
		}
		else {
			append = false;
			
		}
		
	    writeDocPath(docsPath, ext, file, append);
	}

	// Write on IndexPath.txt the path of each document that is going to add at the
	// index
	private void writeDocPath(String docsPath, String ext, String file, Boolean append) throws FileNotFoundException {
		
	    PrintStream write = new PrintStream(new FileOutputStream(file, append));
	    try {
			Files.walk(Paths.get(docsPath)).filter(p -> p.toString().endsWith(ext)).forEach(write::println);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		write.close();
	}
}
