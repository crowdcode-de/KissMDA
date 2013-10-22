/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.crowdcode.kissmda.core.uml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Dependency;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.InterfaceRealization;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.ParameterableElement;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.TemplateBinding;
import org.eclipse.uml2.uml.TemplateParameterSubstitution;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.ValueSpecification;

import de.crowdcode.kissmda.core.jdt.DataTypeUtils;

/**
 * This class is taken from JavaBasic-Generator Fornax oAW project. Extended by
 * Lofi Dewanto for KissMDA.
 * 
 * Helper class for the helper extension of the JavaBasic-Generator. See:
 * http://goo.gl/W13i8
 * 
 * @author Thorsten Kamann <thorsten.kamann@googlemail.com>
 * @author Andre Neumann <andre.v.neumann@gmx.de>
 * @author Lofi Dewanto
 * 
 * @since 1.0.0
 */
public class UmlHelper {

	private static final Logger logger = Logger.getLogger(UmlHelper.class
			.getName());

	@Inject
	private DataTypeUtils dataTypeUtils;

	private Map<String, String> annotationSourceKeyMap = null;

	public void log(Object o) {
		logger.log(Level.FINE, o != null ? o.toString() : "<null>");
	}

	public void log(Object o, String logMsg) {
		logger.log(Level.FINE, logMsg + ": " + o != null ? o.toString()
				: "<null>");
	}

	/**
	 * Calculate the full qualified packagename of the given type.
	 * 
	 * @param type
	 *            The <code>org.eclipse.uml2.uml.Type</code> to calculate the
	 *            full qualified name for
	 * @return The full qualified name as <code>java.lang.String</code>
	 */
	public String getFQNPackageName(Type type) {
		String pn = "";
		Package p = findNearestPackage(type);
		while (p != null) {
			pn = p.getName() + "." + pn;
			p = findNearestPackage(p);
		}
		if (pn.endsWith(".")) {
			pn = pn.substring(0, pn.length() - 1);
		}
		return pn;
	}

	/**
	 * Finds the nearest Package for the given element.
	 * 
	 * @param el
	 *            The <code>org.eclipse.uml2.uml.Element</code> to find the
	 *            nearest package for
	 * @return The nearest Package or null
	 */
	private Package findNearestPackage(Element el) {
		Element o = el.getOwner();
		while (!(o instanceof Package) && (o != null)) {
			o = o.getOwner();
		}
		if (o instanceof Package) {
			if (o instanceof Model || o instanceof Profile) {
				return null;
			} else {
				return (Package) o;
			}
		} else {
			return null;
		}
	}

	/**
	 * Calculate the full qualified packagepath of the given type.
	 * 
	 * @param type
	 *            The <code>org.eclipse.uml2.uml.Type</code> to calculate the
	 *            full qualified path for
	 * @return The full qualified path as <code>java.lang.String</code>
	 */
	public String getFQNPackagePath(Type type) {
		String path = getFQNPackageName(type);
		path = path.replaceAll("\\.", "/");
		return path;
	}

	/**
	 * Calculate the full qualified componentname of the given type.
	 * 
	 * @param type
	 *            The <code>org.eclipse.uml2.uml.Type</code> to calculate the
	 *            full qualified name for
	 * @return The full qualified name as <code>java.lang.String</code>
	 */
	public String getFQNComponentName(Type type) {
		String pn = "";
		Component cp = null;
		// looking for start component
		if (type instanceof Component) {
			cp = (Component) type;
		} else {
			cp = findNearestComponent(type);
		}
		// stepping up component hierarchy
		while (cp != null) {
			pn = cp.getName() + "." + pn;
			cp = findNearestComponent(cp);
		}

		if (pn.endsWith(".")) {
			pn = pn.substring(0, pn.length() - 1);
		}
		return pn;
	}

