package translator;

import xtc.tree.GNode;
import xtc.tree.Node;

public class NewArrayExpression<T> extends JavaExpression{
	
	private String identifier;
	private JavaType type;
	private boolean isTwoD = false;
	private JavaExpression[] dimension = new JavaExpression[2];	
		
	public NewArrayExpression(JavaScope scope, GNode node) {
		super(scope, node);		
		this.identifier= (String) ((GNode) node.get(0)).get(0);
		//System.out.println("       type == " + identifier);
		type = JavaType.getType(identifier);
		int count = 0;
		
		dimension[1] = null;
		
		GNode cDim = (GNode) node.get(1);
		for(Object o : cDim) {
			if(count >= 2) break;
			dimension[count] = (JavaExpression) this.dispatch((GNode) o);
			count++;
		}
		
		if(dimension[1] != null) isTwoD = true;
	}

	public JavaExpression getSecondDim() {
		return dimension[1];
	}
	
	public StringBuffer print() {
		StringBuffer sb = new StringBuffer();
		if(!isTwoD) {
			sb.append("new __rt::Array<").append(type.getCppName()).append(">(").append(dimension[0].print()).append(")");
		} else {
			sb.append("new __rt::Array<__rt::Ptr<__rt::Array<").append(type.getCppName()).append("> > >(").append(dimension[0].print()).append(")");
		}
		
		return sb;
	}

	public JavaType getArrayType() {
		return type;
	}
	
	public boolean isTwoD() {
		return isTwoD;
	}

	public String getTypeName() {
		return null;
	}

}
