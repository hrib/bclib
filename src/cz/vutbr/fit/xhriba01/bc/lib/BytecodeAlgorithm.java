package cz.vutbr.fit.xhriba01.bc.lib;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;


public final class BytecodeAlgorithm extends DefaultASTVisitor { 
	
	private Result fResult;
	
	private IClassContainer fClassContainer;
	
	private IFile fJavaFile;
	
	private Map<String, Object> fOptions;
	
	private CompilationUnit fCompilationUnit; 
	
	private Node fResultNode = new NodeRoot();
	
	private String fPackageName;
	
	private ClassCollector fClassCollector;
	
	private BytecodeAlgorithm() {

	}
	
	private void setOptions(Map<String, Object> options) {
		fOptions = options;
	}
	
	public static Result run(IFile javaFile, IClassContainer classContainer, Map<String, Object> options) {
		
		BytecodeAlgorithm algo = new BytecodeAlgorithm();
		
		algo.fJavaFile = javaFile;
		algo.fClassContainer = classContainer;
		
		algo.setOptions(options);
		
		algo.runInternal();
		
		return algo.fResult;
		
	} 
	
	@Override
	protected RootContext createRootContext() {
		RootContext rootCtx = super.createRootContext();
		rootCtx.setUserObject(fResultNode);
		return rootCtx;
	}
	
