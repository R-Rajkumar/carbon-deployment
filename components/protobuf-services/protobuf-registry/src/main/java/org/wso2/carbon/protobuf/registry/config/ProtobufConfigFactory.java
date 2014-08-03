package org.wso2.carbon.protobuf.registry.config;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.wso2.carbon.base.CarbonBaseConstants;
import org.wso2.carbon.protobuf.registry.config.exception.ProtobufConfigurationException;

public class ProtobufConfigFactory {

	public static ProtobufConfiguration build() throws ProtobufConfigurationException {

		ProtobufConfiguration protobufConfiguration = null;
		
		try {
			String pbsXmlLocation = System.getProperty(CarbonBaseConstants.CARBON_HOME)+File.separator+"repository"+File.separator+"conf"+File.separator+"etc"+File.separator+"pbs.xml";
			File file = new File(pbsXmlLocation);
			JAXBContext context = JAXBContext.newInstance(ProtobufConfiguration.class);
			Unmarshaller un = context.createUnmarshaller();
			protobufConfiguration = (ProtobufConfiguration) un.unmarshal(file);
		} catch (Exception e) {
			String msg = "Error while loading cluster configuration file";
			System.out.println(e);
			throw new ProtobufConfigurationException(msg, e);
		}
		
		return protobufConfiguration;
	}

}
