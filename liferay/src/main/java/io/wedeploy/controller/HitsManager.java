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

import com.wedeploy.api.WeDeploy;
import com.wedeploy.api.sdk.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Manuel de la Peña
 */
public class HitsManager {

	public HitsManager(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public Integer incrementHits(String namespacePath, String key) {
		Map<String, Object> keyValue = fetchKeyValue(namespacePath, key);

		Integer hits = (Integer) keyValue.get("hits");

		if (hits == null) {
			hits = 1;
		}
		else {
			hits++;
		}

		keyValue.put("hits", hits);

		saveKeys(namespacePath, key, keyValue);

		return hits;
	}

	/**
	 * Fetch keys (encodes or decodes) from the datastore.
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> fetchKeyValue(String path, String key) {
		Response response = WeDeploy
			.url(dbUrl)
			.path(path + "/" + key)
			.get();

		if (response.statusCode() == 404) {
			return new HashMap<>();
		}

		if (!response.succeeded()) {
			throw new RuntimeException(
				"Fetching keys failed: " + response.statusCode() + " "
					+ response.statusMessage());
		}

		Map<String, Object> keys = response.bodyMap(String.class, Object.class);

		if (keys == null) {
			return new HashMap<>();
		}

		return keys;
	}

	/**
	 * Saves the keys (encodes or decodes) to the datastore.
	 */
	private void saveKeys(String path, String key, Map<String, Object> keys) {
		Response response = WeDeploy
			.url(dbUrl)
			.path(path + "/" + key)
			.put(keys);

		if (!response.succeeded()) {
			throw new RuntimeException(
				"Saving keys failed: " + response.statusCode() + " " +
					response.statusMessage());
		}
	}

	private String dbUrl;

}