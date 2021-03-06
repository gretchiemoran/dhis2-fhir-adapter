package org.dhis2.fhir.adapter.fhir.transform.scripted.program;

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
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.geo.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Scriptable
public class ImmutableScriptedEnrollment implements ScriptedEnrollment, Serializable
{
    private static final long serialVersionUID = 3106142635120155470L;

    private final ScriptedEnrollment delegate;

    public ImmutableScriptedEnrollment( @Nonnull ScriptedEnrollment delegate )
    {
        this.delegate = delegate;
    }

    @Override
    public boolean isNewResource()
    {
        return delegate.isNewResource();
    }

    @Override
    @Nullable
    public String getId()
    {
        return delegate.getId();
    }

    @Override
    @Nullable
    public String getOrganizationUnitId()
    {
        return delegate.getOrganizationUnitId();
    }

    @Override
    @Nullable
    public ZonedDateTime getEnrollmentDate()
    {
        return delegate.getEnrollmentDate();
    }

    @Override
    @Nullable
    public ZonedDateTime getIncidentDate()
    {
        return delegate.getIncidentDate();
    }

    @Override
    @Nullable
    public Location getCoordinate()
    {
        return delegate.getCoordinate();
    }

    @Override
    public void validate() throws TransformerException
    {
        delegate.validate();
    }
}
