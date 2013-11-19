package de.crowdcode.kissmda.core.jdt;

import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * @author idueppe
 */
public class ImportOrganizer {
	
	/**
	 * Find full qualified names in compilation unit and
	 * tries to reduce the full qualified names. 
	 * @param cu
	 * @param pac
	 */
	public void pack(CompilationUnit cu)
	{
		new ImportOrganizerVisitor(cu).pack();
	}		
	
	
}
