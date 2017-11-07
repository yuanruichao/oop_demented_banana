package translator;

import xtc.tree.GNode;

public class Literal extends JavaExpression{
	private String LitVal;
	
	public Literal(JavaScope scope, GNode n) {
		super(scope, n);
		LitVal = (String) n.get(0);
	}

	public StringBuffer print(){
		StringBuffer sb = new StringBuffer(LitVal);
		return sb;
	}

	public String getTypeName() {
		// TODO Auto-generated method stub
		return null;
	}
}
