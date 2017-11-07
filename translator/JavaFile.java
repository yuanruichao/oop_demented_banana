package translator;

import java.util.Hashtable;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.StringBuffer;
import java.util.Set;

import translator.JavaClass;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Printer;
import xtc.tree.Visitor;
import xtc.util.SymbolTable;

public class JavaFile extends JavaScope {
	
	private String fileName;
	
	private JavaClass mainClass;
	
	private Hashtable<String, JavaClass> classes = new Hashtable<String, JavaClass>();
	
	protected SymbolTable table;
	
	
	//constructor
	JavaFile(String fileName, GNode n, SymbolTable table) {
		super(null, n);
		this.fileName = fileName;
		this.table = table;
		this.dispatch(n);
	}
	
	public String getName() {
		return this.fileName;
	}
	
	public JavaClass getClass(String name) {
		return classes.get(name);
	}
	
	//return the set of all classes in this file
	public Set<String> getAllClass() {
		return classes.keySet();
	}
	
	//set main class(the class with the main method)
	public void setMain(JavaClass cls) {
		this.mainClass = cls;
	}
	
	public SymbolTable getSymbolTable() {
		return this.table;
	}
	
	public void process() {
		
		
		JavaType.setup(this);
		JavaMethod.staticSetup();
		JavaClass.setupJavaLang(this);
		
		Set<String> classNames = classes.keySet();
		for(String key: classNames) {
						
			JavaClass curr = classes.get(key);
			
			this.getSymbolTable().enter(curr.getThisNode());
			//System.out.println("Entered scope " + this.getSymbolTable().current().getName());
			
			curr.process();
			
			this.getSymbolTable().exit();
			
		}
		
		JavaType.setupParent();
		
		this.classes.put(JavaClass.getJavaLangObject().getName(), JavaClass.getJavaLangObject());
		
		//using child list to setup all classes
		JavaClass.getJavaLangObject().setup();//setup VT
		
		/*
		for(JavaClass cls : classes.values()) {
			cls.printVTable();
			cls.printAllField();
		}
		*/
		//System.out.println(this.printCpp().toString());
		this.writeToFile();
		
		//test
		/*
		JavaType.printAllTypes();
		System.out.println();
		for(String key: classNames) {
			JavaClass curr = classes.get(key);
			System.out.print(curr.getName());
			System.out.print(" <- ");
			if(curr.getParent() == null) {
				System.out.println("null");
			} else {
				System.out.println(curr.getParent().getName());
			}
			curr.printAllMethod();
			curr.printAllField();
		}
		*/
	}
	
	//a file writer
	public void writeToFile() {
		File file = new File("./out.h"); 
		if(file.exists() && file.isFile()) {
			file.delete();
		}
		  
	    if(!file.exists()){  
	        try {  
	            file.createNewFile();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    } 
	    
	    synchronized (file) {  
            FileWriter fw;
			try {
				fw = new FileWriter("./out.h");
				fw.write(this.printHeader().toString());  
		        fw.close(); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
            
        }  
	    
	    file = new File("./out.cpp"); 
		if(file.exists() && file.isFile()) {
			file.delete();
		}
		  
	    if(!file.exists()){  
	        try {  
	            file.createNewFile();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    } 
	    
	    synchronized (file) {  
            FileWriter fw;
			try {
				fw = new FileWriter("./out.cpp");
				fw.write(this.printCpp().toString());  
		        fw.close(); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
            
        }
	}
	
	//visit
	public void visitClassDeclaration(GNode n) {
		this.getSymbolTable().enter(n);
		//System.out.println("Entered scope " + this.getSymbolTable().current().getName());
		
		//System.out.println("!!!!");
		JavaClass cls = new JavaClass((JavaScope)this, n);
		this.classes.put(cls.getName(), cls);
		cls.setSymbolScope(cls.getSymbolTable().current());
		
		this.getSymbolTable().exit();
	}
	
	public StringBuffer printCpp() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("#include \"out.h\"\n#include <sstream>\n#include <iostream>\n\nusing namespace std;\nusing namespace java::lang;\n\n");
		
		/*
		File file = new File("./JavaLangObject.cpp"); 
		
		try {  
	        FileInputStream fileInputStream = new FileInputStream(file);  
	        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "GBK");  
	        BufferedReader reader = new BufferedReader(inputStreamReader);  
	        String lineContent = "";  
	        while ((lineContent = reader.readLine()) != null) {  
	            sb.append(lineContent).append("\n");   
	        }  
	          
	        fileInputStream.close();  
	        inputStreamReader.close();  
	        reader.close();  
	    } catch (FileNotFoundException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } 
	    */ 

		sb.append("\n");
		
		for(JavaClass cls : classes.values()) {
			if(cls == JavaClass.getJavaLangObject()) {
				//System.out.println("!!!!!!!!");
				continue;
			}
			//System.out.println(cls.getName() + "!!!!!!!!!");
			this.getSymbolTable().enter(cls.getThisNode());
			//System.out.println("(Print)Entered scope " + this.getSymbolTable().current().getName());
			
			sb.append(cls.printCpp());
			
			this.getSymbolTable().exit();
		}
		
		sb.append("int main(int argc, char* argv[])\n{\n");
		
		sb.append("__rt::Ptr<__rt::Array<String> > args = new __rt::Array<String>(argc - 1);\n");
		sb.append("for (int32_t i = 1; i < argc; i++) {\n(*args)[i - 1] = __rt::literal(argv[i]);\n}\n");
		
		sb.append(mainClass.getCppName()).append("::main(args);\n\nreturn 0;\n}");
		
		return sb;
	}
	
	public StringBuffer printHeader(){
		StringBuffer sb = new StringBuffer();
		sb.append("#pragma once\n\n#include \"java_lang.h\"\n\nusing namespace java::lang;\n\n");
		for(JavaClass cls : classes.values()) {
			if(cls != JavaClass.getJavaLangObject()){
				sb.append("struct " + cls.getCppName() + ";\n");
			}
		}
		for(JavaClass cls : classes.values()) {
			if(cls != JavaClass.getJavaLangObject())
				sb.append("struct " + cls.getVTName() + ";\n");
		}
		for(JavaClass cls : classes.values()) {
			if(cls != JavaClass.getJavaLangObject()) {
				sb.append("typedef __rt::Ptr<").append(cls.getRawCppName()).append("> ").append(cls.getName()).append(";\n");
			}
		}
		sb.append("\n");
		
		/*
		File file = new File("./JavaLangObject.h"); 
		
		try {  
	        FileInputStream fileInputStream = new FileInputStream(file);  
	        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "GBK");  
	        BufferedReader reader = new BufferedReader(inputStreamReader);  
	        String lineContent = "";  
	        while ((lineContent = reader.readLine()) != null) {  
	            sb.append(lineContent).append("\n");   
	        }  
	          
	        fileInputStream.close();  
	        inputStreamReader.close();  
	        reader.close();  
	    } catch (FileNotFoundException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  

		sb.append("\n");
		*/
		
		for(JavaClass cls : classes.values()) {
			if(cls == JavaClass.getJavaLangObject()) continue;
			sb.append(cls.printHeader());
		}
		
		sb.append("\n");
		
		return sb;
	}
	
}