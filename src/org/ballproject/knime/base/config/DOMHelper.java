package org.ballproject.knime.base.config;

import java.util.List;

import org.dom4j.Node;

public class DOMHelper
{
	public static List<Node> selectNodes(Node root, String query) throws Exception
	{
		List<Node> result = root.selectNodes(query);
		//if(result==null)
		//	throw new Exception("XPath query yielded null result");
		return result;
	}
	
	public static Node selectSingleNode(Node root, String query) throws Exception
	{
		Node result = root.selectSingleNode(query);
		if(result==null)
			throw new Exception("XPath query yielded null result");
		return result;
	}
	
	public static String valueOf(Node n, String query) throws Exception
	{
		String val = n.valueOf(query);
		if(val==null)
			throw new Exception("XPath query yielded null result");
		return val;
	}
}
