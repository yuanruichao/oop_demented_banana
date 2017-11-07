package translator;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;
import java.lang.StringBuffer;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Printer;
import xtc.tree.Visitor;

public class JavaClass extends JavaScope {
	
	private GNode node = null;

	private String name;

	private String parent_name = null;

	private JavaClass parent = null;
	
	private JavaMethod.Constructor constructor = null;

	
	private ArrayList<JavaField> fieldTable = new ArrayList<JavaField>();;

	private ArrayList<JavaMethod> vtable = new ArrayList<JavaMethod>();
	
	private ArrayList<JavaClass> childClasses = new ArrayList<JavaClass>();
	
	private boolean hasSuper = false;
	
	private JavaExpression superArguments;
	
	//all virtual methods
	private Hashtable<String, ArrayList<JavaMethod> > methods = new Hashtable<String, ArrayList<JavaMethod> >();

	//all methods defined in this class
	private Hashtable<String, ArrayList<JavaMethod> > classMethods;
	
	//all static method
	private Hashtable<String, ArrayList<JavaMethod> > staticMethods = new Hashtable<String, ArrayList<JavaMethod> >();;
	
	private boolean activated = false;

	private boolean isSetup = false;
	
	private static JavaClass __Object = new JavaClass(null, "Object");;
	
	private static JavaClass __Class = new JavaClass(null, "Class");
	
	//create Java_Lang_*
	public static void setupJavaLang(JavaScope scope) {
		__Object.scope = scope;
		__Object.name = "Object";
		__Object.addMethod(JavaMethod.getObjectToString());
		__Object.addMethod(JavaMethod.hashCode);
		__Object.addMethod(JavaMethod.equals);
		__Object.addMethod(JavaMethod.getClass);
		
		
	}
	
	public static JavaClass getJavaLangObject() {
		return __Object;
	}
	
	public static JavaClass getJavaLangClass() {
		return __Class;
	}
	
	//only for java_Lang_Object and java_Lang_Class(maybe String and Array)
	JavaClass(JavaScope scope, String name){
		super(scope, null);
		this.name = name;
	}
	
	JavaClass(JavaScope scope, GNode n) {
		super(scope, n);
		node = n;
		this.name = this.node.get(1).toString();
		
	}
	
	//add a child class, we may use this when we check a object's type in call expression and return expression(idk)
	public void addChild(JavaClass chd) {
		childClasses.add(chd);
	}

	public void process() {
		this.dispatch(node);
		if(this.activated) return;

		this.activated = true;
		this.setParent(parent_name);
		
		this.parent.addChild(this);
	}

	public String getName() {
		return this.name;
	}
	
	public String getCppName() {
		return "__" + this.name;
	}
	
	public String getRawCppName() {
		return "__" + this.name;
	}
	
	public String getVTName() {
		return "__" + this.name + "_VT";
	}

	//set parent
	private void setParent(String parent) {
		if(this.parent == null) {
			if(parent != null) {
				this.parent = this.getJavaFile().getClass(parent);
			} else {
				this.parent = __Object;
			}
		}
	}

	public JavaClass getParent() {
		return this.parent;
	}

	

