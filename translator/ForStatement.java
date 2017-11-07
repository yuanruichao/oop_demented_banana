package translator;

import xtc.tree.GNode;
import xtc.tree.Node;

public class ForStatement extends JavaStatement {
	// private boolean isForEach = false;
	// JavaExpression init0;
	JavaType init1;
	Declarators init2;
	JavaExpression conditional;
	JavaExpression update;

	JavaBlock body;

	public ForStatement(JavaScope scope, GNode n) {
		super(scope, n);

		// JavaType.setup(null);
		// System.out.print(n);
		init1 = JavaType.getType((String) ((Node) ((Node) ((GNode) n.get(0))
				.get(1)).get(0)).get(0));
		// System.out.println("IN THE CONSTRUCTOR"+ init1.getCppName());
		init2 = (Declarators) this.dispatch((Node) ((GNode) n.get(0)).get(2));
		// init2 = (JavaExpression)this.dispatch((Node) ((GNode)
		// n.get(0)).get(2));
		conditional = (JavaExpression) this.dispatch((Node) ((GNode) n.get(0))
				.get(3));
		update = (JavaExpression) this.dispatch((Node) ((GNode) n.get(0))
				.get(4));
		body = new JavaBlock(this, (GNode) n.get(1));
	}

	public StringBuffer print() {
		StringBuffer sb = new StringBuffer();

		// System.out.println("!!!!!!!");
		
		sb.append("for(").append(init1.getCppName() + " ")
		.append(init2.print()).append(";").append(conditional.print())
		.append(";");
		
		this.getSymbolTable().enter(body.getThisNode());
		//System.out.println("(Print)Entered scope "
				//+ this.getSymbolTable().current().getName());

		

		// System.out.println(update);

		sb.append(update.print()).append(")").append(body.print().toString());

		this.getSymbolTable().exit();

		return sb;
	}

}
