package org.gachette.benchmark.model;

import java.util.ArrayList;
import java.util.Collection;

public class Node {
    private long value;
    private Collection<Node> children = new ArrayList<>();

    public long getResult(){
        long result = value;
        for(Node child : children){
            result += child.getValue();
        }
/*        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {}*/

        return result;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public Collection<Node> getChildren() {
        return children;
    }

    public void setChildren(Collection<Node> children) {
        this.children = children;
    }

    public void addChild(Node child){
        children.add(child);
    }
}
