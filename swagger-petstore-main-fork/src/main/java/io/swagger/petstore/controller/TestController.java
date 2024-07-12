package io.swagger.petstore.controller;

import io.swagger.oas.inflector.models.RequestContext;
import io.swagger.oas.inflector.models.ResponseContext;
import io.swagger.petstore.data.TestData;
import io.swagger.petstore.utils.Util;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//Added controller to clean and get the whole database

public class TestController {

    private static TestData testData = new TestData();

    public ResponseContext getAll(final RequestContext request) {
        final Object allData = testData.getAll();

        if (allData == null) {
            return new ResponseContext().status(Response.Status.NOT_FOUND).entity("Data not found");
        }

        return new ResponseContext()
                .contentType(Util.getMediaType(request))
                .entity(allData);
    }

    public ResponseContext deleteAll(final RequestContext request) {
    	new TestData().deleteAll();

    	final MediaType outputType = Util.getMediaType(request);

        return new ResponseContext()
                .contentType(outputType)
                .entity("All data deleted");
    }

}

