package translator;

import xtc.tree.GNode;

public class Dimensions extends JavaExpression{
	private int dim;
	StringBuffer sb = new StringBuffer();
	
	public Dimensions(JavaScope scope, GNode n) {
		super(scope, n);
		//System.out.println("inDim");
		int i = n.size();
		/*try{
			while(true){
				n.get(i);
				i++;
			}
		}catch(Exception e){}*/
		dim = i;
	}
	
	public StringBuffer print() {
		for(int i = 0; i < dim; i++){
			sb.append("[]");
		}
		return sb;
	}

	public String getTypeName() {
		return null;
	}
}
