package org.wso2.carbon.protobuf.registry.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class LoggerConfiguration {
	
    @XmlElement(name = "LogReqProto", required = true, type = Boolean.class)
    private boolean isLogReqProtoEnabled;
    
    @XmlElement(name = "LogResProto", required = true, type = Boolean.class)
    private boolean isLogResProtoEnabled;
    
    @XmlElement(name = "LogEventProto", required = true, type = Boolean.class)
    private boolean isLogEventProtoEnabled;

	public boolean isLogReqProtoEnabled() {
		return isLogReqProtoEnabled;
	}

	public void setLogReqProtoEnabled(boolean isLogReqProtoEnabled) {
		this.isLogReqProtoEnabled = isLogReqProtoEnabled;
	}

	public boolean isLogResProtoEnabled() {
		return isLogResProtoEnabled;
	}

	public void setLogResProtoEnabled(boolean isLogResProtoEnabled) {
		this.isLogResProtoEnabled = isLogResProtoEnabled;
	}

	public boolean isLogEventProtoEnabled() {
		return isLogEventProtoEnabled;
	}

	public void setLogEventProtoEnabled(boolean isLogEventProtoEnabled) {
		this.isLogEventProtoEnabled = isLogEventProtoEnabled;
	}
}
