package com.trattwalds.rixserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.trattwalds.rixserver.commands.Command;

public class Client {
	SocketChannel socketChannel;
	Command lastCommand;
	String ip;

	public Client(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
		this.ip = socketChannel.socket().getRemoteSocketAddress().toString();
		Server server = Server.getInstance();
		server.onConnect(this.ip, "PC");
	}

	public byte[] read() {
		ByteBuffer dst = ByteBuffer.allocate(1024);
		try {
			socketChannel.read(dst);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dst.array();
	}

	public void sendHeader(long len) {
		Server server = Server.getInstance();
		// Send the command ID, allowing the client to prepare for the coming
		// message
		String header = lastCommand.getId() + "\n" + len + "\n\n";
		server.send(header.getBytes(), header.length());
	}

	public void sendRaw(byte[] data, int len) {
		Server server = Server.getInstance();

		// Send raw data
		server.send(data, len);
	}

	public void sendRaw(byte[] data) {
		Server server = Server.getInstance();

		// Send raw data
		server.send(data, data.length);
	}

	public void send(byte[] data) {
		Server server = Server.getInstance();

		// Send the command ID, allowing the client to prepare for the coming
		// message
		String header = lastCommand.getId() + "\n" + data.length + "\n\n";

		// Send the commandId to the server
		server.send(header.getBytes(), header.length());

		// Send the actual data(if any)
		server.send(data, data.length);
	}

	public void send(byte[] data, int len) {
		Server server = Server.getInstance();

		// Send the command ID, allowing the client to prepare for the coming
		// message
		String header = lastCommand.getId() + "\n" + len + "\n\n";

		// Send the commandId to the server
		server.send(header.getBytes(), header.length());

		// Send the actual data(if any)
		server.send(data, len);
	}

	public void close() {
		Server server = Server.getInstance();
		server.closeSocket(this, socketChannel);
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public void setCommand(Command command) {
		lastCommand = command;
	}

	public void onReceiveData(byte[] array, int numRead) {
		if (lastCommand != null) {
			lastCommand.onReceiveData(array, numRead);
		}
	}

	public String getIP() {
		return ip;
	}
}
