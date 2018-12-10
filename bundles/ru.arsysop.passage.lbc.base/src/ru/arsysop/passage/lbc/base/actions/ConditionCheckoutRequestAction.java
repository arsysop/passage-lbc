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

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.arsysop.passage.lbc.base.BaseComponent;
import ru.arsysop.passage.lbc.base.condition.ServerConditionsDistributor;
import ru.arsysop.passage.lbc.server.ServerRequestAction;
import ru.arsysop.passage.lic.runtime.ConditionEvaluator;
import ru.arsysop.passage.lic.runtime.FeaturePermission;
import ru.arsysop.passage.lic.runtime.LicensingCondition;
import ru.arsysop.passage.lic.runtime.io.FeaturePermissionTransport;
import ru.arsysop.passage.lic.runtime.io.LicensingConditionTransport;

/**
 * According to AccessManager specification implementation of
 * {@code Iterable<FeaturePermission> evaluateConditions()}
 * {@link ru.arsysop.passage.lic.runtime.AccessManager}
 */
public class ConditionCheckoutRequestAction extends BaseComponent implements ServerRequestAction {

	private static final String CHARSET_UTF_8 = "UTF-8"; // NLS-$1
	private static final String APPLICATION_JSON = "application/json"; // NLS-$1

	private static final String CONDITIONS_FOR_EVALUATE_NOT_DEFINED_ERROR = "Conditions for evaluate not defined.";
	private static final String PASSAGE_EXECUTE_TXT = "[Passage] Execute action: %s ";
	private static final String LICENSING_CONDITION_TYPE_SERVER = "server";
	private static final String LICENSING_CONTENT_TYPE = "licensing.content.type"; // NLS-$1

	ServerConditionsDistributor conditionEvaluator;

	private Map<String, FeaturePermissionTransport> mapPermission2Transport = new HashMap<>();
	private Map<String, LicensingConditionTransport> mapCondition2Transport = new HashMap<>();

	@Override
	public boolean execute(HttpServletRequest request, HttpServletResponse response) {
		logger.info(String.format(PASSAGE_EXECUTE_TXT, this.getClass().getName()));
		try {

			String contentType = request.getContentType();
			LicensingConditionTransport transport = mapCondition2Transport.get(contentType);
			if (transport == null) {
				logger.error(String.format("LicensingConditionTransport not defined for contentType: %s", contentType));
				return false;
			}
			Iterable<LicensingCondition> descriptors = transport.readConditionDescriptors(request.getInputStream());

			if (descriptors == null) {
				response.getWriter().println(CONDITIONS_FOR_EVALUATE_NOT_DEFINED_ERROR);
				logger.error(CONDITIONS_FOR_EVALUATE_NOT_DEFINED_ERROR);
				return false;
			}

			Iterable<FeaturePermission> evaluatePermissions = conditionEvaluator.evaluateConditions(descriptors);

			FeaturePermissionTransport transportPermission = mapPermission2Transport.get(contentType);
			if (transportPermission == null) {
				logger.error(String.format("FeaturePermissionTransport not defined for contentType: %s", contentType));
				return false;
			}
			transportPermission.writeFeaturePermissions(evaluatePermissions, response.getOutputStream());

			response.setContentType(APPLICATION_JSON);
			response.setCharacterEncoding(CHARSET_UTF_8);
			PrintWriter printerWriter = response.getWriter();
			printerWriter.flush();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	public void bindServerConditionEvaluator(ConditionEvaluator evaluator, Map<String, String> context) {
		String conditionType = context.get(LICENSING_CONTENT_TYPE);
		if (conditionType != null && conditionType.equals(LICENSING_CONDITION_TYPE_SERVER)) {
			if (evaluator instanceof ServerConditionsDistributor) {
				conditionEvaluator = (ServerConditionsDistributor) evaluator;
			}
		}
	}

	public void unbindServerConditionEvaluator(ConditionEvaluator evaluator, Map<String, String> context) {
		String conditionType = context.get(LICENSING_CONTENT_TYPE);
		if (conditionType != null && conditionType.equals(LICENSING_CONDITION_TYPE_SERVER)) {
			if (evaluator instanceof ServerConditionsDistributor) {
				conditionEvaluator = null;
			}
		}
	}

	public void bindFeaturePermissionTransport(FeaturePermissionTransport transport, Map<String, String> context) {
		String conditionType = context.get(LICENSING_CONTENT_TYPE);
		if (conditionType != null) {
			mapPermission2Transport.put(conditionType, transport);
		}
	}

	public void bindLicensingConditionTransport(LicensingConditionTransport transport, Map<String, String> context) {
		String conditionType = context.get(LICENSING_CONTENT_TYPE);
		if (conditionType != null) {
			mapCondition2Transport.put(conditionType, transport);
		}
	}
}
