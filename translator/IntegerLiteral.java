package translator;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Printer;
import xtc.tree.Visitor;

public class IntegerLiteral extends JavaExpression{//making this useless changing to Literal.java -yrc
	int value;
	
	public IntegerLiteral(JavaScope scope, GNode n){
		super(scope,n);
		value = Integer.parseInt((String)n.get(0));
		
	}
	public StringBuffer print(){
		StringBuffer sb = new StringBuffer();
		sb.append(value);
		return sb;
	}
	public String getTypeName() {
		return "int";
	}
}