	//add method
	private void addMethod(JavaMethod m) {
		String name = m.getName();
		if(this.methods.get(name) == null) this.methods.put(name, new ArrayList<JavaMethod>());
		ArrayList<JavaMethod> methodList = this.methods.get(name);
		methodList.add(m);
	}

	
	//get method 
	public JavaMethod getMethod(JavaMethod m) {
		if(this.methods.get(m.getName()) == null) return null;
		ArrayList<JavaMethod> methodList = this.methods.get(m.getName());
		for(JavaMethod jm : methodList) {
			if(jm.sameSig(m)) return jm;
		}
		
		return null;
	}
	
	
	public JavaMethod getMethod(String name, ArrayList<String> typeList) {
		int pNum = typeList.size();
		ArrayList<JavaType> argumentsType = new ArrayList<JavaType>();
		for(String s : typeList) {
			argumentsType.add(JavaType.getType(s));
		}
		
		ArrayList<JavaMethod> methodList = methods.get(name);
		ArrayList<JavaMethod> possibleList = new ArrayList<JavaMethod>();
		
		int minDis = -1;
		for(JavaMethod jm : methodList) {
			ArrayList<JavaType> p = jm.getParameterTypeList();
			int dis = JavaType.minDistance(p, argumentsType);
			//System.out.println("dis: " + dis);
			if(dis >= 0) {
				if(minDis == -1) minDis = dis;
				else if(minDis > dis) minDis = dis;
			}
		}
		for(JavaMethod jm : methodList) {
			ArrayList<JavaType> p = jm.getParameterTypeList();
			int dis = JavaType.minDistance(p, argumentsType);
			if(dis == minDis) {
				possibleList.add(jm);
			}
		}
		
		//System.out.println("minDis: " + minDis);
		
		if(possibleList.size() == 1) {
			return possibleList.get(0);
		}
		
		
		JavaMethod m = possibleList.get(0);
		int minTotalDis = JavaType.totalDistance(m.getParameterTypeList(), argumentsType);
		for(JavaMethod jm : possibleList) {
			int totalDis = JavaType.totalDistance(jm.getParameterTypeList(), argumentsType);
			if(totalDis < minTotalDis) m = jm;
		}
		
		return m;
	}
	
	public ArrayList<JavaMethod> getAllMethod() {
		ArrayList<JavaMethod> allMethods = new ArrayList<JavaMethod>();
		for(ArrayList<JavaMethod> list : methods.values()) {
			for(JavaMethod jm : list) {
				allMethods.add(jm);
			}
		}
		return allMethods;
	}
	
	//
	private void addClassMethod(JavaMethod m) {
		String name = m.getName();
		if(this.classMethods.get(name) == null) this.classMethods.put(name, new ArrayList<JavaMethod>());
		this.classMethods.get(name).add(m);
	}

	//get class method
	public JavaMethod getClassMethod(JavaMethod m) {
		if(this.classMethods.get(m.getName()) == null) return null;
		ArrayList<JavaMethod> methodList = this.classMethods.get(m.getName());
		for(JavaMethod jm : methodList) {
			if(jm.sameSig(m)) return jm;
		}
		
		return null;
	}

	public JavaMethod getClassMethod(String name) {
		if(this.classMethods.get(name) == null) return null;
		return this.classMethods.get(name).get(0);
	}
	
	public ArrayList<JavaMethod> getAllClassMethod() {
		ArrayList<JavaMethod> allMethods = new ArrayList<JavaMethod>();
		for(ArrayList<JavaMethod> list : classMethods.values()) {
			for(JavaMethod jm : list) {
				allMethods.add(jm);
			}
		}
		return allMethods;
	}
	
	//
	private void addStaticMethod(JavaMethod m) {
		String name = m.getName();
		if(this.staticMethods.get(name) == null) this.staticMethods.put(name, new ArrayList<JavaMethod>());
		ArrayList<JavaMethod> methodList = this.staticMethods.get(name);
		methodList.add(m);
	}
	
	public JavaMethod getStaticMethod(JavaMethod m) {
		if(this.staticMethods.get(m.getName()) == null) return null;
		ArrayList<JavaMethod> methodList = this.staticMethods.get(m.getName());
		for(JavaMethod jm : methodList) {
			if(jm.sameSig(m)) return jm;
		}
		
		return null;
	}

