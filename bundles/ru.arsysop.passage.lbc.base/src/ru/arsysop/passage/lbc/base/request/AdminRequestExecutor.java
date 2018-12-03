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
import ru.arsysop.passage.lic.net.RequestParameters;

public class AdminRequestExecutor extends BaseComponent implements ServerRequestExecutor {

	private static final String MSG_REQUEST_ACTION_NOT_FOUND_ERROR = "Action id: %s not found";

	private static Map<String, ServerRequestAction> mapActionRequest = new HashMap<>();

	private String accessModeId = "";

	@Override
	public void executeRequest(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String actionId = request.getParameter(RequestParameters.SERVER_ACTION_ID);
		ServerRequestAction requestAction = mapActionRequest.get(actionId);
		if (requestAction != null) {
			requestAction.execute(request, response);
		} else {
			logger.info(String.format(MSG_REQUEST_ACTION_NOT_FOUND_ERROR, requestAction));
		}
	}

	@Override
	public boolean checkAccesstMode(HttpServletRequest baseRequest) {
		String requestAccessMode = baseRequest.getParameter(RequestParameters.SERVER_ACCESS_MODE_ID);
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
