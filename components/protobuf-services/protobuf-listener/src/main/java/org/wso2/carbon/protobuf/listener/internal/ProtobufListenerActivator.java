package org.wso2.carbon.protobuf.listener.internal;

import javax.servlet.ServletContainerInitializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.wso2.carbon.protobuf.listener.ProtobufServletContainerInitializer;

public class ProtobufListenerActivator implements BundleActivator{
	
	private static final Log log = LogFactory.getLog(ProtobufListenerActivator.class);
			
	@Override
	public void start(BundleContext bundleContext){
		if (log.isDebugEnabled()) {
			log.debug("ProtobufListener bundle is starting up...");
		}
		ProtobufServletContainerInitializer protobufServletContainerInitializer = new ProtobufServletContainerInitializer();
		bundleContext.registerService(ServletContainerInitializer.class.getName(), protobufServletContainerInitializer, null);
	}

	@Override
	public void stop(BundleContext bundleContext){
		if (log.isDebugEnabled()) {
			log.debug("ProtobufListerner bundle is shutting down...");
		}
	}
}
