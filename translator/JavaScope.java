package translator;

import java.util.Hashtable;
import java.util.ArrayList;
import java.lang.StringBuffer;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Printer;
import xtc.tree.Visitor;
import xtc.util.SymbolTable;

public class JavaScope extends Visitor{
	
	protected Hashtable<String,JavaField> fields = new Hashtable<String,JavaField>();
	
	protected JavaScope scope; // parent scope
	
	protected GNode thisNode;

	protected SymbolTable.Scope symbolScope;
	
	public JavaScope(JavaScope scope, GNode n) {
		this.scope = scope;
		thisNode = n;
	}
	
	public JavaScope getParentScope() {
		return scope;
	}
	
	public boolean hasField(String name) {
		return this.fields.get(name) != null;
	}
	
	public JavaFile getJavaFile() {
		if (this instanceof JavaFile)
			return (JavaFile)this;
		
		//ask the parent if he is a file
		return this.scope.getJavaFile();
	}
	
	public JavaClass getJavaClass() {
		if (this instanceof JavaClass)
			return (JavaClass)this;
		
		//ask the parent if he is a file
		return this.scope.getJavaClass();
	}
	
	public JavaBlock getJavaBlock() {
		if (this instanceof JavaBlock)
			return (JavaBlock)this;
		
		if (this instanceof JavaFile)
			return null;
		
		//ask the parent if he is a file
		return this.scope.getJavaBlock();
	}
	
	public void addField(JavaField field) {
		this.fields.put(field.getName(), field);
	}
	
	public JavaField getField(String key) {
		return this.fields.get(key);
	}
	
	public GNode getThisNode() {
		return thisNode;
	}
	
	public SymbolTable getSymbolTable() {
		return this.getJavaFile().getSymbolTable();
	}
	
	public SymbolTable.Scope getSymbolScope() {
		return this.symbolScope;
	}
	
	public void setSymbolScope(SymbolTable.Scope s) {
		this.symbolScope = s;
	}
	
	//Visit Statements
		public JavaBlock visitBlock(GNode n) {
			
			
			
			return new JavaBlock(this,n);
			
			
			
		}
		public JavaStatement visitConditionalStatement(GNode n){
			return new ConditionalStatement(this,n);
		}
		public JavaStatement visitDoWhileStatement(GNode n){
			return new DoWhileStatement(this,n);
		}
		public JavaStatement visitForStatement(GNode n){
			return new ForStatement(this,n);
		}
		public JavaStatement visitWhileStatement(GNode n){
			return new WhileStatement(this,n);
		}
		public JavaStatement visitReturnStatement(GNode n){
			return new ReturnStatement(this,n);
		}
		public JavaStatement visitExpressionStatement(GNode n){
			return new ExpressionStatement(this,n);
		}
		public RelationalExpression visitRelationalExpression(GNode n){
			return new RelationalExpression(this,n);
		}
		
		public JavaExpression visitIntegerLiteral(GNode n){
			return new IntegerLiteral(this,n);
		}
		
		public JavaExpression visitFloatingPointLiteral(GNode n){
			return new FloatingPointLiteral(this,n);
		}
		
		public Declarators visitDeclarators(GNode n){
			return new Declarators(this,n);
		}

		public Declarator visitDeclarator(GNode n){
			return new Declarator(this,n);
		}

		//Visit Expressions
		

		public JavaExpression visitExpression(GNode n){
			return new Expression(this,n);
		}

		public JavaExpression visitSelectionExpression(GNode n){
			return new SelectionExpression(this,n);
		}

		public JavaExpression visitThisExpression(GNode n){
			return new ThisExpression(this,n);
		}

		public JavaExpression visitAdditiveExpression(GNode n){
			return new ArithmeticExpression(this,n);
		}
		
		public JavaExpression visitMultiplicativeExpression(GNode n){
			return new ArithmeticExpression(this,n);
		}
		
		public JavaExpression visitModifiers(GNode n){
			return new Modifiers(this,n);
		}
		
		public JavaExpression visitStringLiteral(GNode n){
			return new StringLiteral(this,n);
		}
		
		public JavaExpression visitBooleanLiteral(GNode n){
			return new BooleanLiteral(this,n);
		}
		
		public Dimensions visitDimensions(GNode n){
			return new Dimensions(this,n);
		}

		public JavaExpression visitPrimaryIdentifier(GNode n) {
			return new PrimaryIdentifier(this, n);
		}
		
		public JavaExpression visitQualifiedIdentifier(GNode n) {
			//System.out.println("qualified identifier");
			//System.out.println(n);
			return new QualifiedIdentifier(this, n);
		}
	
	
	public JavaField visitFieldDeclaration(GNode n) {
		FieldDec fd = new FieldDec(this, n);
		JavaField jfd = fd.getField();
        return jfd;
	}
	
	public JavaExpression visitCallExpression(GNode n){
		return new CallExpression(this,n);
	}

	public JavaExpression visitBasicCastExpression(GNode n){
		return new BasicCastExpression(this,n);
	}
	
	public JavaExpression visitCastExpression(GNode n){
		return new CastExpression(this,n);
	}
	
	public JavaExpression visitNewClassExpression(GNode n){
		return new NewClassExpression(this,n);
	}
	
	public JavaExpression visitNewArrayExpression(GNode n){
		return new NewArrayExpression(this,n);
	}
	
	public JavaExpression visitArguments(GNode n){
		return new Arguments(this,n);
	}
	
	public JavaExpression visitLogicalAndExpression(GNode n) {
		return new LogicExpression(this, n, "&&");
	}

	public JavaExpression visitLogicalOrExpression(GNode n) {
		return new LogicExpression(this, n, "||");
	}

	public JavaExpression visitLogicalNegationExpression(GNode n) {
		return new LogicExpression(this, n, "!");
	} 
	public JavaExpression visitUnaryExpression(GNode n){
		return new UnaryExpression(this,n);
	}
	public JavaExpression visitPostfixExpression(GNode n){
		
		return new PostfixExpression(this,n);
	}
	public JavaExpression visitExpressionList(GNode n){
		return new ExpressionList(this,n);
	}
	
	public JavaExpression visitSubscriptExpression(GNode n){
		return new SubscriptExpression(this,n);
	}
	
	/**
	* all subclasses of JavaScope should be implementing print(); 
	* print() prints the current scope into c++;
	**/
	//public abstract StringBuffer print();
	
	
	public void visit(Node n) {
		for (Object o : n) {
			if (o instanceof Node)
				this.dispatch((Node)o);
		}
	}
}