package com.yw.compiler;

import javassist.*;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @Author yangwei
 * @create 2020/11/23 17:22
 */
public class JavassistCompiler {
    public static void main(String[] args) throws Exception {
        // 创建一个ClassPool实例，其是CtClass的工具类
        ClassPool pool = ClassPool.getDefault();
        // CtClass：Class Type Class，即字节码类型的类
        CtClass ctClass = genericClass(pool);
        // 创建一个自动生成类的实例，并调用其业务方法
        invokeInstance(ctClass);
    }

    private static CtClass genericClass(ClassPool pool) throws Exception {
        // 通过ClassPool生成一个Person类
        CtClass ctClass = pool.makeClass("com.yw.Person");
        // 下面的代码都是初始化CtClass实例的

        // 向CtClass中添加private String name属性
        CtField nameField = new CtField(pool.getCtClass("java.lang.String"), "name", ctClass);
        nameField.setModifiers(Modifier.PRIVATE);
        ctClass.addField(nameField);
        // 向CtClass中添加private int age属性
        CtField ageField = new CtField(pool.getCtClass("int"), "age", ctClass);
        ageField.setModifiers(Modifier.PRIVATE);
        ctClass.addField(ageField);
        // 向CtClass中添加name、age属性的setter与getter方法
        ctClass.addMethod(CtNewMethod.getter("getName", nameField));
        ctClass.addMethod(CtNewMethod.getter("setName", nameField));
        ctClass.addMethod(CtNewMethod.getter("getAge", ageField));
        ctClass.addMethod(CtNewMethod.getter("setAge", ageField));
        // 向CtClass中添加无参构造器
        CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);
        String body = "{\nname=\"zhangsan\";\nage=23;\n}";
        ctConstructor.setBody(body);
        ctClass.addConstructor(ctConstructor);
        // 向CtClass中添加业务方法
        CtMethod ctMethod = new CtMethod(CtClass.voidType, "personInfo", new CtClass[]{}, ctClass);
        ctMethod.setModifiers(Modifier.PUBLIC);
        StringBuffer sb = new StringBuffer();
        sb.append("{\nSystem.out.println(\"name=\"+name);\n")
                .append("System.out.println(\"age=\"+age);\n}");
        ctMethod.setBody(sb.toString());
        ctClass.addMethod(ctMethod);
        // 将生成的字节码写入到.class文件
        byte[] bytes = ctClass.toBytecode();
        try (FileOutputStream fos = new FileOutputStream(new File("D:/Person.class"))) {
            fos.write(bytes);
        }
        return ctClass;
    }

    private static void invokeInstance(CtClass ctClass) throws Exception {
        Class<?> clazz = ctClass.toClass();
        Object obj = clazz.newInstance();
        obj.getClass().getMethod("personInfo", new Class[]{}).invoke(obj, new Object[]{});
    }
}
