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

import static ru.arsysop.passage.lic.base.LicensingProperties.LICENSING_EXPIRE_TIME;
import static ru.arsysop.passage.lic.base.LicensingProperties.LICENSING_FEATURE_IDENTIFIER;
import static ru.arsysop.passage.lic.base.LicensingProperties.LICENSING_LEASE_TIME;
import static ru.arsysop.passage.lic.base.LicensingProperties.LICENSING_MATCH_RULE;
import static ru.arsysop.passage.lic.base.LicensingProperties.LICENSING_MATCH_VERSION;

import ru.arsysop.passage.lic.runtime.FeaturePermission;

public class ServerFeaturePermission implements FeaturePermission {

	private final String featureId;
	private final String matchVersion;
	private final String matchRule;
	private final long leaseTime;
	private final long expireTime;

	public ServerFeaturePermission(String featureId, String matchVersion, String matchRule, long leaseTime, long expireTime) {
		this.featureId = featureId;
		this.matchVersion = matchVersion;
		this.matchRule = matchRule;
		this.leaseTime = leaseTime;
		this.expireTime = expireTime;
	}

	@Override
	public String getFeatureIdentifier() {
		return featureId;
	}

	@Override
	public String getMatchVersion() {
		return matchVersion;
	}

	@Override
	public String getMatchRule() {
		return matchRule;
	}

	@Override
	public long getLeaseTime() {
		return leaseTime;
	}

	@Override
	public long getExpireTime() {
		return expireTime;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(LICENSING_FEATURE_IDENTIFIER).append('=').append(featureId).append(';');
		sb.append(LICENSING_MATCH_VERSION).append('=').append(matchVersion).append(';');
		sb.append(LICENSING_MATCH_RULE).append('=').append(matchRule).append(';');
		sb.append(LICENSING_LEASE_TIME).append('=').append(leaseTime).append(';');
		sb.append(LICENSING_EXPIRE_TIME).append('=').append(expireTime);
		return sb.toString();
	}

}
