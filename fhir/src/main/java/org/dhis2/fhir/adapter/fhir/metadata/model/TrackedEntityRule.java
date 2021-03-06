package org.dhis2.fhir.adapter.fhir.metadata.model;

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
import org.dhis2.fhir.adapter.dhis.model.ReferenceAttributeConverter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author volsch
 */
@Entity
@Table( name = "fhir_tracked_entity_rule" )
@DiscriminatorValue( "TRACKED_ENTITY" )
public class TrackedEntityRule extends AbstractRule
{
    private static final long serialVersionUID = -3997570895838354307L;

    private Reference trackedEntityReference;
    private ExecutableScript orgUnitLookupScript;
    private ExecutableScript locationLookupScript;
    private Reference trackedEntityIdentifierReference;
    private boolean trackedEntityIdentifierFq;

    @Basic
    @Column( name = "tracked_entity_ref", nullable = false, length = 230 )
    @Convert( converter = ReferenceAttributeConverter.class )
    public Reference getTrackedEntityReference()
    {
        return trackedEntityReference;
    }

    public void setTrackedEntityReference( Reference trackedEntityRef )
    {
        this.trackedEntityReference = trackedEntityRef;
    }

    @ManyToOne( optional = false )
    @JoinColumn( name = "org_lookup_script_id", nullable = false )
    public ExecutableScript getOrgUnitLookupScript()
    {
        return orgUnitLookupScript;
    }

    public void setOrgUnitLookupScript( ExecutableScript orgUnitLookupScript )
    {
        this.orgUnitLookupScript = orgUnitLookupScript;
    }

    @ManyToOne( optional = false )
    @JoinColumn( name = "loc_lookup_script_id", nullable = false )
    public ExecutableScript getLocationLookupScript()
    {
        return locationLookupScript;
    }

    public void setLocationLookupScript( ExecutableScript locationLookupScript )
    {
        this.locationLookupScript = locationLookupScript;
    }

    @Basic
    @Column( name = "tracked_entity_identifier_ref", nullable = false, length = 230 )
    @Convert( converter = ReferenceAttributeConverter.class )
    public Reference getTrackedEntityIdentifierReference()
    {
        return trackedEntityIdentifierReference;
    }

    public void setTrackedEntityIdentifierReference( Reference trackedEntityIdentifierReference )
    {
        this.trackedEntityIdentifierReference = trackedEntityIdentifierReference;
    }

    @Basic
    @Column( name = "tracked_entity_identifier_fq", nullable = false )
    public boolean isTrackedEntityIdentifierFq()
    {
        return trackedEntityIdentifierFq;
    }

    public void setTrackedEntityIdentifierFq( boolean trackedEntityIdentifierFq )
    {
        this.trackedEntityIdentifierFq = trackedEntityIdentifierFq;
    }
}
