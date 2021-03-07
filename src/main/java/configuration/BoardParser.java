package configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import asset.Board;
import asset.LinkType;

public class BoardParser {

	@SuppressWarnings("unchecked")
	public static Board parse()
	{
		Map<Integer, asset.Node> nodes = null;
		
		try
		{
			// Check if there is an already serialized node Map
			try
			{
				FileInputStream fin = new FileInputStream("board.fos");
				ObjectInputStream ois = new ObjectInputStream(fin);
				Object readout = ois.readObject();
				
				if (readout instanceof Map<?, ?>)
				{
					nodes = (Map<Integer, asset.Node>) readout;
				}
				
				ois.close();
				fin.close();
			}
			catch (FileNotFoundException ex)
			{
				System.out.println("No serialized board file, parsing XML...");
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}

			if (nodes != null) return new Board(nodes);

			// Parse XML
			
			nodes = new TreeMap<>();

			ClassLoader classLoader = BoardParser.class.getClassLoader();
			File fXmlFile = new File(classLoader.getResource("board.xml").getFile());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("boardPosition");
			
			int nodeCnt = 0;
			int linkCnt = 0;

			for (int i = 0; i < nList.getLength(); i++)
			{

				Node nNode = nList.item(i);

				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{

					Element eElement = (Element) nNode;
					String nodeId = eElement.getAttribute("id");
					try
					{
						int id = Integer.parseInt(nodeId);
						nodes.put(id, new asset.Node(id));
						nodeCnt++;
					}
					catch (NumberFormatException ex)
					{
						System.out.println("Invalid field ID " + nodeId);
					}

				}
				else
				{
					System.out.println("Node is not an element at item " + i);
				}
			}
			
			System.out.println("So far " + nodeCnt + " nodes");
			
			for (int i = 0; i < nList.getLength(); i++)
			{

				Node nNode = nList.item(i);
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{

					Element eElement = (Element) nNode;
					String nodeId = eElement.getAttribute("id");
					
					try
					{
						int node1Id = Integer.parseInt(nodeId);
						
						NodeList connectionList = eElement.getElementsByTagName("action");
						for (int j = 0; j < connectionList.getLength(); j++)
						{
							Node actionNode = connectionList.item(j);
							if (actionNode.getNodeType() == Node.ELEMENT_NODE)
							{
								Element connectionElement = (Element) actionNode;
								
								int node2Id = Integer.parseInt(connectionElement.getElementsByTagName("destination").item(0).getTextContent());
								LinkType linkType = parseLinkType(connectionElement.getElementsByTagName("transportation").item(0).getTextContent());
								
								asset.Node node1 = nodes.get(node1Id);
								asset.Node node2 = nodes.get(node2Id);
								
								if (node1 != null && node2 != null)
								{
									node1.addLinkTo(node2, linkType);
									linkCnt++;
								}
								else
								{
									System.out.println("Unknown node ID at " + i + ":" + j + ", in link " + node1Id + "-" + node2Id);
								}
							}
							else
							{
								System.out.println("ActionNode is not an element at item " + i);
							}
						}
					}
					catch (NumberFormatException ex)
					{
						System.out.println("Invalid field ID " + nodeId);
					}

				}
				else
				{
					System.out.println("Node is not an element at item " + i);
				}
			}
			
			System.out.println("Found " + nodeCnt + " nodes and " + linkCnt + " links inbetween");
			return new Board(nodes);
	    }
		catch (Exception e)
		{
			e.printStackTrace();
	    }
		
		System.out.println("Unable to parse board file");
		return null;
	}
	
	public static LinkType parseLinkType(String type)
	{
		switch (type)
		{
		case "taxi": return LinkType.Taxi;
		case "bus": return LinkType.Bus;
		case "underground": return LinkType.Metro;
		case "boat": return LinkType.Boat;
		default: throw new IllegalArgumentException(type + " is not a valid LinkType");
		}
	}
}
