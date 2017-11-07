package translator;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Printer;
import xtc.tree.Visitor;

import java.util.Hashtable;
import java.util.ArrayList;
import java.lang.StringBuffer;


public class JavaBlock extends JavaScope implements Printable{
	private GNode node;
	
	public JavaBlock(JavaScope scope,GNode n){
		super(scope,n);
		node = n;
	}
	
	public StringBuffer printInit() {
		StringBuffer bf = new StringBuffer();
		
		for(Object o : node){
			if(o instanceof GNode){
				Printable p = (Printable) this.dispatch((GNode) o);
				
				boolean flag = false;
				if(p instanceof ForStatement || p instanceof JavaBlock) {					
					this.getSymbolTable().enter((Node) o);
					//System.out.println("(Print)Entered scope " + this.getSymbolTable().current().getName());
					flag = true;
				}
				
				bf.append(p.print());

				bf.append(";\n");
				
				if(flag) {
					this.getSymbolTable().exit();
				}
			}
		}
		
		return bf;
	}
	
	public StringBuffer print(){
		StringBuffer bf = new StringBuffer();
		bf.append("{\n");
		
		for(Object o : node){
			if(o instanceof GNode){
				Printable p = (Printable) this.dispatch((GNode) o);
				
				boolean flag = false;
				if(p instanceof ForStatement || p instanceof JavaBlock) {					
					this.getSymbolTable().enter((Node) o);
					//System.out.println("(Print)Entered scope " + this.getSymbolTable().current().getName());
					flag = true;
				}
				
				if(p != null)bf.append(p.print());

				bf.append(";\n");
				
				if(flag) {
					this.getSymbolTable().exit();
				}
			}
		}
		bf.append("}\n");
		return bf;
	}
}
