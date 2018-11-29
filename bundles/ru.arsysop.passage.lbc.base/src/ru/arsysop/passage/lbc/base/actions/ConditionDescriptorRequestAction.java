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
package ru.arsysop.passage.lbc.base.actions;

import static ru.arsysop.passage.lic.transport.RequestProducer.PARAMETER_CONFIGURATION;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.arsysop.passage.lbc.base.BaseComponent;
import ru.arsysop.passage.lbc.server.ServerRequestAction;
import ru.arsysop.passage.lic.runtime.ConditionDescriptor;
import ru.arsysop.passage.lic.runtime.ConditionMiner;
import ru.arsysop.passage.lic.transport.FloatingConditionDescriptor;
import ru.arsysop.passage.lic.transport.TransferObjectDescriptor;

/**
 * According to AccessManager specification implementation of
 * {@code Iterable<ConditionDescriptor> extractConditions(Object configuration)}
 * {@link ru.arsysop.passage.lic.runtime.AccessManager}
 */
public class ConditionDescriptorRequestAction extends BaseComponent implements ServerRequestAction {

	private static final String ERROR_CONDITIONS_NOT_AVAILABLE = "No condition miners available";

	private static final String MSG_LOG = "Executing action request from class: %s";

	private List<ConditionMiner> licenseConditionMiners = new ArrayList<>();

	@Override
	public boolean execute(HttpServletRequest request, HttpServletResponse response) {
		if (logger == null) {
			return false;
		}
		logger.info(String.format(MSG_LOG, this.getClass().getName()));
		if (licenseConditionMiners.isEmpty()) {
			logger.error(ERROR_CONDITIONS_NOT_AVAILABLE);
			return false;
		}
		try {
			String configuration = request.getParameter(PARAMETER_CONFIGURATION);
			TransferObjectDescriptor transportObject = createTransportObject(configuration);
			RequestActionJsonUtil.responseProcessing(response, transportObject);
			return true;
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	private TransferObjectDescriptor createTransportObject(Object configuration) {
		TransferObjectDescriptor transportObject = new TransferObjectDescriptor();

		for (ConditionMiner miner : licenseConditionMiners) {
			Iterable<ConditionDescriptor> extractConditionDescriptors = miner
					.extractConditionDescriptors(configuration);
			for (ConditionDescriptor descriptor : extractConditionDescriptors) {
				transportObject.addDescriptor((FloatingConditionDescriptor) descriptor);
			}
		}

		return transportObject;
	}

	public void bindConditionMiner(ConditionMiner conditionMiner) {
		this.licenseConditionMiners.add(conditionMiner);
	}

	public void unbindConditionMiner(ConditionMiner conditionMiner) {
		this.licenseConditionMiners.remove(conditionMiner);
	}

}
