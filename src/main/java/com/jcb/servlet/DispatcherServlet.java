package com.jcb.servlet;

import com.jcb.annotation.RequestParmas;
import com.jcb.annotation.parse.AnnotationParse;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DispatcherServlet extends HttpServlet {

    public DispatcherServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        super.init();

        String params= config.getInitParameter("configrationContext");

        System.out.println("config = [" + config + "]");
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(params);

        Properties properties= new Properties();
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AnnotationParse.init(properties);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doGet(req, resp);

        doDispatch( req,  resp);


    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        doDispatch( req,  resp);
    }


    private void doDispatch(HttpServletRequest request,HttpServletResponse response){

        String uri= request.getRequestURI();
        String contextPath= request.getContextPath();
        uri=uri.replace(contextPath,"");

        Method hanlderMapping = AnnotationParse.hanldeMapping.get(uri);
        Object controllerMapping= AnnotationParse.controllerMapping.get(uri);
        List params = new ArrayList();

        if(hanlderMapping!=null){
            for (Parameter parameter : hanlderMapping.getParameters()) {
                Class<?> aClass= parameter.getType();

                if(aClass.isAssignableFrom(HttpServletRequest.class)){
                    params.add(request);
                } else if(aClass.isAssignableFrom(HttpServletResponse.class)){
                    params.add(response);
                }else{
                    if(parameter.getAnnotation(RequestParmas.class)!=null) {
                        RequestParmas requestParmas = (RequestParmas) parameter.getAnnotation(RequestParmas.class);
                        String requestParam= request.getParameter(requestParmas.value());
                        params.add(requestParam);
                    }else{
                        params.add(null);
                    }
                }
            }

            try {

                hanlderMapping.invoke(controllerMapping,params.toArray());

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }





    }

}
