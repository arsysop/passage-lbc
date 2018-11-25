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
package ru.arsysop.passage.lic.transport;

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
		return allowedMatchRule;
	}

	@Override
	public String getConditionType() {
		return conditionType;
	}

	@Override
	public String getConditionExpression() {
		return conditionExpression;
	}

}