	public JavaMethod getStaticMethod(String name, ArrayList<String> typeList) {
		int pNum = typeList.size();
		ArrayList<JavaType> argumentsType = new ArrayList<JavaType>();
		for(String s : typeList) {
			argumentsType.add(JavaType.getType(s));
		}
		
		ArrayList<JavaMethod> methodList = staticMethods.get(name);
		ArrayList<JavaMethod> possibleList = new ArrayList<JavaMethod>();
		
		int minDis = -1;
		for(JavaMethod jm : methodList) {
			ArrayList<JavaType> p = jm.getParameterTypeList();
			int dis = JavaType.minDistance(p, argumentsType);
			//System.out.println("dis: " + dis);
			if(dis >= 0) {
				if(minDis == -1) minDis = dis;
				else if(minDis > dis) minDis = dis;
			}
		}
		for(JavaMethod jm : methodList) {
			ArrayList<JavaType> p = jm.getParameterTypeList();
			int dis = JavaType.minDistance(p, argumentsType);
			if(dis == minDis) {
				possibleList.add(jm);
			}
		}
		
		//System.out.println("minDis: " + minDis);
		
		if(possibleList.size() == 1) {
			return possibleList.get(0);
		}
		
		
		JavaMethod m = possibleList.get(0);
		int minTotalDis = JavaType.totalDistance(m.getParameterTypeList(), argumentsType);
		for(JavaMethod jm : possibleList) {
			int totalDis = JavaType.totalDistance(jm.getParameterTypeList(), argumentsType);
			if(totalDis < minTotalDis) m = jm;
		}
		
		return m;
	}
	
	public ArrayList<JavaMethod> getAllStaticMethod() {
		ArrayList<JavaMethod> allMethods = new ArrayList<JavaMethod>();
		for(ArrayList<JavaMethod> list : staticMethods.values()) {
			for(JavaMethod jm : list) {
				allMethods.add(jm);
			}
		}
		return allMethods;
	}

	
	//type
	public JavaType getType() {
		return JavaType.getType(this.getName());
	}

	public ArrayList<JavaField> getAllField() {
		return this.fieldTable;
	}

	
	public void addFieldTable(JavaField fld) {
		this.fieldTable.add(fld);
	}
	
	public boolean hasField(String s) {
		for(JavaField jf : this.fieldTable) {
			if(jf.getName().equals(s)) return true;
		}
		return false;
	}
	
	public void setSuperTrue() {
		hasSuper = true;
	}

	public boolean hasSuper() {
		return hasSuper;
	}
	
	public void setSuperArguments(JavaExpression Arguments) {
		superArguments = Arguments;
	} 
	
	//setup VT
	public void setup() {
		if(this.isSetup) return;
		this.isSetup = true;
		
		//this.classMethods = this.methods;
		
		this.classMethods = new Hashtable<String, ArrayList<JavaMethod> >();
		
		for(JavaMethod m : getAllMethod()) {
			//System.out.println(name + " add " + m.getName());
			this.addClassMethod(m);
		}
		
		this.methods.clear();
		
		if (this.parent != null) {
			//System.out.println(this.getName());
			for (JavaMethod m : this.parent.vtable) {
				
				//System.out.println(m.getName());
				JavaMethod local = this.getClassMethod(m);
				//System.out.println(local == null);
				
				if(local != null && local.sameSig(m)) {
					//System.out.println("same");
					this.addMethod(local);
					
					this.vtable.add(local);
					
					continue;
				}
				
				this.addMethod(m);
				
				this.vtable.add(m);
			}
			
		}
		
		for (JavaMethod jm : getAllClassMethod()) {
			
			//System.out.println("     " + jm.getName() + "    " + this.getName());
			
			if(jm.name.equals("main")) {
				this.getJavaFile().setMain(this);
			}
			
			if(jm.isStatic) {
				this.addStaticMethod(jm); 
				continue;
			}
			
			if (this.vtable.contains(jm)) continue;
			
			this.addMethod(jm);
			
			this.vtable.add(jm);
		}
		//System.out.println("!!" + this.getName());
		
		if(this != JavaClass.getJavaLangObject()) {
			for(JavaField fld : this.parent.getAllField()) {
				//System.out.println("add " + fld.getName() + " from " + this.parent.getName() + " to " + this.name);
				this.addFieldTable(fld);
				//System.out.println(fld.getName());
			}
		}
		
		for(JavaField fld : this.fields.values()) {
			//System.out.println("add " + fld.getName() + " to " + this.name);
			this.addFieldTable(fld);
			//System.out.println("?" + fld.getName());

		}
		
		
	
		
		
		for(JavaClass chd : this.childClasses) {
			chd.setup();
		}
	}



