/*
 * Copyright (c) 2002-2016 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.ha;

import org.neo4j.helpers.Provider;
import org.neo4j.kernel.IdGeneratorFactory;
import org.neo4j.kernel.api.KernelAPI;
import org.neo4j.kernel.ha.cluster.AbstractModeSwitcher;
import org.neo4j.kernel.ha.cluster.ModeSwitcherNotifier;
import org.neo4j.kernel.ha.com.RequestContextFactory;
import org.neo4j.kernel.ha.com.master.Master;
import org.neo4j.kernel.impl.core.DefaultPropertyTokenCreator;
import org.neo4j.kernel.impl.core.TokenCreator;
import org.neo4j.kernel.lifecycle.LifeSupport;

public class PropertyKeyCreatorModeSwitcher extends AbstractModeSwitcher<TokenCreator>
{
    private final DelegateInvocationHandler<Master> master;
    private final RequestContextFactory requestContextFactory;
    private final Provider<KernelAPI> kernelProvider;
    private final IdGeneratorFactory idGeneratorFactory;

    public PropertyKeyCreatorModeSwitcher( ModeSwitcherNotifier modeSwitcherNotifier,
                                           DelegateInvocationHandler<TokenCreator> delegate,
                                           DelegateInvocationHandler<Master> master,
                                           RequestContextFactory requestContextFactory,
                                           Provider<KernelAPI> kernelProvider, IdGeneratorFactory idGeneratorFactory )
    {
        super( modeSwitcherNotifier, delegate );
        this.master = master;
        this.requestContextFactory = requestContextFactory;
        this.kernelProvider = kernelProvider;
        this.idGeneratorFactory = idGeneratorFactory;
    }

    @Override
    protected TokenCreator getMasterImpl( LifeSupport life )
    {
        return new DefaultPropertyTokenCreator( kernelProvider, idGeneratorFactory );
    }

    @Override
    protected TokenCreator getSlaveImpl( LifeSupport life )
    {
        return new SlavePropertyTokenCreator( master.cement(), requestContextFactory );
    }
}
