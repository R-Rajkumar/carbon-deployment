package org.wso2.carbon.protobuf.registry.config;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.wso2.carbon.base.CarbonBaseConstants;
import org.wso2.carbon.protobuf.registry.config.exception.ProtobufConfigurationException;

public class ProtobufConfigFactory {

	public static ProtobufConfiguration build() throws ProtobufConfigurationException {

		ProtobufConfiguration protobufConfiguration = null;
		
		try {
			String pbsXmlLocation = System.getProperty(CarbonBaseConstants.CARBON_HOME)+File.separator+"repository"+File.separator+"conf"+File.separator+"etc"+File.separator+"pbs.xml";
			File file = new File(pbsXmlLocation);
			JAXBContext context = JAXBContext.newInstance(ProtobufConfiguration.class);

            // validate pbs.xml using the schema
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            StreamSource streamSource = new StreamSource();
            streamSource.setInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("pbs.xsd"));
            Schema schema = sf.newSchema(streamSource);
			Unmarshaller un = context.createUnmarshaller();
            un.setSchema(schema);
			protobufConfiguration = (ProtobufConfiguration) un.unmarshal(file);
		} catch (Exception e) {
			String msg = "Error while loading cluster configuration file";
			System.out.println(e);
			throw new ProtobufConfigurationException(msg, e);
		}
		
		return protobufConfiguration;
	}

}
