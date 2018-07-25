package com.jcb.testController;

import com.jcb.annotation.Controller;
import com.jcb.annotation.RequestMapping;
import com.jcb.annotation.RequestParmas;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/index.jsp")
    public void index(HttpServletRequest request, HttpServletResponse response,@RequestParmas("name") String name){

        try {
            PrintWriter printWriter= response.getWriter();
            printWriter.write("index.jsp");
            printWriter.write("name:"+name);
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
