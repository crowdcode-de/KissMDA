package de.crowdcode.kissmda.core.jdt;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleType;

public class ImportOrganizerVisitor extends ASTVisitor {

	private CompilationUnit compilationUnit;
	
	private Map<String, String> importedTypes = new HashMap<String, String>();
	
	protected ImportOrganizerVisitor(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}
	
	@Override
	public boolean visit(SimpleType node) {
		simpleTypeToImport(node);
		return true;
	}

	protected void simpleTypeToImport(SimpleType simpleType)
	{
		Name name = simpleType.getName();

		AST ast = compilationUnit.getAST();

		String fullyQualifiedName = name.getFullyQualifiedName();
		String simpleName = typeName(fullyQualifiedName);
		
		if (!simpleName.equals(fullyQualifiedName))
		{
			if (importedTypes.containsKey(simpleName) && importedTypes.get(simpleName).equals(fullyQualifiedName))
			{
				simpleType.setName(ast.newName(simpleName));
			} else if (!importedTypes.containsKey(simpleName))
			{
				simpleType.setName(ast.newName(simpleName));
				importedTypes.put(simpleName, fullyQualifiedName);
			}
		} 
	}
	
	protected String typeName(String fullQualifiedName)
	{
		int lastIndexOfPoint = fullQualifiedName.lastIndexOf('.');
		if (lastIndexOfPoint < 0)
			return fullQualifiedName.trim();
		else 
			return fullQualifiedName.substring(lastIndexOfPoint+1).trim();
	}

	public void pack() {
		compilationUnit.accept(this);
		addImports();
	}

	private void addImports() {
		List<String> imports = new LinkedList<String>(importedTypes.values());
		Collections.sort(imports);
		AST ast = compilationUnit.getAST();
		for (String typeName : imports) {
			if (!isTypeNameInCUPackage(typeName))
				addImport(ast, typeName);
		}
	}


	private boolean isTypeNameInCUPackage(String typeName) {
		int lastIndex = typeName.lastIndexOf('.');
		String typePackage = typeName.substring(0,lastIndex);
		return compilationUnit.getPackage().getName().toString().equals(typePackage);
	}

	@SuppressWarnings("unchecked")
	private void addImport(AST ast, String importType) {
		ImportDeclaration impDecl = ast.newImportDeclaration();
		impDecl.setName(ast.newName(importType));
		compilationUnit.imports().add(impDecl);
	}
	
	
}
