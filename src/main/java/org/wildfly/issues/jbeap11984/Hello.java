package org.wildfly.issues.jbeap11984;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.cxf.interceptor.InInterceptors;

@InInterceptors(classes = { org.wildfly.issues.jbeap11984.EventHandlerInjector.class })
@WebService
public class Hello {

    @WebMethod
    public String hello(String msg, long times) {
        return "Hello " + msg + " " + times + " times";
    }
}
