/**
 * This file is licensed under the University of Illinois/NCSA Open Source License. See LICENSE.TXT for details.
 */
package edu.illinois.codingtracker.tests.postprocessors.ast.refactoring.properties;



/**
 * This class represents an added declaration of a variable.
 * 
 * @author Stas Negara
 * 
 */
public class AddedVariableDeclarationRefactoringProperty extends RefactoringProperty {


	public AddedVariableDeclarationRefactoringProperty(String entityName) {
		addAttribute(RefactoringPropertyAttributes.ENTITY_NAME, entityName);
	}


}
