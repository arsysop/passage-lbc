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

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import ru.arsysop.passage.lic.transport.TransferObjectDescriptor;

public class RequestActionJsonUtil {
	private static final String CHARSET_UTF_8 = "UTF-8"; // NLS-$1
	private static final String APPLICATION_JSON = "application/json"; // NLS-$1

	public static void responseProcessing(HttpServletResponse response, TransferObjectDescriptor transportObject)
			throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		PrintWriter printerWriter = response.getWriter();
		response.setContentType(APPLICATION_JSON);
		response.setCharacterEncoding(CHARSET_UTF_8);
		printerWriter.print(mapper.writeValueAsString(transportObject));
		printerWriter.flush();

	}
}
