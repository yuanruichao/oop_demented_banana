package translator;

import java.util.Hashtable;
import java.util.ArrayList;
import java.lang.StringBuffer;
import java.util.Set;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Printer;
import xtc.tree.Visitor;

public class JavaMethod extends JavaScope {
	
	protected GNode node = null;
	
	protected String name;
	
	ArrayList<JavaType> parameterTypeList = new ArrayList<JavaType>();
	
	ArrayList<JavaField> parameterList = new ArrayList<JavaField>();
	
	protected JavaBlock block;
	
	static JavaMethod toString, hashCode, equals, getClass;

	protected JavaType returnType;
	
	protected boolean isPrivate = false;
	
	protected boolean isStatic = false;
	
	public static void staticSetup() {
		toString = new JavaMethod((JavaScope)JavaClass.getJavaLangObject(), null);
		toString.name = "toString";
		toString.returnType = JavaType.getType("String");
		hashCode = new JavaMethod((JavaScope)JavaClass.getJavaLangObject(), null);
		hashCode.name = "hashCode";
		hashCode.returnType = JavaType.getType("int");
		equals = new JavaMethod((JavaScope)JavaClass.getJavaLangObject(), null);
		equals.name = "equals";
		equals.returnType = JavaType.getType("boolean");
		equals.parameterTypeList.add(JavaType.getType("Object"));
		getClass = new JavaMethod((JavaScope)JavaClass.getJavaLangObject(), null);
		getClass.name = "getClass";
		getClass.returnType = JavaType.getType("Class");
	}
	
	public static JavaMethod getObjectToString() {
		return JavaMethod.toString;
	}
	
	JavaMethod(JavaScope scope, GNode n) {
		super(scope, n);
		node = n;
	}
	
	public String getName() {
		return this.name;
	}
	
	public JavaField getParameter(int n) {
		int count = 0;
		for(JavaField fld : this.parameterList) {
			if(count == n) return fld;
			count++;
		}
		return null;
	}
	
	public JavaType getReturnType() {
		return this.returnType;
	}
	
	public int getNumParameter() {
		return parameterTypeList.size();
	}
	
	public JavaType getParameterType(int i) {
		return parameterTypeList.get(i);
	}
	
	public ArrayList<JavaType> getParameterTypeList(){
		return this.parameterTypeList;
	}
	
	public ArrayList<JavaField> getParameterList(){
		return this.parameterList;
	}
	
	public void setup() {
		Node modifiers = (Node) this.node.get(0);
		
		for(Object o : modifiers) {
			if(o instanceof Node) {
				if(((Node) o).get(0) != null) {
					if(((Node) o).get(0).toString().equals("private")) {
						isPrivate = true;
					}
					
					if(((Node) o).get(0).toString().equals("static")) {
						isStatic = true;
					}
				}
			}
		}
		
		this.name = this.node.get(3).toString();
		
		GNode First = this.node;
		
		this.node = (GNode)this.node.get(7);
		
		First.set(7, null);
		
		this.dispatch(First);
		
		this.block = new JavaBlock(this, this.node);
		
		this.getSymbolTable().enter(this.node);
		//System.out.println("Entered scope " + this.getSymbolTable().current().getName());
		
		this.getSymbolTable().exit();
		//this.block.dispatch(this.node);
		
		//System.out.println(this.getName() + " private: " + isPrivate + " static: " + isStatic);
		
	}
	
	public void visitType(GNode n) {
		this.returnType = JavaType.getType(((GNode)n.get(0)).get(0).toString());
	}
	
	public void visitVoidType(GNode n) {
		this.returnType = JavaType.getType("void");
	}
	
	public void visitFormalParameter(GNode n) {
		FieldDec fd = new FieldDec.FormalParameters(this, n);
		parameterList.add(fd.getField());
		parameterTypeList.add(fd.getField().getType());
	}
	
	public boolean sameSig(JavaMethod jm) {
		if(jm.isPrivate != this.isPrivate) return false;
		if(jm.isStatic != this.isStatic) return false;
		if(!jm.getName().equals(this.getName())) return false;
		if(this.getNumParameter() != jm.getNumParameter()) return false;
		for(int i = 0; i < this.getNumParameter(); i++) {
			if(this.getParameterType(i) != jm.getParameterType(i)) return false;
		}
		return true;
	}
	
