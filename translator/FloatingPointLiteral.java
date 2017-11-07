package translator;

import xtc.tree.GNode;

public class FloatingPointLiteral extends JavaExpression {

	String value;
	
	public FloatingPointLiteral(JavaScope scope, GNode n){
		super(scope,n);
		value = (String)n.get(0);
		
	}
	public StringBuffer print(){
		StringBuffer sb = new StringBuffer();
		sb.append(value);
		return sb;
	}
	public String getTypeName() {
		return "double";
	}

}
