import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReadXMLFile {

	private String line = "./dataset/clinical_dataset/topics/topics2016.xml";;
	private File XmlFile = null;
	private Integer number = 0;
	private DocumentBuilderFactory dbFactory = null;
	private DocumentBuilder dBuilder = null;
	private Document doc = null;
	private PrintWriter writer;

	public ReadXMLFile(String path) {
		try {
			writer = new PrintWriter(path, "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void SearchQuery(int n) throws ParserConfigurationException, SAXException, IOException, ParseException {

		XmlFile = new File(line);
		if (!XmlFile.exists()) {
			System.out.println("File doesn't exists or the path is wrong, " + "\nplease enter a correct path: ");
		} else {

			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(XmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("topic");

			number = n;

			Node nNode = nList.item(number - 1);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				writer.println("<top>");
				writer.println("<num> Number: " + eElement.getAttribute("number"));
				writer.println("<title> " + eElement.getElementsByTagName("summary").item(0).getTextContent() + "\n");
				writer.println("<desc> Description: \n"
						+ eElement.getElementsByTagName("summary").item(0).getTextContent() + "\n");
				writer.println("<narr> Narrative: \n"
						+ eElement.getElementsByTagName("description").item(0).getTextContent() + "\n");
				writer.println("</top> \n\n");
				if (n == 30) {
					writer.close();
				}

			}
		}
	}

}
