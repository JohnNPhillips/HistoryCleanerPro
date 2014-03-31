package com.ayros.historycleaner.helpers;

import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class PrefsModifier
{
	private String prefData;

	public PrefsModifier(String path)
	{
		prefData = RootHelper.getFileContents(path);
	}

	public String getValue(String key)
	{
		if (prefData == null)
		{
			return null;
		}
		
		XPath xpath = XPathFactory.newInstance().newXPath();
	    InputSource source = new InputSource(new StringReader(prefData));
		String expression = "//*[@name='" + key + "']";
		try
		{
			NodeList nodes = (NodeList) xpath.evaluate(expression, source, XPathConstants.NODESET);
			if (nodes.getLength() != 1)
			{
				return null;
			}
			
			Node n = nodes.item(0);
			
			return n.getTextContent();
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