	/**
	 * Calculate the full qualified componentpath of the given type.
	 * 
	 * @param type
	 *            The <code>org.eclipse.uml2.uml.Type</code> to calculate the
	 *            full qualified path for
	 * @return The full qualified path as <code>java.lang.String</code>
	 */
	public String getFQNComponentPath(Type type) {
		String path = getFQNComponentName(type);
		path = path.replaceAll("\\.", "/");
		return path;
	}

	/**
	 * Finds the nearest Component for the given element.
	 * 
	 * @param el
	 *            The <code>org.eclipse.uml2.uml.Element</code> to find the
	 *            nearest component for
	 * @return The nearest Component or null
	 */
	private Component findNearestComponent(Element el) {
		Element o = el.getOwner();
		while (!(o instanceof Component) && (o != null)) {
			o = o.getOwner();
		}
		if (o != null) {
			return (Component) o;
		} else {
			return null;
		}
	}

	/**
	 * Get XmiId.
	 * 
	 * @param eObject
	 *            The object to retrieve the id from
	 * @return The unique XMI-ID of the given element
	 */
	public String getXmiId(EObject eObject) {
		return ((XMLResource) eObject.eResource()).getID(eObject);
	}

	/**
	 * Collects all attributes the classifier and their interfaces have.
	 * 
	 * @param classifier
	 *            The classifier the attributes should be collected
	 * @return An <code>EList</code> with all collected attributes
	 */
	public EList<Property> getAllAttributes(Classifier classifier) {
		EList<Property> attributes = new UniqueEList<Property>();

		attributes.addAll(classifier.getAttributes());
		if (classifier instanceof org.eclipse.uml2.uml.Class) {
			Class clazz = (Class) classifier;
			for (int i = 0; i < clazz.getImplementedInterfaces().size(); i++) {
				attributes.addAll(getAllInterfaceAttributes(clazz
						.getImplementedInterfaces().get(i)));
			}
		}
		return attributes;
	}

	/**
	 * Collects all operations the classifier and their interfaces have.
	 * 
	 * @param classifier
	 *            The classifier the operations should be collected
	 * @return An <code>EList</code> with all collected operations
	 */
	public EList<Operation> getAllOperations(Classifier classifier) {
		EList<Operation> operations = new UniqueEList<Operation>();

		operations.addAll(classifier.getOperations());
		if (classifier instanceof org.eclipse.uml2.uml.Class) {
			Class clazz = (Class) classifier;
			for (int i = 0; i < clazz.getImplementedInterfaces().size(); i++) {
				operations.addAll(getAllInterfaceOperations(clazz
						.getImplementedInterfaces().get(i)));
			}
		}
		return operations;
	}

	/**
	 * Collects all dependencies the NamedElement has. In the case of a Class
	 * the dependencies of its interfaces will be processed too.
	 * 
	 * @param element
	 *            The NamedElement the dependencies should be collected
	 * @return An <code>EList</code> with all collected dependencies
	 */
	@SuppressWarnings("unchecked")
	public EList<Dependency> getAllDependencies(NamedElement element) {
		EList<Dependency> dependencies = new UniqueEList<Dependency>();

		dependencies.addAll(element.getClientDependencies());
		if (element instanceof org.eclipse.uml2.uml.Class) {
			Class clazz = (Class) element;
			for (int i = 0; i < clazz.getImplementedInterfaces().size(); i++) {
				dependencies.addAll(getAllInterfaceAssociations(clazz
						.getImplementedInterfaces().get(i)));
			}
		}

		EList<Dependency> depsFiltered = new UniqueEList<Dependency>();
		for (Iterator<Dependency> iter = dependencies.iterator(); iter
				.hasNext();) {
			Dependency e = iter.next();
			if (!(e instanceof InterfaceRealization)) {
				depsFiltered.add(e);
			}
		}

		return depsFiltered;
	}

