package translator;

import translator.JavaExpression;
import xtc.lang.JavaEntities;
import xtc.tree.GNode;
import xtc.type.Type;
import xtc.util.SymbolTable;

public class ReturnStatement extends JavaStatement{
	JavaExpression je;
	public ReturnStatement(JavaScope scope, GNode n) {
		super(scope, n);
		je = (JavaExpression) this.dispatch((GNode) n.get(0));
	}
	
	public StringBuffer print() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("return ");
		
		sb.append(je.print());
		return sb;
	}
}
