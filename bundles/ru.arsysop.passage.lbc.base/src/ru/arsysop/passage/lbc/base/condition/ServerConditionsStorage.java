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

import ru.arsysop.passage.lbc.base.BaseComponent;
import ru.arsysop.passage.lbc.server.LicensingConditionStorage;
import ru.arsysop.passage.lic.base.BaseLicensingCondition;
import ru.arsysop.passage.lic.base.LicensingConditions;

public class ServerConditionsStorage extends BaseComponent implements LicensingConditionStorage {

	List<BaseLicensingCondition> listConditionDescriptors = new ArrayList<>();

	private final static String SPLITTER = ";";

	@Override
	public void createConditionDescriptors(String conditionValues) {
		String[] values = conditionValues.split(SPLITTER);
		if (values.length == 4) {
			BaseLicensingCondition descriptor = LicensingConditions.create(values[1], "", "", "", "");
			listConditionDescriptors.add(descriptor);
		}
	}

	@Override
	public List<BaseLicensingCondition> getLicensingCondition() {
		return listConditionDescriptors;
	}

}
