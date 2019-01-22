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

import org.eclipse.passage.lic.runtime.ConditionMiner;
import org.eclipse.passage.lic.runtime.LicensingCondition;
import org.eclipse.passage.lic.runtime.LicensingConfiguration;

import ru.arsysop.passage.lbc.base.BaseComponent;
import ru.arsysop.passage.lbc.server.LicensingConditionStorage;;

public class ServerConditionsMiner extends BaseComponent implements ConditionMiner {
	
	private final List<LicensingConditionStorage> conditionStorages = new ArrayList<>();

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
	public Iterable<LicensingCondition> extractLicensingConditions(LicensingConfiguration configuration) {

		List<LicensingCondition> result = new ArrayList<>();
		if (configuration == null) {
			logger.error("Licensing configuration not defined");
			return result;
		}
		String productIdentifier = configuration.getProductIdentifier();
		String productVersion = configuration.getProductVersion();
		if (productIdentifier == null || productIdentifier.isEmpty()) {
			logger.error("Product identifier not defined");
			return result;
		}
		if (productVersion == null || productVersion.isEmpty()) {
			logger.error("Product version not defined");
			return result;
		}
		for (LicensingConditionStorage storage : conditionStorages) {
			result.addAll(storage.getLicensingCondition(productIdentifier, productVersion));
		}
		return result;
	}
}
