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
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


import ru.arsysop.passage.lbc.server.ServerConditionDescriptor;
import ru.arsysop.passage.lbc.server.ServerRequestAction;
import ru.arsysop.passage.lbc.server.ServerTransferObject;
import ru.arsysop.passage.lic.runtime.ConditionDescriptor;
import ru.arsysop.passage.lic.runtime.ConditionMiner;

public class ConditionDescriptorRequestAction implements ServerRequestAction {

	private static final String CHARSET_UTF_8 = "UTF-8";
	private static final String APPLICATION_JSON = "application/json";
	private static final String MSG_LOG = "Execute action class:";

	private Logger logger;
	private ConditionMiner licenseConditionMiner;

	@Override
	public boolean execute(HttpServletRequest request, HttpServletResponse response) {
		if (logger == null) {
			return false;
		}
		if (licenseConditionMiner == null) {
			logger.error(ConditionMiner.class.getName() + " not bind to action");
			return false;
		}
		logger.debug(MSG_LOG + this.getClass().getName());

		ServerTransferObject transportObject = createTransportObject();
		boolean responseResult = responseProcessing(response, transportObject);
		return responseResult;
	}

	private boolean responseProcessing(HttpServletResponse response, ServerTransferObject transportObject) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		try {
			PrintWriter printerWriter = response.getWriter();
			response.setContentType(APPLICATION_JSON);
			response.setCharacterEncoding(CHARSET_UTF_8);
			printerWriter.print(mapper.writeValueAsString(transportObject));
			printerWriter.flush();
			return true;
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	private ServerTransferObject createTransportObject() {
		ServerTransferObject transportObject = new ServerTransferObject();
		Iterable<ConditionDescriptor> extractConditionDescriptors = licenseConditionMiner
				.extractConditionDescriptors(null);
		for (ConditionDescriptor d : extractConditionDescriptors) {
			transportObject.addDescriptors((ServerConditionDescriptor) d);
		}
		return transportObject;
	}

	public void bindLogger(LoggerFactory loggerFactory) {
		this.logger = loggerFactory.getLogger(this.getClass().getName());
	}

	public void unbindLogger(LoggerFactory loggerFactory) {
		this.logger = null;
	}

	public void bindLicenseComponentAdmin(ConditionMiner licenseAdmin, Map<String, String> context) {
		this.licenseConditionMiner = licenseAdmin;
	}

	public void unbindLicenseComponentAdmin(ConditionMiner licenseAdmin, Map<String, String> context) {
		this.licenseConditionMiner = null;
	}

}
