package org.dhis2.fhir.adapter.dhis.tracker.program.impl;

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

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import org.dhis2.fhir.adapter.dhis.DhisConflictException;
import org.dhis2.fhir.adapter.dhis.DhisImportUnsuccessfulException;
import org.dhis2.fhir.adapter.dhis.model.ImportStatus;
import org.dhis2.fhir.adapter.dhis.model.ImportSummaries;
import org.dhis2.fhir.adapter.dhis.model.ImportSummaryWebMessage;
import org.dhis2.fhir.adapter.dhis.model.Status;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of {@link EnrollmentService}.
 *
 * @author volsch
 */
@Service
public class EnrollmentServiceImpl implements EnrollmentService
{
    protected static final String ENROLLMENTS_URI = "/enrollments.json";

    protected static final String ENROLLMENT_UPDATE_URI = "/enrollments/{id}.json?mergeMode=MERGE";

    protected static final String LATEST_ACTIVE_URI = "/enrollments.json?" +
        "program={programId}&programStatus=ACTIVE&trackedEntityInstance={trackedEntityInstanceId}&" +
        "ouMode=ACCESSIBLE&fields=:all&order=lastUpdated:desc&pageSize=1";

    private final RestTemplate restTemplate;

    @Autowired
    public EnrollmentServiceImpl( @Nonnull @Qualifier( "userDhis2RestTemplate" ) RestTemplate restTemplate )
    {
        this.restTemplate = restTemplate;
    }

    @CacheResult( cacheKeyMethod = "getLatestActiveCacheKey" )
    @HystrixCommand( commandProperties = @HystrixProperty( name = "requestCache.enabled", value = "true" ) )
    @Nonnull
    @Override
    public Optional<Enrollment> getLatestActive( @CacheKey @Nonnull String programId, @CacheKey @Nonnull String trackedEntityInstanceId )
    {
        final ResponseEntity<DhisEnrollments> result = restTemplate.getForEntity(
            LATEST_ACTIVE_URI, DhisEnrollments.class, programId, trackedEntityInstanceId );
        return Objects.requireNonNull( result.getBody() ).getEnrollments().stream().findFirst();
    }

    @Nonnull
    public String getLatestActiveCacheKey( @Nonnull String programId, @Nonnull String trackedEntityInstanceId )
    {
        return "EnrollmentService.getLatestActive|" + programId + "|" + trackedEntityInstanceId;
    }

    @HystrixCommand( ignoreExceptions = { DhisConflictException.class } )
    @Nonnull
    @Override
    public Enrollment create( @Nonnull Enrollment enrollment )
    {
        final ResponseEntity<ImportSummaryWebMessage> response;
        try
        {
            response = restTemplate.postForEntity( ENROLLMENTS_URI, enrollment, ImportSummaryWebMessage.class );
        }
        catch ( HttpClientErrorException e )
        {
            if ( HttpStatus.CONFLICT.equals( e.getStatusCode() ) )
            {
                throw new DhisConflictException( "Enrollment could not be created: " + e.getResponseBodyAsString(), e );
            }
            throw e;
        }
        final ImportSummaryWebMessage result = Objects.requireNonNull( response.getBody() );
        if ( (result.getStatus() != Status.OK) ||
            (result.getResponse().getImportSummaries().size() != 1) ||
            (result.getResponse().getImportSummaries().get( 0 ).getStatus() != ImportStatus.SUCCESS) ||
            (result.getResponse().getImportSummaries().get( 0 ).getReference() == null) )
        {
            throw new DhisImportUnsuccessfulException( "Response indicates an unsuccessful import." );
        }

        enrollment.setId( result.getResponse().getImportSummaries().get( 0 ).getReference() );
        if ( (enrollment.getEvents() != null) && !enrollment.getEvents().isEmpty() )
        {
            final ImportSummaries eventImportSummaries = result.getResponse().getImportSummaries().get( 0 ).getEvents();
            if ( eventImportSummaries == null )
            {
                throw new DhisImportUnsuccessfulException( "Enrollment contains events but response did not." );
            }
            final int size = enrollment.getEvents().size();
            for ( int i = 0; i < size; i++ )
            {
                enrollment.getEvents().get( i ).setId( eventImportSummaries.getImportSummaries().get( i ).getReference() );
            }
        }
        return enrollment;
    }

    @HystrixCommand( ignoreExceptions = { DhisConflictException.class } )
    @Nonnull
    @Override
    public Enrollment update( @Nonnull Enrollment enrollment )
    {
        // update of included events is not supported
        enrollment.setEvents( new ArrayList<>() );

        final ResponseEntity<ImportSummaryWebMessage> response;
        try
        {
            response = restTemplate.exchange( ENROLLMENT_UPDATE_URI, HttpMethod.PUT, new HttpEntity<>( enrollment ),
                ImportSummaryWebMessage.class, enrollment.getId() );
        }
        catch ( HttpClientErrorException e )
        {
            if ( HttpStatus.CONFLICT.equals( e.getStatusCode() ) )
            {
                throw new DhisConflictException( "Enrollment could not be updated: " + e.getResponseBodyAsString(), e );
            }
            throw e;
        }
        final ImportSummaryWebMessage result = Objects.requireNonNull( response.getBody() );
        if ( result.getStatus() != Status.OK )
        {
            throw new DhisImportUnsuccessfulException( "Response indicates an unsuccessful enrollment import: " +
                result.getStatus() );
        }
        return enrollment;
    }
}