	/**
	 * Collects all associations the classifier and their interfaces have.
	 * 
	 * @param classifier
	 *            The classifier the associations should be collected
	 * @return An <code>EList</code> with all collected associations
	 */
	@SuppressWarnings("unchecked")
	public EList<Association> getAllAssociations(Classifier classifier) {
		EList<Association> associations = new UniqueEList<Association>();

		associations.addAll(classifier.getAssociations());
		if (classifier instanceof org.eclipse.uml2.uml.Class) {
			Class clazz = (Class) classifier;
			for (int i = 0; i < clazz.getImplementedInterfaces().size(); i++) {
				associations.addAll(getAllInterfaceAssociations(clazz
						.getImplementedInterfaces().get(i)));
			}
		}
		return associations;
	}

	/**
	 * Collects all attributes of the given interface needed to implement
	 * (creating fields and getter/setter methods) by a implementing class.
	 * 
	 * @param iFace
	 *            The interface to collect the operations from
	 * @return An <code>EList</code> with all collected attributes
	 */
	private EList<Property> getAllInterfaceAttributes(Interface iFace) {
		EList<Property> attributes = new UniqueEList<Property>();

		attributes.addAll(iFace.getAllAttributes());
		if (iFace.getGeneralizations().size() > 0) {
			for (int i = 0; i < iFace.getGeneralizations().size(); i++) {
				attributes.addAll(getAllInterfaceAttributes((Interface) iFace
						.getGeneralizations().get(i).getGeneral()));
			}
		}
		return attributes;
	}

	/**
	 * Collects all operation of the given interface needed to implement by a
	 * implementing class.
	 * 
	 * @param iFace
	 *            The interface to collect the operations from
	 * @return An <code>EList</code> with all collected operations
	 */
	private EList<Operation> getAllInterfaceOperations(Interface iFace) {
		EList<Operation> operations = new UniqueEList<Operation>();

		operations.addAll(iFace.getAllOperations());
		if (iFace.getGeneralizations().size() > 0) {
			for (int i = 0; i < iFace.getGeneralizations().size(); i++) {
				operations.addAll(getAllInterfaceOperations((Interface) iFace
						.getGeneralizations().get(i).getGeneral()));
			}
		}
		return operations;
	}

