package ru.arsysop.passage.lbc.base;

import java.util.ArrayList;
import java.util.List;

import ru.arsysop.passage.lic.runtime.ConditionDescriptor;

public class ConditionsArbitr {

	/**
	 * Descriptors reserved on a minute
	 */
	List<ConditionDescriptor> reservedDescriptors = new ArrayList<>();

	/**
	 * Descriptors in lease on an hour
	 */
	List<ConditionDescriptor> leasedDescriptors = new ArrayList<>();

	public boolean reservCondition(ConditionDescriptor descriptor) {
		
		return false;
	}

	public boolean leaseCondition(ConditionDescriptor descriptor) {
		
		return false;
	}

}
