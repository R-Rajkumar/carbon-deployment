package org.wso2.carbon.protobuf.registry;

import com.google.protobuf.BlockingService;
import com.google.protobuf.Service;

public interface ProtobufServiceRegistry {

	public String registerBlockingService(BlockingService blockingService);

	public String registerService(Service service);

	public String removeBlockingService(String serviceName);

	public String removeService(String serviceName);
}