	/**
	 * Collects all associations of the given interface needed to implement by a
	 * implementing class.
	 * 
	 * @param iFace
	 *            The interface to collect the associations from
	 * @return An <code>EList</code> with all collected associations
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private EList getAllInterfaceAssociations(Interface iFace) {
		EList associations = new UniqueEList();

		associations.addAll(iFace.getAssociations());
		if (iFace.getGeneralizations().size() > 0) {
			for (int i = 0; i < iFace.getGeneralizations().size(); i++) {
				associations
						.addAll(getAllInterfaceAssociations((Interface) iFace
								.getGeneralizations().get(i).getGeneral()));
			}
		}
		return associations;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String formatComment(Element element, String prefix,
			String propertyFileName) {
		String comment = "";
		EList comments = new BasicEList();

		createAnnotationSourceKey(propertyFileName);
		for (Iterator<String> iter = annotationSourceKeyMap.keySet().iterator(); iter
				.hasNext();) {
			String source = iter.next();
			String key = annotationSourceKeyMap.get(source);
			comment = findDocumentationAnnotation(element, source, key);
			if (comment != null) {
				break;
			}
		}

		if (comment != null) {
			comments.add(comment);
		} else {
			comments = element.getOwnedComments();
		}

		return getFormatedComment(comments, prefix, null);
	}

	public boolean createAnnotationSourceKey(String propertyFileName) {
		String[] items;
		String source;
		String key;

		if (annotationSourceKeyMap != null) {
			return false;
		}

		annotationSourceKeyMap = new HashMap<String, String>();
		items = propertyFileName.split(",");
		for (int i = 0; i < items.length; i++) {
			String item = items[i];
			item = item.trim();

			int start = item.indexOf("[");
			int end = item.indexOf("]");

			if (start < 0 || end < start) {
				source = item;
				key = "documentation";
			} else {
				source = item.substring(0, start);
				key = item.substring(start + 1, end);
			}
			annotationSourceKeyMap.put(source, key);
		}
		return true;
	}

	private String findDocumentationAnnotation(Element parent, String source,
			String key) {
		EAnnotation ea = parent
				.getEAnnotation("http://www.topcased.org/documentation");
		String comment = null;

		if (ea != null) {
			EMap<?, ?> details = ea.getDetails();
			if (details != null && !details.isEmpty()
					&& details.get("documentation") != null) {
				comment = details.get("documentation").toString();
			}
		}

		return comment;
	}

	/**
	 * A method comment is extended by the description of the parameters.
	 * 
	 * @precondition Iff the parameter is a return type, the type of the
	 *               parameter must be not null.
	 * 
	 * @param parameter
	 * @param prefix
	 * @param annotation
	 * @param propertyFileName
	 * @return The line in the comment describing given parameter
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String formatParameterComment(Parameter parameter, String prefix,
			String annotation, String propertyFileName) {
		String comment = "";
		EList comments = new BasicEList();
		createAnnotationSourceKey(propertyFileName);
		for (Iterator iter = annotationSourceKeyMap.keySet().iterator(); iter
				.hasNext();) {
			String source = (String) iter.next();
			String key = annotationSourceKeyMap.get(source);
			comment = findDocumentationAnnotation(parameter, source, key);
			if (comment != null) {
				break;
			}
		}

		if (comment != null) {
			comments.add(comment);
		} else {
			comments = parameter.getOwnedComments();
		}

		if (comments.isEmpty()) {
			comment = prefix
					+ "@"
					+ annotation
					+ " "
					+ ((parameter.getDirection()
							.equals(ParameterDirectionKind.RETURN_LITERAL)) ? "<code>"
							+ parameter.getType().getName() + "</code>"
							: parameter.getName());
		} else {
			comment = getFormatedComment(
					comments,
					prefix,
					annotation
							+ " "
							+ ((parameter.getDirection()
									.equals(ParameterDirectionKind.RETURN_LITERAL)) ? ""
									: parameter.getName()));
		}
		return comment;
	}

	public String getDefaultValue(Property property) {
		ValueSpecification vs = property.getDefaultValue();

		if (vs == null) {
			if (property.getType() instanceof PrimitiveType) {
				PrimitiveType pt = (PrimitiveType) property.getType();
				if (pt.getName().equals("int")) {
					return "0";
				}
			}
			return "null";
		} else {
			if (vs.getType() == null) {
				return vs.stringValue();
			} else if (vs.getType().getName().equals("String")) {
				return vs.stringValue();
			} else if (vs.getType().getName().equals("Integer")) {
				return vs.integerValue() + "";
			} else if (vs.getType().getName().equals("Boolean")) {
				return vs.booleanValue() + "";
			}
			return vs.stringValue();
		}
	}

	private String getFormatedComment(EList<?> comments, String prefix,
			String annotation) {
		String s = "";

		if (!comments.isEmpty()) {
			for (int i = 0; i < comments.size(); i++) {
				Object commentsObject = comments.get(i);
				String text = "";
				if (commentsObject instanceof String) {
					text = (String) commentsObject;
				} else if (commentsObject instanceof Comment) {
					Comment c = (Comment) comments.get(i);
					text = c.getBody();
				}

				if (text != null && !"".equals(text)) {
					s = doFormatting(text, s, prefix, annotation);
				} else {
					s = "* ";
				}
			}
		} else {
			s = "* ";
		}
		return s;
	}

	private String doFormatting(String text, String appendTo, String prefix,
			String annotation) {
		if (!"".equals(appendTo)) {
			appendTo += "\n";
		}

		text = text.replaceAll("\\n", "\n" + prefix);
		appendTo += prefix;
		if (annotation != null && !"".equals(annotation)) {
			appendTo += "@" + annotation;
		}
		appendTo += " " + text;
		return appendTo;
	}

	/**
	 * This method takes a string and try's to cut it down into its peaces. The
	 * character given as delimiter will be used to determine the single peaces.
	 * 
	 * @param thwString
	 *            .
	 * @return theList
	 * @since 2.1.0
	 */
	public EList<String> convertStringToList(String theString, String delimiter) {
		if (delimiter == null || delimiter.equals("")) {
			delimiter = ",";
		}

		StringTokenizer tokenizer = new StringTokenizer(theString, delimiter);
		EList<String> theList = new UniqueEList<String>(tokenizer.countTokens());

		String currentToken = null;
		while (tokenizer.hasMoreTokens()) {
			currentToken = tokenizer.nextToken();
			if (StringUtils.isNotEmpty(currentToken)) {
				theList.add(currentToken.trim());
			}
		}

		return theList;
	}

