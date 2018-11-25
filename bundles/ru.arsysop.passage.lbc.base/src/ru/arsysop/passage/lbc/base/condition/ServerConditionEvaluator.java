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
