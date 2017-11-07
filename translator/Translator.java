package translator;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

import xtc.Constants;
import xtc.lang.JavaAstSimplifier;
import xtc.lang.JavaEntities;
import xtc.lang.JavaFiveParser;
import xtc.parser.ParseException;
import xtc.parser.Result;
import xtc.tree.Attribute;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Printer;
import xtc.tree.Visitor;
import xtc.type.AliasT;
import xtc.type.Type;
import xtc.type.VariableT;
import xtc.util.SymbolTable;

import java.util.*;

import translator.SymbolTableBuilder;

/**
 * Tranlator that translate java to c++
 *
 * @author Jeremy Jie Huang 
 * 
 */
public class Translator extends xtc.util.Tool {
  /** Create a new tool. */

	private String currentFile;
	

  public Translator() {
    // Nothing to do.
  }
  

  public String getName() {
    return "Tranlator";
  }

  public File locate(String name) throws IOException {
    File file = super.locate(name);
    if (Integer.MAX_VALUE < file.length()) {
      throw new IllegalArgumentException(file + ": file too large");
    }
    return file;
  }

  public Node parse(Reader in, File file) throws IOException, ParseException {
	  this.currentFile = file.toString();
	  
    JavaFiveParser parser =
      new JavaFiveParser(in, file.toString(), (int)file.length());
    Result result = parser.pCompilationUnit(0);
    return (Node)parser.value(result);
  }
  
  
  public void process(Node node) {
	  
	  //System.out.println(node.toString());
	  
	  SymbolTable table = new SymbolTable();
	  new SymbolTableBuilder(runtime, table).dispatch(node);
	  
	  //table.current().dump(runtime.console());
	  //runtime.console().flush();
	  
	  //System.out.println(node.getName() + "!!!!!");
	  table.enter(node);
	  //System.out.println(table.current().getName());
	  
	  JavaFile f = new JavaFile(this.currentFile, (GNode)node, table);
	  f.setSymbolScope(table.current());
	  f.process();
	  
	  table.exit();
  }
  
  /**
   * Run the tool with the specified command line arguments.
   *
   * @param args The command line arguments.
   */
  public static void main(String[] args) {
    new Translator().run(args);
  }

}
