package org.objectstream.transaction;


import org.objectstream.value.Value;

import java.util.Stack;

public class DependencyContext {
    public static final ThreadLocal<Stack<Value>> callStack= new ThreadLocal<Stack<Value>>(){
        @Override
        protected Stack<Value> initialValue(){
            return new Stack<>();
        }
    };

    public static boolean empty(){
        return callStack.get().empty();
    }

    public static void push(Value value){
        callStack.get().push(value);
    }

    public static Value top(){
        return callStack.get().peek();
    }

    public static Value pop(){
        return callStack.get().pop();
    }

    public static void clear(){
        callStack.get().clear();
    }
}
