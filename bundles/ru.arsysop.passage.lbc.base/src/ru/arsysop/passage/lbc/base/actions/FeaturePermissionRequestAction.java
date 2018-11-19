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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import ru.arsysop.passage.lbc.base.condition.ServerConditionsMiner;
import ru.arsysop.passage.lbc.server.ServerRequestAction;
import ru.arsysop.passage.lbc.server.ServerRuntimeRequestParameters;

public class FeaturePermissionRequestAction implements ServerRequestAction {

	ServerConditionsMiner licenseConditionMiner;

	LoggerFactory loggerFactory;

	@Override
	public boolean execute(HttpServletRequest request, HttpServletResponse response) {
		Logger logger = loggerFactory.getLogger(this.getClass().getName());
		if (licenseConditionMiner == null) {
			logger.error("Reference service LicensingComponentAdmin does not recived");
			return false;
		}
		logger.info("[Passage] Execute action: " + this.getClass().getName());
		String clientId = request.getParameter(ServerRuntimeRequestParameters.CLIENT_ID);
		String productId = request.getParameter(ServerRuntimeRequestParameters.CLIENT_PRODUCT_ID);
		String featureId = request.getParameter(ServerRuntimeRequestParameters.CLIENT_FEATURE_ID);
		boolean result = licenseConditionMiner.evaluate(clientId, productId, featureId);
		if (!result) {
			logger.error("[Passage Error] Could not checkout license by param client: " + clientId + " product: "
					+ productId);
		}

		return result;
	}

}
