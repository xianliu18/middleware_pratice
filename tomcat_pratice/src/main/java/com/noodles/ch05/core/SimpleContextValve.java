package com.noodles.ch05.core;

import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;
import org.apache.catalina.Wrapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description
 * @Author liuxian
 * @Date 2023/11/9 18:09
 **/
public class SimpleContextValve implements Valve, Contained {

    protected Container container;

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public void invoke(Request request, Response response, ValveContext valveContext) throws IOException, ServletException {
        // validate the request and response object types
        if (!(request.getRequest() instanceof HttpServletRequest) ||
            !(response.getResponse() instanceof HttpServletResponse)) {
            return;
        }

        // Disallow any direct access to resources under WEB-INF or META-INF
        HttpServletRequest hreq = (HttpServletRequest)request.getRequest();
        String contextPath = hreq.getContextPath();
        String requestURI = ((HttpRequest)request).getDecodedRequestURI();
        String relativeURI = requestURI.substring(contextPath.length()).toUpperCase();

        Context context = (Context) getContainer();
        // Select the Wrapper to be used for this Request
        Wrapper wrapper = null;
        try {
            wrapper = (Wrapper)context.map(request, true);
            System.out.println("step4 准备映射到具体的 wrapper " + relativeURI);
        } catch (IllegalArgumentException e) {
            badRequest(requestURI, (HttpServletResponse)response.getResponse());
            return;
        }
        if (wrapper == null) {
            notFound(requestURI, (HttpServletResponse)response.getResponse());
            return;
        }
        // Ask this wrapper to process this request
        response.setContext(context);
        wrapper.invoke(request, response);
    }

    private void badRequest(String requestURI, HttpServletResponse response) {
        try {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, requestURI);
        } catch(IllegalStateException e) {
            ;
        } catch (IOException e) {
            ;
        }
    }

    private void notFound(String requestURI, HttpServletResponse response) {
        try {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, requestURI);
        } catch (IllegalStateException e) {
            ;
        } catch (IOException e) {
            ;
        }
    }
}
