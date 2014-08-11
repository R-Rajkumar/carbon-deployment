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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.base.ServerConfiguration;
import org.wso2.carbon.protobuf.registry.ProtobufServiceRegistry;
import org.wso2.carbon.protobuf.registry.config.ProtobufConfigFactory;
import org.wso2.carbon.protobuf.registry.config.ProtobufConfiguration;
import org.wso2.carbon.protobuf.registry.config.exception.ProtobufConfigurationException;

import com.googlecode.protobuf.pro.duplex.CleanShutdownHandler;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.RpcConnectionEventNotifier;
import com.googlecode.protobuf.pro.duplex.RpcSSLContext;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.listener.RpcConnectionEventListener;
import com.googlecode.protobuf.pro.duplex.logging.CategoryPerServiceLogger;
import com.googlecode.protobuf.pro.duplex.server.DuplexTcpServerPipelineFactory;
import com.googlecode.protobuf.pro.duplex.timeout.RpcTimeoutChecker;
import com.googlecode.protobuf.pro.duplex.timeout.RpcTimeoutExecutor;
import com.googlecode.protobuf.pro.duplex.timeout.TimeoutChecker;
import com.googlecode.protobuf.pro.duplex.timeout.TimeoutExecutor;
import com.googlecode.protobuf.pro.duplex.util.RenamingThreadFactoryProxy;

/*
 * This class starts an RPC server and register its registry as an OSGI service
 * for binary services.
 * 
 * It reads configuration information from pbs xml which should be placed
 * inside AS's components/repository/lib directory.
 */

public class ProtobufServiceRegistryActivator implements BundleActivator {

	private static Logger log = LoggerFactory.getLogger(ProtobufServiceRegistry.class);

	static DuplexTcpServerPipelineFactory serverFactory;

	public void start(BundleContext bundleContext) {

		log.info("Starting Protobuf Server...");

		// load protobuf server configurations from pbs xml
		ProtobufConfiguration configuration = null;
		try {
			configuration = ProtobufConfigFactory.build();
		} catch (ProtobufConfigurationException e) {
			String msg = "Error while loading cluster configuration file "
					+ e.getLocalizedMessage();
			log.debug(msg);
			return;
		}

		if (!configuration.isEnabled()) {
			log.debug("Protobuf Server is not enabled in pbs xml");
			return;
		}

		// server information
		PeerInfo serverInfo = new PeerInfo(configuration.getServerConfiguration().getHost(),
				configuration.getServerConfiguration().getPort());

		RpcServerCallExecutor executor = new ThreadPoolCallExecutor(configuration
				.getServerConfiguration().getServerCallExecutorThreadPoolConfiguration()
				.getCorePoolSize(), configuration.getServerConfiguration()
				.getServerCallExecutorThreadPoolConfiguration().getMaxPoolSize(), configuration
				.getServerConfiguration().getServerCallExecutorThreadPoolConfiguration()
				.getMaxPoolTimeout(), TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1000),
				Executors.defaultThreadFactory());

		serverFactory = new DuplexTcpServerPipelineFactory(serverInfo);

		serverFactory.setRpcServerCallExecutor(executor);

		// if SSL encryption is enabled
		if (configuration.getServerConfiguration().isSSLEnabled()) {

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

			serverFactory.setSslContext(sslCtx);
		}

		RpcTimeoutExecutor timeoutExecutor = new TimeoutExecutor(configuration
				.getServerConfiguration().getTimeoutExecutorThreadPoolConfiguration()
				.getCorePoolSize(), configuration.getServerConfiguration()
				.getTimeoutExecutorThreadPoolConfiguration().getMaxPoolSize());
		RpcTimeoutChecker timeoutChecker = new TimeoutChecker();
		timeoutChecker.setTimeoutExecutor(timeoutExecutor);
		timeoutChecker.startChecking(serverFactory.getRpcClientRegistry());

