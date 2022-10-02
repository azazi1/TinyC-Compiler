package tinycc.implementation.type;

public class PointerType extends ObjectType {
    
    private Type pointsTo;

    public PointerType(Type pointsTo) {
        this.pointsTo = pointsTo;
    }

    public Type getPointsTo() {
        return pointsTo;
    }

    @Override
    public String toString() {
        return "Pointer["+pointsTo.toString()+"]";
    }

    @Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PointerType))
            return false;
        else {
            return this.pointsTo.equals(((PointerType)obj).getPointsTo());
        }

	}

    @Override
    public int hashCode() {
        return pointsTo.hashCode() + 100;
    }

    /*@Override
    public void clone(Type ty) {
        this.pointsTo.clone(((PointerType)ty).getPointsTo());
    }*/

}
