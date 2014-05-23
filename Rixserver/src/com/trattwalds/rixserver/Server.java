package com.trattwalds.rixserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class Server {
	private static final int DEFAULT_PORT = 37810;
	private static final int MAX_TCP_ALLOC = 1024 * 4;
	private static Server server;

	private List<Client> clients;
	private Map<SocketChannel, Client> keyClientMap;
	private Client selectedClient;
	private int port;

	private Selector selector;
	private ServerSocketChannel serverChannel;
	private ByteBuffer readBuffer;

	public Server(int port) {
		this.port = port;
		initialize();
	}

	public Server() {
		this.port = DEFAULT_PORT;
		initialize();
	}

	public static Server getInstance() {
		return server;
	}

	private void initialize() {
		server = this;
		readBuffer = ByteBuffer.allocate(MAX_TCP_ALLOC);
		try {
			selector = initSelector();
			clients = new ArrayList<Client>();
			keyClientMap = new HashMap<SocketChannel, Client>();
			run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void selectClient(int index) {
		int selectedIndex = -1;
		try {
			selectedClient = clients.get(index);
			selectedIndex = index;
		} catch (IndexOutOfBoundsException e) {
			if (clients.size() > 0) {
				selectedIndex = 0;
				selectedClient = clients.get(0);
			}
		}

		CommandLine.put(selectedIndex + " selected." + System.lineSeparator());
		if (selectedClient == null) {
			CommandLine.TARGET = "";
		} else {
			CommandLine.TARGET = "(" + selectedClient.getIP() + ")";
		}
	}

	private Selector initSelector() throws IOException {
		// Create a new selector
		Selector socketSelector = SelectorProvider.provider().openSelector();

		// Create a new non-blocking server socket channel
		serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);

		// Bind the server socket to the specified address and port
		InetSocketAddress isa = new InetSocketAddress(port);
		serverChannel.socket().bind(isa);

		// Register the server socket channel, indicating an interest in
		// accepting new connections
		serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

		return socketSelector;
	}

	public void run() {
		while (true) {
			try {
				// Wait for an event one of the registered channels
				selector.select();

				// Iterate over the set of keys for which events are available
				Iterator<SelectionKey> selectedKeys = selector.selectedKeys()
						.iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid()) {
						continue;
					}

					// Check what event is available and deal with it
					if (key.isAcceptable()) {
						accept(key);
					} else if (key.isReadable()) {
						read(key);
					} else if (key.isWritable()) {
						write(key);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void accept(SelectionKey key) throws IOException {
		// For an accept to be pending the channel must be a server socket
		// channel.
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key
				.channel();

		// Accept the connection and make it non-blocking
		SocketChannel socketChannel = serverSocketChannel.accept();
		Socket socket = socketChannel.socket();
		socketChannel.configureBlocking(false);

		// Register the new SocketChannel with our Selector, indicating
		// we'd like to be notified when there's data waiting to be read
		socketChannel.register(selector, SelectionKey.OP_READ);

		Client client = new Client(socketChannel);
		clients.add(client);
		keyClientMap.put(socketChannel, client);
	}

	private void read(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		// Clear out our read buffer so it's ready for new data
		readBuffer = ByteBuffer.allocate(MAX_TCP_ALLOC);

		// Attempt to read off the channel
		int numRead;
		try {
			numRead = socketChannel.read(readBuffer);
		} catch (IOException e) {
			// The remote forcibly closed the connection, cancel
			// the selection key and close the channel.
			key.cancel();
			socketChannel.close();
			keyClientMap.remove(socketChannel);
			clients.remove(socketChannel);
			return;
		}

		if (numRead == -1) {
			// Remote entity shut the socket down cleanly. Do the
			// same from our end and cancel the channel.
			key.channel().close();
			key.cancel();
			return;
		}

		// Hand the data off to our worker thread
		Client client = keyClientMap.get(socketChannel);
		client.onReceiveData(readBuffer.array(), numRead);
	}

	// Maps a SocketChannel to a list of ByteBuffer instances
	private Map pendingData = new HashMap();

	public void send(byte[] data, int len) {
		SocketChannel socket = selectedClient.getSocketChannel();
		try {
			socket.register(selector, SelectionKey.OP_WRITE);
		} catch (ClosedChannelException e) {
			e.printStackTrace();
		}

		// And queue the data we want written
		synchronized (this.pendingData) {
			List queue = (List) this.pendingData.get(socket);
			if (queue == null) {
				queue = new ArrayList();
				this.pendingData.put(socket, queue);
			}
			queue.add(ByteBuffer.wrap(data, 0, len));
		}

		// Finally, wake up our selecting thread so it can make the required
		// changes
		this.selector.wakeup();
	}

	private void write(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		synchronized (this.pendingData) {
			List queue = (List) this.pendingData.get(socketChannel);

			// Write until there's not more data ...
			while (!queue.isEmpty()) {
				ByteBuffer buf = (ByteBuffer) queue.get(0);
				int wrote = socketChannel.write(buf);
				if (buf.remaining() > 0) {
					// ... or the socket's buffer fills up
					break;
				}
				queue.remove(0);
			}

			if (queue.isEmpty()) {
				// We wrote away all data, so we're no longer interested
				// in writing on this socket. Switch back to waiting for
				// data.
				key.interestOps(SelectionKey.OP_READ);
			}
		}
	}

	public abstract void onConnect(String ip, String name);

	public Client getSelectedClient() {
		return selectedClient;
	}

	public List<Client> getClients() {
		return clients;
	}

	public void closeSocket(Client client, SocketChannel socketChannel) {
		try {
			socketChannel.close();
			clients.remove(client);
			keyClientMap.remove(socketChannel);

			CommandLine.TARGET = "";
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
