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
package org.neo4j.shell;

import static java.lang.String.format;

@SuppressWarnings( "WeakerAccess" )
public class Version implements Comparable<Version>
{
    private final int major;
    private final int minor;
    private final int patch;

    Version( int major, int minor, int patch )
    {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public int major()
    {
        return major;
    }

    public int minor()
    {
        return minor;
    }

    public int patch()
    {
        return patch;
    }

    @Override
    public int compareTo( Version o )
    {
        int comp = Integer.compare( major, o.major );
        if ( comp == 0 )
        {
            comp = Integer.compare( minor, o.minor );
            if ( comp == 0 )
            {
                comp = Integer.compare( patch, o.patch );
            }
        }
        return comp;
    }

    @Override
    public String toString()
    {
        return format( "%d.%d.%d", major, minor, patch );
    }
}
