package translator;

import java.util.ArrayList;

import xtc.tree.GNode;
import xtc.tree.Node;

public class FormalParameters extends JavaExpression{
	ArrayList<FormalParameter> parameters = new ArrayList<FormalParameter>();
	
	public FormalParameters(JavaScope scope, GNode n) {
		super(scope, n);
		for(int i = 0; i < n.size(); i++){
			parameters.add((FormalParameter) this.dispatch((GNode) n.get(i)));
		}
	}
	
	public StringBuffer print() {
		StringBuffer sb = new StringBuffer();
		for(int i = 0;i < parameters.size();i++){
			if( i != parameters.size()-1) sb.append(parameters.get(i).print()).append(", ");
			else sb.append(parameters.get(i).print());
		}
		return sb;
	}

	public String getTypeName() {
		return null;
	}
}