		// setup a RPC event listener - it just logs what happens
		RpcConnectionEventNotifier rpcEventNotifier = new RpcConnectionEventNotifier();
		RpcConnectionEventListener listener = new RpcConnectionEventListener() {

			@Override
			public void connectionReestablished(RpcClientChannel clientChannel) {
				log.info("connectionReestablished " + clientChannel);
			}

			@Override
			public void connectionOpened(RpcClientChannel clientChannel) {
				log.info("connectionOpened " + clientChannel);
			}

			@Override
			public void connectionLost(RpcClientChannel clientChannel) {
				log.info("connectionLost " + clientChannel);
			}

			@Override
			public void connectionChanged(RpcClientChannel clientChannel) {
				log.info("connectionChanged " + clientChannel);
			}
		};
		rpcEventNotifier.setEventListener(listener);
		serverFactory.registerConnectionEventListener(rpcEventNotifier);

		// Binary Services Server Logger
		CategoryPerServiceLogger logger = new CategoryPerServiceLogger();
		logger.setLogRequestProto(configuration.getServerConfiguration().getLoggerConfiguration()
				.isLogReqProtoEnabled());
		logger.setLogResponseProto(configuration.getServerConfiguration().getLoggerConfiguration()
				.isLogResProtoEnabled());
		logger.setLogEventProto(configuration.getServerConfiguration().getLoggerConfiguration()
				.isLogEventProtoEnabled());

		if (!configuration.getServerConfiguration().getLoggerConfiguration().isLogReqProtoEnabled()
				&& !configuration.getServerConfiguration().getLoggerConfiguration()
						.isLogResProtoEnabled()
				&& !configuration.getServerConfiguration().getLoggerConfiguration()
						.isLogEventProtoEnabled()) {
			serverFactory.setLogger(null);
		} else {
			serverFactory.setLogger(logger);
		}

		// Configure the server.
		ServerBootstrap bootstrap = new ServerBootstrap();
		NioEventLoopGroup boss = new NioEventLoopGroup(configuration.getTransportConfiguration()
				.getAcceptorsConfiguration().getPoolSize(), new RenamingThreadFactoryProxy("boss",
				Executors.defaultThreadFactory()));
		NioEventLoopGroup workers = new NioEventLoopGroup(configuration.getTransportConfiguration()
				.getChannelHandlersConfiguration().getPoolSize(), new RenamingThreadFactoryProxy(
				"worker", Executors.defaultThreadFactory()));
		bootstrap.group(boss, workers);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.option(ChannelOption.SO_SNDBUF, configuration.getTransportConfiguration()
				.getAcceptorsConfiguration().getSendBufferSize());
		bootstrap.option(ChannelOption.SO_RCVBUF, configuration.getTransportConfiguration()
				.getAcceptorsConfiguration().getReceiverBufferSize());
		bootstrap.childOption(ChannelOption.SO_RCVBUF, configuration.getTransportConfiguration()
				.getChannelHandlersConfiguration().getReceiverBufferSize());
		bootstrap.childOption(ChannelOption.SO_SNDBUF, configuration.getTransportConfiguration()
				.getChannelHandlersConfiguration().getSendBufferSize());
		bootstrap.option(ChannelOption.TCP_NODELAY, configuration.getTransportConfiguration()
				.isTCPNoDelay());
		bootstrap.childHandler(serverFactory);
		bootstrap.localAddress(serverInfo.getPort());

		CleanShutdownHandler shutdownHandler = new CleanShutdownHandler();
		shutdownHandler.addResource(boss);
		shutdownHandler.addResource(workers);
		shutdownHandler.addResource(executor);
		shutdownHandler.addResource(timeoutChecker);
		shutdownHandler.addResource(timeoutExecutor);

		// Bind and start to accept incoming connections.
		bootstrap.bind();

		log.info("Serving " + serverInfo);

		// Register Binary Service Registry as an OSGi service
		ProtobufServiceRegistry protobufServiceRegistry = new ProtobufServiceRegistryImpl(
				serverFactory);
		bundleContext.registerService(ProtobufServiceRegistry.class.getName(),
				protobufServiceRegistry, null);

	}

	public void stop(BundleContext bundleContext) {
		log.info("Protobuf Server Shutting Down...");
	}
}
