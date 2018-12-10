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

import static ru.arsysop.passage.lic.net.RequestParameters.PRODUCT_IDETIFIER;
import static ru.arsysop.passage.lic.net.RequestParameters.PRODUCT_VERSION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.arsysop.passage.lbc.base.BaseComponent;
import ru.arsysop.passage.lbc.server.LicensingConditionStorage;
import ru.arsysop.passage.lic.runtime.ConditionMiner;
import ru.arsysop.passage.lic.runtime.LicensingCondition;;

public class ServerConditionsMiner extends BaseComponent implements ConditionMiner {
	private static final String PRODUCT_VERSION_NOT_DEF = "Product version not defined";
	private static final String PRODUCT_IDETIFIER_NOT_DEF = "Product identifier not defined";
	List<LicensingConditionStorage> conditionStorages = new ArrayList<>();

	public boolean checkProductById(String productId) {
		return false;
	}

	public void bindLicensingConditionStorage(LicensingConditionStorage conditionStorage) {
		logger.debug(conditionStorage.getClass().getName());
		if (conditionStorage != null) {
			if (!conditionStorages.contains(conditionStorage)) {
				conditionStorages.add(conditionStorage);
			}

		}
	}

	public void unbindLicensingConditionStorage(LicensingConditionStorage conditionStorage) {
		logger.debug(conditionStorage.getClass().getName());

		if (conditionStorage != null) {
			if (conditionStorages.contains(conditionStorage)) {
				conditionStorages.remove(conditionStorage);
			}
		}
	}

	@Override
	public Iterable<LicensingCondition> extractLicensingConditions(Object configuration) {

		List<LicensingCondition> result = new ArrayList<>();

		if (configuration instanceof HashMap<?, ?>) {
			Map<String, String> configurationMap = (HashMap<String, String>) configuration;
			String productId = configurationMap.get(PRODUCT_IDETIFIER);
			String productVersion = configurationMap.get(PRODUCT_VERSION);

			if (productId == null || productId.isEmpty()) {
				logger.error(PRODUCT_IDETIFIER_NOT_DEF);
				return result;
			}
			if (productVersion == null || productVersion.isEmpty()) {
				logger.error(PRODUCT_VERSION_NOT_DEF);
				return result;
			}
			for (LicensingConditionStorage storage : conditionStorages) {
				result.addAll(storage.getLicensingCondition(productId, productVersion));
			}
		}
		return result;
	}
}
