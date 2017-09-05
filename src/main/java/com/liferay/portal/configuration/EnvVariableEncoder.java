/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
 
package com.liferay.portal.configuration;

import io.wedeploy.controller.WeDeployController;

/**
 * @author Manuel de la Peña
 */
public class EnvVariableEncoder {

    public static String encode(String portalProperty) {
		StringBuilder sb = new StringBuilder();

		sb.append(WeDeployController.ENV_OVERRIDE_PREFIX);

		char[] chars = portalProperty.toCharArray();

		for (char c : chars) {
			if (Character.isUpperCase(c)) {
				sb.append("_UPPERCASE");
				sb.append(Character.toUpperCase(c));
				sb.append("_");
			}
			else if (c == CharPool.CLOSE_BRACKET) {
				sb.append("_CLOSEBRACKET_");
			}
			else if (c == CharPool.DASH) {
				sb.append("_DASH_");
			}
			else if (c == CharPool.OPEN_BRACKET) {
				sb.append("_OPENBRACKET_");
			}
			else if (c == CharPool.PERIOD) {
				sb.append("_PERIOD_");
			}
			else if (c == CharPool.SLASH) {
				sb.append("_SLASH_");
			}
			else {
				sb.append(Character.toUpperCase(c));
			}
		}

		return sb.toString();
	}

}