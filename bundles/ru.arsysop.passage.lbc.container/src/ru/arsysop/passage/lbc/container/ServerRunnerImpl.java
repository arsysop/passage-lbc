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
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     ArSysOp - initial API and implementation
 *******************************************************************************/
package ru.arsysop.passage.lbc.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import ru.arsysop.passage.lbc.server.ServerHandler;
import ru.arsysop.passage.lbc.server.ServerRequestAction;
import ru.arsysop.passage.lbc.server.ServerRequestExecutor;
import ru.arsysop.passage.lbc.server.ServerRequestHandler;
import ru.arsysop.passage.lbc.server.ServerRunner;
import ru.arsysop.passage.lic.net.RequestParameters;

public class ServerRunnerImpl implements ServerRunner {

	private static final String REGISTERED = "[Registered]  %s";
	private static final String REQUEST_HANDLER_NOT_FOUND = "Request handler not registrated for component ";

	private ServerHandler serverHandler;
	private Map<String, ServerRequestHandler> requestHandlers = new HashMap<>();
	private Map<String, ServerRequestExecutor> requestExecutors = new HashMap<>();
	private Map<String, ServerRequestAction> requestActions = new HashMap<>();

	private Logger logger;

	public void activate() {
		if (serverHandler != null) {
			serverHandler.launch();
		} else {
			logger.error("Server not registrated");
		}
	}

	public void bindLogger(LoggerFactory loggerFactory) {
		this.logger = loggerFactory.getLogger(ServerRunnerImpl.class);
	}

	public void unbindLogger(LoggerFactory logService) {
		this.logger = null;
	}

	public void bindServerHandler(ServerHandler serverHandler) {
		logger.info(String.format(REGISTERED, serverHandler.toString()));
		this.serverHandler = serverHandler;

	}

	public void unbindServerHandler(ServerHandler serverHandler) {
		this.serverHandler = null;
	}

	public void bindServerRequestHandler(ServerRequestHandler serverRequestHandler, Map<String, String> context) {
		logger.debug(String.format(REGISTERED, serverRequestHandler.getClass().getName()));
		String requestHandlerId = context.get(RequestParameters.SERVER_HANDLER_ID);
		if (requestHandlerId != null) {
			requestHandlers.put(requestHandlerId, serverRequestHandler);
			serverHandler.addServerRequestHandler(serverRequestHandler);
		} else {
			logger.error(REQUEST_HANDLER_NOT_FOUND + serverRequestHandler.toString());
		}
	}

	public void unbindServerRequestHandler(ServerRequestHandler serverRequestHandler, Map<String, String> context) {
		String requestHandlerId = context.get(RequestParameters.SERVER_HANDLER_ID);
		if (requestHandlerId != null) {
			requestHandlers.remove(requestHandlerId, serverRequestHandler);
			serverHandler.remServerRequestHandler(serverRequestHandler);
		} else {
			logger.error(REQUEST_HANDLER_NOT_FOUND + serverRequestHandler.toString());
		}
	}

	public void bindServerRequestExecutor(ServerRequestExecutor serverRequestExecutor, Map<String, String> context) {
		logger.debug(String.format(REGISTERED, serverRequestExecutor.getClass().getName()));
		String requestExecutorModeId = context.get(RequestParameters.SERVER_ACCESS_MODE_ID);
		if (requestExecutorModeId != null) {
			serverRequestExecutor.setAccessModeId(requestExecutorModeId);
			if (!requestExecutors.containsKey(requestExecutorModeId)) {
				requestExecutors.put(requestExecutorModeId, serverRequestExecutor);
				for (Entry<String, ServerRequestHandler> entry : requestHandlers.entrySet()) {
					entry.getValue().addRequestExecutor(serverRequestExecutor);
				}
			}
		} else {
			logger.error(REQUEST_HANDLER_NOT_FOUND + serverRequestExecutor.toString()
					+ RequestParameters.SERVER_ACCESS_MODE_ID + requestExecutorModeId);
			;
		}
	}

	public void unbindServerRequestExecutor(ServerRequestExecutor serverRequestExecutor, Map<String, String> context) {
		String requestExecutorModeId = context.get(RequestParameters.SERVER_ACCESS_MODE_ID);
		if (requestExecutorModeId != null) {
			requestExecutors.remove(requestExecutorModeId, serverRequestExecutor);
			for (Entry<String, ServerRequestHandler> entry : requestHandlers.entrySet()) {
				entry.getValue().addRequestExecutor(serverRequestExecutor);
			}
		}
	}

	public void bindExecutorByRequest(Map<String, ServerRequestHandler> mapHandlers,
			Map<String, Map<String, ServerRequestExecutor>> mapRequestExecutors) {
		for (String keyExecutor : mapRequestExecutors.keySet()) {
			Map<String, ServerRequestExecutor> mapExecutors = mapRequestExecutors.get(keyExecutor);
			ServerRequestHandler requestHandler = mapHandlers.get(keyExecutor);

			for (Entry<String, ServerRequestExecutor> iter : mapExecutors.entrySet()) {
				requestHandler.addRequestExecutor(iter.getValue());
			}
		}
	}

	public void bindHandlerByServer(ServerHandler serverHandler,
			Map<String, ServerRequestHandler> serverRequestHandlers) {
		for (ServerRequestHandler requestHandler : serverRequestHandlers.values()) {
			serverHandler.addServerRequestHandler(requestHandler);
		}
	}

	public void bindServerRequestActions(ServerRequestAction action, Map<String, String> context) {
		logger.debug(String.format(REGISTERED, action.getClass().getName()));

		String actionId = context.get(RequestParameters.SERVER_ACTION_ID);
		if (actionId != null) {
			if (!this.requestActions.containsKey(actionId)) {
				this.requestActions.put(actionId, action);
				requestExecutors.values().stream().forEach(a -> a.setRequestAction(requestActions));
			}
		}
	}

	public void unbindServerRequestActions(ServerRequestAction action, Map<String, String> context) {
		logger.debug(action.getClass().getName());

		String actionId = context.get(RequestParameters.SERVER_ACTION_ID);
		if (actionId != null) {
			if (this.requestActions.containsKey(actionId)) {
				this.requestActions.remove(actionId, action);
				requestExecutors.values().stream().forEach(a -> a.setRequestAction(requestActions));
			}
		}
	}
}