	/**
	 * Get the list of template parameter substitution.
	 * 
	 * @param type
	 *            UML2 type
	 * @return List of all UML2 ParameterableElement
	 */
	public List<String> getTemplateParameterSubstitution(Type type) {
		List<String> results = new ArrayList<String>();
		// We need to take care of following cases:
		// -> Data::datatype-bindings::Collection<Company>
		// -> Data::de::crowdcode::kissmda::testapp::Collection<Person>
		// We need to have a full qualified name for the Type in
		// Collection<Type>. Something like
		// -> Data::datatype-bindings::Collection<de.test.Company>
		logger.log(Level.FINE,
				"getTemplateParameterSubstitution: " + type.getQualifiedName()
						+ " - " + type.getTemplateParameter());

		EList<Element> elements = type.allOwnedElements();
		for (Element element : elements) {
			if (element instanceof TemplateBinding) {
				TemplateBinding templateBinding = (TemplateBinding) element;
				EList<TemplateParameterSubstitution> subs = templateBinding
						.getParameterSubstitutions();
				for (TemplateParameterSubstitution templateParameterSubstitution : subs) {
					ParameterableElement paramElement = templateParameterSubstitution
							.getActual();
					if (paramElement instanceof Classifier) {
						Classifier clazzifier = (Classifier) paramElement;
						if (!dataTypeUtils
								.isPrimitiveType(clazzifier.getName())
								&& !dataTypeUtils.isJavaType(clazzifier
										.getName())) {
							results.add(clazzifier.getQualifiedName());
						} else {
							results.add(clazzifier.getName());
						}
					}
				}
			}
		}

		return results;
	}

	/**
	 * Check for parameterized type to get the template parameter substitution.
	 * 
	 * @param type
	 *            UML2 type
	 * @return Map of umlTypeName and umlQualifiedTypeName
	 */
	public Map<String, String> checkParameterizedTypeForTemplateParameterSubstitution(
			Type type) {
		Map<String, String> results = new HashMap<String, String>();

		List<String> templateSubstitutions = getTemplateParameterSubstitution(type);
		int index = 0;

		String umlTypeName = type.getName();
		String umlQualifiedTypeName = type.getQualifiedName();

		String paramTypeNames = StringUtils.substringAfter(umlTypeName, "<");
		paramTypeNames = StringUtils.removeEnd(paramTypeNames, ">");
		EList<String> paramTypeNameList = convertStringToList(paramTypeNames,
				",");

		for (String paramTypeName : paramTypeNameList) {
			umlTypeName = StringUtils.replace(umlTypeName, paramTypeName,
					templateSubstitutions.get(index));
			umlQualifiedTypeName = StringUtils.replace(umlQualifiedTypeName,
					paramTypeName, templateSubstitutions.get(index));
			index = index + 1;
		}

		results.put("umlTypeName", umlTypeName);
		results.put("umlQualifiedTypeName", umlQualifiedTypeName);

		return results;
	}
}
