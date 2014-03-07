package com.chalcodes.weaponm.network;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.LogName;
import com.chalcodes.weaponm.database.DatabaseState;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.event.WeaponEvent;

/**
 * Network sessions update the manager by posting runnables to the UI thread.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class NetworkManager {
	static final int BUFFER_SIZE = 8192;
	private final Logger log = LoggerFactory.getLogger(LogName.forObject(this));
	private final EventSupport eventSupport;
	private final ByteBuffer writeBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
	private NetworkState status = NetworkState.DISCONNECTED;
	private Thread networkThread;
	private int threadCount = 0;
	private SocketChannel channel;
	private long bytesWritten;
	private boolean locked;

	public NetworkManager(EventSupport eventSupport) {
		this.eventSupport = eventSupport;
		PropertyChangeListener listener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				WeaponEvent evt = (WeaponEvent) e;
				switch(evt.getType()) {
				case TEXT_TYPED:
					String text = (String) evt.getNewValue();
					try {
						write(text);
					} catch (IOException ex) {
						// ignore
					}
					break;
				case DATABASE_STATE:
					DatabaseState state = (DatabaseState) evt.getNewValue();
					if(state == DatabaseState.CLOSING) {
						disconnect();
					}
					break;
				}
			}
		};
		eventSupport.addPropertyChangeListener(EventType.TEXT_TYPED, listener);
		eventSupport.addPropertyChangeListener(EventType.DATABASE_STATE, listener);
	}
	
	public void connect(String host, int port) {
		if(status == NetworkState.DISCONNECTED) {
			setStatus(NetworkState.CONNECTING);
			networkThread = new Thread(new NetworkSession(this, host, port, eventSupport), "Network" + threadCount++);
			networkThread.start();
		}
	}
	
	public void disconnect() {
		if(status != NetworkState.DISCONNECTED) {
			networkThread.interrupt();
		}
	}
	
	public boolean isConnected() {
		return status == NetworkState.CONNECTED;
	}
	
	public boolean isConnecting() {
		return status == NetworkState.CONNECTING;
	}
	
	void setStatus(NetworkState newStatus) {
		if(this.status != newStatus) {
			NetworkState oldStatus = this.status;
			this.status = newStatus;
			switch(newStatus) {
			case CONNECTING:
				// nothing special, just fire the event
				break;
			case CONNECTED:
				try {
					// this convinces TWGS we're a proper Telnet client
					write("\u00FF\u00FC\u00F6");
				}
				catch(IOException e) {
					log.error("error sending Telnet handshake", e);
					disconnect();
					break;
				}
				break;
			case DISCONNECTED:
				networkThread = null;
				channel = null;
				break;
			}
			eventSupport.fireEvent(this, EventType.NETWORK_STATE, oldStatus, newStatus);
		}
	}

	void setChannel(SocketChannel channel) {
		this.channel = channel;
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
		else if(status != NetworkState.CONNECTED){
			throw new IOException("Network is not connected.");
		}
		else {
			int copied = 0;
			while(copied < string.length()) {
				while(writeBuffer.hasRemaining() && copied < string.length()) {
					writeBuffer.put((byte) string.charAt(copied));
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
	}
	
	/**
	 * Returns the total number of bytes written since the creation of this
	 * network manager.  Used by event dispatch tasks to determine if anything
	 * wrote to the network during the current batch of events.
	 * 
	 * @return the number of bytes sent
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
