/*
 * This file was last modified at 2026.05.08 11:52 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * RestApplication.java
 * $Id$
 */

package su.svn.api;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@SuppressWarnings("unused")
@OpenAPIDefinition(
        info = @Info(
                title = "DayBook 3 API",
                version = "1.0",
                description = "API Documentation"
        ),
        components = @Components(
                responses = {
                        @APIResponse(
                                name = "200OK",
                                responseCode = "200",
                                description = "OK"
                        ),
                        @APIResponse(
                                name = "201Created",
                                responseCode = "201",
                                description = "Created"
                        ),
                        @APIResponse(
                                name = "204NoCont",
                                responseCode = "204",
                                description = "No Content"
                        ),
                        @APIResponse(
                                name = "500Error",
                                responseCode = "500",
                                description = "Internal Server Error"
                        )
                }
        )
)
public class RestApplication extends Application {
}