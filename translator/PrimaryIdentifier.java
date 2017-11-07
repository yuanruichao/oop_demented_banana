package translator;

import xtc.lang.JavaEntities;
import xtc.tree.GNode;
import xtc.type.Type;
import xtc.util.SymbolTable;

public class PrimaryIdentifier extends JavaExpression {
	String identifier;
	static boolean DONTPRINTTHIS = false;
	
	public static void setThis(boolean b) {
		DONTPRINTTHIS = b;
	}
	
	public PrimaryIdentifier(JavaScope scope , GNode n){
			super(scope,n);
			identifier = (String)n.get(0); 
	}	
	
	public StringBuffer print(){
		
		//System.out.println("PrimaryIdentifier:::::" + identifier + ": " + this.getTypeName());
		
		if(DONTPRINTTHIS) return new StringBuffer(identifier);
		
		SymbolTable table = this.getSymbolTable();
		if (table.current().isDefined(identifier)) {
		      Type type = (Type) table.current().lookup(identifier);
		      if (JavaEntities.isLocalT(type)){
		        //System.out.print("Found occurrence of local variable: ");
		        
		        Type val = (Type)(table.current().lookupScope(identifier).lookupLocally(identifier));
		        if(val.hasAlias()) {
		        	//System.out.print(val.toAlias().getName() + " ");
		        }else {
		        	//System.out.print(JavaType.translateSymbolType(val.toString()) + " ");
		        }
		        
		      	//System.out.println(identifier);
		      }
		      else {
		    	  //System.out.print("not found local ");System.out.println(identifier);
		    	  //System.out.println("{");
		    	 // System.out.println(table.current().lookupScope(identifier).getName());
		    	  Type val = (Type)(table.current().lookupScope(identifier).lookupLocally(identifier));
		    	  if(val.hasAlias()) {
			        	//System.out.println(val.toAlias().getName() + " ");
			        }else {
			        	//System.out.println(JavaType.translateSymbolType(val.toString()) + " ");
			        }
		    	  //System.out.println("}");
		      }
		    } //else System.out.print("not defined ");System.out.println(identifier);
		  
		    
		    StringBuffer sb = new StringBuffer();
		
		    if (table.current().isDefined(identifier)) {
				Type type = (Type) table.current().lookup(identifier);
				if (!JavaEntities.isLocalT(type)) {

					SymbolTable.Scope Scope = table.current().lookupScope(
							identifier);
					
					boolean flag = false;
					JavaClass cls = this.getJavaClass();
					while(true) {
						if(cls == null) break;
						if(Scope == cls.getSymbolScope()) flag = true;
						cls = cls.getParent();
					}
					cls = this.getJavaClass();
					if(flag) {
						flag = false;
						for(JavaField jfd : cls.getAllField()) {
							if(jfd == null) continue;
							if(jfd.getName().equals(identifier)) flag = true;
						}
					}
					
					if (flag) {
						
						//System.out.println("!!!! " + identifier + ": " + Scope.getName());
						
						boolean isStatic = false;
						
						cls = this.getJavaClass();
						for(JavaField jfd : cls.getAllField()) {
							if(jfd == null) continue;
							if(jfd.getName().equals(identifier) && jfd.isStatic()) isStatic = true;
						}
						if(!isStatic)sb.append("__this->");
							else sb.append(cls.getCppName()).append("::");

					}

				}
			} else {//not defined
				JavaClass cls = this.getJavaClass();
				cls = cls.getParent();
				boolean flag = false;
				while(true) {
					if(cls == null) break;
					if(cls.hasField(identifier)) flag = true;
					cls = cls.getParent();
				}
				if (flag) {
					//System.out.println("!!!! " + identifier + ": " + "found in parent scope");
					//sb.append("((");
					//sb.append(this.getJavaClass().getCppName());
					sb.append("__this->");

				}
			}

		    
		return sb.append(identifier);
	}
	
	public String getTypeName() {
		String typeName;
		SymbolTable table = this.getSymbolTable();
		if (table.current().isDefined(identifier)) {
			Type type = (Type) table.current().lookup(identifier);
			if (JavaEntities.isLocalT(type)){
		        
				 //System.out.print("(find type)Found occurrence of local variable: ");
			        
				
		        Type val = (Type)(table.current().lookupScope(identifier).lookupLocally(identifier));
		        if(val.hasAlias()) {
		        	//System.out.print(val.toAlias().getName() + " ");
		        	typeName = val.toAlias().getName();
		        }else {
		        	typeName = JavaType.translateSymbolType(val.toString());
		        }
		        
		        
		      }
		      else {
		    	 
		    	  Type val = (Type)(table.current().lookupScope(identifier).lookupLocally(identifier));
		    	  if(val.hasAlias()) {
		    		  	typeName = val.toAlias().getName();
			        }else {
			        	typeName = JavaType.translateSymbolType(val.toString());
			        }
		    	  
		      }
			if(JavaType.getType(typeName) == null) return "Object";
			return typeName;
		}
		
		return "Object";
	}
	
	public StringBuffer simplePrint() {
		return new StringBuffer(identifier);
	}
}
