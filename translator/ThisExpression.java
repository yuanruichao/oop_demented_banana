package translator;

import xtc.tree.GNode;

public class ThisExpression extends JavaExpression  {
		public ThisExpression(JavaScope scope,GNode node){
			super(scope,node);
		}
		
		public StringBuffer print(){
			return new StringBuffer("__this");
		}

		public String getTypeName() {
			return null;
		}
}
