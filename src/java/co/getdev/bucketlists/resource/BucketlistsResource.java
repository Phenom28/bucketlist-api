package co.getdev.bucketlists.resource;

import co.getdev.bucketlists.model.Bucketlists;
import co.getdev.bucketlists.model.Items;
import co.getdev.bucketlists.model.User;
import co.getdev.bucketlists.model.queryresult.QueryBucketlistResult;
import co.getdev.bucketlists.persistence.BucketlistsService;
import co.getdev.bucketlists.persistence.UserService;
import co.getdev.bucketlists.resource.wrapper.BucketlistWrapper;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.security.Principal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author Ogundipe Segun David
 */
@Path("bucketlists")
@RequestScoped
public class BucketlistsResource {

    @Context
    private SecurityContext securityContext;

    @EJB
    private BucketlistsService bucketlistsService;

    @EJB
    private UserService userService;

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "USER"})
    @Operation(summary = "Create a new bucketlist",
            tags = {"Bucketlists"},
            security = {@SecurityRequirement(
                            name = "ADMIN"),
                        @SecurityRequirement(
                            name = "USER")},
            responses = {
                @ApiResponse(content = @Content(mediaType = "text/html"))})
    public Response createBucketlist(@RequestBody(
            description = "Name of the Bucketlist that needs to be created", required = true,
            content = @Content(schema = @Schema(implementation = BucketlistWrapper.class)))
            BucketlistWrapper wrapper) {

        Principal principal = securityContext.getUserPrincipal();
        if (principal == null) {

            return Response.serverError().build();
        }
        User user = userService.findByUsernameOrEmail(principal.getName());

        Bucketlists bucketlists = new Bucketlists();
        bucketlists.setName(wrapper.getName());
        bucketlists.setCreatedBy(user);

        bucketlistsService.create(bucketlists);

        return Response.ok("Bucketlist: " + bucketlists.getName() + " has been created").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "USER"})
    @Operation(summary = "Get all bucketlists",
            description = "Returns a list of all bucketlists of the logged in user."
                    + " Admin users have the authority to retrieve bucketlists belonging to other users",
            tags = {"Bucketlists"},
            security = {@SecurityRequirement(
                            name = "ADMIN"),
                        @SecurityRequirement(
                            name = "USER")},
            responses = {
                @ApiResponse(content = @Content(schema = @Schema(implementation = QueryBucketlistResult.class),
                        mediaType = "application/json"), description = "A list of bucketlists in json format")
                })
    public Response getAllBucketList() {

        if (securityContext.isUserInRole("ADMIN")) {
            List<QueryBucketlistResult> queryBucketlistResults = bucketlistsService
                    .findAll().stream().map(this::toQueryBucketlistResult)
                    .collect(Collectors.toList());

            return Response.ok(queryBucketlistResults).build();
        } else {

            String username = securityContext.getUserPrincipal().getName();

            List<QueryBucketlistResult> queryBucketlistResults = bucketlistsService
                    .findAllByName(username).stream().map(this::toQueryBucketlistResult)
                    .collect(Collectors.toList());

            return Response.ok(queryBucketlistResults).build();
        }

    }

    /**
     * Retrieves representation of an instance of {@link Items}
     *
     * @param id
     * @return an instance of {@link String}
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
    @RolesAllowed({"ADMIN", "USER"})
    @Operation(summary = "Find a bucketlist by id", 
            tags = {"Bucketlists"},
            description = "Returns a bucketlist with the id provided.",
            security = {@SecurityRequirement(
                            name = "ADMIN"),
                        @SecurityRequirement(
                            name = "USER")},
            responses = {
                @ApiResponse(description = "The bucketlist searched for",
                        content = @Content(mediaType = ("application/json"),
                        schema = @Schema(implementation = QueryBucketlistResult.class)))
//                @ApiResponse(description = "Response if user search for another user's bucketlist",
//                        content = @Content(mediaType = ("text/html")))
            })
    public Response getById(@Parameter(description = "Id of the bucketlist to search for",
            required = true)@PathParam("id") Integer id) {

        Bucketlists bucketlists = bucketlistsService.findById(id).orElseThrow(NotFoundException::new);

        Principal principal = securityContext.getUserPrincipal();
        if (principal == null) {
            return Response.serverError().build();
        }

        QueryBucketlistResult qbr = toQueryBucketlistResult(bucketlists);

        if (verifyUser(principal, bucketlists) || securityContext.isUserInRole("ADMIN")) {

            return Response.ok(qbr).build();
        } else {
            return Response.ok("You did not create the bucketlists you searched for").build();
        }
    }

    @GET
    @Path("n")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
    @RolesAllowed({"ADMIN", "USER"})
    @Operation(summary = "Find a bucketlist by name", 
            tags = {"Bucketlists"},
            description = "Returns a bucketlist with the name provided.",
            security = {@SecurityRequirement(
                            name = "ADMIN"),
                        @SecurityRequirement(
                            name = "USER")},
            responses = {
                @ApiResponse(description = "The bucketlist searched for",
                        content = @Content(
                        schema = @Schema(implementation = QueryBucketlistResult.class)))
            })
    public Response getByName(@Parameter(description = "Name of the buckelist to search for",
                            required = true) @QueryParam("q") String name) {
        Bucketlists bucketlists = bucketlistsService.findByName(name).orElseThrow(NotFoundException::new);

        Principal principal = securityContext.getUserPrincipal();
        if (principal == null) {
            return Response.serverError().build();
        }

        QueryBucketlistResult qbr = toQueryBucketlistResult(bucketlists);

        if (verifyUser(principal, bucketlists) || securityContext.isUserInRole("ADMIN")) {

            return Response.ok(qbr).build();
        } else {
            return Response.ok("You did not create the buckelists you searched for").build();
        }
    }

    @GET
    @Path("p")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
    @RolesAllowed({"ADMIN", "USER"})
    @Operation(summary = "Find paginated list of bucketlists", 
            tags = {"Bucketlists"},
            description = "Returns a paginated list of bucketlists belonging to the logged in user",
            security = {@SecurityRequirement(
                            name = "ADMIN"),
                        @SecurityRequirement(
                            name = "USER")},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = ("application/json"),
                        schema = @Schema(implementation = QueryBucketlistResult.class)))
            })
    public Response getPaginated(@Parameter(description = "The page to display. Defaults to 1 if no value is specified",
            required = true) @DefaultValue("1") @QueryParam("page") Integer page,
            @Parameter(description = "The number of bucketlists to fetch. Defaults is 20 if no value is specified", required = true) 
            @DefaultValue("20") @QueryParam("limit") Integer limit) {
        Integer pag = page - 1;
        Integer lim = limit - 1;
        List<Bucketlists> bucketlists = bucketlistsService.findRange(pag, lim);

        Principal principal = securityContext.getUserPrincipal();
        if (principal == null) {
            return Response.serverError().build();
        }

        List<QueryBucketlistResult> qbr = bucketlists.stream().map(this::toQueryBucketlistResult)
                .collect(Collectors.toList());

        if (verifyUser(principal, bucketlists.get(0))
                || securityContext.isUserInRole("ADMIN")) {
            return Response.ok(qbr).build();
        } else {
            return Response.ok("Your bucketlists is empty").build();
        }
    }

    @PUT
    @Path("{id}")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "USER"})
    @Operation(summary = "Edit a bucketlist", 
            tags = {"Bucketlists"},
            description = "Edit the bucketlist with the provided id. This method retuns with success if the "
                    + "bucketlist belongs to the user or the current user has admin status",
            security = {@SecurityRequirement(
                            name = "ADMIN"),
                        @SecurityRequirement(
                            name = "USER")},
            responses = {
                @ApiResponse(description = "Success if current user owns the bucketlists",
                content = @Content(mediaType = "text/html"))
            })
    public Response edit(@Parameter(description = "Id of the buckelists to be edited", required = true) @PathParam("id") Integer id,
                        @RequestBody(description = "The vaiables to be edited", required = true, content =@Content(schema =  @Schema(implementation = BucketlistWrapper.class)))
                                BucketlistWrapper bucketlistWrapper) {
        Bucketlists bucketlists = bucketlistsService.findById(id).orElseThrow(NotFoundException::new);

        Principal principal = securityContext.getUserPrincipal();
        if (principal == null) {
            return Response.serverError().build();
        }

        if (verifyUser(principal, bucketlists) || securityContext.isUserInRole("ADMIN")) {
            bucketlists.setName(bucketlistWrapper.getName());
            bucketlists.setDateModified(Date.from(Instant.now()));
            bucketlistsService.edit(bucketlists);

            return Response.ok("Bucketlist: " + bucketlistWrapper.getName() + " has been edited").build();
        } else {
            return Response.ok("You did not create the bucketlists you want to edit").build();
        }
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed({"ADMIN", "USER"})
    @Operation(summary = "Delete a buckelists", 
            tags = {"Bucketlists"},
            description = "Delete the bucketlist with the provided id. Returns success if the logged in user"
                    + " owns the bucketlists or the logged in user has admin status",
            security = {@SecurityRequirement(
                            name = "ADMIN"),
                        @SecurityRequirement(
                            name = "USER")})
    public Response remove(@Parameter(description = "The id of the buckelists to be deleted", required = true)
                        @PathParam("id") Integer id) {
        Bucketlists bucketlists = bucketlistsService.findById(id).orElseThrow(NotFoundException::new);
        String bucketName = bucketlists.getName();
        Principal principal = securityContext.getUserPrincipal();
        if (principal == null) {
            return Response.serverError().build();
        }

        if (verifyUser(principal, bucketlists) || securityContext.isUserInRole("ADMIN")) {

            bucketlistsService.remove(bucketlists);

            return Response.ok("Bucketlist: " + bucketName + " has been deleted").build();

        } else {
            return Response.ok("You did not create the bucketlists you want to delete").build();
        }
    }

    /**
     * Maps a {@link Bucketlists} instance to a {@link QueryBucketlistsResult}
     * instance.
     *
     * @param bucketlists the {@link Bucketlists} to be mapped to
     * {@link QueryBucketListResult}
     * @return a {@link QueryBucketListResult object
     */
    private QueryBucketlistResult toQueryBucketlistResult(Bucketlists bucketlists) {
        QueryBucketlistResult queryBucketlistResult = new QueryBucketlistResult();
        queryBucketlistResult.setId(bucketlists.getId());
        queryBucketlistResult.setName(bucketlists.getName());
        queryBucketlistResult.setItems(bucketlists.getItems());
        queryBucketlistResult.setDateCreated(bucketlists.getDateCreated());
        queryBucketlistResult.setDateModified(bucketlists.getDateModified());
        queryBucketlistResult.setCreatedBy(bucketlists.getCreatedBy().getUsername());

        return queryBucketlistResult;
    }

    private Boolean verifyUser(Principal principal, Bucketlists bucketlists) {
        String createdBy = bucketlists.getCreatedBy().getUsername();
        String loggedUser = principal.getName();
        return createdBy.equals(loggedUser);
    }
}
