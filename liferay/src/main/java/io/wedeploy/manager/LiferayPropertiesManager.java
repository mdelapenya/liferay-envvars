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

package io.wedeploy.manager;

import io.wedeploy.controller.Constants;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.Properties;

/**
 * @author Manuel de la Peña
 */
public final class LiferayPropertiesManager {

	public static final LiferayPropertiesManager getInstance() {
		return _instance;
	}

	public boolean containsKey(String key) {
		return _liferayProperties.containsKey(key);
	}

	private LiferayPropertiesManager() {
		try (InputStream in =
				new URL(
					Constants.GITHUB_PORTAL_PROPERTIES_RAW_URL).openStream()) {

			_liferayProperties = new Properties();

			_liferayProperties.load(in);
		}
		catch (IOException e) {
			System.err.println(
				"Could not fetch properties file from Github, properties " +
					"will be empty");

			_liferayProperties = new Properties();
		}
	}

	private static final LiferayPropertiesManager _instance =
		new LiferayPropertiesManager();

	private Properties _liferayProperties;

}