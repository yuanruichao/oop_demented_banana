package translator;

import xtc.tree.GNode;

public class ConditionalStatement extends JavaStatement {
	JavaExpression je;
	JavaBlock jb;
	JavaBlock jbe;
	
	public ConditionalStatement(JavaScope scope, GNode n) {
		super(scope, n);
		//System.out.println("in ComditionalStatement");
		//System.out.println(n);
		je = (JavaExpression)this.dispatch((GNode)n.get(0));
		jb = new JavaBlock(this,(GNode)n.get(1));
		if(n.get(2)!=null){
			jbe = new JavaBlock(this,(GNode)n.get(2));
		}
	}

	public StringBuffer print() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("if(").append(je.print()).append(")");
		sb.append("\n").append(jb.print());
		if(jbe!=null){
			sb.append("else").append("\n").append(jbe.print());
		}
		return sb;
	}
	
}
