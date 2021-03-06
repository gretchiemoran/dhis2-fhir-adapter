package org.dhis2.fhir.adapter.util;

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility methods for extracting information from exceptions.
 *
 * @author volsch
 */
public abstract class ExceptionUtils
{
    /**
     * Returns the first exception instance that is instance of one
     * of the specified cause classes. Also the specified exception may be
     * returned of it is an instance of one of the specified cause classes.
     *
     * @param exception    the exception for which a cause with the specified cause classes should be extracted.
     * @param causeClasses the cause classes of which one of the causes must be instance of.
     * @return the found cause that is instance of one of the specified cause classes or <code>null</code> if
     * no such exception has been found.
     */
    @Nullable
    public static Throwable findCause( @Nonnull Throwable exception, @Nonnull Class<?>... causeClasses )
    {
        final Set<Throwable> checkedCauses = new HashSet<>();
        Throwable cause = exception;
        do
        {
            if ( isInstance( cause, causeClasses ) )
            {
                break;
            }
            checkedCauses.add( cause );
            cause = cause.getCause();
        }
        while ( (cause != null) && !checkedCauses.contains( cause ) );
        return cause;
    }

    private static boolean isInstance( @Nonnull Throwable throwable, @Nonnull Class<?>[] exceptionClasses )
    {
        for ( final Class<?> causeClass : exceptionClasses )
        {
            if ( causeClass.isInstance( throwable ) )
            {
                return true;
            }
        }
        return false;
    }

    private ExceptionUtils()
    {
        super();
    }
}
