package tinycc.implementation.type;

import java.util.List;

import tinycc.implementation.expression.Expression;
import tinycc.mipsasmgen.MemoryInstruction;


/**
 * The main type class (see project description)
 *
 * You can change this class but the given name of the class must not be
 * modified.
 */
public abstract class Type {

	/**
	 * Creates a string representation of this type.
	 *
	 * @remarks See project documentation.
	 * @see StringBuilder
	 */
	@Override
	public abstract String toString();

	// type checkers

	public boolean isFunctionType() {
		return this instanceof FunctionType;
	}

	public boolean isObjectType() {
		return this.isCompleteType() || this.isVoidType();
	}

	public boolean isVoidType() {
		return this instanceof VoidType;
	}

	public boolean isCompleteType() {
		return this.isPointerType() || this.isIntegerType();
	}

	public boolean isScalarType() {
		return this.isPointerType() || this.isIntegerType();
	}

	public boolean isPointerType() {
		return this instanceof PointerType;
	}

	public boolean isPointerToCompleteType() {
		if(this.isPointerType()) {
			return ((PointerType) this).getPointsTo().isCompleteType();
		} else {
			return false;
		}
	}

	public boolean isPointerToChar() {
		if(this.isPointerType()) {
			return ((PointerType) this).getPointsTo().isCharType();
		} else {
			return false;
		}
	}

	public boolean isPointerToVoid () {
		if(this.isPointerType()) {
			return ((PointerType) this).getPointsTo().isVoidType();
		} else {
			return false;
		}
	}

	public boolean isIntegerType() {
		return this instanceof IntegerType;
	}

	public boolean isIntType() {
		return this instanceof IntType;
	}

	public boolean isCharType() {
		return this instanceof CharType;
	}

	// type factories
	
	public static Type getIntType() {
		return new IntType();
	}

	public static Type getCharType() {
		return new CharType();
	}

	public static Type getVoidType() {
		return new VoidType();
	}

	public static Type getPointerType(Type pointsTo) {
		return new PointerType(pointsTo);
	}

	public static Type getFunctionType(Type returnType, List<Type> parameters) {
		return new FunctionType(returnType, parameters);
	}

	public static Type getErrorType() {
		return null;
	}

	// type pseudo methods

	public boolean isAssignableFrom(Expression right) {
		Type rightTy = right.getType();
		if (this.isScalarType() && rightTy.isScalarType()) {
			// identical type
			if (this.equals(rightTy)) {
				return true;
			} 
			// both integer_type
			else if (this.isIntegerType() && rightTy.isIntegerType()) {
				return true;
			} 
			// both pointer_type and at least one is void*
			else if (this.isPointerType() && rightTy.isPointerType() 
				   && (this.isPointerToVoid() || rightTy.isPointerToVoid())) {
				return true;
			} 
			// left is pointer and right is NULL
			else if (this.isPointerType() && right.isNullPointer()) {
				return true;
			}
		}
		return false;
	}

	/*public void generateMove(MipsAsmGen out, GPRegister resReg, GPRegister inputReg) {
		out.emitInstruction(RegisterInstruction.OR, resReg, inputReg, GPRegister.ZERO);
	}*/
	
	public MemoryInstruction getStoreInsn() {
		if(this.isCharType()) {
			return MemoryInstruction.SB;
		} else {
			return MemoryInstruction.SW;
		}
	}

	public MemoryInstruction getLodeInsn() {
		if(this.isCharType()) {
			return MemoryInstruction.LB;
		} else {
			return MemoryInstruction.LW;
		}
	}

	public int getSize() {
		if(this.isCharType()) {
			return 1;
		} else {
			return 4;
		}
	}

	
	public static Type clone(Type ty) {
		if(ty instanceof IntType) {
			return getIntType();
		} else if (ty instanceof CharType) {
			return getCharType();
		} else if (ty instanceof VoidType) {
			return getVoidType();
		} else if (ty instanceof PointerType) {
			return getPointerType(((PointerType) ty).getPointsTo());
		} else if (ty instanceof FunctionType) {
			FunctionType funTy = (FunctionType) ty;
			return getFunctionType(funTy.getReturnType(), funTy.getParameters());
		} else {
			return null;
		}
	}
	
}
