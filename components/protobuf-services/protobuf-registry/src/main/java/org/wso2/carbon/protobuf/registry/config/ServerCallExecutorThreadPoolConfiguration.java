package org.wso2.carbon.protobuf.registry.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ServerCallExecutorThreadPoolConfiguration {

    @XmlElement(name = "CorePoolSize", required = true)
    private int corePoolSize;
    
    @XmlElement(name = "MaxPoolSize", required = true)
    private int maxPoolSize;
    
    @XmlElement(name = "MaxPoolTimeout", required = true)
    private int maxPoolTimeout;

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public int getMaxPoolTimeout() {
		return maxPoolTimeout;
	}

	public void setMaxPoolTimeout(int maxPoolTimeout) {
		this.maxPoolTimeout = maxPoolTimeout;
	}
    
    
}
