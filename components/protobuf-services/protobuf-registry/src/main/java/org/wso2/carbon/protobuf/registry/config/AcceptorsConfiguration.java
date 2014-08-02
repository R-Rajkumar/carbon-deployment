package org.wso2.carbon.protobuf.registry.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class AcceptorsConfiguration {

    @XmlElement(name = "PoolSize", required = true)
    private int poolSize;
    
    @XmlElement(name = "SendBufferSize", required = true)
    private int sendBufferSize;
    
    @XmlElement(name = "ReceiverBufferSize", required = true)
    private int receiverBufferSize;

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public int getSendBufferSize() {
		return sendBufferSize;
	}

	public void setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
	}

	public int getReceiverBufferSize() {
		return receiverBufferSize;
	}

	public void setReceiverBufferSize(int receiverBufferSize) {
		this.receiverBufferSize = receiverBufferSize;
	}
}
