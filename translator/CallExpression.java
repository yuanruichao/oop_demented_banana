package translator;
import java.util.ArrayList;

import xtc.tree.GNode;
import xtc.tree.Node;

public class CallExpression extends JavaExpression{
    private String methodName; // methodNames
    JavaExpression operand1;	// caller
    JavaExpression operand2; // arguments
    JavaMethod jm;
	
    public CallExpression(JavaScope scope, GNode n) {
		super(scope, n);
		methodName = (String) n.get(2);
		operand1 = (JavaExpression)this.dispatch((Node) n.get(0));
		operand2 = (JavaExpression)this.dispatch((Node) n.get(3));
	}
    
	
	public StringBuffer print() {
		/*
		System.out.print("args: ");
		for(String s : ((Arguments) operand2).getTypeNameList()) {
			System.out.print(s + " // " + s + " ///");
		}
		*/
		//System.out.println("end");
		
		StringBuffer sb = new StringBuffer();
		boolean isStatic = false;
		
		if(methodName.equals("super")) {
			this.getJavaClass().setSuperTrue();
			this.getJavaClass().setSuperArguments(operand2);
			return sb;
		}
		
		if(methodName.equals("println") || methodName.equals("print")){
			sb.append("std::cout << ")
			.append(operand2.print());
			
			if(methodName.equals("println")) sb.append("<< std::endl");
		}else{
			if(operand1 != null){
				String callerName = operand1.print().toString();
				
				JavaClass thisCls;
				if(this.getJavaFile().getClass(callerName) == null) {
					thisCls = this.getJavaFile().getClass(operand1.getTypeName());
					if(operand1.getTypeName().equals("Class")) thisCls = this.getJavaFile().getClass("Object");
					//System.out.println("TypeCheck: " + methodName + " " + operand1.getTypeName());
					sb.append(operand1.print()).append("->__vptr->");
					jm = thisCls.getMethod(methodName, ((Arguments) operand2).getTypeNameList());
				} else {
					thisCls = this.getJavaFile().getClass(callerName);
					sb.append(this.getJavaFile().getClass(callerName).getCppName()).append("::");
					isStatic = true;
					jm = thisCls.getStaticMethod(methodName, ((Arguments) operand2).getTypeNameList());
				}
				
				//System.out.println("thisCls = " + thisCls.getName());


			sb.append(jm.printMethodName());
			sb.append("(");
			if(!isStatic)sb.append(operand1.print());
			}else {
				if(this.getJavaClass().getStaticMethod(this.methodName, ((Arguments) operand2).getTypeNameList()) != null) isStatic = true;
				if(isStatic) {
					jm = this.getJavaClass().getStaticMethod(this.methodName, ((Arguments) operand2).getTypeNameList());
					sb.append(this.getJavaClass().getCppName()).append("::");
					sb.append(jm.printMethodName());				
					sb.append("(");
				} else {
					jm = this.getJavaClass().getMethod(this.methodName, ((Arguments) operand2).getTypeNameList());
					sb.append("(__this)->__vptr->");
					sb.append(jm.printMethodName());				
					sb.append("(");
					sb.append("__this");
				}
			}
			
			
			if(operand2!=null) {
				if(!operand2.print().toString().equals("")) {
					if(!isStatic) sb.append(", ");
				}
				sb.append(operand2.print());
			}
			sb.append(")");
		}
		return sb;
	}

	public String getTypeName() {
		print();
		return jm.getReturnType().getName();
	}
}
