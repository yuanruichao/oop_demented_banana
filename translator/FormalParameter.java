package translator;
import xtc.tree.GNode;
import xtc.tree.Node;

public class FormalParameter extends JavaExpression{
	private Modifiers modifiers = null;
	private String javaType;
	private String parameterName = null;
	private Dimensions dimensions = null;
	
	public FormalParameter(JavaScope scope, GNode n) {
		super(scope, n);
		JavaType.setup(null);
		modifiers = (Modifiers) this.dispatch((GNode)n.get(0));
		javaType = (String) ((Node) ((Node) n.get(1)).get(0)).get(0);
		parameterName = (String) n.get(3);
		if(n.get(4) != null) dimensions = (Dimensions) this.dispatch((GNode) n.get(4));
	}


	public StringBuffer print(){
		StringBuffer sb = new StringBuffer();
		JavaType.setup(null);
		if(modifiers.getSize()>0) sb.append(modifiers.print());
		sb.append(JavaType.getType(javaType).getCppName());
		sb.append(" ").append(parameterName);
		if(dimensions != null) sb.append(dimensions.print());
		return sb;
	}


	public String getTypeName() {
		return null;
	}
}