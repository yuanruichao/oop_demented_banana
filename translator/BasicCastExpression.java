package translator;

import xtc.tree.GNode;

public class BasicCastExpression extends JavaExpression {

	JavaType type;
	JavaExpression je;
	
	public BasicCastExpression(JavaScope scope, GNode node){
		super(scope,node);
		type = JavaType.getType((String)(((GNode)node.get(0)).get(0)));
		je = (JavaExpression) this.dispatch(((GNode)node.get(2)));
	}
	
	public StringBuffer print() {
		StringBuffer sb = new StringBuffer();
		sb.append("(").append(type.getCppName()).append(")").append(je.print());
		
		return sb;
	}

	public String getTypeName() {
		return type.getName();
	}

}
