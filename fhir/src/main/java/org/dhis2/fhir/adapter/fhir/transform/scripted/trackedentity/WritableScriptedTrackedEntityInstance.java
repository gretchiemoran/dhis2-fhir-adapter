package org.dhis2.fhir.adapter.fhir.transform.scripted.trackedentity;

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
import org.dhis2.fhir.adapter.converter.ConversionException;
import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttribute;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributeValue;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityTypeAttribute;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.scripted.util.ScriptedDateTimeUtils;
import org.dhis2.fhir.adapter.model.ValueType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Scriptable
public class WritableScriptedTrackedEntityInstance implements ScriptedTrackedEntityInstance
{
    private final TrackedEntityAttributes trackedEntityAttributes;

    private final TrackedEntityType trackedEntityType;

    private final TrackedEntityInstance trackedEntityInstance;

    private final ValueConverter valueConverter;

    public WritableScriptedTrackedEntityInstance( @Nonnull FhirToDhisTransformerContext transformerContext,
        @Nonnull TrackedEntityAttributes trackedEntityAttributes, @Nonnull TrackedEntityType trackedEntityType,
        @Nonnull TrackedEntityInstance trackedEntityInstance, @Nonnull ValueConverter valueConverter )
    {
        this.trackedEntityAttributes = trackedEntityAttributes;
        this.trackedEntityType = trackedEntityType;
        this.trackedEntityInstance = trackedEntityInstance;
        this.valueConverter = valueConverter;
    }

    @Override
    public boolean isNewResource()
    {
        return trackedEntityInstance.isNewResource();
    }

    @Nullable
    @Override
    public String getId()
    {
        return trackedEntityInstance.getId();
    }

    @Nonnull
    @Override
    public String getTypeId()
    {
        return trackedEntityType.getId();
    }

    @Override
    @Nullable
    public String getOrganizationUnitId()
    {
        return trackedEntityInstance.getOrgUnitId();
    }

    public boolean setOrganizationUnitId( @Nullable String id ) throws TransformerException
    {
        if ( id == null )
        {
            throw new TransformerMappingException( "Organization unit ID of tracked entity instance must not be null." );
        }
        if ( !Objects.equals( trackedEntityInstance.getId(), id ) )
        {
            trackedEntityInstance.setModified( true );
        }
        trackedEntityInstance.setOrgUnitId( id );
        return true;
    }

    @Nullable
    public String getCoordinates()
    {
        return trackedEntityInstance.getCoordinates();
    }

    public boolean setCoordinates( @Nullable Object coordinates )
    {
        final String convertedValue;
        try
        {
            convertedValue = valueConverter.convert( coordinates, ValueType.COORDINATE, String.class );
        }
        catch ( ConversionException e )
        {
            throw new TransformerMappingException( "Value of tracked entity coordinates could not be converted: " + e.getMessage(), e );
        }
        if ( !Objects.equals( trackedEntityInstance.getCoordinates(), convertedValue ) )
        {
            trackedEntityInstance.setModified( true );
        }
        trackedEntityInstance.setCoordinates( convertedValue );
        return true;
    }

    public boolean setValue( @Nonnull Reference attributeReference, @Nullable Object value ) throws TransformerException
    {
        return setValue( attributeReference, value, null );
    }

    public boolean setValue( @Nonnull Reference attributeReference, @Nullable Object value, @Nullable Object lastUpdated ) throws TransformerException
    {
        final TrackedEntityAttribute attribute = trackedEntityAttributes.getOptional( attributeReference ).orElseThrow( () ->
            new TransformerMappingException( "Tracked entity type attribute \"" + attributeReference + "\" does not exist." ) );
        final TrackedEntityTypeAttribute typeAttribute = trackedEntityType.getOptionalTypeAttribute( attributeReference ).orElse( null );
        return setValue( attribute, typeAttribute, value, ScriptedDateTimeUtils.toZonedDateTime( lastUpdated, valueConverter ) );
    }

