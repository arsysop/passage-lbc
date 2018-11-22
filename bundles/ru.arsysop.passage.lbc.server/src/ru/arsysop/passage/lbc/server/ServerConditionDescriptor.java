package ru.arsysop.passage.lbc.server;

import ru.arsysop.passage.lic.runtime.ConditionDescriptor;

public class ServerConditionDescriptor implements ConditionDescriptor {

	String allowedFeatureId;
	String allowedMatchVersion;
	String allowedMatchRule;
	String conditionType;
	String conditionExpression;

	public ServerConditionDescriptor() {
	}

	public ServerConditionDescriptor(String id, String version, String rule, String type, String expresssion) {
		super();
		this.allowedFeatureId = id;
		this.allowedMatchVersion = version;
		this.allowedMatchRule = rule;
		this.conditionType = type;
		this.conditionExpression = expresssion;
	}

	@Override
	public String getAllowedFeatureId() {
		return allowedFeatureId;
	}

	@Override
	public String getAllowedMatchVersion() {
		return allowedMatchVersion;
	}

	@Override
	public String getAllowedMatchRule() {
		// TODO Auto-generated method stub
		return allowedMatchRule;
	}

	@Override
	public String getConditionType() {
		// TODO Auto-generated method stub
		return conditionType;
	}

	@Override
	public String getConditionExpression() {
		// TODO Auto-generated method stub
		return conditionExpression;
	}

}
