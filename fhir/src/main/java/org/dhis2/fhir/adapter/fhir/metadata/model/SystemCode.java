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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.hibernate.annotations.GenericGenerator;

import javax.annotation.Nonnull;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Defines system specific codes.
 * <br><br>
 * E.g. for systolic blood pressure "8480-6" that is defined for system "http://loinc.org" can be used. There may also
 * be other systems that use for systolic blood pressure a different code. All these codes can be defined as system
 * codes and can be mapped to an adapter specific code in order to enable rules and transformations to use a single
 * adapter specific code that can be changed dynamically (also with multiple values).
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_system_code" )
public class SystemCode implements Serializable
{
    private static final long serialVersionUID = 7048763667494469394L;

    private UUID id;
    private Long version;
    private LocalDateTime createdAt;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;
    private System system;
    private String systemCode;
    private Code code;

    @GeneratedValue( generator = "uuid2" )
    @GenericGenerator( name = "uuid2", strategy = "uuid2" )
    @Id
    @Column( name = "id", nullable = false )
    public UUID getId()
    {
        return id;
    }

    public void setId( UUID id )
    {
        this.id = id;
    }

    @Version
    @Column( name = "version", nullable = false )
    public Long getVersion()
    {
        return version;
    }

    public void setVersion( Long version )
    {
        this.version = version;
    }

    @Basic
    @Column( name = "created_at", nullable = false )
    public LocalDateTime getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt( LocalDateTime createdAt )
    {
        this.createdAt = createdAt;
    }

    @Basic
    @Column( name = "last_updated_by", length = 11 )
    public String getLastUpdatedBy()
    {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy( String lastUpdatedBy )
    {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    @Basic
    @Column( name = "last_updated_at", nullable = false )
    public LocalDateTime getLastUpdatedAt()
    {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt( LocalDateTime lastUpdatedAt )
    {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @Basic
    @Column( name = "system_code", nullable = false, length = 120 )
    public String getSystemCode()
    {
        return systemCode;
    }

    public void setSystemCode( String systemCode )
    {
        this.systemCode = systemCode;
    }

    @ManyToOne( optional = false )
    @JoinColumn( name = "code_id", referencedColumnName = "id", nullable = false )
    public Code getCode()
    {
        return code;
    }

    public void setCode( Code code )
    {
        this.code = code;
    }

    @ManyToOne( optional = false )
    @JoinColumn( name = "system_id", referencedColumnName = "id", nullable = false )
    public System getSystem()
    {
        return system;
    }

    public void setSystem( System system )
    {
        this.system = system;
    }

    @Transient
    @JsonIgnore
    @Nonnull
    public SystemCodeValue getSystemCodeValue()
    {
        return new SystemCodeValue( getSystem().getSystemUri(), getSystemCode() );
    }
}
