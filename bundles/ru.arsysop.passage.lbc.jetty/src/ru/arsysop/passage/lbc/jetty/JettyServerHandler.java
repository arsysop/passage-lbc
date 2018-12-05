/*******************************************************************************
 * Copyright (c) 2018 ArSysOp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     ArSysOp - initial API and implementation
 *******************************************************************************/

package ru.arsysop.passage.lbc.jetty;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;

import ru.arsysop.passage.lbc.container.params.ServerPrams;
import ru.arsysop.passage.lbc.server.ServerHandler;
import ru.arsysop.passage.lbc.server.ServerRequestHandler;

public class JettyServerHandler implements ServerHandler {
	private static Logger LOG = Logger.getLogger(JettyServerHandler.class.getName());
	private List<ServerRequestHandler> serverHandlers = new ArrayList<>();

	private Server server;

	@Override
	public void launch() {
		server = new Server(ServerPrams.DEFAULT_SERVER_PORT);
		try {

			HandlerList handlers = new HandlerList();
			for (ServerRequestHandler handler : serverHandlers) {
				if (handler instanceof Handler) {
					handlers.addHandler((Handler) handler);
				}
			}
			server.setHandler(handlers);
			server.start();
			LOG.info(server.getState());
		} catch (Exception e) {
			LOG.info(e.getMessage());
		}
	}

	@Override
	public void terminate() {
		if (server != null) {
			try {
				server.stop();
				LOG.info(server.getState());
			} catch (Exception e) {
				LOG.info(e.getMessage());
			}
		}
	}

	@Override
	public void addServerRequestHandler(ServerRequestHandler handler) {
		this.serverHandlers.add(handler);
	}

	@Override
	public void remServerRequestHandler(ServerRequestHandler handler) {
		this.serverHandlers.remove(handler);
	}
}