	//visit
	public void visitExtension(GNode n) {
		parent_name = (String) ((Node) ((Node) n.get(0)).get(0)).get(0);
		this.dispatch((Node)((GNode)n.get(0)).get(0));
	}

	public void visitMethodDeclaration(GNode n) {
		//System.out.println("!!!!Entered scope " + this.getSymbolTable().current().getName());
		this.getSymbolTable().enter(n);
		//System.out.println("Entered scope " + this.getSymbolTable().current().getName());
		
		
		JavaMethod jm = new JavaMethod(this, n);
		jm.setSymbolScope(jm.getSymbolTable().current());
		jm.setup();
		//System.out.println(this.getName() + " add " + jm.getName());
		this.addMethod(jm);
		
		
		this.getSymbolTable().exit();
	}

	public JavaExpression visitModifiers(GNode n) {
		return null;
	}

	public void visitConstructorDeclaration(GNode n) {
		JavaMethod.Constructor cst = new JavaMethod.Constructor(this, n);
		cst.setup();
		this.constructor = cst;
	}
	
	
	//print
	
	public StringBuffer printConstructorCall() {
		StringBuffer sb = new StringBuffer();
		if(this.parent != null) sb.append(parent.printConstructorCall());
		if(this != JavaClass.getJavaLangObject()) {
			if(this.constructor != null) {
				sb.append(this.getCppName()).append("::");
				sb.append(this.getConstructorCppName()).append("((")
					.append(this.getCppName()).append("*)this");
				for(JavaField jf :constructor.getParameterList()) {
					sb.append(", ").append(jf.getName());
				}
				sb.append(");\n");
			}
		}
		return sb;
	}
	
	public String getConstructorCppName() {
		return "__CONSTRUCTOR__" + this.name;
	}
	
	public StringBuffer printConstructorCpp() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("void ").append(this.getCppName()).append("::")
			.append(this.getConstructorCppName()).append("(")
			.append(this.getCppName()).append("* __this");
		
		for(JavaField jfd : this.constructor.parameterList) {
			sb.append(", ").append(jfd.getType().getCppName()).append(" ").append(jfd.getName());
		}
		
