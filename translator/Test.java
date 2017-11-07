package translator;

import java.util.Hashtable;
import java.util.ArrayList;
import java.lang.StringBuffer;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Printer;
import xtc.tree.Visitor;


public class Test{
	final int global = 0;
	
	class A {
		  public String toString() {
		    return "A";
		  }
	}
	
	public static void main(String[] args)
	{
		System.out.println("testtest"); 
	}

	public void secondMethod(int a,int b)
	{
		int x = 5, y = 100;
		int z = x + y;
		System.out.println("z is " + z);
		A c = new A();
	}

	public void thirdMethod(int a,int b)
	{
		int y = 0;
		for(int i = 0; i > 0; i++){
			++y;
		}
	}
}