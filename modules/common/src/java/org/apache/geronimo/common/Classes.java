/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.geronimo.common;

import java.beans.PropertyEditor;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.geronimo.common.propertyeditor.PropertyEditors;

/**
 * A collection of <code>Class</code> utilities.
 *
 * @version $Revision: 1.10 $ $Date: 2004/02/25 09:57:02 $
 */
public class Classes {
    private static final Class[] stringArg = new Class[]{String.class};

    /////////////////////////////////////////////////////////////////////////
    //                              Primitives                             //
    /////////////////////////////////////////////////////////////////////////

    /** Primitive type name -> class map. */
    private static final Map PRIMITIVES = new HashMap();

    /** Setup the primitives map. */
    static {
        PRIMITIVES.put("boolean", Boolean.TYPE);
        PRIMITIVES.put("byte", Byte.TYPE);
        PRIMITIVES.put("char", Character.TYPE);
        PRIMITIVES.put("short", Short.TYPE);
        PRIMITIVES.put("int", Integer.TYPE);
        PRIMITIVES.put("long", Long.TYPE);
        PRIMITIVES.put("float", Float.TYPE);
        PRIMITIVES.put("double", Double.TYPE);
        PRIMITIVES.put("void", Void.TYPE);
    }

    /**
     * Get the primitive type for the given primitive name.
     *
     * @param name    Primitive type name (boolean, byte, int, ...)
     * @return        Primitive type or null.
     */
    public static Class getPrimitiveType(final String name) {
        return (Class) PRIMITIVES.get(name);
    }

    /** VM primitive type name -> primitive type */
    private static final HashMap VM_PRIMITIVES = new HashMap();

    /** Setup the vm primitives map. */
    static {
        VM_PRIMITIVES.put("B", byte.class);
        VM_PRIMITIVES.put("C", char.class);
        VM_PRIMITIVES.put("D", double.class);
        VM_PRIMITIVES.put("F", float.class);
        VM_PRIMITIVES.put("I", int.class);
        VM_PRIMITIVES.put("J", long.class);
        VM_PRIMITIVES.put("S", short.class);
        VM_PRIMITIVES.put("Z", boolean.class);
        VM_PRIMITIVES.put("V", void.class);
    }

    /** VM primitive type primitive type ->  name  */
    private static final HashMap VM_PRIMITIVES_REVERSE = new HashMap();

    /** Setup the vm primitives reverse map. */
    static {
        VM_PRIMITIVES_REVERSE.put(byte.class, "B");
        VM_PRIMITIVES_REVERSE.put(char.class, "C");
        VM_PRIMITIVES_REVERSE.put(double.class, "D");
        VM_PRIMITIVES_REVERSE.put(float.class, "F");
        VM_PRIMITIVES_REVERSE.put(int.class, "I");
        VM_PRIMITIVES_REVERSE.put(long.class, "J");
        VM_PRIMITIVES_REVERSE.put(short.class, "S");
        VM_PRIMITIVES_REVERSE.put(boolean.class, "Z");
        VM_PRIMITIVES_REVERSE.put(void.class, "V");
    }

    /**
     * Get the primitive type for the given VM primitive name.
     *
     * <p>Mapping:
     * <pre>
     *   B - byte
     *   C - char
     *   D - double
     *   F - float
     *   I - int
     *   J - long
     *   S - short
     *   Z - boolean
     *   V - void
     * </pre>
     *
     * @param name    VM primitive type name (B, C, J, ...)
     * @return        Primitive type or null.
     */
    public static Class getVMPrimitiveType(final String name) {
        return (Class) VM_PRIMITIVES.get(name);
    }

    /** Map of primitive types to their wrapper classes */
    private static final Map PRIMITIVE_WRAPPERS = new HashMap();

    /** Setup the wrapper map. */
    static {
        PRIMITIVE_WRAPPERS.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_WRAPPERS.put(Byte.TYPE, Byte.class);
        PRIMITIVE_WRAPPERS.put(Character.TYPE, Character.class);
        PRIMITIVE_WRAPPERS.put(Double.TYPE, Double.class);
        PRIMITIVE_WRAPPERS.put(Float.TYPE, Float.class);
        PRIMITIVE_WRAPPERS.put(Integer.TYPE, Integer.class);
        PRIMITIVE_WRAPPERS.put(Long.TYPE, Long.class);
        PRIMITIVE_WRAPPERS.put(Short.TYPE, Short.class);
        PRIMITIVE_WRAPPERS.put(Void.TYPE, Void.class);
    }

    /**
     * Get the wrapper class for the given primitive type.
     *
     * @param type    Primitive class.
     * @return        Wrapper class for primitive.
     *
     * @exception IllegalArgumentException    Type is not a primitive class
     */
    public static Class getPrimitiveWrapper(final Class type) {
        if (type == null) {
            throw new NullArgumentException("type");
        }
        if (!type.isPrimitive()) {
            throw new IllegalArgumentException("type is not a primitive class");
        }

        Class wtype = (Class) PRIMITIVE_WRAPPERS.get(type);

        // If wrapper type is null then the map is not valid & should be updated
        assert wtype != null;

        return wtype;
    }

    /**
     * Check if the given class is a primitive wrapper class.
     *
     * @param type    Class to check.
     * @return        True if the class is a primitive wrapper.
     */
    public static boolean isPrimitiveWrapper(final Class type) {
        if (type == null) {
            throw new NullArgumentException("type");
        }

        return PRIMITIVE_WRAPPERS.containsKey(type);
    }

