package org.j2s;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class J2SCompiler {

    private final Class<?> aClass;
    private final J2SCompilationType compilationType;

    private final StringBuilder stringBuilder = new StringBuilder();
    private final List<J2SCompilationResult> results = new ArrayList<>();

    public J2SCompiler(Class<?> aClass, J2SCompilationType compilationType) {
        this.aClass = aClass;
        this.compilationType = compilationType;
    }

    public static List<J2SCompilationResult> compile(Class<?> aClass, J2SCompilationType compilationType) {
        if (aClass == null || compilationType == null)
            throw new RuntimeException();
        if (!isJ2SType(aClass))
            throw new RuntimeException();
        String fileName = getTypeName(aClass) + "." + compilationType.getFileExtension();
        File file = new File(fileName);
        J2SCompiler j2SCompiler = new J2SCompiler(aClass, compilationType);
        String compile = j2SCompiler.compile();
        J2SCompilationResult compilationResult = new J2SCompilationResult(aClass, file, compile);
        j2SCompiler.results.add(compilationResult);
        return j2SCompiler.results;
    }

    private static boolean isJ2SType(Class<?> aClass) {
        return aClass.isAnnotationPresent(J2SModel.class);
    }

    private static String getTypeName(Class<?> aClass) {
        J2SModel annotation = aClass.getAnnotation(J2SModel.class);
        return annotation.name().isEmpty() ? aClass.getSimpleName() : annotation.name();
    }

    private String compile() {
        stringBuilder.append(constructHead()).append("\n");
        for (Field field : aClass.getDeclaredFields()) {
            String s1 = fieldToString(field);
            stringBuilder.append(s1).append("\n");
        }
        stringBuilder.append(constructTail());
        return stringBuilder.toString();
    }

    private void putImport(Class<?> aClass) {
        if (compilationType == J2SCompilationType.TYPESCRIPT) {
            String typeName = getTypeName(aClass);
            String ns = "\n";
            if (!stringBuilder.substring(0, stringBuilder.indexOf("\n")).contains("import"))
                ns += "\n";
            String s = stringBuilder.toString();
            String s1;
            int index = 0;
            while ((s1 = s.substring(index, s.indexOf("\n"))).contains("import")) {
                if (s1.contains(typeName))
                    return;
                s = s.replace(s1, "");
                index = s.indexOf("\n");
            }
            stringBuilder.insert(0, String.format("import { %s } from \"./%s\";%s", typeName, typeName, ns));
        }
    }

    private String constructHead() {
        switch (compilationType) {
            default: throw new RuntimeException();
            case TYPESCRIPT:
                return String.format("export class %s {", getTypeName(aClass));
            case JAVASCRIPT:
                return String.format("class %s {", getTypeName(aClass));
        }
    }

    private String constructTail() {
        switch (compilationType) {
            default: throw new RuntimeException();
            case TYPESCRIPT:
            case JAVASCRIPT:
                return "}";
        }
    }

    private String fieldToString(Field field) {
        String fieldName = field.getName();
        Class<?> fieldType = field.getType();
        String fieldTypeName = fieldToString(fieldType);
        switch (compilationType) {
            default: throw new RuntimeException();
            case TYPESCRIPT:
                return "\t" + fieldName + ": " + fieldTypeName;
            case JAVASCRIPT:
                return "\t" + fieldName;
        }
    }

    private String fieldToString(Class<?> fieldType) {
        if (fieldType.isPrimitive() || fieldType.isAssignableFrom(String.class) || fieldType.isArray())
            return convertPrimitive(fieldType);
        else if(isJ2SType(fieldType)) {
            List<J2SCompilationResult> list = compile(fieldType, compilationType);
            list.stream().filter(j2SCompilationResult -> !results.contains(j2SCompilationResult)).forEach(results::add);
            putImport(fieldType);
            return getTypeName(fieldType);
        } else
            throw new RuntimeException();
    }

    private String convertPrimitive(Class<?> type) {
        if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(int.class)) {
            return "number";
        } else if (type.isAssignableFrom(Float.class) || type.isAssignableFrom(float.class)) {
            return "number";
        } else if (type.isAssignableFrom(Double.class) || type.isAssignableFrom(double.class)) {
            return "number";
        } else if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(boolean.class)) {
            return "boolean";
        } else if (type.isArray()) {
            Class<?> typeParameters = type.getComponentType();
            String s = fieldToString(typeParameters);
            return s + "[]";
        } else if (type.isAssignableFrom(String.class)) {
            return "string";
        }
        throw new RuntimeException();
    }
}
