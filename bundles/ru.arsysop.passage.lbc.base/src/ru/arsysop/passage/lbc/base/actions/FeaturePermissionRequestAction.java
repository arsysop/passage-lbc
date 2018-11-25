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

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.arsysop.passage.lbc.base.condition.ServerConditionEvaluator;
import ru.arsysop.passage.lbc.server.ServerRequestAction;
import ru.arsysop.passage.lic.runtime.ConditionDescriptor;
import ru.arsysop.passage.lic.runtime.ConditionEvaluator;
import ru.arsysop.passage.lic.runtime.FeaturePermission;
import ru.arsysop.passage.lic.transport.ServerConditionDescriptor;
import ru.arsysop.passage.lic.transport.ServerFeaturePermission;
import ru.arsysop.passage.lic.transport.TransferObjectDescriptor;

/**
 * According to AccessManager specification implementation of
 * {@code Iterable<FeaturePermission> evaluateConditions()}
 * {@link ru.arsysop.passage.lic.runtime.AccessManager}
 */
public class FeaturePermissionRequestAction implements ServerRequestAction {

	private static final String LICENSING_CONDITION_TYPE_SERVER = "server";
	private static final String LICENSING_CONDITION_TYPE = "licensing.condition.type";

	ServerConditionEvaluator conditionEvaluator;

	LoggerFactory loggerFactory;

	@Override
	public boolean execute(HttpServletRequest request, HttpServletResponse response) {
		Logger logger = loggerFactory.getLogger(this.getClass().getName());
		logger.info("[Passage] Execute action: " + this.getClass().getName());

		// get object from request

		TransferObjectDescriptor transferObject = null;
		ObjectMapper mapper = new ObjectMapper();
		try (InputStream inputContext = request.getInputStream()) {
			transferObject = mapper.readValue(inputContext, TransferObjectDescriptor.class);
			List<ServerConditionDescriptor> descriptors = transferObject.getDescriptors();
			if (descriptors == null || descriptors.isEmpty()) {
				String message = "[Passage] " + this.getClass().getName() + " conditions for evaluate not defined.";
				response.getWriter().println(message);
				logger.error(message);
				return false;
			}

			@SuppressWarnings("unchecked")
			Iterable<FeaturePermission> evaluatePermissions = conditionEvaluator
					.evaluateConditions((List<ConditionDescriptor>) (Object) descriptors);
			TransferObjectDescriptor responseTransferObject = new TransferObjectDescriptor();
			for (FeaturePermission permission : evaluatePermissions) {
				if (permission instanceof ServerFeaturePermission) {
					responseTransferObject.addPermission((ServerFeaturePermission) permission);
				}
			}
			RequestActionUtil.responseProcessing(response, responseTransferObject);
		} catch (Exception e) {
			logger.info(e.getMessage());
			return false;
		}
		return true;
	}

	public void bindServerConditionEvaluator(ConditionEvaluator evaluator, Map<String, String> context) {
		String conditionType = context.get(LICENSING_CONDITION_TYPE);
		if (conditionType.equals(LICENSING_CONDITION_TYPE_SERVER)) {
			if (evaluator instanceof ServerConditionEvaluator) {
				conditionEvaluator = (ServerConditionEvaluator) evaluator;
			}
		}
	}

	public void unbindServerConditionEvaluator(ConditionEvaluator evaluator, Map<String, String> context) {
		conditionEvaluator = null;
	}
}
