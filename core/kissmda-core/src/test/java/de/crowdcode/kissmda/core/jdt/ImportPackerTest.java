package de.crowdcode.kissmda.core.jdt;

import static org.junit.Assert.assertEquals;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.junit.Ignore;
import org.junit.Test;

public class ImportPackerTest {

	@Test
	public void test_Do_Not_Import_Java_Lang_Classes() {
		String original = //
		"package org.kissmda.test.junit; " //
				+ "import java.util.Collection;" //
				+ "public abstract class ClazzName extends java.util.Collection" //
				+ "{" //
				+ "  public abstract java.util.List<java.lang.Integer> asList(java.util.Collection<java.lang.Integer> values);" //
				+ "}";

		CompilationUnit cu = buildAndPackCompilationUnit(original);

		assertEquals(
				"package org.kissmda.test.junit;\n"
						+ "import java.util.Collection;\n"
						+ "import java.util.List;\n"
						+ "public abstract class ClazzName extends Collection {\n"
						+ "  public abstract List<Integer> asList(  Collection<Integer> values);\n}",
				cu.toString().trim());
	}

	@Test
	public void test_Parameterized_Map_Types() {
		String original = //
		"package org.kissmda.test.junit; " //
				+ "import java.util.Collection;" //
				+ "public abstract class ClazzName extends java.util.Collection" //
				+ "{" //
				+ "  public java.util.Map<java.lang.Integer, java.util.List<?>> asMap(java.util.Collection<java.lang.Integer> values);" //
				+ "}";

		CompilationUnit cu = buildAndPackCompilationUnit(original);

		assertEquals(
				"package org.kissmda.test.junit;\n"
						+ "import java.util.Collection;\n"
						+ "import java.util.List;\n"
						+ "import java.util.Map;\n"
						+ "public abstract class ClazzName extends Collection {\n"
						+ "  public Map<Integer,List<?>> asMap(  Collection<Integer> values);\n"
						+ "}", cu.toString().trim());
	}

	@Test
	public void test_Parameterized_Methods() {
		String original = //
		"package org.kissmda.test.junit; " //
				+ "import java.util.Collection;" //
				+ "public abstract class ClazzName" //
				+ "{" //
				+ "  public <T> java.util.List<T> asList(java.util.Collection<T> values){return null;};" //
				+ "}";

		CompilationUnit cu = buildAndPackCompilationUnit(original);

		assertEquals("package org.kissmda.test.junit;\n"
				+ "import java.util.Collection;\n" + "import java.util.List;\n"
				+ "public abstract class ClazzName {\n"
				+ "  public <T>List<T> asList(  Collection<T> values){\n"
				+ "    return null;\n" + "  }\n" + "}", cu.toString().trim());
	}

	@Test
	public void test_Types_From_The_Same_Project() {
		String original = //
		"package org.kissmda.test.junit; " //
				+ " public class ClazzName {" //
				+ "  public void doSomething(org.kissmda.test.junit.Name values){ }" //
				+ " }";

		CompilationUnit cu = buildAndPackCompilationUnit(original);

		assertEquals("package org.kissmda.test.junit;\n" //
				+ "public class ClazzName {\n" //
				+ "  public void doSomething(  Name values){\n" //
				+ "  }\n" //
				+ "}", cu.toString().trim());
	}

	@Test
	public void test_Types_In_Method_Block() {
		String original = //
		"package org.kissmda.test.junit; " //
				+ " public class ClazzName {" //
				+ "  public void doSomething(org.kissmda.test.junit.Name values){"
				+ "     java.lang.Integer count = null;"
				+ "     java.util.List<java.lang.String> list = new java.util.LinkedList<>();"
				+ "  }" //
				+ "}";

		CompilationUnit cu = buildAndPackCompilationUnit(original);

		assertEquals("package org.kissmda.test.junit;\n" //
				+ "import java.util.LinkedList;\n" //
				+ "import java.util.List;\n" //
				+ "public class ClazzName {\n" //
				+ "  public void doSomething(  Name values){\n" //
				+ "    Integer count=null;\n" //
				+ "    List<String> list=new LinkedList<>();\n" //
				+ "  }\n" //
				+ "}", cu.toString().trim());
	}

