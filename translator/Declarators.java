package translator;
import xtc.tree.GNode;
import xtc.tree.Node;

import java.util.ArrayList;

public class Declarators extends JavaExpression{
	ArrayList<Declarator> decls = new ArrayList<Declarator>();
	
	public Declarators(JavaScope scope, GNode n) {
		super(scope,n);
		//System.out.println("in declartors");
		
		for(int i = 0; i < n.size(); i++){
			decls.add((Declarator) this.dispatch((Node) n.get(i)));
		}
	}


	public StringBuffer print() {
		StringBuffer sb = new StringBuffer();
		for(Declarator i: decls){
			sb.append(i.print()).append(" ");
		}
		return sb;
	}


	public String getTypeName() {
		return null;
	}
}