    /**
     * Check if the given class is a primitive class or a primitive
     * wrapper class.
     *
     * @param type    Class to check.
     * @return        True if the class is a primitive or primitive wrapper.
     */
    public static boolean isPrimitive(final Class type) {
        if (type == null) {
            throw new NullArgumentException("type");
        }
        if (type.isPrimitive() || isPrimitiveWrapper(type)) {
            return true;
        }

        return false;
    }


    /////////////////////////////////////////////////////////////////////////
    //                            Class Loading                            //
    /////////////////////////////////////////////////////////////////////////

    /**
     * This method acts equivalently to invoking
     * <code>Thread.currentThread().getContextClassLoader()</code>.
     *
     * @return The thread context class Loader.
     */
    public static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * Load a class for the given name using the context class loader.
     *
     * @see #loadClass(String,ClassLoader)
     *
     * @param className    The name of the Class to be loaded.
     * @return             The Class object for the given name.
     *
     * @throws ClassNotFoundException   Failed to load Class object.
     */
    public static Class loadClass(final String className) throws ClassNotFoundException {
        return loadClass(className, getContextClassLoader());
    }

    /**
     * Load a class for the given name.
     *
     * <p>Handles loading primitive types as well as VM class and array syntax.
     *
     * @param className     The name of the Class to be loaded.
     * @param classLoader   The class loader to load the Class object from.
     * @return              The Class object for the given name.
     *
     * @throws ClassNotFoundException   Failed to load Class object.
     */
    public static Class loadClass(final String className, final ClassLoader classLoader)
            throws ClassNotFoundException {
        if (className == null) {
            throw new NullArgumentException("className");
        }
        if (classLoader == null) {
            throw new NullArgumentException("classLoader");
        }

        // First just try to load
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException ignore) {
            // handle special cases below
        }

        Class type = null;

        // Check if it is a primitive type
        type = getPrimitiveType(className);
        if (type != null) return type;

        // Check if it is a vm primitive
        type = getVMPrimitiveType(className);
        if (type != null) return type;

        // Handle VM class syntax (Lclassname;)
        if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
            return classLoader.loadClass(className.substring(1, className.length() - 1));
        }

        // Handle VM array syntax ([type)
        if (className.charAt(0) == '[') {
            int arrayDimension = className.lastIndexOf('[') + 1;
            String componentClassName = className.substring(arrayDimension, className.length());
            type = loadClass(componentClassName, classLoader);

            int dim[] = new int[arrayDimension];
            java.util.Arrays.fill(dim, 0);
            return Array.newInstance(type, dim).getClass();
        }

        // Handle user friendly type[] syntax
        if (className.endsWith("[]")) {
            // get the base component class name and the arrayDimensions
            int arrayDimension = 0;
            String componentClassName = className;
            while (componentClassName.endsWith("[]")) {
                componentClassName = componentClassName.substring(0, componentClassName.length() - 2);
                arrayDimension++;
            }

            // load the base type
            type = loadClass(componentClassName, classLoader);

            // return the array type
            int[] dim = new int[arrayDimension];
            java.util.Arrays.fill(dim, 0);
            return Array.newInstance(type, dim).getClass();
        }

        // Else we can not load (give up)
        throw new ClassNotFoundException(className);
    }

    /**
     */
    public static String getClassName(Class clazz) {
        StringBuffer rc = new StringBuffer();
        while (clazz.isArray()) {
            rc.append('[');
            clazz = clazz.getComponentType();
        }
        if (!clazz.isPrimitive()) {
            rc.append('L');
            rc.append(clazz.getName());
            rc.append(';');
        } else {
            rc.append(VM_PRIMITIVES_REVERSE.get(clazz));
        }
        return rc.toString();
    }

    public static Object getValue(Class type, String value, URI baseURI) {
        StringValueParser parser = new StringValueParser();
        value = parser.parse(value);

        if (URI.class.equals(type)) {
            return baseURI.resolve(value);
        }
        if (URL.class.equals(type)) {
            try {
                return baseURI.resolve(value).toURL();
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Value is not a valid URI: value=" + value);
            }
        }
        if (File.class.equals(type)) {
            return new File(baseURI.resolve(value));
        }

        return getValue(type, value);
    }

    public static Object getValue(Class type, String value) {
        StringValueParser parser = new StringValueParser();
        value = parser.parse(value);

        // try a property editor
        PropertyEditor editor = PropertyEditors.findEditor(type);
        if (editor != null) {
            editor.setAsText(value);
            return editor.getValue();
        }

        // try a String constructor
        try {
            Constructor cons = type.getConstructor(stringArg);
            return cons.newInstance(new Object[]{value});
        } catch (Exception e) {
            throw new CoercionException("Type does not have a registered property editor or a String constructor:" +
                    " type=" + type.getName());
        }
    }

    public static Object getValue(ClassLoader cl, String typeName, String value) throws ClassNotFoundException {
        Class type = null;
        type = loadClass(typeName, cl);
        return getValue(type, value);
    }

    public static Object getValue(ClassLoader cl, String typeName, String value, URI baseURI) throws ClassNotFoundException {
        Class type = null;
        type = loadClass(typeName, cl);
        return getValue(type, value, baseURI);
    }
    
    static public Method getMethod(Class source, String name) {
        Method[] methods = source.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if( method.getName().equals(name) )
                return method;
        }
        throw new RuntimeException("Method name not found: "+name+", in class: "+source.getName());
    }

    static public Method getMethod(Class source, String name, Class args[] ) {
        try {
            return  source.getMethod(name, args);
        } catch (SecurityException e) {
            throw new RuntimeException("Method could not be found.", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Method could not be found.", e);
        }
    }
    
}

