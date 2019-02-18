package ru.arsysop.passage.lbc.base;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.passage.lic.base.BaseLicensingCondition;

public class ConditionsArbitr {

	/**
	 * Descriptors reserved on a minute
	 */
	List<BaseLicensingCondition> reservedDescriptors = new ArrayList<>();

	/**
	 * Descriptors in lease on an hour
	 */
	List<BaseLicensingCondition> leasedDescriptors = new ArrayList<>();

	public boolean reservCondition(BaseLicensingCondition descriptor) {

		return false;
	}

	public boolean leaseCondition(BaseLicensingCondition descriptor) {

		return false;
	}

}
