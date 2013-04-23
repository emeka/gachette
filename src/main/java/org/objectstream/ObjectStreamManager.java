package org.objectstream;

import org.objectstream.simple.DefaultObjectStreamImpl;

public class ObjectStreamManager {
    public static ObjectStream create(){
        return new DefaultObjectStreamImpl();
    }
}
