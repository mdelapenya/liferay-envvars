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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Manuel de la Peña
 */
public class EnvVariableEncoderTest {

    @Test
    public void testDecode() {
        String decoded = EnvVariableEncoder.encode("setup.wizard.enabled");

        assertEquals("LIFERAY_SETUP_PERIOD_WIZARD_PERIOD_ENABLED", decoded);
    }

    @Test
    public void testDecodeWithBrackets() {
        String decoded = EnvVariableEncoder.encode(
            "setup.database.driverClassName[db2]");

        assertEquals(
            "LIFERAY_SETUP_PERIOD_DATABASE_PERIOD_DRIVER_UPPERCASEC_LASS_" +
                "UPPERCASEN_AME_OPENBRACKET_DB2_CLOSEBRACKET_", decoded);
    }

    @Test
    public void testDecodeWithDash() {
        String decoded = EnvVariableEncoder.encode(
            "layout.static.portlets.start.column-1[user][/home]");

        assertEquals(
            "LIFERAY_LAYOUT_PERIOD_STATIC_PERIOD_PORTLETS_PERIOD_START_" +
                "PERIOD_COLUMN_DASH_1_OPENBRACKET_USER_CLOSEBRACKET__" +
                "OPENBRACKET__SLASH_HOME_CLOSEBRACKET_", decoded);
    }

    @Test
    public void testDecodeWithUppercase() {
        String decoded = EnvVariableEncoder.encode("jdbc.driver.className");

        assertEquals(
            "LIFERAY_JDBC_PERIOD_DRIVER_PERIOD_CLASS_UPPERCASEN_AME", decoded);
    }

}