		sb.append(")").append(this.constructor.block.print()).append("\n");
		return sb;
	}
	
	public StringBuffer printConstructorHeader() {
		StringBuffer sb = new StringBuffer();
		//TODO
		sb.append(this.getCppName()).append("(");
		
		boolean first = true;
		if(constructor != null){
		for(JavaField jf : constructor.getParameterList()) {
			if(!first) sb.append(", ");
			first = false;
			sb.append(jf.getType().getCppName()).append(" ").append(jf.getName());
		}
		}
			
		sb.append(") :\n").append("__vptr(&__vtable)");
		sb.append(",\nclass_name(\"").append(this.getName()).append("\")");
		sb.append(" {\n");
		
		if(this.parent != null) {
			sb.append(this.parent.printConstructorCall());
		}
		
		if(constructor != null) {
			
			sb.append(this.getConstructorCppName()).append("(this");
			for(JavaField jf :constructor.getParameterList()) {
				sb.append(", ").append(jf.getName());
			}
			sb.append(");\n");
		}
		
		sb.append("};\n");
		
		return sb;
	}
	
	
	public StringBuffer printHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append("struct ").append(this.getCppName()).append("{\n");
		
		sb.append(this.getVTName()).append("* __vptr;\n\n");
		
		//sb.append("std::string class_name;\n");
		
		for(JavaField fld : this.fieldTable) {
			sb.append(fld.print()).append(";\n");
		}
		sb.append("\n\n");
		
		sb.append(this.getCppName()).append("();\n");
		
		//sb.append(this.printConstructorHeader());
		sb.append("\n");
		
		/*
		if(constructor != null) {
			sb.append("static void ").append(this.getConstructorCppName());
			sb.append("(").append(this.getName());
			for(JavaType jt :constructor.getParameterTypeList()) {
				sb.append(", ").append(jt.getCppName());
			}
			sb.append(");\n");
		}
		*/
		
		sb.append("static ").append(this.getName()).append(" init(");
		sb.append(this.getName());
		if(constructor != null) {
			for(JavaType jt :constructor.getParameterTypeList()) {
				sb.append(", ").append(jt.getCppName());
			}
		}
		sb.append(");\n");
		
		for(JavaMethod m : getAllClassMethod()) {

			if(((JavaClass)m.getParentScope()).getName().equals(this.getName())){
				sb.append(m.printMethodHeader(this)).append(";\n");
			}
		}
		
		sb.append("\nstatic Class __class();\n\n");
		
		sb.append("static ").append(this.getVTName()).append(" __vtable;\n");
		
		sb.append("};\n\n");
		
		sb.append("struct ").append(this.getVTName()).append("{\n");
		
		sb.append("Class __isa;\n");
		
		sb.append("void (*__delete)(").append(JavaType.getType(this.getName()).getCppRawName()).append(");\n");
		
		
		
		sb.append(JavaMethod.hashCode.printFunctionPointer(this));
		sb.append(JavaMethod.equals.printFunctionPointer(this));
		sb.append(JavaMethod.getClass.printFunctionPointer(this));
		sb.append(JavaMethod.toString.printFunctionPointer(this));
		
		for(JavaMethod m : this.vtable) {
			
			if(!m.sameSig(JavaMethod.hashCode) && !m.sameSig(JavaMethod.equals) && !m.sameSig(JavaMethod.getClass) && !m.sameSig(JavaMethod.toString))
				sb.append(m.printFunctionPointer(this));
			
		}
		sb.append("\n");
		
		sb.append(this.getVTName()).append("() : \n");
		
		sb.append("__isa(").append(this.getRawCppName()).append("::__class()),\n__delete(&__rt::__delete<")
			.append(this.getRawCppName()).append(">)");
		
		for(JavaMethod m : this.vtable) {
			//System.out.println(m.printMethodName());
			sb.append(",\n");
			sb.append(m.printMethodName()).append("(");
			sb.append(m.printFunctionPointerCasting(this));
			sb.append("&")
				.append(((JavaClass)m.getParentScope()).getCppName())
				.append("::").append(m.printMethodName()).append(")");
		}
		
		sb.append("{};\n");
		
		sb.append("};\n\n");
		return sb;
	}
	
	public StringBuffer printCpp() {
		StringBuffer sb = new StringBuffer();
		
		for(JavaField jfd : this.getAllField()) {
			if(jfd == null) continue;
			if(jfd.isStatic()) {
				sb.append(jfd.printInit()).append(";\n");
			}
		}
		
		sb.append("\n");
		sb.append(this.getCppName()).append("::").append(this.getCppName()).append("() : __vptr(&__vtable) {\n}\n\n");
		
		sb.append(this.printInit());
		
		sb.append(this.getVTName()).append(" ")
		.append(this.getCppName()).append("::__vtable;\n\n");
		
		sb.append(this.printClassInit());
		
		for(JavaMethod m : getAllClassMethod()) {
			//System.out.println(m.getName() + "????????" + ((JavaClass)(m.getParentScope())).getName());
			this.getSymbolTable().enter(m.getThisNode());
			//System.out.println("(Print)Entered scope " + this.getSymbolTable().current().getName());
			
			sb.append(m.printImplementation());
			
			
			this.getSymbolTable().exit();
		}
		
		/*
		if(this.constructor != null) {
			sb.append(this.printConstructorCpp());
		}
		*/
		
		sb.append("namespace __rt {\ntemplate<>\njava::lang::Class Array<").append(this.getName());
		sb.append(">::__class() {\nstatic java::lang::Class k =\nnew java::lang::__Class(literal(\"[L").append(this.getName());
		sb.append(";\"),\nArray<java::lang::Object>::__class(),\n").append(this.getCppName()).append("::__class());\nreturn k;\n}\n}\n\n");
		
		sb.append("namespace __rt {\ntemplate<>\njava::lang::Class Array<Ptr<Array<").append(this.getName());
		sb.append("> > >::__class() {\nstatic java::lang::Class k =\nnew java::lang::__Class(literal(\"[[L").append(this.getName());
		sb.append(";\"),\nArray<java::lang::Object>::__class(),\n").append(this.getCppName()).append("::__class());\nreturn k;\n}\n}\n\n");
		
		
		return sb;
	}
	
	public StringBuffer printClassInit() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("Class ").append(this.getCppName()).append("::__class() {\n");
		sb.append("static Class __k = \n").append("new __Class(__rt::literal(\"");
		sb.append(this.getName());
		sb.append("\"), ").append(this.getParent().getCppName()).append("::__class());\nreturn __k;\n");
		sb.append("}\n\n");
		return sb;
	}
	
	public StringBuffer printInit() {
		StringBuffer sb = new StringBuffer();
		
		
		
		sb.append(this.getName()).append(" ").append(this.getCppName()).append("::init(");
		sb.append(this.getName()).append(" __this");
		if(this.constructor != null) {
			for(JavaField jfd : this.constructor.getParameterList()) {
				sb.append(", ").append(jfd.getType().getCppName()).append(" ").append(jfd.getName());
			}
		}
		sb.append(") {\n");
		
		Stack<JavaClass> stack = new Stack<JavaClass>();
		JavaClass cls = this.getParent();
		while(cls != null) {
			stack.push(cls);
			cls = cls.getParent();
		}
		
		if(this.constructor != null) {
			//this.getSymbolTable().enter(this.getThisNode());
			//this.getSymbolTable().enter(this.constructor.getThisNode());
			
			//System.out.println(this.constructor.getThisNode().toString());
			
			constructor.printInitBody();
		}
		
		while(!stack.empty()) {
			cls = stack.pop();
			if(stack.empty()) {
				if(this.hasSuper) {
					sb.append(cls.getCppName()).append("::init(__this");
					
					if(superArguments!=null) {
						if(!superArguments.print().toString().equals("")) {
							sb.append(", ");
						}
						PrimaryIdentifier.setThis(true);
						sb.append(superArguments.print());
						PrimaryIdentifier.setThis(false);
					}
					sb.append(")");
					
					sb.append(";\n");
					break;
				}
			}
			sb.append(cls.getCppName()).append("::init(__this);\n");
		}
		
		if(this.constructor != null) {
			sb.append(constructor.printInitBody());
			//this.getSymbolTable().exit();
		}
		
		sb.append("return __this;\n}\n\n");
		return sb;
	}
	
	
	//test
	/*
	public void printAllMethod() {
		Set<String> methodNames = methods.keySet();
		for(String key: methodNames) {
			JavaMethod jm = methods.get(key);
			System.out.println(jm.getName());
			jm.printAllParameter();
			jm.printParaTypeList();
			System.out.println("return " + jm.getReturnType().getName());
		}

	}
	*/
	
	public void printThisField() {
		Set<String> key = this.fields.keySet();
		for(String k : key) {
			JavaField fld = this.fields.get(k);
			//System.out.println("class: " + this.name + " fc: " + fld.getType().getName() + " " + fld.getName());
		}
	}

	public void printVTable() {
		//System.out.println(this.getName());
		for (JavaMethod m : this.vtable) {
			//System.out.println(this.getName() + ": " + m.getName() + " in " + ((JavaClass) m.getParentScope()).getName());
		}
	}
	
	public void printAllField() {
		//System.out.println(this.getName());
		for(JavaField fld : fieldTable) {
			//System.out.println("field of " + this.getName() + ": "+ fld.getType().getName() + " " + fld.getName());
		}
	}
}