/*
 * Copyright (c) 2002-2018 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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
package org.neo4j.kernel.impl.index.schema;

import org.neo4j.index.internal.gbptree.ValueMerger;
import org.neo4j.index.internal.gbptree.Writer;

/**
 * {@link ValueMerger} which will merely detect conflict, not change any value if conflict, i.e. if the
 * key already exists. After this merge has been used in a call to {@link Writer#merge(Object, Object, ValueMerger)}
 * the {@link #wasConflict()} accessor can be called to check whether or not that call conflicted with
 * an existing key. A call to {@link #wasConflict()} will also clear the conflict flag.
 *
 * @param <VALUE> type of values being merged.
 */
public class ConflictDetectingValueMerger<KEY extends NativeSchemaKey, VALUE extends NativeSchemaValue> implements ValueMerger<KEY,VALUE>
{
    private boolean conflict;
    private long existingNodeId;
    private long addedNodeId;

    @Override
    public VALUE merge( KEY existingKey, KEY newKey, VALUE existingValue, VALUE newValue )
    {
        if ( existingKey.getEntityId() != newKey.getEntityId() )
        {
            conflict = true;
            existingNodeId = existingKey.getEntityId();
            addedNodeId = newKey.getEntityId();
        }
        return null;
    }

    /**
     * @return whether or not merge conflicted with an existing key. This call also clears the conflict flag.
     */
    public boolean wasConflict()
    {
        boolean result = conflict;
        if ( conflict )
        {
            conflict = false;
        }
        return result;
    }

    public long existingNodeId()
    {
        return existingNodeId;
    }

    public long addedNodeId()
    {
        return addedNodeId;
    }
}
