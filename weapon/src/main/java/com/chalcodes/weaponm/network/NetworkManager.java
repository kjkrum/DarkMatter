package com.chalcodes.weaponm.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;

/**
 * Network sessions update the manager by posting runnables to the UI thread.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class NetworkManager {
	static final int BUFFER_SIZE = 8192;
	//private final Logger log = LoggerFactory.getLogger(LogName.forObject(this));
	private final EventSupport eventSupport;
	private final ByteBuffer writeBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
	private State state;
	private Thread networkThread;
	private SocketChannel channel;
	private long bytesWritten;
	private boolean locked;
	// TODO parser

	public NetworkManager(EventSupport eventSupport) {
		this.eventSupport = eventSupport;
	}
	
	public void connect(String host, int port) {
		if(state == State.DISCONNECTED) {
			setState(State.CONNECTING);
			networkThread = new Thread(new NetworkSession(this, host, port, eventSupport));
			networkThread.start();
		}
	}
	
	public void disconnect() {
		if(state != State.DISCONNECTED) {
			networkThread.interrupt();
		}
	}
	
	public boolean isConnected() {
		return state == State.CONNECTED;
	}
	
	public boolean isConnecting() {
		return state == State.CONNECTING;
	}
	
	void setState(State state) {
		this.state = state;
		switch(state) {
		case CONNECTING:
			eventSupport.dispatchEvent(EventType.NET_CONNECTING);
			break;
		case CONNECTED:
			eventSupport.dispatchEvent(EventType.NET_CONNECTED);
			break;
		case DISCONNECTED:
			networkThread = null;
			channel = null;
			eventSupport.dispatchEvent(EventType.NET_DISCONNECTED);
		}
	}

	void setChannel(SocketChannel channel) {
		this.channel = channel;
	}
	
	static enum State {
		DISCONNECTED,
		CONNECTING,
		CONNECTED
	}
	
	/**
	 * 
	 * @param string the text to send
	 * @throws NetworkLockedException if the network is locked
	 * @throws IOException if an I/O error occurs
	 */
	public void write(String string) throws IOException {
		if(locked) {
			throw new NetworkLockedException();
		}
		else if(state == State.CONNECTED) {
			int copied = 0;
			while(copied < string.length()) {
				while(writeBuffer.hasRemaining()) {
					writeBuffer.put((byte) string.charAt(copied	));
					++copied;
				}
				writeBuffer.flip();
				try {
					while(writeBuffer.hasRemaining()) {
						bytesWritten += channel.write(writeBuffer);
					}
				}
				catch(IOException e) {
					disconnect();
					throw e;
				}
				finally {
					writeBuffer.clear();
				}				
			}
		}
		else {
			throw new IOException("Network is not connected.");
		}
	}
	
	/**
	 * Returns the total number of bytes written since the creation of this
	 * network manager.  Used by event dispatch tasks to determine if anything
	 * wrote to the network during the current batch of events.
	 * 
	 * @return
	 */
	long getBytesWritten() {
		return bytesWritten;
	}
	
	void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	/**
	 * Calling {@link #write(String)} while the network is locked will result
	 * in a {@link NetworkLockedException}.
	 * 
	 * @return true if the network is locked; otherwise false
	 */
	public boolean isLocked() {
		return locked;
	}

}
