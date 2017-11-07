package translator;

import xtc.tree.GNode;

public class BooleanLiteral extends JavaExpression {
	String str;
	
	public BooleanLiteral(JavaScope scope, GNode n){
		super(scope,n);
		str = (String)n.get(0);
	}
	
	public StringBuffer print(){
		StringBuffer sb = new StringBuffer();
		sb.append(str);
		return sb;
	}

	public String getTypeName() {
		return "boolean";
	}
}