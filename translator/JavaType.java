package translator;

import java.util.Hashtable;
import java.util.ArrayList;
import java.lang.StringBuffer;
import java.util.Set;

abstract public class JavaType {
	
	private static boolean isSetup = false;
	
	private static Hashtable<String, JavaType> types = new Hashtable<String, JavaType>();
	
	protected JavaType parent = null;
	
	protected boolean hasParent = false;
	
	public abstract String getName();
	
	public abstract JavaClass getJavaClass();
	
	public abstract String getDefaultValue();
	
	public abstract String getCppName();
	
	public abstract String getCppRawName();
	
	public abstract boolean hasParent();
	
	public abstract JavaType getParentType();
	
	public boolean isPrimitive() {
		return (this instanceof Primitive);
	}
	
	public static JavaType getType(String t) {
		return types.get(t);
	}
	
	public String getConstructorName() {
		return null;
	}
	
	public static String translateSymbolType(String s) {
		String type;
		String[] tmp = s.split(",");
		type = tmp[0].substring(6);
		
		return type;
	}
	
	public boolean hasChild(JavaType chd) {
		//System.out.println("Check hasChild: " + this.getName());
		
		JavaType curr = chd.getParentType();
		while(curr != null) {
			//System.out.println(curr.getName());
			if(curr == this) return true;
			curr = curr.getParentType();
		}
		return false;
	}
	
	public int getLevel() {
		int lv = 0;
		JavaType tmp = this.getParentType();
		
		while(tmp != null) {
			//System.out.println(tmp.getName());
			tmp = tmp.getParentType();
			
			lv++;
		}
		return lv;
	}
	
	public static int minDistance(ArrayList<JavaType> parameters, ArrayList<JavaType> arguments) {
		int minDis = -1;
		if(parameters.size() != arguments.size()) return -1;
		if(parameters.size() == 0) return 0;
		for(int i = 0; i < parameters.size(); i++) {
			if(parameters.get(i) == arguments.get(i)) {
				minDis = 0;
			} else {
				JavaType p = parameters.get(i);
				JavaType a = arguments.get(i);
				if(!p.hasChild(a)) {
					//System.out.println(p.getName() + " does not have child " + a.getName());
					return -1;
				} else {
					//System.out.println(p.getName() + " has child " + a.getName());
				}
				int dis = a.getLevel() - p.getLevel();
				if(minDis == -1) {
					minDis = dis;
				} else if(minDis > dis) minDis = dis;
			}
		}
		
		return minDis;
	} 
	
	public static int totalDistance(ArrayList<JavaType> parameters, ArrayList<JavaType> arguments) {
		int totalDis = 0;
		if(parameters.size() != arguments.size()) return -1;
		if(parameters.size() == 0) return 0;
		for(int i = 0; i < parameters.size(); i++) {
			if(parameters.get(i) == arguments.get(i)) {
			} else {
				JavaType p = parameters.get(i);
				JavaType a = arguments.get(i);
				if(!p.hasChild(a)) {
					//System.out.println(p.getName() + " does not have child " + a.getName());
					return -1;
				} else {
					//System.out.println(p.getName() + " has child " + a.getName());
				}
				int dis = a.getLevel() - p.getLevel();
				totalDis = totalDis + dis;
			}
		}
		
		return totalDis;
	} 
	
	public static void setup(JavaFile file) {
		if(isSetup) return;
		isSetup = true;
		Primitive dbl = new Primitive("double", "double", "0.0", null);
		Primitive flt = new Primitive("float", "float", "0.0", dbl);
		Primitive lng = new Primitive("long", "int64_t", "0", flt);
		Primitive it = new Primitive("int", "int32_t", "0", lng);
		Primitive chr = new Primitive("char", "char_t", "0", it);
		Primitive shrt = new Primitive("short", "int16_t", "0", it);
		Primitive byt = new Primitive("byte", "int8_t", "0", shrt);
		
		Primitive vd = new Primitive("void", "void", "NULL", null);
		Primitive bool = new Primitive("boolean", "bool", "false", null);
		
		Primitive str = new Primitive("String", "String", "__rt::literal(\"\")", null);
		
		types.put(dbl.getName(), dbl);
		types.put(flt.getName(), flt);
		types.put(lng.getName(), lng);
		types.put(it.getName(), it);
		types.put(chr.getName(), chr);
		types.put(shrt.getName(), shrt);
		types.put(byt.getName(), byt);
		types.put(vd.getName(), vd);
		types.put(bool.getName(), bool);
		types.put(str.getName(), str);
		
		if(file != null) {
			for(String key : file.getAllClass()) {
				JavaType.Object o = new JavaType.Object(file.getClass(key));
				types.put(o.getName(), o);
			}
		}
		JavaType.Object o = new JavaType.Object(JavaClass.getJavaLangObject());
		types.put(o.getName(), o);
		JavaType.Object c = new JavaType.Object(JavaClass.getJavaLangClass());
		types.put(c.getName(), c);
		
		ArrayList<JavaType> tmp = new ArrayList<JavaType>();
		
		for(JavaType jt : types.values()) {
			if(jt instanceof Object) {
				JavaClass cls = jt.getJavaClass();
				if(cls.getParent() != null) {
					jt.hasParent = true;
					//System.out.println(jt.getName() + " set parent class " + cls.getParent().getName());
					jt.parent = JavaType.getType(cls.getParent().getName());
				} else if(jt != JavaType.getType("Object")) {
					jt.hasParent = true;
					jt.parent = JavaType.getType("Object");
				}
			}
		}
		
		for(JavaType jt : types.values()) {
			Array oneD = new Array(jt, false);
			Array twoD = new Array(jt, true);
			tmp.add(oneD);
			tmp.add(twoD);
 		}
		
		for(JavaType jt : tmp) {
			types.put(jt.getName(), jt);
		}
	}
	
