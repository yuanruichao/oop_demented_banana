package translator;

import xtc.tree.GNode;


public class Identifier extends JavaExpression{
	private String IdName;
	private StringBuffer sb = new StringBuffer();
	
	public Identifier(JavaScope scope, GNode n){
		super(scope, n);
		IdName = (String) n.get(0);
	}
		
	public StringBuffer print(){
		sb.append(IdName); 
		return sb;
	}

	public String getTypeName() {
		return null;
	}
}