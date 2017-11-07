package translator;

import xtc.tree.GNode;

public class NewClassExpression extends JavaExpression {
	
	private JavaExpression className;
	private JavaExpression arguments;
	
	public NewClassExpression(JavaScope scope, GNode node){
		super(scope,node);
		//System.out.println("new class");
		//System.out.println(node.get(2));
		className = (JavaExpression) this.dispatch( (GNode) node.get(2));
		arguments = (JavaExpression) this.dispatch( (GNode) node.get(3));
	}
	
	public StringBuffer print(){
		StringBuffer sb = new StringBuffer();
		
		JavaType.Object jt = (JavaType.Object) JavaType.getType(className.print().toString());
		
		sb.append(jt.getJavaClass().getCppName()).append("::init(");
		
		sb.append("new ")
			.append(JavaType.getType(className.print().toString()).getConstructorName())
			.append("(")
			.append(")");
		
		StringBuffer arg = arguments.print();
		if(!arg.toString().equals("")) {
			sb.append(", ");
			sb.append(arg);
		}
		
		sb.append(")");
		return sb;
	}

	public String getTypeName() {
		return className.getTypeName();
	}
}
