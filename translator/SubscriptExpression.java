package translator;

import java.util.ArrayList;

import xtc.tree.GNode;
import xtc.tree.Node;

public class SubscriptExpression extends JavaExpression{
	
	JavaExpression name;
	ArrayList<JavaExpression> dim = new ArrayList<JavaExpression>();
	
	public SubscriptExpression(JavaScope scope, GNode node) {
		super(scope, node);
		boolean first = true;
		
		for(Object o : node) {
			if(o instanceof GNode) {
				if(first) {
					name = (JavaExpression) this.dispatch((GNode) o);
				} else {
					dim.add((JavaExpression) this.dispatch((GNode) o));
				}
				first = false;
			}
		}
	}

	public StringBuffer print() {
		StringBuffer sb = new StringBuffer();
		sb.append("(*").append(name.print()).append(")");
		if(dim.size() == 1) {
			sb.append("[").append(dim.get(0).print()).append("]");
		} else if(dim.size() > 1) {
			sb.append("[").append(dim.get(0).print()).append("]");
			sb.append("[").append(dim.get(1).print()).append("]");
		}
		
		return sb;
	}

	public String getTypeName() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
