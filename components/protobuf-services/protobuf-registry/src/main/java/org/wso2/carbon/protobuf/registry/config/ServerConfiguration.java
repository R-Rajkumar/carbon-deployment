package org.wso2.carbon.protobuf.registry.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ServerConfiguration {
	
    @XmlElement(name = "Host", required = true)
    private String host;

    @XmlElement(name = "Port", required = true)
    private int port;
    
    @XmlElement(name = "EnableSSL", required = true, type = Boolean.class)
    private boolean isSSLEnabled;
    
    @XmlElement(name = "ServerCallExecutorThreadPool", required = true)
    private ServerCallExecutorThreadPoolConfiguration serverCallExecutorThreadPoolConfiguration;
    
    @XmlElement(name = "TimeoutExecutorThreadPool", required = true)
    private TimeoutExecutorThreadPoolConfiguration timeoutExecutorThreadPoolConfiguration;
    
    @XmlElement(name = "TimeoutCheckerThreadPool", required = true)
    private TimeoutCheckerThreadPoolConfiguration timeoutCheckerThreadPoolConfiguration;
    
    @XmlElement(name = "Logger", required = true)
    private LoggerConfiguration loggerConfiguration;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isSSLEnabled() {
		return isSSLEnabled;
	}

	public void setSSLEnabled(boolean isSSLEnabled) {
		this.isSSLEnabled = isSSLEnabled;
	}

	public ServerCallExecutorThreadPoolConfiguration getServerCallExecutorThreadPoolConfiguration() {
		return serverCallExecutorThreadPoolConfiguration;
	}

	public void setServerCallExecutorThreadPoolConfiguration(
			ServerCallExecutorThreadPoolConfiguration serverCallExecutorThreadPoolConfiguration) {
		this.serverCallExecutorThreadPoolConfiguration = serverCallExecutorThreadPoolConfiguration;
	}

	public TimeoutExecutorThreadPoolConfiguration getTimeoutExecutorThreadPoolConfiguration() {
		return timeoutExecutorThreadPoolConfiguration;
	}

	public void setTimeoutExecutorThreadPoolConfiguration(
			TimeoutExecutorThreadPoolConfiguration timeoutExecutorThreadPoolConfiguration) {
		this.timeoutExecutorThreadPoolConfiguration = timeoutExecutorThreadPoolConfiguration;
	}

	public TimeoutCheckerThreadPoolConfiguration getTimeoutCheckerThreadPoolConfiguration() {
		return timeoutCheckerThreadPoolConfiguration;
	}

	public void setTimeoutCheckerThreadPoolConfiguration(
			TimeoutCheckerThreadPoolConfiguration timeoutCheckerThreadPoolConfiguration) {
		this.timeoutCheckerThreadPoolConfiguration = timeoutCheckerThreadPoolConfiguration;
	}

	public LoggerConfiguration getLoggerConfiguration() {
		return loggerConfiguration;
	}

	public void setLoggerConfiguration(LoggerConfiguration loggerConfiguration) {
		this.loggerConfiguration = loggerConfiguration;
	}

    
}
