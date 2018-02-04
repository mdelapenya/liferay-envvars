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

package io.wedeploy.controller;

import com.liferay.portal.configuration.EnvVariableDecoder;
import com.liferay.portal.configuration.EnvVariableEncoder;

import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Manuel de la Peña
 */
@RestController
@RequestMapping("/api")
public class WeDeployAPIController {

	public WeDeployAPIController() {
		Map<String, String> env = System.getenv();

		while (dbUrl == null) {
			if (env.containsKey("WEDEPLOY_DB_URL")) {
				dbUrl = env.get("WEDEPLOY_DB_URL");

				hitsManager = new HitsManager(dbUrl);
			}
			else {
				System.out.println(
					"Please set up an environment variable with the URL to " +
						"the datastore service, with key WEDEPLOY_DB_URL");

				try {
					Thread.sleep(5000);
				}
				catch (InterruptedException e) {
					throw new RuntimeException(
						"Cannot interrupt process while waiting for proper " +
							"environment configuration");
				}
			}
		}
	}

	@RequestMapping(value = "/decode/{key}", method = RequestMethod.GET)
	public String decode(@PathVariable("key") String key) {
		if (key == null || key.isEmpty() ||
			!key.startsWith(Constants.ENV_OVERRIDE_PREFIX)) {

			throw new IllegalArgumentException(
				"Liferay Env Vars start with LIFERAY_ prefix");
		}

		String envKey = key.substring(
			Constants.ENV_OVERRIDE_PREFIX.length());

		String decodedKey = EnvVariableDecoder.decode(envKey.toLowerCase());

		hitsManager.incrementHits(Constants.DECODES_PATH, key);

		return decodedKey;
	}

	@RequestMapping(value = "/encode/{key}", method = RequestMethod.GET)
	public String encode(@PathVariable("key") String key) {
		if (key == null || key.isEmpty()) {

			throw new IllegalArgumentException(
				"Please add a Liferay Portal property");
		}

		String encodedKey = EnvVariableEncoder.encode(key);

		hitsManager.incrementHits(Constants.ENCODES_PATH, key);

		return encodedKey;
	}

	private String dbUrl;
	private HitsManager hitsManager;

}