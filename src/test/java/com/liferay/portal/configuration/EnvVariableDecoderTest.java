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

import static org.junit.Assert.assertEquals;

import io.wedeploy.controller.Constants;

import org.junit.Test;

/**
 * @author Manuel de la Peña
 */
public class EnvVariableDecoderTest {

    @Test
    public void testDecode() {
        String envKey = "LIFERAY_SETUP_PERIOD_WIZARD_PERIOD_ENABLED";

        envKey = envKey.substring(
            Constants.ENV_OVERRIDE_PREFIX.length());

        String newKey = envKey.toLowerCase();

        String decoded = EnvVariableDecoder.decode(newKey);

        assertEquals("setup.wizard.enabled", decoded);
    }

    @Test
    public void testDecodeWithBrackets() {
         String envKey =
            "LIFERAY_SETUP_PERIOD_DATABASE_PERIOD_DRIVER_UPPERCASEC_LASS_" +
                "UPPERCASEN_AME_OPENBRACKET_DB2_CLOSEBRACKET_";

        envKey = envKey.substring(
            Constants.ENV_OVERRIDE_PREFIX.length());

        String newKey = envKey.toLowerCase();

        String decoded = EnvVariableDecoder.decode(newKey);

        assertEquals("setup.database.driverClassName[db2]", decoded);
    }

    @Test
    public void testDecodeWithDash() {
        String envKey =
            "LIFERAY_LAYOUT_PERIOD_STATIC_PERIOD_PORTLETS_PERIOD_START_" +
                "PERIOD_COLUMN_DASH_1_OPENBRACKET_USER_CLOSEBRACKET__" +
                "OPENBRACKET__SLASH_HOME_CLOSEBRACKET_";

        envKey = envKey.substring(
            Constants.ENV_OVERRIDE_PREFIX.length());

        String newKey = envKey.toLowerCase();

        String decoded = EnvVariableDecoder.decode(newKey);

        assertEquals(
            "layout.static.portlets.start.column-1[user][/home]", decoded);
    }

    @Test
    public void testDecodeWithUppercase() {
        String envKey =
            "LIFERAY_JDBC_PERIOD_DRIVER_PERIOD_CLASS_UPPERCASEN_AME";

        envKey = envKey.substring(
            Constants.ENV_OVERRIDE_PREFIX.length());

        String newKey = envKey.toLowerCase();

        String decoded = EnvVariableDecoder.decode(newKey);

        assertEquals("jdbc.driver.className", decoded);
    }

}