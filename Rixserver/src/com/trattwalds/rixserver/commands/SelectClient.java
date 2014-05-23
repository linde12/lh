package com.trattwalds.rixserver.commands;

import java.util.List;

import com.trattwalds.logger.Logger;
import com.trattwalds.rixserver.Client;
import com.trattwalds.rixserver.Server;

public class SelectClient extends Command {

	@Override
	public void execute(Client client, List<String> arguments) {
		if (arguments.size() > 0) {
			String sClientIndex = arguments.get(0);
			if (sClientIndex != null) {
				try {
					int nClientIndex = Integer.parseInt(sClientIndex);
					Server.getInstance().selectClient(nClientIndex);
				} catch (NumberFormatException e) {
					Logger.info("NumberFormatException in SelectClient.java");
				}
			}
		}
	}

	@Override
	public void onReceiveData(byte[] array, int numRead) {
		// TODO Auto-generated method stub

	}

}
