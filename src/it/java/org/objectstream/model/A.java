package org.objectstream.model;

public class A {
    private B b;
    private long value;

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getValue(){
        return this.value;
    }

    public long getResult() {
        return value + b.getValue();
    }
}
