package com.chalcodes.weaponm.network;

import java.nio.channels.SocketChannel;

import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;

/**
 * Network sessions update the manager by posting runnables in the UI thread.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class NetworkManager {
	//private final Logger log = LoggerFactory.getLogger(LogName.forObject(this));
	private final EventSupport eventSupport;
	private State state;
	private Thread networkThread;
	private SocketChannel channel;
	private int bytesWritten;
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
	
	// TODO write methods	
	
	/**
	 * Returns the total number of bytes written since the creation of this
	 * network manager.  Used by event dispatch tasks to determine if anything
	 * wrote to the network during the current batch of events.
	 * 
	 * @return
	 */
	int getBytesWritten() {
		return bytesWritten;
	}
	
	void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public boolean isLocked() {
		return locked;
	}

}
