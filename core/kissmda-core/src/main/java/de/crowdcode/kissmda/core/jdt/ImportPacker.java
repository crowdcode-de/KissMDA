package de.crowdcode.kissmda.core.jdt;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleType;

/**
 * This ImportPacker scan the CompilationUnit for FullQualifiedTypes
 * and set these as simple named types and imports them.# 
 * @author idueppe
 */
public class ImportPacker {

	private CompilationUnit compilationUnit;

	private List<ImportDeclaration> staticImports = new LinkedList<ImportDeclaration>();
	
	private Set<String> ignoredSimpleNames = new HashSet<String>();

	private Map<String, QualifiedName> importedTypes = new HashMap<String, QualifiedName>();
	
	private ASTVisitor searchSimpleNameToIgnoreVisitor = new ASTVisitor() {
		@Override
		public boolean visit(SimpleType type) {
			if (type.getName().isSimpleName())
			{
				ignoredSimpleNames.add(type.getName().getFullyQualifiedName());
			}
			return true;
		}
	};
	
	private ASTVisitor searchQualifiedNamesVisitor = new ASTVisitor() {
		@Override
		public boolean visit(SimpleType type) {
			if (type.getName().isQualifiedName())
			{
				QualifiedName qualifiedName = (QualifiedName) type.getName();
				if (!ignoredSimpleNames.contains(qualifiedName.getName().getFullyQualifiedName()))
				{
					addTypeToImport(type);					
				}
			}
			return true;
		}
	};

	private ASTVisitor searchImportStatementVisitor = new ASTVisitor() {

		@Override
		public boolean visit(ImportDeclaration node) {
			if (node.isStatic() || node.isOnDemand())
			{
				staticImports.add(node);
			} else {
				QualifiedName qualifiedName = (QualifiedName) node.getName();
				String simpleName = qualifiedName.getName().getFullyQualifiedName();
				if (!importedTypes.containsKey(simpleName))
					importedTypes.put(simpleName, qualifiedName);
			}
			return true;
		}
	};

	public ImportPacker(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	public void pack() {
		compilationUnit.accept(searchImportStatementVisitor);
		compilationUnit.accept(searchSimpleNameToIgnoreVisitor);
		compilationUnit.accept(searchQualifiedNamesVisitor);
		rewriteImports();
	}

	protected void addTypeToImport(SimpleType simpleType) {
		if (simpleType.getName().isQualifiedName())
		{
			QualifiedName qualifiedName = (QualifiedName) simpleType.getName();
			String qualifier = qualifiedName.getQualifier().getFullyQualifiedName();
			String simpleName = qualifiedName.getName().getFullyQualifiedName();
			AST ast = compilationUnit.getAST();
			if (importedTypes.containsKey(simpleName)
				&& importedTypes.get(simpleName).getQualifier().getFullyQualifiedName().equals(qualifier))
			{
				simpleType.setName(ast.newName(simpleName));
			} else if (!importedTypes.containsKey(simpleName))
			{
				simpleType.setName(ast.newName(simpleName));
				importedTypes.put(simpleName, qualifiedName);
			}
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
		List<QualifiedName> imports = new LinkedList<QualifiedName>(importedTypes.values());
		Collections.sort(imports, new Comparator<QualifiedName>() {

			@Override
			public int compare(QualifiedName o1, QualifiedName o2) {
				return o1.getFullyQualifiedName().compareTo(o2.getFullyQualifiedName());
			}
		});
		AST ast = compilationUnit.getAST();
		for (QualifiedName qualifiedName : imports) {
			if (!isTypeNameInCUPackage(qualifiedName) && !isJavaLang(qualifiedName))
				addImport(ast, qualifiedName);
		}
	}

	private boolean isJavaLang(QualifiedName name) {
		return "java.lang".equals(name.getQualifier().getFullyQualifiedName());
	}

	private boolean isTypeNameInCUPackage(QualifiedName name) {
		return compilationUnit.getPackage().getName().getFullyQualifiedName()
				.equals(name.getQualifier().getFullyQualifiedName());
	}

	@SuppressWarnings("unchecked")
	private void addImport(AST ast, QualifiedName qualifiedName) {
		ImportDeclaration impDecl = ast.newImportDeclaration();
		impDecl.setName(ast.newName(qualifiedName.getFullyQualifiedName()));
		compilationUnit.imports().add(impDecl);
	}

}