    public boolean setOptionalValue( @Nullable Reference attributeReference, @Nullable Object value ) throws TransformerException
    {
        return setOptionalValue( attributeReference, value, null );
    }

    public boolean setOptionalValue( @Nullable Reference attributeReference, @Nullable Object value, @Nullable Object lastUpdated ) throws TransformerException
    {
        if ( attributeReference != null )
        {
            return setValue( attributeReference, value, lastUpdated );
        }
        return true;
    }

    @Override
    public void initValue( @Nonnull Reference attributeReference )
    {
        trackedEntityType.getOptionalTypeAttribute( attributeReference ).ifPresent( ta -> trackedEntityInstance.getAttribute( ta.getAttributeId() ) );
    }

    @Nullable
    @Override
    public Object getValue( @Nonnull Reference attributeReference )
    {
        final TrackedEntityAttribute attribute = getTypeAttribute( attributeReference );
        return getValue( attribute );
    }

    protected boolean setValue( @Nonnull TrackedEntityAttribute attribute, @Nullable TrackedEntityTypeAttribute typeAttribute, @Nullable Object value, @Nullable ZonedDateTime lastUpdated ) throws TransformerException
    {
        if ( (value == null) && (typeAttribute != null) && typeAttribute.isMandatory() )
        {
            throw new TransformerMappingException( "Value of tracked entity type attribute \"" + typeAttribute.getName() + "\" is mandatory and cannot be null." );
        }
        if ( attribute.isGenerated() )
        {
            throw new TransformerMappingException( "Value of tracked entity type attribute \"" + attribute.getName() + "\" is generated and cannot be set." );
        }

        final Object convertedValue;
        try
        {
            convertedValue = valueConverter.convert( value, attribute.getValueType(), String.class );
        }
        catch ( ConversionException e )
        {
            throw new TransformerMappingException( "Value of tracked entity type attribute \"" + attribute.getName() + "\" could not be converted: " + e.getMessage(), e );
        }

        final TrackedEntityAttributeValue attributeValue = trackedEntityInstance.getAttribute( attribute.getId() );
        if ( (lastUpdated != null) && (attributeValue.getLastUpdated() != null) && attributeValue.getLastUpdated().isAfter( lastUpdated ) )
        {
            return false;
        }

        if ( !Objects.equals( attributeValue.getValue(), convertedValue ) )
        {
            trackedEntityInstance.setModified( true );
        }
        attributeValue.setValue( convertedValue );
        return true;
    }

    protected Object getValue( @Nonnull TrackedEntityAttribute attribute ) throws TransformerException
    {
        final TrackedEntityAttributeValue attributeValue = trackedEntityInstance.getAttribute( attribute.getId() );
        final Object convertedValue;
        try
        {
            convertedValue = valueConverter.convert( attributeValue.getValue(), attribute.getValueType(), attribute.getValueType().getJavaClass() );
        }
        catch ( ConversionException e )
        {
            throw new TransformerMappingException( "Value of tracked entity attribute \"" + attribute.getName() + "\" could not be converted: " + e.getMessage(), e );
        }
        return convertedValue;
    }

    @Override
    public void validate() throws TransformerException
    {
        if ( trackedEntityInstance.getOrgUnitId() == null )
        {
            throw new TransformerMappingException( "Organization unit ID of tracked entity instance has not been specified." );
        }

        trackedEntityType.getAttributes().stream().filter( TrackedEntityTypeAttribute::isMandatory ).forEach( ta -> {
            if ( !trackedEntityInstance.containsAttribute( ta.getAttributeId() ) )
            {
                throw new TransformerMappingException( "Value of tracked entity type attribute \"" + ta.getName() + "\" is mandatory and must be set." );
            }
        } );
    }

    @Nonnull
    private TrackedEntityAttribute getTypeAttribute( @Nonnull Reference attributeReference )
    {
        return trackedEntityAttributes.getOptional( attributeReference ).orElseThrow( () ->
            new TransformerMappingException( "Tracked entity type attribute does not exist: " + attributeReference ) );
    }
}
