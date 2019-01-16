
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CreateIndexPath {

	public static void main(String[] args) {
		try {
			new CreateIndexPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public CreateIndexPath() throws IOException {

		String docsPath;
		String file;
		String ext;
		Boolean append = null;
		BufferedReader in;
		in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
		final String OS = System.getProperty("os.name").toLowerCase();
		System.out.println(OS);
		file = "./dataset/clinical_dataset/IndexPath.txt";
		System.out.println("Insert the path to root folder: ");

		docsPath = "./dataset/clinical_dataset/pmc-text-00/";
		// docsPath = in.readLine();
		System.out.println("Insert file extension: ");
		while ((ext = in.readLine()) != null) {
			ext = ext.toLowerCase();
			if (ext.matches(".[a-z]+")) {
				break;
			} else
				System.out.println("Wrong extension, reinsert: ");
		}
		String app;
		System.out.println("Do you want to append on file " + file + " [y/n]: ");
		while ((app = in.readLine()) != null) {
			if (app.equals("y")) {
				append = true;
				break;
			} else if (app.equals("n")) {
				append = false;
				break;
			} else
				System.out.println("Wrong answer, reinsert [y/n]: ");
		}

		writeDocPath(docsPath, ext, file, append);
	}

	// Write on IndexPath.txt the path of each document that is going to add at the
	// index
	private void writeDocPath(String docsPath, String ext, String file, Boolean append) throws FileNotFoundException {

		PrintStream write;

		write = new PrintStream(new FileOutputStream(file, append));
		try {
			Files.walk(Paths.get(docsPath)).filter(p -> p.toString().endsWith(ext)).forEach(write::println);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		write.close();
	}
}
