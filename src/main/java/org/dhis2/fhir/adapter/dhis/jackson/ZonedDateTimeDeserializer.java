package org.dhis2.fhir.adapter.dhis.jackson;

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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.dhis2.fhir.adapter.util.DateTimeUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ZonedDateTimeDeserializer extends StdDeserializer<ZonedDateTime>
{
    private static final long serialVersionUID = -7804311923431170580L;

    private final ZoneId zoneId = ZoneId.systemDefault();

    public ZonedDateTimeDeserializer()
    {
        super( ZonedDateTime.class );
    }

    @Override public ZonedDateTime deserialize( JsonParser jsonParser, DeserializationContext deserializationContext ) throws IOException
    {
        final String string = jsonParser.getText().trim();
        if ( string.isEmpty() )
        {
            return null;
        }
        try
        {
            if ( DateTimeUtils.containsDateTimeOffset( string ) )
            {
                return ZonedDateTime.parse( string, DateTimeFormatter.ISO_DATE_TIME );
            }
            else
            {
                return ZonedDateTime.of( LocalDateTime.parse( string, DateTimeFormatter.ISO_LOCAL_DATE_TIME ), zoneId );
            }
        }
        catch ( DateTimeParseException e )
        {
            throw JsonMappingException.from( jsonParser, "Could not parse zoned date time: " + string, e );
        }
    }
}
