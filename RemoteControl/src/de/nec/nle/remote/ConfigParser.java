/*
 * Copyright NEC Europe Ltd. 2006-2007
 * 
 * This file is part of the Remote Control for the context simulator Siafu.
 * 
 * Siafu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * Siafu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.nec.nle.remote;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.nec.nle.remote.view.GUI;

public class ConfigParser implements org.xml.sax.ErrorHandler {
	private Logger logger = Logger.getLogger(this.getClass());

	private SAXParseException parserException = null;
	private Document dom;
	private boolean addIconsToButtons;

	private String xmlPath;

	private Remote control;

	public String getXmlPath() {
		return xmlPath;
	}

	public ConfigParser(String xmlPath, Remote control) throws IOException,
			SAXParseException {
		this.xmlPath = xmlPath;
		this.control = control;

		SchemaFactory sf = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema s;
		try {
			s = sf.newSchema(this.getClass().getResource(
					"/res/RemoteControlSchema.xsd"));
		} catch (SAXException e2) {
			throw new RuntimeException(
					"Invalid Schema stored in the progam code");
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setValidating(false); // No DTD
		dbf.setSchema(s);
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			db.setErrorHandler(this);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Invalid parser configuration", e);
		}

		try {
			dom = db.parse(xmlPath);
		} catch (SAXException e) {
			this.control.die();
			throw parserException;
		}
		if (parserException != null) {
			throw parserException;
		}

	}

	public InetSocketAddress getServerAddr() throws UnknownHostException {
		Element e = (Element) dom.getElementsByTagName("rc:server").item(0);

		String hostName = e.getElementsByTagName("rc:hostName").item(0)
				.getTextContent();
		Integer port = new Integer(e.getElementsByTagName("rc:port").item(0)
				.getTextContent());

		return new InetSocketAddress(InetAddress.getByName(hostName), port);

	}

	public void populateGUI(GUI gui) {
		String title = dom.getElementsByTagName("rc:title").item(0)
				.getTextContent();
		gui.setShellTitle(title);

		NodeList fontSizeNL = dom.getElementsByTagName("rc:fontSize");
		if (fontSizeNL.getLength() > 0) {
			try {
				gui.setButtonFontSize(new Integer((fontSizeNL.item(0)
						.getTextContent())));
			} catch (NumberFormatException e) {
				logger
						.warn("The fontSize element doesn't contain a positive integer. Ignoring.");
			}
		}

		addIconsToButtons = addIconsToButtons();

		NodeList groups = dom.getElementsByTagName("rc:group");

		if (groups.getLength() > 0) {
			for (int i = 0; i < groups.getLength(); i++) {
				Element group = (Element) groups.item(i);
				String caption = group.getElementsByTagName("rc:title").item(0)
						.getTextContent();
				Group groupWidget = gui.createGroup(caption);
				addButtons(gui, group, groupWidget);
			}

		} else {
			Element e = (Element) dom.getElementsByTagName("rc:remoteControl")
					.item(0);
			addButtons(gui, e, gui.getMainComposite());
		}

		gui.createFooter(getLogo(), getResetCommands());
	}

	private String[] getResetCommands() {
		String[] resetCommands = null;
		NodeList rbNL = dom.getElementsByTagName("rc:resetButton");
		if (rbNL.getLength() > 0) {
			Element rb = (Element) rbNL.item(0);
			NodeList commands = rb.getElementsByTagName("rc:command");
			resetCommands = new String[commands.getLength()];
			for (int i = 0; i < commands.getLength(); i++) {
				resetCommands[i] = commands.item(i).getTextContent();
			}
		}
		return resetCommands;
	}

	private Image getLogo() {
		Image logo = null;
		NodeList logoNodeList = dom.getElementsByTagName("rc:logo");
		if (logoNodeList.getLength() > 0) {
			URL logoURL;
			try {
				logoURL = new URL(logoNodeList.item(0).getTextContent());

				InputStream is = logoURL.openStream();
				logo = new Image(Display.getCurrent(), is);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(this.getClass().getResource(
						"/res/siafuLogo.png"));
				logger.warn("Couldn't fetch the logo image. Leaving it blank");
			}
		}
		return logo;
	}

	private void addButtons(GUI gui, Element e, Composite c) {
		NodeList nl = e.getElementsByTagName("rc:button");

		for (int i = 0; i < nl.getLength(); i++) {
			Element button = (Element) nl.item(i);
			String caption = button.getElementsByTagName("rc:caption").item(0)
					.getTextContent();
			Image icon = getIcon(e);

			String[] commands = getCommands(button);
			gui.addButton(commands, icon, caption, c);
		}
	}

	private String[] getCommands(Element e) {
		ArrayList<String> commands = new ArrayList<String>(5);
		NodeList nl = e.getElementsByTagName("rc:command");
		for (int i = 0; i < nl.getLength(); i++) {
			commands.add(nl.item(i).getTextContent());
		}
		return commands.toArray(new String[] {});

	}

	private Image getIcon(Element e) {
		if (!addIconsToButtons) {
			return null;
		} else {
			NodeList nl = e.getElementsByTagName("rc:iconURL");
			if (nl.getLength() > 0) {
				String urlString = nl.item(0).getTextContent();

				try {
					Image img = new Image(Display.getCurrent(), new URL(
							urlString).openStream());
					return img;
				} catch (Exception exception) {
					logger.warn("Can't read " + urlString
							+ " using default icon instead.");
				}
			}
			return new Image(Display.getCurrent(), this.getClass()
					.getResourceAsStream("/res/default.png"));

		}
	}

	private boolean addIconsToButtons() {
		Element e = (Element) dom.getElementsByTagName("rc:remoteControl")
				.item(0);
		String add = e.getAttribute("addIconsToButtons");
		if (add.equals("")) {
			return true;
		} else {
			return new Boolean(add);
		}
	}

	public void error(SAXParseException exception) throws SAXException {
		logger.warn(exception.getMessage());
		this.parserException = exception;
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		logger.warn(exception.getMessage());
		this.parserException = exception;
	}

	public void warning(SAXParseException exception) throws SAXException {
		logger.warn(exception.getMessage());
		exception.printStackTrace();
	}

}
