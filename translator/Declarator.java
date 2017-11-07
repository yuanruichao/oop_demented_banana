package translator;
import xtc.tree.GNode;
import xtc.tree.Node;

public class Declarator extends JavaExpression { 
	private StringBuffer sb = new StringBuffer();
	private String valN;
	private Dimensions dim;
	private JavaExpression expression;
	public Declarator(JavaScope scope, GNode n) {
		super(scope,n);
		valN = (String)n.get(0);
		if (n.get(1) != null) dim = (Dimensions) this.dispatch((Node) n.get(1));
		if (n.get(2) != null) expression = (JavaExpression) this.dispatch((Node) n.get(2));
	}
	
	public StringBuffer print() {
		sb.append(valN);
		if (dim != null) sb.append(dim.print());
		if (expression != null)sb.append(" = ").append(expression.print());
		return sb;
	}

	public String getTypeName() {
		return null;
	}
}


