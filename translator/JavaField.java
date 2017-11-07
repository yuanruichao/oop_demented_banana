package translator;
import java.lang.StringBuffer;

import translator.JavaType.Primitive;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Printer;
import xtc.tree.Visitor;

public class JavaField extends JavaScope implements Printable{	

	private GNode node = null;
	
	protected String name;
	
	private JavaType type;
	
	private JavaExpression init = null;
	
	private boolean isStatic = false;
	
	public JavaField(JavaScope scope, GNode n, JavaType type, boolean isFormal) {
		super(scope, n);
		this.type = type;
		node = n;
		if(!isFormal) {
			this.name = (String) n.get(0);
			if(n.get(2) != null) {
				init = (JavaExpression) this.dispatch((GNode) n.get(2));
			}
		}
	}
	
	public void setStatic() {
		isStatic = true;
	}
	
	public boolean isStatic() {
		return isStatic;
	}
	
	public String getName() {
		return this.name;
	}
	
	public JavaType getType() {
		return this.type;
	}
	
	
	public StringBuffer printInit() {
		StringBuffer sb = new StringBuffer();		
		sb.append(type.print()).append(" ").append(this.getJavaClass().getCppName()).append("::").append(name);
		if(init != null) {
			sb.append(" = ");
			sb.append(init.print());
		} else {
			if(type instanceof JavaType.Primitive) {
				sb.append(" = ").append(type.getDefaultValue());
			}
		}
		return sb;
	}
	
	public static class FormalParameters extends JavaField {

		public FormalParameters(JavaScope scope, GNode n, JavaType type, boolean isFormal) {
			super(scope, n, type, isFormal);
			this.name = (String) n.get(3);
		}
		
	}
	
	public StringBuffer print(){
		StringBuffer sb = new StringBuffer();
		if(isStatic) sb.append("static ");
		if(init != null && init instanceof NewArrayExpression) {
			boolean isTwoD = ((NewArrayExpression) init).isTwoD();
			JavaType arrayType = ((NewArrayExpression) init).getArrayType();
			if(!isTwoD) {
				sb.append("__rt::Ptr<__rt::Array<").append(arrayType.getCppName()).append("> > __tmpArray = ").append(init.print()).append(";\n");
				sb.append(type.print()).append(" ").append(name).append(" = __tmpArray");
			} else {
				sb.append("__rt::Ptr<__rt::Array<__rt::Ptr<__rt::Array<").append(arrayType.getCppName()).append("> > > > ").append(name);
				sb.append(" = ").append(init.print()).append(";\n");
				sb.append("for(int32_t i = 0 ;i < ").append(name).append("->length;i++){\n(*");
				sb.append(name).append(")[i] = new __rt::Array<").append(arrayType.getCppName()).append(">(");
				sb.append(((NewArrayExpression) init).getSecondDim().print()).append(");\n}");
			}
		} else if(init != null){
			sb.append(type.print())
				.append(" ")
				.append(name);
				if(!isStatic)sb.append(" = ")//.append("(").append(this.type.getCppName()).append(")")
				.append(init.print());
		}else{
			sb.append(type.print());
				sb.append(" ")
				.append(name);
		}
		return sb;
	}
}
