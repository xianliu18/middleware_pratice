package com.noodles.ch13.startup;

import com.noodles.ch13.core.SimpleContextConfig;
import org.apache.catalina.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.loader.WebappLoader;

import java.io.File;

/**
 * @Description 启动类
 * @Author liuxian
 * @Date 2023/11/28 11:41
 **/
public class Bootstrap1 {

    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "tomcat-webroot";

    public static void main(String[] args) {
        System.setProperty("catalina.base", WEB_ROOT);
        Connector connector = new HttpConnector();

        Wrapper wrapper1 = new StandardWrapper();
        wrapper1.setName("Primitive");
        wrapper1.setServletClass("PrimitiveServlet");
        Wrapper wrapper2 = new StandardWrapper();
        wrapper2.setName("Modern");
        wrapper2.setServletClass("ModernServlet");

        Context context = new StandardContext();
        context.setPath("/app1");
        context.setDocBase("app1");

        context.addChild(wrapper1);
        context.addChild(wrapper2);

        LifecycleListener listener = new SimpleContextConfig();
        ((Lifecycle)context).addLifecycleListener(listener);

        Host host = new StandardHost();
        host.addChild(context);
        host.setName("localhost");
        host.setAppBase("webapps");

        Loader loader = new WebappLoader();
        context.setLoader(loader);
        context.addServletMapping("/Primitive", "Primitive");
        context.addServletMapping("/Modern", "Modern");

        connector.setContainer(host);
        try {
            connector.initialize();
            ((Lifecycle)connector).start();
            ((Lifecycle)host).start();

            System.in.read();
            ((Lifecycle)host).stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
