package org.ballproject.knime.base.treetabledialog;

import java.util.ArrayList;
import java.util.List;

public class Node<T>
{
	private Node<T> parent;
	private T       payload;
	
	private List<Node<T>> children = new ArrayList<Node<T>>();
	
	private String name;
	
	public Node()
	{
		parent = null;
		name   = "root";
	}
	
	public Node(Node<T> p, T payload, String name)
	{
		parent = p;
		this.payload = payload;
		this.name = name;
	}
	
	public void addChild(Node<T> child)
	{
		children.add(child);
	}
	
	public Node<T> getChild(int idx)
	{
		return children.get(idx);
	}
	
	public int getNumChildren()
	{
		return children.size();
	}
	
	public int getChildIndex(Node<T> child)
	{
		int idx = 0;
		for(Node<T> c: children)
		{
			if(child.equals(c))
				return idx;
			idx++;
		}
		return -1;
	}
	
	public boolean isLeaf()
	{
		return (children.size()==0);
	}
	
	public T getPayload()
	{
		return payload;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String toString()
	{
		return name;
	}
}
