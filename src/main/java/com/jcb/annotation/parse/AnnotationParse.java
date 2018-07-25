package com.jcb.annotation.parse;


import com.jcb.annotation.Controller;
import com.jcb.annotation.RequestMapping;

import java.io.File;
 import java.lang.reflect.Method;
 import java.net.URL;
import java.util.*;

public  class  AnnotationParse {

    public final static Map<String ,Object> context= new HashMap<>();

    public final static Map<String ,Method> hanldeMapping= new HashMap<>();

    public final static Map<String ,Object> controllerMapping= new HashMap<>();

    public final static List<String> ClassNames=new ArrayList<>();

    public static  void init(Properties properties){

        String packageName= properties.getProperty("scan.package");
        String path= packageName.replaceAll("\\.","/");

        URL url= AnnotationParse.class.getClassLoader().getResource("/"+path);
        File file=new File( url.getFile());
        if(file.isDirectory()){

            List<String> ClassName= ScanPackage(file,packageName);
            ClassNames.addAll(ClassName);

            instance();

            initHolderMapping();

        }

        System.out.println(path);

    }

    private static List<String>  ScanPackage(File file,String packageName){
        List<String> list = new ArrayList<>();
        for (File file1 : file.listFiles()) {
            if(file1.isDirectory()){

                String tempStr= file1.getPath().replaceAll("/","\\.");
                String tempPackageName= tempStr.substring(tempStr.indexOf(packageName));

                list.addAll(ScanPackage(file1,packageName));
            }else{
               if(file1.getName().endsWith(".class")){
                   list.add(packageName+"."+file1.getName().replace(".class",""));
               }
            }
        }


        return list;
    }


    private static void instance(){
        ClassNames.forEach(className->{
            try {
                Class<?> clazz = Class.forName(className);
                if(clazz.isAnnotationPresent(Controller.class)){
                    Object object= clazz.newInstance();
                    context.put(object.getClass().getName(),object);
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        });


    }

    private static void  initHolderMapping(){
        context.values().forEach(obj->{
            Class<?> clazz= obj.getClass();
            String baseUrl="";

            if(clazz.isAnnotationPresent(RequestMapping.class)){
                RequestMapping requestMapping= clazz.getAnnotation(RequestMapping.class);
                baseUrl=requestMapping.value();
            }

            for (Method method : clazz.getMethods()) {
                if(method.isAnnotationPresent(RequestMapping.class)){
                    String  url=baseUrl;
                    RequestMapping requestMapping= method.getAnnotation(RequestMapping.class);
                    url+=requestMapping.value();
                    hanldeMapping.put(url,method);
                    try {
                        controllerMapping.put(url,clazz.newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }

        });

    }


}
