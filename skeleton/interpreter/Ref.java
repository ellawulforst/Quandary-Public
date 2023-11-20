package interpreter;

import java.util.concurrent.atomic.AtomicLongArray;

public class Ref {

    Object left;
    Object right;
    boolean isNil;

    public Ref(Object left, Object right, boolean isNil) {
        this.left = left;
        this.right = right;
        this.isNil = isNil;
    }

    public Object getLeft() {
        return this.left;
    }

    public Object getRight() {
        return this.right;
    }

    public void setLeft(Object value) {
        this.left = value;
    }

    public void setRight(Object value) {
        this.right = value;
    }

    public long isNil() {
        if (this.isNil) {
            return 1;
        } else {
            return 0;
        }
    }

    public String toString() {
        String rightString = "" + this.right;
        String leftString = "" + this.left;
        if (this.isNil) {
            return "nil";
        }
        if (this.right == null) {
            rightString = "nil";
        }
        if (this.left == null) {
            leftString = "nil";
        }
        return "(" + leftString + " . " + rightString + ")";
    }


}
