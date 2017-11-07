package translator;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Printer;
import xtc.tree.Visitor;

public class FieldDec extends Visitor {
	
	protected JavaType type;
	
	protected JavaScope scope;
	
	protected JavaField fld;
	
	protected boolean isStatic = false;
	
	public JavaField getField() {
		return this.fld;
	}
	
	public FieldDec(JavaScope scope, GNode n) {
		this.scope = scope;
		this.dispatch(n);
	}
	
	public void visitType(GNode n) {
		String type = (String) ((GNode)n.get(0)).get(0);
		int dim = 0;
		if(n.get(1) != null) {
			for(Object o : (GNode) n.get(1)) {
				dim++;
			}
		}
		//System.out.println("         dim: " + dim);
		String tmp = "";
		if(dim == 1) tmp = tmp + "[]";
		if(dim > 1) tmp = tmp + "[][]";
		if(dim == 0) this.type = JavaType.getType(type);
			else this.type = JavaType.getType(type + tmp);
	}
	
	public void visitDeclarator(GNode n) {
		JavaField fld = new JavaField(this.scope, n, this.type, false);
		this.fld = fld;
		if(isStatic) {
			fld.setStatic();
			
		}
		
		this.scope.addField(fld);
	}
	
	public void visitModifiers(GNode n) {
		for(Object o : n) {
			if(o instanceof Node) {
				String s = (String) ((Node) o).get(0);
				if(s.equals("static")) {
					this.isStatic = true;
				}
				
			}
		}
	}
	
	public void visit(Node n) {
		for (Object o : n) {
			if (o instanceof Node)
				this.dispatch((Node)o);
		}
	}
	
	public static class FormalParameters extends FieldDec {

		public FormalParameters(JavaScope scope, GNode n) {
			super(scope, n);
			JavaField fld = new JavaField.FormalParameters(this.scope, n, this.type, true);
			this.fld = fld;
			
			this.scope.addField(fld);
		}
			
	}
	
}
