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

package ru.arsysop.passage.lbc.server;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.http.client.utils.URIBuilder;

public class RequestCreator {

	private static Logger LOG = Logger.getLogger(RequestCreator.class.getName());

	public static URIBuilder createRequestUriBuilder(Map<String, String> attributes) {
		String host = "";
		String port = "";
		Object hostAttr = attributes.get(ServerRuntimeRequestParameters.HOST);
		Object portAttr = attributes.get(ServerRuntimeRequestParameters.PORT);
		if (hostAttr instanceof String) {
			host = (String) hostAttr;
		} else {
			LOG.info("Host value undefined.");
			return null;
		}

		if (portAttr instanceof String) {
			port = (String) portAttr;
		} else {
			LOG.info("Port value undefined.");
			return null;
		}

		String requestHead = String.format("%s://%s:%s", ServerRuntimeRequestParameters.PROTOCOL_TYPE_ID, host, port);
		URIBuilder builder;
		try {
			builder = new URIBuilder(requestHead);
			for (Entry<String, String> entry : attributes.entrySet()) {
				if (entry.getKey().equals(ServerRuntimeRequestParameters.HOST)
						|| entry.getKey().equals(ServerRuntimeRequestParameters.PORT)) {
					continue;
				}
				builder.setParameter(entry.getKey(), entry.getValue());
			}
			return builder;

		} catch (URISyntaxException e) {
			LOG.info(e.getMessage());
		}
		return null;
	}

}
