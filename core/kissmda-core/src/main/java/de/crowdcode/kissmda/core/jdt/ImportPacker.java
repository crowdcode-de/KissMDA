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
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;

/**
 * This ImportPacker scan the CompilationUnit for FullQualifiedTypes and set
 * these as simple named types and imports them.#
 * 
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
			if (type.getName().isSimpleName()) {
				ignoredSimpleNames.add(type.getName().getFullyQualifiedName());
			}
			return true;
		}
	};

	private ASTVisitor searchQualifiedNamesVisitor = new ASTVisitor() {
		@Override
		public boolean visit(SimpleType type) {
			if (type.getName().isQualifiedName()) {
				QualifiedName qualifiedName = (QualifiedName) type.getName();
				if (!ignoredSimpleNames.contains(simpleName(qualifiedName))) {
					useSimpleNameAndAddToImport(type);
				}
			}
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean visit(MethodDeclaration node) {
			List<Name> newExceptions = new LinkedList<Name>();
			for (Name name : (List<Name>) node.thrownExceptions()) {
				if (name.isQualifiedName()) {
					QualifiedName qualifiedName = (QualifiedName) name;
					if (isAlreadyImported(qualifiedName)) {
						newExceptions.add(node.getAST().newName(simpleName(qualifiedName)));
					} else if (isNewSimpleName(qualifiedName.getName())) {
						newExceptions.add(node.getAST().newName(simpleName(qualifiedName)));
						importedTypes.put(simpleName(qualifiedName), qualifiedName);
					}
				}
			}
			
			if (!newExceptions.isEmpty())
			{
				node.thrownExceptions().clear();
				node.thrownExceptions().addAll(newExceptions);
			}
			
			return super.visit(node);
		}

	};

	private ASTVisitor searchImportStatementVisitor = new ASTVisitor() {

		@Override
		public boolean visit(ImportDeclaration node) {
			if (node.isStatic() || node.isOnDemand()) {
				staticImports.add(node);
			} else {
				QualifiedName qualifiedName = (QualifiedName) node.getName();
				String simpleName = simpleName(qualifiedName);
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

	protected void useSimpleNameAndAddToImport(SimpleType simpleType) {
		if (simpleType.getName().isQualifiedName()) {
			QualifiedName qualifiedName = (QualifiedName) simpleType.getName();
			String qualifier = qualifiedName.getQualifier().getFullyQualifiedName();
			String simpleName = simpleName(qualifiedName);
			AST ast = compilationUnit.getAST();
			if (isAlreadyImported(simpleName, qualifier)) {
				simpleType.setName(ast.newName(simpleName));
			} else if (isNewSimpleName(simpleName)) {
				simpleType.setName(ast.newName(simpleName));
				importedTypes.put(simpleName, qualifiedName);
			}
		}
	}

	private boolean isNewSimpleName(SimpleName name) {
		return isNewSimpleName(name.getFullyQualifiedName());
	}

	private boolean isNewSimpleName(String simpleName) {
		return !importedTypes.containsKey(simpleName);
	}

	private boolean isAlreadyImported(QualifiedName qualifiedName) {
		return isAlreadyImported(simpleName(qualifiedName), qualifiedName.getQualifier()
				.getFullyQualifiedName());
	}

	private boolean isAlreadyImported(String simpleName, String qualifier) {
		return importedTypes.containsKey(simpleName)
				&& importedTypes.get(simpleName).getQualifier().getFullyQualifiedName().equals(qualifier);
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

	private String simpleName(QualifiedName qualifiedName) {
		return qualifiedName.getName().getFullyQualifiedName();
	}

}