	private void runInternal() {
		
		Map options = JavaCore.getOptions();
		
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
		
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setCompilerOptions(options);
		
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		try {
			parser.setSource(IOUtils.toCharArray(fJavaFile.getContent(), "UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		fCompilationUnit = (CompilationUnit) parser.createAST(null);
		
		fClassCollector = new ClassCollector();
		
		fCompilationUnit.getRoot().accept(fClassCollector);
		
		fClassCollector.dump();
		/*
		PackageDeclaration packageDeclaration = fCompilationUnit.getPackage();
		
		if (packageDeclaration != null) {
			fPackageName = packageDeclaration.getName().getFullyQualifiedName();
			fPackageName = fPackageName.replace('.', '/');
		}
		*/
		/*
		ClassCollector collector = new ClassCollector();
		fCompilationUnit.getRoot().accept(collector);
		fCollCtx = collector.fRealRootCtx;
		//collector.dump();
		 */
		
		fCompilationUnit.getRoot().accept(this);
		
		JavaSourceResult javaResult = new JavaSourceResult();
		
		javaResult.setFilename(fJavaFile.getFilename());
		
		try {
			
			javaResult.setText(IOUtils.toString(fJavaFile.getContent()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		for(Context ctx : getRootContexts()) {
			Node node = (Node) ctx.getUserObject();
			if (node != null) {
				fResultNode.add(node);
			}
		}
		*/
		
		ClassSourceResult classResult = new ClassSourceResult(fResultNode);
		
		fResult = new Result(javaResult, classResult);
		
	}
	
	/*
	private Result runClass() {
		IClassItem selectedItem = (IClassItem)fSelectedItem;
		ClassReader classReader = new ClassReader(selectedItem.getContent());
		ClassNode classNode = new ClassNode(Opcodes.ASM5);
		classReader.accept(classNode, 0);
		fSourceItem = selectedItem.getResolver().getSourceItemForName(
				BinaryName.getPackage(classNode.name), 
				classNode.sourceFile);
		
		parse(); // go!!
		
		return makeResult();
	}
	*/
	
	@Override
	protected BlockContext createBlockContext() {
		return null;
	}
	
	@Override
	protected void onContextActivated(Context context) {
		
		if (context.isMethodContext()) {
			
			if (context.getParent().getUserObject() == null) {
				return;
			}
			
			setNodeMethod((MethodContext) context);
			
		}
		else if (context.isFieldContext()) {
			
			if (context.getParent().getUserObject() == null) {
				return;
			}
			
			setNodeField((FieldContext) context);
			
		}
		else if (context.isClassContext()) {
			
			if (context.getParent().getUserObject() == null) {
				return;
			}
			
			setNodeClass((ClassContext) context);
			
		}
		
	}
	
	@Override 
	protected void onContextDeactivated(Context context) {
		
		if (context.isClassContext()) {
			
		}
	}
	
	private void setNodeClass(ClassContext classContext) {
		
		BinaryName binaryName = classContext.getBinaryName();
		
		String filename = binaryName.getClassName() + ".class";
		
		IFile classFile = fClassContainer.getClassFile(filename);
		
		if (classFile == null) {
			// unable to find .class file in class container
			return;
		}
		
		ClassNode classNode = createClassNode(classFile);
		
		if (classNode == null) {
			// unable to create ASM ClassNode from IFile (shouldn't happen i think)
			return;
		}
		
		NodeClass nodeClass = new NodeClass(classNode);
		
		// now set Node* parent (parent Node should be in parent Context as user object)
		Context parentContext = classContext.getParent();
		
		//if (parentContext != null) {
		
		ClassContext parentClassContext = classContext.findParentContext(ClassContext.class);
		
		if (parentClassContext != null) {
			// class is somewhere in other class, needs to validate inner classes
			NodeClass parentNodeClass = (NodeClass) parentClassContext.getUserObject();
			InnerClassNode innerClassNode = parentNodeClass.getUnusedInnerClass(classNode.name);
			if (innerClassNode == null) {
				// parent class does not have this inner class
				return;
			}
			parentNodeClass.setAsUsed(innerClassNode);
		}
		
		Node parentNode = (Node) parentContext.getUserObject();
		parentNode.prepend(nodeClass);
		
		nodeClass.setFromClassContext(classContext);
		
		classContext.setUserObject(nodeClass);
		
		
		//Node parentNode = (Node) parentContext.getUserObject();
			
			//if (parentNode != null) {
				
		//parentNode.add(nodeClass);
			//}
		//}
		
	}
	
	private void setNodeField(FieldContext fieldContext) {
		
		
		NodeClass nodeClass = (NodeClass) fieldContext.getClassContext().getUserObject();
		
		/*
		if (nodeClass == null) {
			return;
		}
		*/
		
		NodeField nodeField = nodeClass.getUnusedNodeField(fieldContext.getName());
		
		if (nodeField == null) {
			// no field with this name found
			return;
		}
		
		nodeField.setFromFieldContext(fieldContext);
		
		nodeClass.setAsUsed(nodeField);
		
		fieldContext.setUserObject(nodeField);
	}
	
	private void setNodeMethod(MethodContext methodContext) {
		
		ClassContext classContext = methodContext.findParentContext(ClassContext.class);
		
		NodeClass nodeClass = (NodeClass) classContext.getUserObject();
		
		List<NodeMethod> sameNameMethods = new ArrayList<NodeMethod>();
 		
		for(NodeMethod nodeMethod : nodeClass.getUnusedMethods()) {
			
			MethodNode methodNode = nodeMethod.getAsmMethodNode();
			
			if (methodNode.name.equals(methodContext.getName().toString())) {
				
				sameNameMethods.add(nodeMethod);
			}
			else {
				
				if (methodNode.name.equals("<init>")) {
					
					if (methodContext.isConstructor()) sameNameMethods.add(nodeMethod); 
				}
				
			}
	
		}
		
		if (sameNameMethods.isEmpty()) {
			return;
		}
		
		/*
		if (sameNameMethods.size() != 1) {
			// more methods with same name
			return;
		}
		*/
		
		NodeMethod nodeMethod = sameNameMethods.get(0);
		
		methodContext.setUserObject(nodeMethod);
		
		nodeClass.setAsUsed(nodeMethod);
		
		if (sameNameMethods.size() == 1) {
			for (Node check : nodeClass) {
				if (check.isMethod()) {
					if (((NodeMethod) check).getAsmMethodNode().name.equals(nodeMethod.getAsmMethodNode().name)) {
						return;
					}
				}
			}
			// currently set line only if there is only one method 
			nodeMethod.setFromMethodContext(methodContext);
		}
		
		//BinaryNameResolver resolver =  new BinaryNameResolver(fClassCollector.getContext(classContext), fClassCollector.getImports());
		
		//MethodDescriptor maybeNonExactDesc = MethodDescriptor.fromMethodDeclaration(methodContext.getMethodDeclaration(), resolver);
		
		//for (NodeMethod nodeMethod : sameNameMethods) {
			
			/*
			MethodNode methodNode = nodeMethod.getAsmMethodNode();
			
			MethodDescriptor exactDesc = MethodDescriptor.fromAsmMethodDescriptor(methodNode.desc);
			
			int matchValue = exactDesc.match(maybeNonExactDesc, resolver);
			
			if (matchValue == 0) {
			*/
				//methodContext.setUserObject(nodeMethod);
				//nodeClass.setAsUsed(nodeMethod);
			//}
			
		//}
		
	}
	
	private ClassNode createClassNode(IFile classFile) {
		
		ClassReader classReader = null;
		
		try {
			
			classReader = new ClassReader(IOUtils.toByteArray(classFile.getContent()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		ClassNode classNode = new ClassNode(Opcodes.ASM5);
		
		classReader.accept(classNode, 0);
		
		return classNode;
	}
	
	/*
	private Result runClass(IClassItem classItem) {
		
		fCtxStack.add(new Context(classItem));
		NodeComposite rootNode = new NodeComposite();
		NodeComposite context = rootNode;
		do {
			Context currentContext = fCtxStack.pop();
			NodeClass classContext = currentContext.getNodeClass();
			IClassItem currentClassItem = currentContext.getClassItem();
			if (classContext != null) {
				context = classContext;
			}
			ClassReader classReader = new ClassReader(currentClassItem.getContent());
			ClassNode classNode = new ClassNode(Opcodes.ASM5);
			classReader.accept(classNode, 0);
			if (fSourceItem == null) {
				fSourceItem = currentClassItem.getResolver()
						.getSourceItemForName(Utils.getPackageFromClassName(classNode.name), classNode.sourceFile);
				ASTInformation info = new ASTInformation();
				info.collectInfo(fSourceItem);
			}
			NodeClass currentNodeClass = new NodeClass(classNode);
			//TODO: why i need to convert type? I need to attach sources or why it doesn't know that 
			// ClassNode.methods is List<MethodNode> type
			for (MethodNode methodNode : (List<MethodNode>)classNode.methods) {
				NodeMethod nodeMethod = new NodeMethod(methodNode);
				currentNodeClass.addChildNode(nodeMethod);
				for (Iterator<AbstractInsnNode> it = methodNode.instructions.iterator() ; it.hasNext(); ) {
					AbstractInsnNode insnNode = it.next();
					NodeInstruction nodeInstruction = new NodeInstruction(insnNode);
					nodeMethod.addChildNode(nodeInstruction);
				}
			}
			for (InnerClassNode innerClassNode : (List<InnerClassNode>)classNode.innerClasses) {
				if (innerClassNode.outerName == classNode.name) {
					IClassItem innerClassItem = currentClassItem.getResolver()
							.getClassItemForName(Utils.getPackageFromClassName(innerClassNode.name), 
									Utils.getClassFileFromClassName(innerClassNode.name));
					fCtxStack.push(new Context(innerClassItem, currentNodeClass));
				}
			}
			context.addChildNode(currentNodeClass);
		} while (!fCtxStack.isEmpty());
		
		return new Result(
				new JavaSourceResult(new String(fSourceItem.getContent())), 
				new ClassSourceResult(rootNode));
	}
	*/
	
	/*
	private Result makeResult() {
		byte[] source = fSourceItem.getContent();
		fJavaResult.setText(new String(source));
		fJavaResult.setFilename(fSourceItem.getName());
		fClassResult.setResultNode(fResultNode);
		return new Result(fJavaResult, fClassResult);
	}
	*/
	
	/*
	private ClassContext newContext(String declarationName) {
		
		ClassContext parentCtx = fCurrentCtx;
		
		NodeMethod currentMethod = null;
		
		if (parentCtx != null) {
			currentMethod = parentCtx.getActiveNodeMethod();
		}
		
		ClassContext newCtx = new ClassContext();
		
		if (currentMethod != null) {
			//novy typ v metode
			int fromCollMethodIndex = parentCtx.fClassInMethodCounter++;
			newCtx.fCollClassCtx = parentCtx.fCollClassCtx.fMethodsCtx.get(parentCtx.fMethodCounter).fClassCtx.get(fromCollMethodIndex);
			newCtx.fClassInMethodPosition = fromCollMethodIndex;
		}
		else {
			if (parentCtx == null) {
				newCtx.fCollClassCtx = fCollCtx.fClassCtx.get(fCollCounter++);
			}
			else {
				newCtx.fCollClassCtx = parentCtx.fCollClassCtx.fClassCtx.get(parentCtx.fClassCounter++);
			}
		}
		
		String newName = null;
		
		if (parentCtx != null) {
			
			if (declarationName == null) {
				// anonymous class
				newName = BinaryName.formatAnonymClassName(parentCtx.getName(), parentCtx.getNextAnonymInc());
			}
			else {
				
				if (currentMethod != null) {
					// local class
					newName = BinaryName.formatLocalClassName(parentCtx.getName(), parentCtx.getNextLocalInc(declarationName), declarationName);
				}
				else {
					// static nested class or inner class
					newName = BinaryName.formatInnerOrStaticNestedClassName(parentCtx.getName(), declarationName);
				}
			}
		}
		else {
			
			newName = declarationName;
			
		}
		
		newCtx.setName(newName);
		
		newCtx.setParentContext(parentCtx);(parentCtx);
		
		newCtx.loadContext();
		
		if (currentMethod != null) {
			String outerMethod = newCtx.getNodeClass().getAsmClassNode().outerMethod;
			String outerDesc = newCtx.getNodeClass().getAsmClassNode().outerMethodDesc;
			currentMethod.addChildNode(newCtx.getNodeClass());
		}
		else {
			if (parentCtx != null) {
				parentCtx.getNodeClass().addChildNode(newCtx.getNodeClass());
			}
			else {
				fResultNode.addChildNode(newCtx.getNodeClass());
			}
		}
		
		return newCtx;
	}
	
	private class ClassContext {
		
		private Map<String, Integer> fNextLocals;
		
		private int fNextAnonym = 1; 
		
		private int fClassCounter = 0;
		
		private int fMethodCounter = 0;
		
		private int fClassInMethodCounter = 0;
		
		private int fClassInMethodPosition = -1;
		
		private String fName;
		
		private NodeClass fNodeClass;
		
		private NodeMethod fNodeMethod;
		
		private NodeField fNodeField;
		
		private ClassContext fParentCtx;
		
		private ClassCollector.CollectorClassContext fCollClassCtx;
		
		private NodeMethod getActiveNodeMethod() {
			return fNodeMethod;
		}
		
		private NodeField getActiveNodeField() {
			return fNodeField;
		}
		
		private int getNextLocalInc(String declarationName) {
			if (fNextLocals == null) {
				fNextLocals = new HashMap<>();
			}
			
			Integer val = fNextLocals.get(declarationName);
			
			if (val == null) {
				fNextLocals.put(declarationName, 1);
				return 1;
			}
			else {
				fNextLocals.put(declarationName, val+1);
				return val++;
			}
		}
		
		private void loadContext() {
			System.out.println("#####beforeeeee");
			System.out.println(fName);
			System.out.println(fPackageName);
			
			IFile classFile = fClassContainer.getClassFile(fName);
			
			try {
				
				ClassReader classReader = new ClassReader(IOUtils.toByteArray(classFile.getContent()));
				ClassNode classNode = new ClassNode(Opcodes.ASM5);
				
				classReader.accept(classNode, 0);
				
				fNodeClass = new NodeClass(classNode);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		private int getNextAnonymInc() {
			return fNextAnonym++;
		}
		
		private void setParentContext(ClassContext parentCtx) {
			fParentCtx = parentCtx;
		}
		
		private ClassContext getParentContext() {
			return fParentCtx;
		}
		
		private String getName() {
			return fName;
		}
		
		private void setName(String name) {
			fName = name;
		}
		
		private NodeClass getNodeClass() {
			return fNodeClass;
		}
		
	}
	
	*/
}
