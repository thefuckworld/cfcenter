package com.dw.cfcenter.client.core.transfer.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dw.cfcenter.client.core.storage.DataStorageAdapter;
import com.dw.cfcenter.client.core.storage.DefaultDataStorageAdapter;
import com.dw.cfcenter.client.core.transfer.ReceiveServer;

/**
 * Description:
 * @author caohui
 */
public class TcpReceiveServer implements ReceiveServer{

	private final Logger LOGGER = LoggerFactory.getLogger(TcpReceiveServer.class);
	private Charset charset = Charset.forName("UTF-8");
	private Selector selector = null;
	private ServerSocketChannel server = null;
	private volatile String ip = null;
	
	private DataStorageAdapter dataStorageAdapter = DefaultDataStorageAdapter.getInstance();
	
	@Override
	public void start(int port) throws Exception {
		init(port);
		new Thread(new ReceiveWorker()).start();
	}
	
	private void init(int port) throws Exception {
		InetSocketAddress address = null;
		selector = Selector.open();
		server = ServerSocketChannel.open();
		address = new InetSocketAddress(port);
		server.socket().bind(address);
		server.configureBlocking(false);
		server.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	// 接收消息线程
	class ReceiveWorker implements Runnable {
		@Override
		public void run() {
			SelectionKey key = null;
			while(true) {
				try {
					if(selector.select() > 0) {
						Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
						while(iterator.hasNext()) {
							key = iterator.next();
							iterator.remove();
							if(key.isAcceptable()) {
								SocketChannel sc = server.accept();
								ip = sc.socket().getInetAddress().getHostAddress();
								sc.configureBlocking(false);
								sc.register(selector, SelectionKey.OP_READ);
							}
							
							if(key.isReadable() && ip != null) {
								String tempIp = ip;
								ip = null;
								handleMessage(tempIp, key);
							}
						}
					}
				}catch(IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		
	}

	
	// 消息处理
	private void handleMessage(String ip, SelectionKey key) {
		SocketChannel channel = null;
		StringBuilder content = new StringBuilder();
		ByteBuffer buffer = ByteBuffer.allocate(1024 * 10);
		try {
			channel = (SocketChannel) key.channel();
			// 临时解决网络问题导致的读速度大于写速度的NIO问题
			Thread.sleep(101);
			do {
				channel.read(buffer);
				buffer.flip();
				content.append(charset.decode(buffer));
			}while(buffer.hasRemaining());
			
			dataStorageAdapter.changeDataFromSocket(ip, content.toString());
			key.interestOps(SelectionKey.OP_READ);
		}catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
				
	}
}
