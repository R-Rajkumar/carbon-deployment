package org.wso2.carbon.protobuf.registry.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ProtobufConfig")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProtobufConfiguration {
	
    @XmlElement(name = "Enable", required = true, type = Boolean.class)
    private boolean isEnabled;
    
    @XmlElement(name = "Server", required = true)
    private ServerConfiguration serverConfiguration;
    
    @XmlElement(name = "Transport", required = true)
    private TransportConfiguration transportConfiguration;

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public ServerConfiguration getServerConfiguration() {
		return serverConfiguration;
	}

	public void setServerConfiguration(ServerConfiguration serverConfiguration) {
		this.serverConfiguration = serverConfiguration;
	}

	public TransportConfiguration getTransportConfiguration() {
		return transportConfiguration;
	}

	public void setTransportConfiguration(
			TransportConfiguration transportConfiguration) {
		this.transportConfiguration = transportConfiguration;
	}
}
