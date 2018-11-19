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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import ru.arsysop.passage.lbc.server.ServerRequestAction;

public class ValidateActionRequest implements ServerRequestAction {

	private Logger logger;
	private static final String MSG_LOG = "Execute action class:";

	@Override
	public boolean execute(HttpServletRequest request, HttpServletResponse response) {
		if (logger != null) {
			logger.info(MSG_LOG + this.getClass().getName());
		}
		return false;
	}

	public void bindLogger(LoggerFactory loggerFactory) {
		this.logger = loggerFactory.getLogger(this.getClass().getName());
	}

	public void unbindLogger(LoggerFactory loggerFactory) {
		this.logger = null;
	}
}
