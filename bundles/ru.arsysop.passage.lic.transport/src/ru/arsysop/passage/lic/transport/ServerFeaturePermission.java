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
