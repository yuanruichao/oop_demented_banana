package translator;

import java.util.ArrayList;

import xtc.tree.GNode;
import xtc.tree.Node;

public class Arguments extends JavaExpression {
	ArrayList<JavaExpression> args = new ArrayList<JavaExpression>();
	public Arguments(JavaScope scope, GNode n) {
		super(scope, n);
		for(int i = 0; i < n.size(); i++){
			args.add((JavaExpression) this.dispatch((Node) n.get(i)));
		}
	}

	public StringBuffer print() {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		int count = 0;
		for(JavaExpression i: args){
			if(!first) sb.append(", ");
			first = false;
			
			sb.append(i.print());
			count++;
		}
		return sb;
	}
	
	public ArrayList<String> getTypeNameList(){
		ArrayList<String> nameList = new ArrayList<String>();
		for(JavaExpression i: args) {
			if(i != null) nameList.add(i.getTypeName());
		}
		return nameList;
	}

	public String getTypeName() {
		return null;
	}
}