	@Test
	public void test_Imports_Of_Inner_Classes() {
		String original = //
		"package org.kissmda.test.junit;\n" //
				+ "public class ClazzName {\n" //
				+ "  public void doSomething(  Name values){\n" //
				+ "    java.util.Map.Entry entry=null;\n" //
				+ "    java.util.List<String> list=new java.util.LinkedList<>();\n" //
				+ "  }\n" //
				+ "}";

		CompilationUnit cu = buildAndPackCompilationUnit(original);

		assertEquals("package org.kissmda.test.junit;\n" //
				+ "import java.util.LinkedList;\n"
				+ "import java.util.List;\n" //
				+ "import java.util.Map.Entry;\n"//
				+ "public class ClazzName {\n" //
				+ "  public void doSomething(  Name values){\n" //
				+ "    Entry entry=null;\n" //
				+ "    List<String> list=new LinkedList<>();\n" //
				+ "  }\n" //
				+ "}", cu.toString().trim());
	}
	
	@Test
	@Ignore("It seems that 'import static' not parsed correctly.")
	public void test_Existing_Static_Imports() {
		String original = //
				"package org.kissmda.test.junit;\n" //
				+ "import static org.junit.Assert.assertNotNull;\n"
				+ "public class ClazzName {\n" //
				+ "  public void doSomething(  Name values){\n" //
				+ "    assertNotNull(values);\n" //
				+ "  }\n" //
				+ "}";
		
		CompilationUnit cu = buildAndPackCompilationUnit(original);
		System.out.println(cu.toString());
		
		assertEquals("package org.kissmda.test.junit;\n" //
				+ "import static org.junit.Assert.assertNotNull;\n"
				+ "public class ClazzName {\n" //
				+ "  public void doSomething(  Name values){\n" //
				+ "    assertNotNull(values);\n" //
				+ "  }\n" //
				+ "}", cu.toString().trim());
	}

	@Test
	@Ignore("Not implemented yet.")
	public void test_Packing_Exception_Types() {
		String original = //
				"package org.kissmda.test.junit;\n" //
				+ "public class ClazzName {\n" //
				+ "  public void doSomething(  Name values) throws org.kissmda.Exception {\n" //
				+ "    assertNotNull(values);\n" //
				+ "  }\n" //
				+ "}";
		
		CompilationUnit cu = buildAndPackCompilationUnit(original);
		System.out.println(cu.toString());
		
		assertEquals("package org.kissmda.test.junit;\n" //
				+ "import org.kissmda.Exception;\n"
				+ "public class ClazzName {\n" //
				+ "  public void doSomething(  Name values) throws Exception {\n" //
				+ "    assertNotNull(values);\n" //
				+ "  }\n" //
				+ "}", cu.toString().trim());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_Existing_Static_Imports2() {
		AST ast = AST.newAST(AST.JLS4);
		CompilationUnit cu = ast.newCompilationUnit();
		
		PackageDeclaration packageDeclaration = ast.newPackageDeclaration();
		packageDeclaration.setName(ast.newName("org.kissmda.test.junit"));
		cu.setPackage(packageDeclaration);
		
		ImportDeclaration importDeclaration = ast.newImportDeclaration();
		importDeclaration.setName(ast.newName("org.junit.Assert.assertNotNull"));
		importDeclaration.setStatic(true);
		cu.imports().add(importDeclaration);
		
		System.out.println(cu.toString());
		new ImportPacker(cu).pack();
		
		System.out.println(cu.toString());
		
		assertEquals("package org.kissmda.test.junit;\n" //
				+ "import static org.junit.Assert.assertNotNull;", cu.toString().trim());
	}
	
	private CompilationUnit buildAndPackCompilationUnit(String java) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(java.toCharArray());
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		System.out.println(cu.toString());
		new ImportPacker(cu).pack();
		return cu;
	}

}
