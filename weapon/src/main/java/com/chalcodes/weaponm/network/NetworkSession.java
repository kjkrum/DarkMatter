package com.chalcodes.weaponm.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.LogName;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.event.WeaponEvent;

class NetworkSession implements Runnable {
	private final Logger log = LoggerFactory.getLogger(LogName.forObject(this));
	private final NetworkManager manager;
	private final String host;
	private final int port;
	private final EventSupport eventSupport;
	private final ByteBuffer readBuffer = ByteBuffer.allocateDirect(NetworkManager.BUFFER_SIZE);
	private final StringBuilder sb = new StringBuilder();
	private SocketChannel channel;
	
	NetworkSession(NetworkManager manager, String host, int port, EventSupport eventSupport) {
		this.manager = manager;
		this.host = host;
		this.port = port;
		this.eventSupport = eventSupport;
	}

	@Override
	public void run() {
		log.debug("network thread started");
		try {
			// run once
			InetSocketAddress address = new InetSocketAddress(host, port);
			channel = SocketChannel.open(address);
			channel.configureBlocking(true);
			reportChannel();
			
			// main loop
			while(!Thread.currentThread().isInterrupted()) {
				int bytesRead = channel.read(readBuffer);
				if(bytesRead == -1) break;

				// TODO these StringBuilder shenanigans are fake
				// actually put chars in a CharBuffer and write to parser
				// parser returns list of events to be dispatched
				
				readBuffer.flip();
				while(readBuffer.hasRemaining()) {
					sb.append((char) (readBuffer.get() & 0xFF));
				}
				dispatchEvent(EventType.TEXT_RECEIVED, null, sb.toString());
				sb.setLength(0);
				readBuffer.clear();
			}

		}
		catch(ClosedByInterruptException e) {
			// normal result of being interrupted
		}
		catch(Throwable t) {
			log.error("network error", t);
			dispatchEvent(EventType.NETWORK_ERROR, null, t);
		}
		finally {
			if(channel != null && channel.isOpen()) {
				try {
					channel.close();
				} catch (IOException e) {
					// ignore
				}
			}
			reportStatus(NetworkState.DISCONNECTED);
			log.debug("network thread exiting");
		}
	}
	
	/**
	 * Posts state changes to the manager in the UI thread.
	 * 
	 * @param status the new state
	 */
	private void reportStatus(final NetworkState status) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				manager.setStatus(status);				
			}
		});
	}
	
	/**
	 * Posts the channel identity to the manager in the UI thread.
	 */
	private void reportChannel() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				manager.setChannel(channel);
				manager.setStatus(NetworkState.CONNECTED);
			}
		});
	}
	
	/**
	 * Dispatch a single event in the UI thread.
	 * 
	 * @param type
	 * @param oldValue
	 * @param newValue
	 */
	private void dispatchEvent(EventType type, Object oldValue, Object newValue) {
		final WeaponEvent event = new WeaponEvent(manager, type, oldValue, newValue);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				eventSupport.firePropertyChange(event);		
			}
		});
	}
}
