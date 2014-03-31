package com.ayros.historycleaner.helpers;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XML
{
	public static Document getDocument(String xml)
	{
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);
		}
		catch (ParserConfigurationException e)
		{
			return null;
		}
		catch (SAXException e)
		{
			return null;
		}
		catch (IOException e)
		{
			return null;
		}
		
		return doc;
	}
}
