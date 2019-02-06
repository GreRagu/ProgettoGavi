import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.FileNotFoundException;

public class ReadXMLFile {

	private BufferedReader in = null;
	private String line = null;
	private File XmlFile = null;
	private Integer number = 0;
	private DocumentBuilderFactory dbFactory = null;
	private DocumentBuilder dBuilder = null;
	private Document doc = null;
	private String topicPath ="C:\\Users\\Andrea\\eclipse-workspace\\ProgettoGavi\\dataset\\clinical_dataset\\Topics\\newTopics2014.txt";
	private PrintWriter writer;

	public ReadXMLFile() {
		 try {
			writer = new PrintWriter(topicPath, "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String SearchQuery( int n ) {
		
		try {
			in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
			while (true) {
				// line = in.readLine();
				line = new java.io.File(".")+"/dataset/clinical_dataset/topics/topics2014.xml";
				XmlFile = new File(line);
				if (!XmlFile.exists()) {
					System.out.println("File doesn't exists or the path is wrong, " + "\nplease enter a correct path: ");
				} else
					break;
			}

			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(XmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("topic");

			/*System.out.println("Enter query number(1-30): ");
			while ((line = in.readLine()) != null) {
				if (line.matches("([1-9]|[12][0-9]|3[0])")) {
					number = Integer.parseInt(line);
					if (number - 1 < nList.getLength())
						break;
					else
						System.out.println("Number out of range, \nplease enter a correct ones: ");
				} else
					System.out.println("Not a number, \nplease enter a correct ones: ");
			}*/
			
			number = n;

			Node nNode = nList.item(number - 1);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
					
				  /*System.out.println("Topic number : " + eElement.getAttribute("number"));
				  System.out.println("Description : " +
				  eElement.getElementsByTagName("description").item(0).getTextContent());
				  System.out.println("Summary : " +
				  eElement.getElementsByTagName("summary").item(0).getTextContent());*/
				  //System.out.println("Demographic : " +
				  //eElement.getElementsByTagName("demographic").item(0).getTextContent());
				  //System.out.println("Other : " +
				  //eElement.getElementsByTagName("other").item(0).getTextContent());
				

				writer.println("<top>");
				writer.println("<num> Number: " + eElement.getAttribute("number") );
				//writer.println("<type> " + eElement.getElementsByTagName("description").item(0).getTextContent());
				writer.println("<title> " + eElement.getElementsByTagName("summary").item(0).getTextContent() );				
				writer.println("<desc> Description: " + eElement.getElementsByTagName("description").item(0).getTextContent()  );
				writer.println("<narr> Narrative: ");
				writer.println("</top>" );
				if ( n == 30 ) {
					writer.close();
				} 
				 
				line = eElement.getElementsByTagName("summary").item(0).getTextContent();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;
	}
	 
}
