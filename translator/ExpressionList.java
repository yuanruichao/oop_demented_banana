package translator;

import java.util.ArrayList;

import xtc.tree.GNode;
import xtc.tree.Node;

public class ExpressionList extends JavaExpression{
	ArrayList<JavaExpression> jes = new ArrayList<JavaExpression>();
	
	public ExpressionList(JavaScope scope, GNode n) {
		super(scope, n);
		for(int i = 0; i < n.size(); i++){
			jes.add((JavaExpression) this.dispatch((Node) n.get(i)));
		}
	}
	
	public StringBuffer print(){
		StringBuffer sb = new StringBuffer();
		for(JavaExpression i : jes){
			sb  .append(i.print());
		}
		return sb;
	}

	public String getTypeName() {
		return null;
	}
}