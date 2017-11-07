package translator;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.util.SymbolTable;
import xtc.Constants;

public abstract class JavaExpression extends JavaScope implements Printable, Typed {
	
	
	public JavaExpression(JavaScope scope,GNode node){
		super(scope,node);
		
	}
}
