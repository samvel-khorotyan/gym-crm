package com.gymcrm;

import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class MainApplication {
	public static void main(String[] args) throws Exception {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.setConfigLocation("com.gymcrm.configuration");

		Tomcat tomcat = new Tomcat();
		tomcat.setPort(8080);
		tomcat.getConnector();

		DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

		String contextPath = "";
		String docBase = new java.io.File(".").getAbsolutePath();

		var tomcatContext = tomcat.addContext(contextPath, docBase);

		Tomcat.addServlet(tomcatContext, "dispatcher", dispatcherServlet).setLoadOnStartup(1);
		tomcatContext.addServletMappingDecoded("/", "dispatcher");

		tomcatContext.addWelcomeFile("swagger-ui/index.html");

		tomcat.start();
		tomcat.getServer().await();
	}
}
