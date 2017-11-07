package translator;

import xtc.tree.GNode;

public class CastExpression extends JavaExpression {
	
	JavaType type;
	JavaExpression je;
	
	public CastExpression(JavaScope scope, GNode node){
		super(scope,node);
		type = JavaType.getType((String)((GNode)((GNode)node.get(0)).get(0)).get(0));
		je = (JavaExpression) this.dispatch(((GNode)node.get(1)));
	}
	
	public StringBuffer print(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("__rt::java_cast<");
		
		sb.append(type.getCppName()).append(", ");
		
		if(je instanceof PrimaryIdentifier) {
			String type = ((PrimaryIdentifier) je).getTypeName();
			sb.append(JavaType.getType(type).getCppName());
		} else sb.append("Object");
		
		sb.append(">(").append(je.print()).append(")");
		
		return sb;
	}

	public String getTypeName() {
		return type.getName();
	}
	
	
}
