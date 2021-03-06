package org.dhis2.fhir.adapter.fhir.transform.model;

/*
 * Copyright (c) 2004-2018, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.dhis2.fhir.adapter.Scriptable;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Scriptable
public class ImmutableFhirRequest implements FhirRequest, Serializable
{
    private static final long serialVersionUID = 8079249171843824509L;

    private final FhirRequest delegate;

    public ImmutableFhirRequest( @Nonnull FhirRequest delegate )
    {
        this.delegate = delegate;
    }

    @Nullable
    @Override
    public FhirRequestMethod getRequestMethod()
    {
        return delegate.getRequestMethod();
    }

    @Nullable
    @Override
    public FhirResourceType getResourceType()
    {
        return delegate.getResourceType();
    }

    @Nullable
    @Override
    public String getResourceId()
    {
        return delegate.getResourceId();
    }

    @Override
    @Nullable
    public String getResourceVersionId()
    {
        return delegate.getResourceVersionId();
    }

    @Override
    @Nullable
    public ZonedDateTime getLastUpdated()
    {
        return delegate.getLastUpdated();
    }

    @Nonnull
    @Override
    public FhirVersion getVersion()
    {
        return delegate.getVersion();
    }

    @Override
    @Nullable
    public String getDhisUsername()
    {
        return delegate.getDhisUsername();
    }

    @Override
    public boolean isRemoteSubscription()
    {
        return delegate.isRemoteSubscription();
    }

    @Override
    @Nullable
    public UUID getRemoteSubscriptionResourceId()
    {
        return delegate.getRemoteSubscriptionResourceId();
    }

    @Nullable
    @Override
    public ResourceSystem getResourceSystem( @Nonnull FhirResourceType resourceType )
    {
        return delegate.getResourceSystem( resourceType );
    }

    @Nonnull
    @Override
    public Optional<ResourceSystem> getOptionalResourceSystem( @Nonnull FhirResourceType resourceType )
    {
        return delegate.getOptionalResourceSystem( resourceType );
    }
}
