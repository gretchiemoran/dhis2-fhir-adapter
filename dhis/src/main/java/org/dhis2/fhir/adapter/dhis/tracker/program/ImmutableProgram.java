package org.dhis2.fhir.adapter.dhis.tracker.program;

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

import org.dhis2.fhir.adapter.dhis.model.Reference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ImmutableProgram implements Program, Serializable
{
    private static final long serialVersionUID = 4157706296470781519L;

    private final Program delegate;

    public ImmutableProgram( @Nonnull Program delegate )
    {
        this.delegate = delegate;
    }

    @Override
    public String getId()
    {
        return delegate.getId();
    }

    @Override
    public String getName()
    {
        return delegate.getName();
    }

    @Override
    public String getCode()
    {
        return delegate.getCode();
    }

    @Override
    public String getTrackedEntityTypeId()
    {
        return delegate.getTrackedEntityTypeId();
    }

    @Override
    public boolean isSelectIncidentDatesInFuture()
    {
        return delegate.isSelectIncidentDatesInFuture();
    }

    @Override
    public boolean isSelectEnrollmentDatesInFuture()
    {
        return delegate.isSelectEnrollmentDatesInFuture();
    }

    @Override
    public boolean isDisplayIncidentDate()
    {
        return delegate.isDisplayIncidentDate();
    }

    @Override
    public boolean isRegistration()
    {
        return delegate.isRegistration();
    }

    @Override
    public boolean isWithoutRegistration()
    {
        return delegate.isWithoutRegistration();
    }

    @Override
    public boolean isCaptureCoordinates()
    {
        return delegate.isCaptureCoordinates();
    }

    @Override
    public List<? extends ProgramTrackedEntityAttribute> getTrackedEntityAttributes()
    {
        return (delegate.getTrackedEntityAttributes() == null) ? null : delegate.getTrackedEntityAttributes().stream().map( ImmutableProgramTrackedEntityAttribute::new ).collect( Collectors.toList() );
    }

    @Override
    public List<? extends ProgramStage> getStages()
    {
        return (delegate.getStages() == null) ? null : delegate.getStages().stream().map( ImmutableProgramStage::new ).collect( Collectors.toList() );
    }

    @Override
    @Nonnull
    public Optional<ProgramStage> getOptionalStage( @Nonnull Reference reference )
    {
        return delegate.getOptionalStage( reference ).map( ImmutableProgramStage::new );
    }

    @Override
    @Nullable
    public ProgramStage getStageByName( @Nonnull String name )
    {
        return delegate.getStageByName( name );
    }
}
