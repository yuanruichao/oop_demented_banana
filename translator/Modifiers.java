package translator;

import java.util.ArrayList;

import xtc.tree.GNode;
import xtc.tree.Node;

public class Modifiers extends JavaExpression{
	private ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
	
	public Modifiers(JavaScope scope, GNode n) {
		super(scope, n);
		for(int i = 0; i < n.size(); i++){
			modifiers.add((Modifier)this.dispatch((GNode) n.get(i)));
		}
	}
	
	public StringBuffer print() {
		StringBuffer sb = new StringBuffer();
		for(Modifier i: modifiers){
			sb.append(i.print()).append(" ");
		}
		return sb;
	}
	
	public int getSize(){
		return modifiers.size();
	}

	public String getTypeName() {
		return null;
	}
}
