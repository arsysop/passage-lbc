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
package ru.arsysop.passage.lbc.base.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import ru.arsysop.passage.lic.runtime.ConditionDescriptor;
import ru.arsysop.passage.lic.runtime.ConditionMiner;

import ru.arsysop.passage.lbc.server.LicensingConditionStorage;
import ru.arsysop.passage.lbc.server.ServerRuntimeRequestParameters;


public class ServerConditionsMiner implements ConditionMiner {

	List<LicensingConditionStorage> conditionStorages = new ArrayList<>();
	private Logger logger;

	public boolean checkProductById(String productId) {

		return false;
	}

	public void bindLogger(LoggerFactory loggerFactory) {
		this.logger = loggerFactory.getLogger(this.getClass().getName());
	}

	public void unbindLogger(LoggerFactory loggerFactory) {
		this.logger = null;
	}

	public void bindLicensingComponent(LicensingConditionStorage conditionStorage, Map<String, String> context) {
		logger.debug(conditionStorage.getClass().getName());

		String conditions = context.get(ServerRuntimeRequestParameters.LICENSING_DATA);

		if (conditions != null && !conditions.isEmpty()) {
			String conditionDatas[] = conditions.split(",");
			for (String condition : conditionDatas) {
				conditionStorage.createConditionDescriptors(condition);
				if (!conditionStorages.contains(conditionStorage)) {
					conditionStorages.add(conditionStorage);
				}
			}
		}
	}

	public void unbindLicensingComponent(LicensingConditionStorage conditionStorage, Map<String, String> context) {
		logger.debug(conditionStorage.getClass().getName());

		String conditions = context.get(ServerRuntimeRequestParameters.LICENSING_DATA);

		if (conditions != null && !conditions.isEmpty()) {
			String conditionDatas[] = conditions.split(",");
			for (String condition : conditionDatas) {
				conditionStorage.createConditionDescriptors(condition);
				if (conditionStorages.contains(conditionStorage)) {
					conditionStorages.remove(conditionStorage);
				}
			}
		}
	}

	@Override
	public Iterable<ConditionDescriptor> extractConditionDescriptors(Object configuration) {
		List<ConditionDescriptor> result = new ArrayList<>();
		for (LicensingConditionStorage storage : conditionStorages) {
			result.addAll(storage.getConditionDescriptors());
		}
		return result;
	}

	public boolean evaluate(String clientId, String productId, String featureId) {
		// TODO Auto-generated method stub
		return false;
	}

}
