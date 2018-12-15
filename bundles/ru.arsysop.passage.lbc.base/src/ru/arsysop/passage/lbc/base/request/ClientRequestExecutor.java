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

package ru.arsysop.passage.lbc.base.request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.arsysop.passage.lbc.base.BaseComponent;
import ru.arsysop.passage.lbc.server.ServerRequestAction;
import ru.arsysop.passage.lbc.server.ServerRequestExecutor;
import ru.arsysop.passage.lic.net.LicensingRequests;

public class ClientRequestExecutor extends BaseComponent implements ServerRequestExecutor {

	private static Map<String, ServerRequestAction> mapActionRequest = new HashMap<>();

	private static final String EXECUTION_ACTION_ERROR = "Execution action: [%s] result [FALSE]";
	private static final String RECIEVED_ACTION_TXT = "Recieved action id [%s]";
	private static final Object CLIENT_TRUSTED_VALUE = "12345678";

	private static final String RESPONSE_ERROR_UNTRUSTED = "Recieved unttrusted client";
	private static final String MSG_REQUEST_ACTION_NOT_FOUND_ERROR = "Action executor with id: [%s] not registered";

	private String accessModeId = "";

	@Override
	public void executeRequest(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String actionId = request.getParameter(LicensingRequests.ACTION);
		logger.info(String.format(RECIEVED_ACTION_TXT, actionId));
		if (clientRecognition(request)) {
			ServerRequestAction requestAction = mapActionRequest.get(actionId);
			if (requestAction == null) {
				logger.info(String.format(MSG_REQUEST_ACTION_NOT_FOUND_ERROR, actionId));
				return;
			}
			if (!requestAction.execute(request, response)) {
				logger.info(EXECUTION_ACTION_ERROR, requestAction.getClass().getName());
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} else {
			logger.info(RESPONSE_ERROR_UNTRUSTED);
		}

	}

	public boolean clientRecognition(HttpServletRequest request) {
		String httpClientTrustId = request.getParameter(LicensingRequests.USER);
		if (CLIENT_TRUSTED_VALUE.equals(httpClientTrustId)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean checkAccesstMode(HttpServletRequest baseRequest) {
		String requestAccessMode = baseRequest.getParameter(LicensingRequests.MODE);
		if (requestAccessMode != null && requestAccessMode.equals(accessModeId)) {
			return true;
		}
		return false;
	}

	@Override
	public void setRequestAction(Map<String, ServerRequestAction> mapActions) {
		mapActionRequest.putAll(mapActions);
	}

	@Override
	public void setAccessModeId(String accessModeId) {
		this.accessModeId = accessModeId;
	}
}
