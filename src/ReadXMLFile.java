import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class ReadXMLFile {

	private BufferedReader in = null;
	private String line = null;
	private File XmlFile = null;
	private Integer number = 0;
	private DocumentBuilderFactory dbFactory = null;
	private DocumentBuilder dBuilder = null;
	private Document doc = null;

	public ReadXMLFile() {
	}

	public String SearchQuery() {
		System.out.println("Enter query file path: ");
		try {
			in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
			while (true) {
				// line = in.readLine();
				line = "./dataset/clinical_dataset/query/topics2014.xml";
				XmlFile = new File(line);
				if (!XmlFile.exists()) {
					System.out
							.println("File doesn't exists or the path is wrong, " + "\nplease enter a correct path: ");
				} else
					break;
			}

			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(XmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("topic");

			System.out.println("Enter query number(1-30): ");
			while ((line = in.readLine()) != null) {
				if (line.matches("([1-9]|[12][0-9]|3[0])")) {
					number = Integer.parseInt(line);
					if (number - 1 < nList.getLength())
						break;
					else
						System.out.println("Number out of range, \nplease enter a correct ones: ");
				} else
					System.out.println("Not a number, \nplease enter a correct ones: ");
			}

			Node nNode = nList.item(number - 1);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				/*
				 * System.out.println("Topic number : " + eElement.getAttribute("number"));
				 * System.out.println("Description : " +
				 * eElement.getElementsByTagName("description").item(0).getTextContent());
				 * System.out.println("Summary : " +
				 * eElement.getElementsByTagName("summary").item(0).getTextContent());
				 * System.out.println("Demographic : " +
				 * eElement.getElementsByTagName("demographic").item(0).getTextContent());
				 * System.out.println("Other : " +
				 * eElement.getElementsByTagName("other").item(0).getTextContent());
				 */
				line = eElement.getElementsByTagName("summary").item(0).getTextContent();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;
	}
}
