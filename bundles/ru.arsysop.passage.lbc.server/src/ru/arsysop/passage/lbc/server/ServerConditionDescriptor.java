package ru.arsysop.passage.lbc.server;

import ru.arsysop.passage.lic.runtime.ConditionDescriptor;

public class ServerConditionDescriptor implements ConditionDescriptor {

	String allowedFeatureId;
	String allowedFeatureMatchVersion;
	String allowedFeatureMatchRule;
	String type;
	String expression;

	public ServerConditionDescriptor() {
	}

	public ServerConditionDescriptor(String featureId, String featureMatchVersion, String featureMatchRule,
			String featureType, String featureExpresssion) {
		super();
		this.allowedFeatureId = featureId;
		this.allowedFeatureMatchVersion = featureMatchVersion;
		this.allowedFeatureMatchRule = featureMatchRule;
		this.type = featureType;
		this.expression = featureExpresssion;
	}

	@Override
	public String getAllowedFeatureId() {
		return allowedFeatureId;
	}

	@Override
	public String getAllowedFeatureMatchVersion() {
		return allowedFeatureMatchVersion;
	}

	@Override
	public String getAllowedFeatureMatchRule() {
		// TODO Auto-generated method stub
		return allowedFeatureMatchRule;
	}

	@Override
	public String getConditionType() {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public String getConditionExpression() {
		// TODO Auto-generated method stub
		return expression;
	}

}
