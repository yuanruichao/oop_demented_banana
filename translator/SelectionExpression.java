package translator;

import xtc.tree.GNode;

public class SelectionExpression extends JavaExpression{
	
	JavaExpression className;
	String method;
	
	public SelectionExpression(JavaScope scope,GNode node){
		super(scope,node);
		className = (JavaExpression) this.dispatch((GNode) node.get(0));
		method = (String) node.get(1);
	}
	
	public StringBuffer print(){
		StringBuffer sb = new StringBuffer();
		if(className != null) {
			if(!className.print().toString().equals(this.getJavaClass().getName())) sb.append(className.print()).append("->");
			else sb.append(this.getJavaClass().getCppName()).append("::");
		}
		sb.append(method);
		return sb;
	}

	public String getTypeName() {
		String name = className.getTypeName();
		JavaClass cls = this.getJavaFile().getClass(name);
		JavaField jfd = cls.getField(method);
		return jfd.getType().getName();
	}
}
