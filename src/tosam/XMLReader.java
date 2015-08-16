package tosam;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Class for reading a network state of TOSAM from an XML file.
 * 
 * @author Matthias Keysermann
 *
 */
public class XMLReader {

	private static final String FILE_VERSION = "1.3";

	public void readFromFile(String filename, Network network) {
		try {

			// DEBUG
			System.out.println("Loading from XML...");

			// DEBUG
			System.out.println("Reading from file \"" + filename + "\"...");

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(filename);

			// TOSAM
			Element elemTOSAM = (Element) doc.getElementsByTagName("TOSAM").item(0);

			// check file version
			String fileVersion = elemTOSAM.getAttribute("fileVersion");
			if (!fileVersion.equals(FILE_VERSION)) {
				System.err.println("Wrong file version!");
				return;
			}

			// DEBUG
			System.out.println("Recreating units...");

			// clear old units
			network.getUnits().clear();

			// Units			
			Element elemUnits = (Element) elemTOSAM.getElementsByTagName("Units").item(0);
			NodeList nlUnits = elemUnits.getElementsByTagName("Unit");
			for (int i = 0; i < nlUnits.getLength(); i++) {
				Element elemUnit = (Element) nlUnits.item(i);
				long id = Long.parseLong(elemUnit.getAttribute("id"));
				Object data = elemUnit.getAttribute("data");
				double load = Double.parseDouble(elemUnit.getAttribute("load"));
				double activation = Double.parseDouble(elemUnit.getAttribute("activation"));
				Unit unit = new Unit(id, data);
				unit.setLoad(load);
				unit.setActivation(activation);
				network.getUnits().add(unit);
			}

			// DEBUG
			System.out.println("Recreating associations...");

			// clear old associations
			network.getAssociations().clear();

			// Associations			
			Element elemAssociations = (Element) elemTOSAM.getElementsByTagName("Associations").item(0);
			NodeList nlAssociations = elemAssociations.getElementsByTagName("Association");
			for (int i = 0; i < nlAssociations.getLength(); i++) {
				Element elemAssociation = (Element) nlAssociations.item(i);
				long id = Long.parseLong(elemAssociation.getAttribute("id"));
				long srcUnitId = Long.parseLong(elemAssociation.getAttribute("srcUnitId"));
				long dstUnitId = Long.parseLong(elemAssociation.getAttribute("dstUnitId"));
				double learningRate = Double.parseDouble(elemAssociation.getAttribute("learningRate"));
				double weight = Double.parseDouble(elemAssociation.getAttribute("weight"));
				double signal = Double.parseDouble(elemAssociation.getAttribute("signal"));
				Unit srcUnit = null;
				Unit dstUnit = null;
				for (Unit unit : network.getUnits()) {
					if (unit.getId() == srcUnitId) {
						srcUnit = unit;
					}
					if (unit.getId() == dstUnitId) {
						dstUnit = unit;
					}
				}
				Association association = new Association(id, srcUnit, dstUnit);
				association.setLearningRate(learningRate);
				association.setWeight(weight);
				association.setSignal(signal);
				network.getAssociations().add(association);
				// associationsIn & associationsOut
				srcUnit.getAssociationsOut().add(association);
				dstUnit.getAssociationsIn().add(association);
			}

			// DEBUG
			System.out.println("Setting variables...");

			// Variables
			Element elemVariables = (Element) elemTOSAM.getElementsByTagName("Variables").item(0);
			network.setNextUnitId(Long.parseLong(elemVariables.getAttribute("nextUnitId")));
			network.setNextAssociationId(Long.parseLong(elemVariables.getAttribute("nextAssociationId")));
			network.setSignalAttraction(Boolean.parseBoolean(elemVariables.getAttribute("signalAttraction")));
			network.setAllowOverspreading(Boolean.parseBoolean(elemVariables.getAttribute("allowOverspreading")));
			network.setFullyConnected(Boolean.parseBoolean(elemVariables.getAttribute("fullyConnected")));

			// DEBUG
			System.out.println("Loading from XML finished!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