	//print
	public StringBuffer printMethodName() {
		StringBuffer sb = new StringBuffer();
		if(sameSig(JavaMethod.hashCode) || sameSig(JavaMethod.equals) || sameSig(JavaMethod.getClass) || sameSig(JavaMethod.toString) || name.equals("main")) {return sb.append(this.name);}
		sb.append(this.name).append("_impl");
		for(JavaType jt : parameterTypeList) {
			if(jt != null) {
				sb.append("_").append(jt.getName());
			}
		}
		return sb;
	}
	
	
	public StringBuffer printMethodHeader(JavaClass cls) {
		StringBuffer sb = new StringBuffer();
			sb.append("static ").append(this.returnType.getCppName() + " ").append(this.printMethodName());
			if(!this.name.equals("main")){
				if(this.isStatic) {sb.append("(");}
				else sb.append("(").append(JavaType.getType(cls.getName()).getCppName());
				boolean first = true;
				for(JavaType jt : this.parameterTypeList) {
					if(!first || !this.isStatic) sb.append(", ");
					first = false;
					sb.append(jt.getCppName());
				}
				sb.append(")");
			} else {
				sb.append("(__rt::Ptr<__rt::Array<String> > args)");
			}
		return sb;
	}
	
	public StringBuffer printMethodCpp() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.returnType.getCppName() + " ")
			.append(((JavaClass)this.getParentScope())
			.getCppName()).append("::").append(this.printMethodName());		
		if(!this.name.equals("main")){
			if(this.isStatic) {sb.append("(");}
			else sb.append("(").append(this.getJavaClass().getName()).append(" __this");
			boolean first = true;
			for(JavaField jfd : this.parameterList) {
				if(!first || !this.isStatic) sb.append(", ");
				first = false;
				sb.append(jfd.getType().getCppName()).append(" ").append(jfd.getName());
			}
			sb.append(")");
		} else {
			sb.append("(__rt::Ptr<__rt::Array<String> > args)");
		}
		
		return sb;
	}
	
	public StringBuffer printImplementation() {
		StringBuffer sb = new StringBuffer();
		//System.out.println(this.getName());
		//System.out.println(this.printMethodCpp().toString());
		//System.out.println(this.block.print().toString());
		
		this.getSymbolTable().enter(this.block.getThisNode());
		//System.out.println("(Print)Entered scope " + this.getSymbolTable().current().getName());
		
		sb.append(this.printMethodCpp()).append(this.block.print()).append("\n");
		
		this.getSymbolTable().exit();
		
		return sb;
	}
	
	public StringBuffer printFunctionPointer(JavaClass cls) {
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.returnType.getCppName() + " ").append("(*").append(this.printMethodName()).append(")");
		if(!this.name.equals("main")) {
			sb.append("(").append(JavaType.getType(cls.getName()).getCppName());
			for(JavaType jt : this.parameterTypeList) {
				sb.append(", ").append(jt.getCppName());
			}
			sb.append(");\n");
		} else {
			sb.append("();\n");
		}
		
		return sb;
	}
	
	public StringBuffer printFunctionPointerCasting(JavaClass cls) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("(").append(this.getReturnType().getCppName()).append("(*)");
		
		sb.append("(").append(JavaType.getType(cls.getName()).getCppName());
		for(JavaType jt : this.parameterTypeList) {
			sb.append(", ").append(jt.getCppName());
		}
		sb.append(")");
		
		
		sb.append(")");
		
		return sb;
	}
	
	
	public static class Constructor extends JavaMethod {

		Constructor(JavaScope scope, GNode n) {
			super(scope, n);
			// TODO Auto-generated constructor stub
		}
		
		public void setup() {
			this.name = this.node.get(2).toString();
			
			GNode First = this.node;
			
			this.node = (GNode)this.node.get(5);
			
			First.set(5, null);
			
			this.dispatch(First);

			this.block = new JavaBlock(this, this.node);
			
			this.getSymbolTable().enter(this.node);
			//System.out.println("Entered scope " + this.getSymbolTable().current().getName());
			this.block.setSymbolScope(this.getSymbolTable().current());
			
			this.getSymbolTable().exit();
			//System.out.println(this.printMethodHeader().toString());
		
			//System.out.println(this.block.print().toString());
			
		}
		
	}
	
	
	public StringBuffer printInitBody() {
		this.getSymbolTable().enter(this.node);
		
		StringBuffer sb =  block.printInit();
		this.getSymbolTable().exit();
		return sb;
	}
	
	
	//test
	public void printAllParameter() {
		Set<String> key = this.fields.keySet();
		for(String k : key) {
			JavaField fld = this.fields.get(k);
			//System.out.println("method: " + this.name + " fp: " + fld.getType().getName() + " " + fld.getName());
		}
	}
	
	public void printParaTypeList() {
		System.out.print("types: ");
		for(JavaType jt : parameterTypeList) {
			System.out.print(jt.getName() +  " ");
		} 
		System.out.println();
	}
}
