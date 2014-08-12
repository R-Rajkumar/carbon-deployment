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

package org.wso2.carbon.protobuf.client.internal;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.base.ServerConfiguration;
import org.wso2.carbon.protobuf.client.ProtobufClient;
import org.wso2.carbon.protobuf.client.config.ProtobufConfigFactory;
import org.wso2.carbon.protobuf.client.config.exception.ProtobufConfigurationException;
import org.wso2.carbon.protobuf.client.config.ProtobufConfiguration;

import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.CleanShutdownHandler;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.RpcConnectionEventNotifier;
import com.googlecode.protobuf.pro.duplex.RpcSSLContext;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientPipelineFactory;
import com.googlecode.protobuf.pro.duplex.listener.RpcConnectionEventListener;
import com.googlecode.protobuf.pro.duplex.timeout.RpcTimeoutChecker;
import com.googlecode.protobuf.pro.duplex.timeout.RpcTimeoutExecutor;
import com.googlecode.protobuf.pro.duplex.timeout.TimeoutChecker;
import com.googlecode.protobuf.pro.duplex.timeout.TimeoutExecutor;
import com.googlecode.protobuf.pro.duplex.util.RenamingThreadFactoryProxy;

/*
 * This class starts an RPC Client which makes a TCP connection to
 * the Binary Services Server that is running on WSO2 Application Server
 * and keeps the connection alive until bundle is shut down.
 * 
 * It reads configuration information such as server name, server port, thread
 * pool sizes etc,
 * from pbs xml which is be placed inside repository/config/etc directory of
 * WSO2 ESB.
 * 
 * If WSO2 AS is not running when starting WSO2 ESB, RPC Client will not be
 * started up.
 */
public class ProtobufClientActivator implements BundleActivator {

	private static Logger log = LoggerFactory.getLogger(ProtobufClientImpl.class);
	private static RpcClientChannel channel = null;
	private static RpcController controller = null;

	public void start(BundleContext bundleContext) {

		log.info("Starting ProtobufClient...");
		//reading pbs xml
		ProtobufConfiguration configuration = null;
		try {
			configuration = ProtobufConfigFactory.build();
		} catch (ProtobufConfigurationException e) {
			String msg = "Error while loading pbs xml file "
					+ e.getLocalizedMessage();
			log.debug(msg);
			return;
		}

		// if Binary Service ESB Client is not enabled in pbs xml
		if (!configuration.isEnabled()) {
			log.debug("ProtobufClient is not enabled in pbs xml");
			return;
		}

		// server information
		PeerInfo server = new PeerInfo(configuration.getServerConfiguration().getHost(),
				configuration.getServerConfiguration().getPort());
		// client information
		PeerInfo client = new PeerInfo(configuration.getClientConfiguration().getHost(),
				configuration.getClientConfiguration().getPort());
		// It works with netty to construct TCP Channel
		DuplexTcpClientPipelineFactory clientFactory = new DuplexTcpClientPipelineFactory();
		clientFactory.setClientInfo(client);

		// if SSL encryption is enabled
		if (configuration.getClientConfiguration().isSSLEnabled()) {

			ServerConfiguration carbonServerConfiguration = ServerConfiguration.getInstance();
			RpcSSLContext sslCtx = new RpcSSLContext();
			sslCtx.setKeystorePassword(carbonServerConfiguration
					.getFirstProperty("Security.KeyStore.Password"));
			sslCtx.setKeystorePath(carbonServerConfiguration
					.getFirstProperty("Security.KeyStore.Location"));
			sslCtx.setTruststorePassword(carbonServerConfiguration
					.getFirstProperty("Security.TrustStore.Password"));
			sslCtx.setTruststorePath(carbonServerConfiguration
					.getFirstProperty("Security.TrustStore.Location"));
			
			try {
				sslCtx.init();
			} catch (Exception e) {
				log.error("Couldn't create SSL Context : " + e.getLocalizedMessage());
				log.info("SSL not enanbled");
			}
			clientFactory.setSslContext(sslCtx);
		}
		
		// client will terminate after waiting this much of time
		clientFactory.setConnectResponseTimeoutMillis(configuration.getClientConfiguration().getConnectResponseTimeoutMillis());
		//  to compress all data traffic to and from the server, you can switch on compression
		clientFactory.setCompression(configuration.getClientConfiguration().isCompressionEnabled());
		
		//TimeoutExecutor uses a pool of threads to handle the timeout of client and server side RPC calls.
		RpcTimeoutExecutor timeoutExecutor = new TimeoutExecutor(configuration
				.getClientConfiguration().getTimeoutExecutorThreadPoolConfiguration()
				.getCorePoolSize(), configuration.getClientConfiguration()
				.getTimeoutExecutorThreadPoolConfiguration().getMaxPoolSize());
		RpcTimeoutChecker checker = new TimeoutChecker();
		checker.setTimeoutExecutor(timeoutExecutor);
		checker.startChecking(clientFactory.getRpcClientRegistry());

		// setup a RPC event listener - it just logs what happens
		RpcConnectionEventNotifier rpcEventNotifier = new RpcConnectionEventNotifier();
		RpcConnectionEventListener listener = new RpcConnectionEventListener() {
			@Override
			public void connectionReestablished(RpcClientChannel clientChannel) {
				log.info("Protobuf connection Reestablished " + clientChannel);
			}
			@Override
			public void connectionOpened(RpcClientChannel clientChannel) {
				log.info("Protobuf connection Opened " + clientChannel);
			}
			@Override
			public void connectionLost(RpcClientChannel clientChannel) {
				log.info("Protobuf connection Lost " + clientChannel);
			}
			@Override
			public void connectionChanged(RpcClientChannel clientChannel) {
				log.info("Protobuf connection Changed " + clientChannel);
			}
		};
		rpcEventNotifier.setEventListener(listener);
		clientFactory.registerConnectionEventListener(rpcEventNotifier);

		// creates netty bootstrap
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup workers = new NioEventLoopGroup(configuration.getTransportConfiguration()
				.getChannelHandlersConfiguration().getPoolSize(), new RenamingThreadFactoryProxy(
				"workers", Executors.defaultThreadFactory()));
		bootstrap.group(workers);
		bootstrap.handler(clientFactory);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 0);
		bootstrap.option(ChannelOption.SO_SNDBUF, configuration.getTransportConfiguration()
				.getChannelHandlersConfiguration().getSendBufferSize());
		bootstrap.option(ChannelOption.SO_RCVBUF, configuration.getTransportConfiguration()
				.getChannelHandlersConfiguration().getReceiverBufferSize());

		// to shut down the channel gracefully
		CleanShutdownHandler shutdownHandler = new CleanShutdownHandler();
		shutdownHandler.addResource(checker);
		shutdownHandler.addResource(timeoutExecutor);
		shutdownHandler.addResource(bootstrap.group());

		try {
			// connect to the server
			channel = clientFactory.peerWith(server, bootstrap);
			controller = channel.newRpcController();
			// register ProtobufClient as an OSGi service
			ProtobufClient protobufClient = new ProtobufClientImpl(channel, controller);
			bundleContext.registerService(ProtobufClient.class.getName(), protobufClient, null);
		} catch (IOException e) {
			// can happen if Address is already in use
			String msg = "IOException " + e.getLocalizedMessage();
			log.error(msg);
		}
	}

	public void stop(BundleContext bundleContext) {
		// shut down the channel and reset controller
		channel.close();
		controller.reset();
		log.info("ProtobufClient Shutting Down...");
	}
}
