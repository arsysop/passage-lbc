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
import ru.arsysop.passage.lic.base.FeaturePermissions;
import ru.arsysop.passage.lic.net.TimeConditions;
import ru.arsysop.passage.lic.runtime.ConditionEvaluator;
import ru.arsysop.passage.lic.runtime.ConditionMiner;
import ru.arsysop.passage.lic.runtime.FeaturePermission;
import ru.arsysop.passage.lic.runtime.LicensingCondition;

public class ServerConditionsDistributor extends BaseComponent implements ConditionEvaluator {

	List<ConditionMiner> miners = new ArrayList<>();
	List<LicensingCondition> lockedConditions = new ArrayList<>();
	List<ConditionTimerTask> conditionTasks = new ArrayList<>();

	@Override
	public Iterable<FeaturePermission> evaluateConditions(Iterable<LicensingCondition> conditions) {
		List<FeaturePermission> permissionsResult = new ArrayList<>();

		for (LicensingCondition condition : conditions) {
			boolean conditionExists = checkExistense(condition);
			if (conditionExists) {

				synchronized (condition) {

					// lease on time period
					if (condition.getConditionType() == TimeConditions.CONDITION_TYPE_TIME) {

						boolean conditionIsLocked = lockedConditions.contains(condition);
						if (!conditionIsLocked) {
							FeaturePermission createFeaturePermition = createFeaturePermission(condition);
							launchFeaturePermissionTask(condition, createFeaturePermition);
							lockCondition(condition);
							permissionsResult.add(createFeaturePermition);
						}
					}
				}

			}
		}
		return permissionsResult;
	}

	private void launchFeaturePermissionTask(LicensingCondition condition, FeaturePermission createFeaturePermition) {

		String conditionLeaseTime = condition.getConditionExpression();

		ConditionTimerTask task = new ConditionTimerTask(conditionLeaseTime) {

			@Override
			void timeExpired() {
				unlockCondition(condition);
			}
		};

		conditionTasks.add(task);

		task.run();
	}

	private synchronized void unlockCondition(LicensingCondition condition) {
		if (lockedConditions.contains(condition)) {
			lockedConditions.remove(condition);
		}
	}

	private synchronized void lockCondition(LicensingCondition condition) {
		lockedConditions.add(condition);
	}

	private FeaturePermission createFeaturePermission(LicensingCondition condition) {
		long leaseTime = System.currentTimeMillis();
		long expireTime = leaseTime + 60 * 60 * 1000;
		FeaturePermission permission = FeaturePermissions.create(condition, leaseTime, expireTime);
		return permission;

	}

	private boolean checkExistense(LicensingCondition condition) {
		for (ConditionMiner miner : miners) {
			for (LicensingCondition extractedCondition : miner.extractLicensingConditions(condition)) {
				if (condition.equals(extractedCondition)) {
					return true;
				}
			}
		}
		return false;
	}

	public void bindConditionMiner(ConditionMiner miner) {
		if (!miners.contains(miner)) {
			miners.add(miner);
		}
	}

	public void unbindConditionMiner(ConditionMiner miner) {
		if (miners.contains(miner)) {
			miners.remove(miner);
		}
	}

}
