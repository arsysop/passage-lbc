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
package ru.arsysop.passage.lbc.base.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.arsysop.passage.lbc.base.BaseComponent;
import ru.arsysop.passage.lbc.server.ServerRequestAction;
import ru.arsysop.passage.lic.net.RequestParameters;
import ru.arsysop.passage.lic.runtime.ConditionMiner;
import ru.arsysop.passage.lic.runtime.LicensingCondition;
import ru.arsysop.passage.lic.runtime.io.LicensingConditionTransport;

/**
 * According to AccessManager specification implementation of
 * {@code Iterable<ConditionDescriptor> extractConditions(Object configuration)}
 * {@link ru.arsysop.passage.lic.runtime.AccessManager}
 */
public class ConditionDescriptorRequestAction extends BaseComponent implements ServerRequestAction {

	private static final String TRANSPORT_NOT_DEFINED_ERROR = "LicensingConditionTransport not defined for contentType: %s";
	private static final String SERVER_MINER_TYPE = "server.miner"; // NLS-$1
	private static final String ERROR_CONDITIONS_NOT_AVAILABLE = "No condition miners available"; // NLS-$1
	private static final String MSG_LOG = "Executing action request from class: %s"; // NLS-$1
	private static final String PARAMETER_CONFIGURATION = "configuration"; // NLS-$1

	private static final String APPLICATION_JSON = "application/json"; // NLS-$1
	private static final String LICENSING_CONTENT_TYPE = "licensing.content.type"; // NLS-$1
	private static final String MINER_TYPE_KEY = "miner.type";// NLS-$1

	private List<ConditionMiner> licenseConditionMiners = new ArrayList<>();
	private Map<String, LicensingConditionTransport> mapCondition2Transport = new HashMap<>();

	@Override
	public boolean execute(HttpServletRequest request, HttpServletResponse response) {
		if (logger == null) {
			return false;
		}
		logger.info(String.format(MSG_LOG, this.getClass().getName()));
		if (licenseConditionMiners.isEmpty()) {
			logger.error(ERROR_CONDITIONS_NOT_AVAILABLE);
			return false;
		}
		try {
			String configuration = request.getParameter(PARAMETER_CONFIGURATION);
			Collection<LicensingCondition> resultConditions = new ArrayList<>();

			for (ConditionMiner miner : licenseConditionMiners) {
				Iterable<LicensingCondition> descriptors = miner.extractLicensingConditions(configuration);

				resultConditions.addAll((Collection<? extends LicensingCondition>) descriptors);
			}
			String contentType = request.getParameter(RequestParameters.CONTENT_TYPE);
			LicensingConditionTransport transport = mapCondition2Transport.get(contentType);
			if (transport == null) {
				logger.error(String.format(TRANSPORT_NOT_DEFINED_ERROR, contentType));
				return false;
			}

			transport.writeConditionDescriptors(resultConditions, response.getOutputStream());
			response.setContentType(APPLICATION_JSON);

			return true;
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	public void bindConditionMiner(ConditionMiner conditionMiner, Map<String, String> context) {

		String minerType = context.get(MINER_TYPE_KEY);
		if (minerType != null && minerType.equals(SERVER_MINER_TYPE)) {
			this.licenseConditionMiners.add(conditionMiner);
		}
	}

	public void unbindConditionMiner(ConditionMiner conditionMiner, Map<String, String> context) {
		this.licenseConditionMiners.remove(conditionMiner);
	}

	public void bindLicensingConditionTransport(LicensingConditionTransport transport, Map<String, String> context) {
		String conditionType = context.get(LICENSING_CONTENT_TYPE);
		if (conditionType != null) {
			mapCondition2Transport.put(conditionType, transport);
		}
	}
}
