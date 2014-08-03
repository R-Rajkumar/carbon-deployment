package org.wso2.carbon.protobuf.client;

import com.google.protobuf.RpcChannel;
import com.google.protobuf.RpcController;

public interface ProtobufClient {

	public RpcChannel getRpcChannel();
	public RpcController getRpcController();
}