	public static void setupParent() {
		for(JavaType jt : types.values()) {
			if(jt instanceof Object) {
				JavaClass cls = jt.getJavaClass();
				if(cls.getParent() != null) {
					jt.hasParent = true;
					//System.out.println(jt.getName() + " set parent class " + cls.getParent().getName());
					jt.parent = JavaType.getType(cls.getParent().getName());
				} else if(jt != JavaType.getType("Object")) {
					jt.hasParent = true;
					jt.parent = JavaType.getType("Object");
				}
			}
		}
	}
	
	protected static class Primitive extends JavaType {
		
		private String typeName;
		
		private String cppTypeName;
		
		private String defaultValue;
		
		Primitive(String javaTypeName, String cppTypeName, String defaultValue, JavaType parent) {
			this.typeName = javaTypeName;
			this.cppTypeName = cppTypeName;
			this.defaultValue = defaultValue;
			if(parent != null) {
				this.hasParent = true;
				this.parent = parent;
			}
		}

		
		@Override
		public String getName() {
			return typeName;
		}

		@Override
		public JavaClass getJavaClass() {
			return null;
		}

		@Override
		public String getDefaultValue() {
			return defaultValue;
		}

		@Override
		public String getCppName() {
			return cppTypeName;
		}
		
		public String getCppRawName() {
			return cppTypeName;
		}


		@Override
		public boolean hasParent() {
			
			return this.hasParent;
		}


		@Override
		public JavaType getParentType() {
			return this.parent;
		}
		
	}
	
	protected static class Object extends JavaType {
		
		protected JavaClass cls;
		
		Object(JavaClass cls) {
			this.cls = cls;
		}

		@Override
		public String getName() {
			return cls.getName();
		}

		@Override
		public JavaClass getJavaClass() {
			return cls;
		}

		@Override
		public String getDefaultValue() {
			return "NULL";
		}

		@Override
		public String getCppName() {
			return cls.getName();
		}
		
		public String getCppRawName() {
			return cls.getCppName() + "*";
		}
		
		public String getConstructorName() {
			return cls.getCppName();
		}

		@Override
		public boolean hasParent() {
			
			return hasParent;
		}

		@Override
		public JavaType getParentType() {
			
			return parent;
		}
		
	}
	
	protected static class Array extends JavaType {
		
		JavaType jt;
		
		boolean isTwoD;
		
		Array(JavaType jt, boolean isTwoD) {
			this.jt = jt;
			this.isTwoD = isTwoD;
		}
		
		@Override
		public String getName() {
			String Name = jt.getName() + "[]";
			if(isTwoD) Name = Name + "[]";
			return Name;
		}

		@Override
		public JavaClass getJavaClass() {
			
			return null;
		}

		@Override
		public String getDefaultValue() {
			
			return null;
		}

		@Override
		public String getCppName() {
			if(!isTwoD) {
				return "__rt::Ptr<__rt::Array<" + jt.getCppName() + "> >";
			} else {
				return "__rt::Ptr<__rt::Array<__rt::Array<"+ jt.getCppName() + "> > >";
			}
		}

		@Override
		public String getCppRawName() {
			
			return null;
		}

		@Override
		public boolean hasParent() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public JavaType getParentType() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public StringBuffer print(){
		return new StringBuffer(this.getCppName());
	}
	
	//Test
	public static void printAllTypes() {
		Set<String> key = types.keySet();
		for(String k : key) {
			System.out.print("\"" + k + "\" " + "= " + getType(k).getDefaultValue() + "  ");
			System.out.println(getType(k).isPrimitive());
		}
	}
}
