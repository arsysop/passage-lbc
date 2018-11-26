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

import ru.arsysop.passage.lic.runtime.ConditionDescriptor;
import ru.arsysop.passage.lic.runtime.ConditionEvaluator;
import ru.arsysop.passage.lic.runtime.ConditionMiner;
import ru.arsysop.passage.lic.runtime.FeaturePermission;
import ru.arsysop.passage.lic.transport.ServerFeaturePermission;

public class ServerConditionEvaluator implements ConditionEvaluator {

	List<ConditionMiner> miners = new ArrayList<>();
	List<ConditionDescriptor> lockedConditions = new ArrayList<>();

	@Override
	public Iterable<FeaturePermission> evaluateConditions(Iterable<ConditionDescriptor> conditions) {
		List<FeaturePermission> permitions = new ArrayList<>();

		for (ConditionDescriptor condition : conditions) {
			boolean conditionExists = checkExistense(condition);
			if (conditionExists) {
				boolean conditionIsLocked = lockedConditions.contains(condition);
				if (!conditionIsLocked) {
					FeaturePermission createFeaturePermition = createFeaturePermission(condition);
					permitions.add(createFeaturePermition);
					lockCondition(condition);
				}
			}
		}
		return permitions;
	}

	private void lockCondition(ConditionDescriptor condition) {
		lockedConditions.add(condition);
	}

	private FeaturePermission createFeaturePermission(ConditionDescriptor condition) {
		long leaseTime = System.currentTimeMillis();
		long expireTime = leaseTime + 60 * 60 * 1000;
		String featureId = condition.getAllowedFeatureId();
		String matchVersion = condition.getAllowedMatchVersion();
		String matchRule = condition.getAllowedMatchRule();
		ServerFeaturePermission permission = new ServerFeaturePermission(featureId, matchVersion, matchRule, leaseTime,
				expireTime);
		return (FeaturePermission) permission;

	}

	private boolean checkExistense(ConditionDescriptor condition) {
		for (ConditionMiner miner : miners) {
			for (ConditionDescriptor extractedCondition : miner.extractConditionDescriptors(null)) {
				if (condition.equals(extractedCondition)) {
					return true;
				}
			}
		}
		return false;
	}

	public void bindConditionMiner(ConditionMiner miner, Map<String, String> context) {
		if (!miners.contains(miner)) {
			miners.add(miner);
		}
	}

	public void unbindConditionMiner(ConditionMiner miner, Map<String, String> context) {
		if (miners.contains(miner)) {
			miners.remove(miner);
		}
	}

}
