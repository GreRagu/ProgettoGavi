import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReadXMLFile implements ActionListener{

	private String line = null;
	private String path =  "./dataset/clinical_dataset/topics/topics2014.xml";
	private File XmlFile = null;
	private Integer number = 0;
	private DocumentBuilderFactory dbFactory = null;
	private DocumentBuilder dBuilder = null;
	private Document doc = null;
	private JFrame Parent;
	private JButton okbtn;
	private JFormattedTextField query;
	private JDialog queryNumber;

	public ReadXMLFile(JFrame Parent) {
		this.Parent = Parent;
	}
	
	@SuppressWarnings("deprecation")
	public int queryNumber() {
		queryNumber = new JDialog(Parent, "Desired query", true);
		queryNumber.setSize(350, 150);
		queryNumber.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		queryNumber.setLayout(null);
		JLabel label = new JLabel("Insert the query number(1-30) (only number, default 1)");
		label.setBounds(10, 10, 400, 20);
		queryNumber.add(label);
		query = new JFormattedTextField(NumberFormat.getIntegerInstance());
		query.setBounds(50, 50, 50, 20);
		query.setValue(new Long("1"));
		queryNumber.add(query);
		okbtn = new JButton("OK");
		okbtn.setBounds(120, 50, 70, 20);
		okbtn.addActionListener(this);
		queryNumber.add(okbtn);
		queryNumber.setVisible(true);
		return number;
	}

	public String SearchQuery(Integer num) {
		
		XmlFile = new File(path);
		if (!XmlFile.exists()) {
			System.out.println("File doesn't exists or the path is wrong");
		}

		dbFactory = DocumentBuilderFactory.newInstance();
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(Parent, "Can't open the file", "Error", JOptionPane.ERROR_MESSAGE);
		}
		try {
			doc = dBuilder.parse(XmlFile);
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(Parent, "Can't open the file", "Error", JOptionPane.ERROR_MESSAGE);
		}

		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("topic");

		Node nNode = nList.item(num - 1);

		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) nNode;

			line = eElement.getElementsByTagName("summary").item(0).getTextContent();
		}
		return line;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == okbtn) {
			if(!query.getValue().equals("0")) {
				int k = (int) (long) query.getValue();
				if(k > 0 & k <= 30) {
					number = k;
				}
				else {
					number = 1;
				}
			}
			queryNumber.dispose();
		}
	}
	 
}
