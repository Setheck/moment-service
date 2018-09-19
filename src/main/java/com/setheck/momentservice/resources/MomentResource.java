package com.setheck.momentservice.resources;

import com.codahale.metrics.annotation.*;
import com.setheck.momentservice.api.*;
import com.setheck.momentservice.db.*;
import io.dropwizard.hibernate.*;
import org.apache.commons.lang3.*;
import org.slf4j.*;

import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

@Path("/moment")
@Produces(MediaType.APPLICATION_JSON)
public class MomentResource {
    private static final Logger logger = LoggerFactory.getLogger(MomentResource.class);
    private static final Pattern ID_LIST = Pattern.compile("^[0-9,]+$");
    private final MomentDAO momentDAO;

    public MomentResource(MomentDAO momentDAO) {
        this.momentDAO = momentDAO;
    }

    @GET
    @UnitOfWork
    @Path("/{id}")
    @Timed
    public Response getById(@PathParam("id") final Integer id) {
        Moment moment = this.momentDAO.findById(id);
        if (Objects.nonNull(moment)) {
            return Response.ok(moment, MediaType.APPLICATION_JSON_TYPE).build();
        } else {
            return Response.noContent().build();
        }
    }

    @GET
    @UnitOfWork
    @Path("/random")
    @Timed
    public Response getRandom() {
        try {
            return Response.ok(this.momentDAO.findRandomApproved()).build();
        } catch (NoResultException nre) {
            return Response.noContent().build();
        }
    }

    @GET
    @UnitOfWork
    @Path("/toapprove")
    public Response getMomentsToApprove() {
        try
        {
            return Response.ok(this.momentDAO.findMomentsNeedingApproval()).build();
        } catch (NoResultException nre) {
            return Response.noContent().build();
        }
    }

    @PUT
    @UnitOfWork
    @Path("/approve")
    public Response approveByIds(@QueryParam("ids") final String ids) {
        if (StringUtils.isNotBlank(ids) && ID_LIST.matcher(ids).matches()) {
            Set<Integer> idList = Arrays.stream(ids.split(","))
                    .mapToInt(Integer::parseInt)
                    .boxed()
                    .collect(Collectors.toSet());

            int updatedCount = this.momentDAO.updateSingleColumn(idList, "approved", true);
            logger.info("Approved the following ids: {}", idList);
            return Response.ok(updatedCount, MediaType.APPLICATION_JSON_TYPE).build();
        }
        return Response.notModified().build();
    }

    @POST
    @UnitOfWork
    @Timed
    public Response add(final Moment moment) {
        if (Objects.isNull(moment)) {
            return Response.status(Response.Status.NOT_MODIFIED).build();
        }
        logger.info("Adding new moment: {}", moment);
        Integer id = this.momentDAO.create(moment);
        URI relativeCreateLocation = URI.create("/moment/" + id.toString());
        return Response.created(relativeCreateLocation).build();
    }

    @DELETE
    @UnitOfWork
    @Timed
    public Response delete(@QueryParam("id") final Integer id) {
        if (Objects.isNull(id)) {
            return Response.status(Response.Status.NOT_MODIFIED).build();
        }
        logger.info("Deleting moment with id: {}", id);
        this.momentDAO.deleteById(id);
        return Response.accepted().build();
    }
}
