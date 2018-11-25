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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import ru.arsysop.passage.lbc.server.ServerRequestAction;
import ru.arsysop.passage.lic.runtime.ConditionDescriptor;
import ru.arsysop.passage.lic.runtime.ConditionMiner;
import ru.arsysop.passage.lic.transport.ServerConditionDescriptor;
import ru.arsysop.passage.lic.transport.TransferObjectDescriptor;

/**
 * According to AccessManager specification implementation of
 * {@code Iterable<ConditionDescriptor> extractConditions(Object configuration)}
 * {@link ru.arsysop.passage.lic.runtime.AccessManager}
 */
public class ConditionDescriptorRequestAction implements ServerRequestAction {

	private static final String MSG_LOG = "Execute action class:";

	private Logger logger;

	private List<ConditionMiner> licenseConditionMiners = new ArrayList<>();

	@Override
	public boolean execute(HttpServletRequest request, HttpServletResponse response) {
		if (logger == null) {
			return false;
		}
		if (licenseConditionMiners == null || licenseConditionMiners.isEmpty()) {
			logger.error(ConditionMiner.class.getName() + " miners not available or defined");
			return false;
		}
		logger.info(MSG_LOG + this.getClass().getName());

		TransferObjectDescriptor transportObject = createTransportObject();
		try {
			RequestActionUtil.responseProcessing(response, transportObject);
		} catch (IOException e) {
			logger.error(e.getMessage());
			return false;
		}

		return true;
	}

	private TransferObjectDescriptor createTransportObject() {
		TransferObjectDescriptor transportObject = new TransferObjectDescriptor();

		for (ConditionMiner miner : licenseConditionMiners) {
			Iterable<ConditionDescriptor> extractConditionDescriptors = miner.extractConditionDescriptors(null);
			for (ConditionDescriptor descriptor : extractConditionDescriptors) {
				transportObject.addDescriptor((ServerConditionDescriptor) descriptor);
			}
		}

		return transportObject;
	}

	public void bindLogger(LoggerFactory loggerFactory) {
		this.logger = loggerFactory.getLogger(this.getClass().getName());
	}

	public void unbindLogger(LoggerFactory loggerFactory) {
		this.logger = null;
	}

	public void bindConditionMiner(ConditionMiner conditionMiner, Map<String, String> context) {
		this.licenseConditionMiners.add(conditionMiner);
	}

	public void unbindConditionMiner(ConditionMiner conditionMiner, Map<String, String> context) {
		this.licenseConditionMiners.remove(conditionMiner);
	}

}
