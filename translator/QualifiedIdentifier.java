package translator;

import xtc.tree.GNode;

public class QualifiedIdentifier extends JavaExpression {
	String identifier;
	
	public QualifiedIdentifier(JavaScope scope , GNode n){
			super(scope,n);
			identifier = (String)n.get(0); 
	}	
	
	public StringBuffer print(){
		return new StringBuffer(identifier);
	}

	public String getTypeName() {
		return identifier;
	}
}
