/*
 *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.protobuf.registry.internal;

import org.wso2.carbon.protobuf.registry.ProtobufRegistry;

import com.google.protobuf.BlockingService;
import com.google.protobuf.Service;
import com.googlecode.protobuf.pro.duplex.RpcServiceRegistry;

public class ProtobufRegistryImpl implements ProtobufRegistry {
	
	private RpcServiceRegistry rpcServiceRegistry;

	ProtobufRegistryImpl(RpcServiceRegistry rpcServiceRegistry) {
		this.rpcServiceRegistry = rpcServiceRegistry;
	}

	@Override
	public void registerBlockingService(BlockingService blockingService) {
		rpcServiceRegistry.registerService(blockingService);
	}

	@Override
	public void registerService(Service service) {
		rpcServiceRegistry.registerService(service);
	}

	@Override
	public void removeBlockingService(String serviceName) {
		BlockingService blockingService = rpcServiceRegistry.resolveService(serviceName).getBlockingService();
		rpcServiceRegistry.removeService(blockingService);
	}

	@Override
	public void removeService(String serviceName) {
		Service service = rpcServiceRegistry.resolveService(serviceName).getService();
		rpcServiceRegistry.removeService(service);
	}
}