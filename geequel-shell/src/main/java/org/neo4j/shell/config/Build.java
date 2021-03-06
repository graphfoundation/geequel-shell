/*
 * Copyright (c) 2018-2020 "Graph Foundation"
 * Graph Foundation, Inc. [https://graphfoundation.org]
 *
 * This file is part of ONgDB.
 *
 * ONgDB is free software: you can redistribute it and/or modify
 * it underm the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.shell.config;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class provides access to build time variables
 */
public class Build {

    private static Properties props = null;

    /**
     * Reads the build generated properties file the first time it is called.
     *
     * @return build properties
     */
    @Nonnull
    private static Properties getProperties() {
        if (props == null) {
            props = new Properties();
            try (InputStream stream = Build.class.getClassLoader().getResourceAsStream("build.properties")) {
                if (stream == null) {
                    throw new IllegalStateException("Cannot read build.properties");
                } else {
                    props.load(stream);
                }
            } catch (IOException e) {
                System.err.println("Could not read build properties: " + e.getMessage());
            }
        }

        return props;
    }

    /**
     * @return the revision of the source code, or "dev" if no properties file could be read.
     */
    @Nonnull
    public static String version() {
        return getProperties().getProperty("version", "dev");
    }

    /**
     * @return the revision of the Neo4j Driver, or "dev" if no properties file could be read.
     */
    @Nonnull
    public static String driverVersion() {
        return getProperties().getProperty("driverVersion", "dev");
    }
}
