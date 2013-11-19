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
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleType;

/**
 * This ImportPacker scan the CompilationUnit for FullQualifiedTypes
 * and set these as simple named types and imports them.# 
 * @author idueppe
 */
public class ImportPacker {

	private CompilationUnit compilationUnit;

	private Map<String, String> importedTypes = new HashMap<String, String>();
	private List<ImportDeclaration> staticImports = new LinkedList<ImportDeclaration>();

	private ASTVisitor simpleTypeVisitor = new ASTVisitor() {
		@Override
		public boolean visit(SimpleType node) {
			simpleTypeToImport(node);
			return true;
		}

		@Override
		public boolean visit(MethodDeclaration node) {
			// FIXME idueppe - to be done
			for (Name exception : (List<Name>)node.thrownExceptions())
			{
				System.out.println(" :: "+exception.getFullyQualifiedName());
			}
			return true;
		}
		
		
	};

	private ASTVisitor importStatementVisitor = new ASTVisitor() {

		@Override
		public boolean visit(ImportDeclaration node) {
			if (node.isStatic())
			{
				staticImports.add(node);
			} else {
				String fullQualifiedName = node.getName().getFullyQualifiedName();
				String simpleName = typeName(fullQualifiedName);
				if (!importedTypes.containsKey(simpleName))
					importedTypes.put(simpleName, fullQualifiedName);
			}
			return true;
		}
	};

	public ImportPacker(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	public void pack() {
		compilationUnit.accept(importStatementVisitor);
		compilationUnit.accept(simpleTypeVisitor);
		rewriteImports();
	}

	protected void simpleTypeToImport(SimpleType simpleType) {
		Name name = simpleType.getName();

		AST ast = compilationUnit.getAST();

		String fullyQualifiedName = name.getFullyQualifiedName();
		String simpleName = typeName(fullyQualifiedName);

		if (!simpleName.equals(fullyQualifiedName)) {
			if (importedTypes.containsKey(simpleName)
					&& importedTypes.get(simpleName).equals(fullyQualifiedName)) {
				simpleType.setName(ast.newName(simpleName));
			} else if (!importedTypes.containsKey(simpleName)) {
				simpleType.setName(ast.newName(simpleName));
				importedTypes.put(simpleName, fullyQualifiedName);
			}
		}
	}

	protected String typeName(String fullQualifiedName) {
		int lastIndexOfPoint = fullQualifiedName.lastIndexOf('.');
		if (lastIndexOfPoint < 0) {
			return fullQualifiedName.trim();
		} else {
			return fullQualifiedName.substring(lastIndexOfPoint + 1).trim();
		}
	}

	private void rewriteImports() {
		compilationUnit.imports().clear();
		writeStaticImports();
		writeImports();
	}

	@SuppressWarnings("unchecked")
	private void writeStaticImports() {
		compilationUnit.imports().addAll(staticImports);
	}

	private void writeImports() {
		List<String> imports = new LinkedList<String>(importedTypes.values());
		Collections.sort(imports);
		AST ast = compilationUnit.getAST();
		for (String typeName : imports) {
			if (!isTypeNameInCUPackage(typeName) && !isJavaLang(typeName))
				addImport(ast, typeName);
		}
	}

	private boolean isJavaLang(String typeName) {
		return "java.lang".equals(typePackageName(typeName));
	}

	private boolean isTypeNameInCUPackage(String typeName) {
		return compilationUnit.getPackage().getName().getFullyQualifiedName()
				.equals(typePackageName(typeName));
	}

	private String typePackageName(String typeName) {
		int lastIndex = typeName.lastIndexOf('.');
		String typePackage = typeName.substring(0, lastIndex);
		return typePackage;
	}

	@SuppressWarnings("unchecked")
	private void addImport(AST ast, String importType) {
		ImportDeclaration impDecl = ast.newImportDeclaration();
		impDecl.setName(ast.newName(importType));
		compilationUnit.imports().add(impDecl);
	}

}
