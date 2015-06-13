package cqdwTest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class Connector {
	private SocketAcceptor acceptor = null;
	private Handler handler;
	public Handler getHandler() {
		return handler;
	}
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
	public void StartServer() throws IOException{
		acceptor=new NioSocketAcceptor(Runtime.getRuntime()
				.availableProcessors()+1);
		SocketSessionConfig sessionConfig=acceptor.getSessionConfig();
		sessionConfig.setReadBufferSize(4096);
		sessionConfig.setReceiveBufferSize(4096);
		sessionConfig.setSendBufferSize(4096);
		sessionConfig.setTcpNoDelay(true);
		sessionConfig.setSoLinger(0);
		sessionConfig.setBothIdleTime(9000);
		sessionConfig.setReaderIdleTime(9000);
		sessionConfig.setWriterIdleTime(9000);
		sessionConfig.setReuseAddress(true);
		acceptor.setHandler(handler);
		DefaultIoFilterChainBuilder chainBuilder= acceptor.getFilterChain();
		
		chainBuilder.addLast("codec",
				new ProtocolCodecFilter(new CodecFactory()));

		acceptor.bind(new InetSocketAddress(9000));
	}
	
	public void StopServer(){
		if(acceptor!=null){
			acceptor.unbind();
			acceptor.dispose();
		}
	}
}
