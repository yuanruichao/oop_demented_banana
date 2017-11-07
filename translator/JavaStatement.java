package translator;

import xtc.tree.GNode;

public abstract class JavaStatement extends JavaScope implements Printable {
	public JavaStatement(JavaScope scope,GNode node){
		super(scope,node);
	}
}
