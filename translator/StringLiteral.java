package translator;

import xtc.tree.GNode;

public class StringLiteral extends JavaExpression {
	String str;

	public StringLiteral(JavaScope scope, GNode n){
		super(scope,n);
		str = (String)n.get(0);
	}
	
	public StringBuffer print(){
		StringBuffer sb = new StringBuffer();
		sb.append("__rt::literal(");
		sb.append(str).append(")");
		return sb;
	}

	public String getTypeName() {
		return "String";
	}
}
