package org.wso2.carbon.protobuf.registry.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
public class TransportConfiguration {

    @XmlElement(name = "Acceptors", required = true)
    private AcceptorsConfiguration acceptorsConfiguration;
    
    @XmlElement(name = "ChannelHandlers", required = true)
    private ChannelHandlersConfiguration channelHandlersConfiguration;
    
    @XmlElement(name = "TCPNoDelay", required = true, type = Boolean.class)
    private boolean isTCPNoDelay;

	public AcceptorsConfiguration getAcceptorsConfiguration() {
		return acceptorsConfiguration;
	}

	public void setAcceptorsConfiguration(
			AcceptorsConfiguration acceptorsConfiguration) {
		this.acceptorsConfiguration = acceptorsConfiguration;
	}

	public ChannelHandlersConfiguration getChannelHandlersConfiguration() {
		return channelHandlersConfiguration;
	}

	public void setChannelHandlersConfiguration(
			ChannelHandlersConfiguration channelHandlersConfiguration) {
		this.channelHandlersConfiguration = channelHandlersConfiguration;
	}

	public boolean isTCPNoDelay() {
		return isTCPNoDelay;
	}

	public void setTCPNoDelay(boolean isTCPNoDelay) {
		this.isTCPNoDelay = isTCPNoDelay;
	}

	
    
    
}
