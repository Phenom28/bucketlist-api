package co.getdev.bucketlists.resource;

import co.getdev.bucketlists.model.Bucketlists;
import co.getdev.bucketlists.model.Items;
import co.getdev.bucketlists.model.queryresult.QueryItemsResult;
import co.getdev.bucketlists.persistence.BucketlistsService;
import co.getdev.bucketlists.persistence.ItemsService;
import co.getdev.bucketlists.resource.wrapper.ItemsWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * REST Web Service
 *
 * @author Ogundipe Segun David
 */
@Path("bucketlists/{id}/items")
@RequestScoped
public class ItemsResource {

    @Context
    private UriInfo context;

    @Context
    private SecurityContext securityContext;

    @EJB
    private BucketlistsService bucketlistsService;

    @EJB
    private ItemsService itemsService;

    /**
     * POST method for creating an instance of {@link Items}
     *
     * @param id the {@link Bucketlists} the item belongs to
     * @param content representation for the new {@link Items}
     * @return an HTTP response with content of the created resource
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
    @RolesAllowed({"ADMIN", "USER"})
    @Operation(summary = "Create a new Item",
            description = "Create a new item in a specified buckelists",
            tags = {"Items"},
            security = {@SecurityRequirement(
                            name = "ADMIN"),
                        @SecurityRequirement(
                            name = "USER")},
            responses = {
                @ApiResponse(content = @Content(mediaType = "text/html")),
                @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(
                implementation = QueryItemsResult.class)))})
    public Response createItem(@Parameter(description = "Id of the bucketlists to add the created item to", required = true)
                                @PathParam("id") Integer id, @RequestBody(
                                description = "Name and status of the item to be created", required = true,
                                content = @Content(schema = @Schema(implementation = ItemsWrapper.class))) ItemsWrapper content) {

        Bucketlists bucketlists = bucketlistsService.findById(id).orElseThrow(NotFoundException::new);

        Principal principal = securityContext.getUserPrincipal();
        if (principal == null) {

            return Response.serverError().build();
        }

        if (verifyUser(principal, bucketlists) || securityContext.isUserInRole("ADMIN")) {

            Items items = new Items();
            items.setName(content.getName());
            items.setDone(content.getDone());
            items.setBucketlists(bucketlists);

            itemsService.create(items);
            
            QueryItemsResult qir = new QueryItemsResult();
            qir.setName(items.getName());
            qir.setId(items.getId());
            qir.setDone(items.getDone());
            qir.setDateCreated(items.getDateCreated());
            qir.setDateModified(items.getDateModified());

            return Response.ok(qir).build();
        } else {
            return Response.ok("You don't have the required permission to create "
                    + "items in this bucketlist").build();
        }
    }

    /**
     * Retrieves representation of all instances of {@link Items}
     *
     * @param id the {@link Bucketlists} the item belongs to
     * @return an instance of JSON
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
    @RolesAllowed({"ADMIN", "USER"})
    @Operation(summary = "Get all Items",
            description = "Returns a list of all items in a specified bucketlists owned by the logged in user."
                    + " Admin users have the authority to retrieve items belonging to other users",
            tags = {"Items"},
            security = {@SecurityRequirement(
                            name = "ADMIN"),
                        @SecurityRequirement(
                            name = "USER")},
            responses = {
                @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = QueryItemsResult.class)),
                        description = "A list of items in a bucketlists in json format"),
                @ApiResponse(content = @Content(mediaType = "text/html"))})
    public Response getAllItems(@Parameter(description = "Id of the bucketlists to fetch the items from", required = true)
                                @PathParam("id") Integer id) {

        Bucketlists bucketlists = bucketlistsService.findById(id).orElseThrow(NotFoundException::new);

        Principal principal = securityContext.getUserPrincipal();
        if (principal == null) {

            return Response.serverError().build();
        }

        if (verifyUser(principal, bucketlists) || securityContext.isUserInRole("ADMIN")) {

            List<QueryItemsResult> queryItemsResults = itemsService
                    .findAll(bucketlists).stream().map(this::toQueryItemsResult)
                    .collect(Collectors.toList());

            return Response.ok(queryItemsResults).build();
        } else {
            return Response.ok("You don't have the required permission to get all "
                    + "items in this bucketlist").build();
        }
    }

    /**
     * Retrieves representation of an instance of {@link Items}
     * @param blId
     * @param itemId
     * @return an instance of {@link String}
     */
    @GET
    @Path("{item_id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
    @RolesAllowed({"ADMIN", "USER"})
    @Operation(summary = "Find an Item by id", 
            tags = {"Items"},
            description = "Returns an Item within a specified bucketlist with the id provided.",
            security = {@SecurityRequirement(
                            name = "ADMIN"),
                        @SecurityRequirement(
                            name = "USER")},
            responses = {
                @ApiResponse(description = "The item searched for",
                        content = @Content(mediaType = ("application/json"),
                        schema = @Schema(implementation = QueryItemsResult.class))),
                @ApiResponse(description = "Response if user search for another user's item",
                        content = @Content(mediaType = ("text/html")))
            })
    public Response getById(@Parameter(description = "Id of the Bucketlist to fetch the items from", required = true)
                            @PathParam("id") Integer blId, @Parameter(description = "Id of the Item to fetch", required = true)
                            @PathParam("item_id") Integer itemId) {

        Bucketlists bucketlists = bucketlistsService.findById(blId).orElseThrow(NotFoundException::new);
        Items item = itemsService.findById(itemId).orElseThrow(NotFoundException::new);

        Principal principal = securityContext.getUserPrincipal();
        if (principal == null) {
            return Response.serverError().build();
        }

        QueryItemsResult qir = toQueryItemsResult(item);

        if (verifyUser(principal, bucketlists) || securityContext.isUserInRole("ADMIN")) {

            return Response.ok(qir).build();
        } else {
            return Response.ok("You did not create the item you searched for").build();
        }
    }

    /**
     * PUT method for updating an instance of {@link Items}
     * @param blId {@link Bucketlists} id
     * @param itemId {@link {@link Items} id
     * @param content representation for the resource
     * @return
     */
    @PUT
    @Path("{item_id}")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "USER"})
    @Operation(summary = "Edit an Item", 
            tags = {"Items"},
            description = "Edit the item with the provided id. This method retuns with success if the "
                    + "item belongs to the user or the current user has admin status",
            security = {@SecurityRequirement(
                            name = "ADMIN"),
                        @SecurityRequirement(
                            name = "USER")},
            responses = {
                @ApiResponse(description = "Success if current user owns the bucketlists the item is in",
                content = @Content(mediaType = "text/html"))
            })
    public Response edit(@Parameter(description = "Id of the Bucketlist to fetch the item from", required = true)
                        @PathParam("id") Integer blId, @Parameter(description = "Id of the Item to delete", required = true)
                        @PathParam("item_id") Integer itemId, @RequestBody(description = "The properties to be edited", required = true,
                                content = @Content(schema = @Schema(implementation = ItemsWrapper.class))) ItemsWrapper content) {

        Bucketlists bucketlists = bucketlistsService.findById(blId).orElseThrow(NotFoundException::new);
        Items item = itemsService.findById(itemId).orElseThrow(NotFoundException::new);

        Principal principal = securityContext.getUserPrincipal();
        if (principal == null) {
            return Response.serverError().build();
        }

        if (verifyUser(principal, bucketlists) || securityContext.isUserInRole("ADMIN")) {
            item.setName(content.getName());
            item.setDone(content.getDone());
            item.setDateModified(Date.from(Instant.now()));

            itemsService.edit(item);

            return Response.ok("Item: " + item.getName() + " has been edited").build();
        } else {
            return Response.ok("You did not create the item you want to edit").build();
        }
    }

    /**
     * DELETE method for removing an instance of {@link Items}
     * @param blId {@link Bucketlists} instance to remove {@link Items} from
     * @param itemId {@link Items} instance to be removed
     * @return 
     */
    @DELETE
    @Path("{item_id}")
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed({"ADMIN", "USER"})
    @Operation(summary = "Delete an Item", 
            tags = {"Items"},
            description = "Delete the item with the provided id. Returns success if the logged in user"
                    + " owns the bucketlists the item is in or the logged in user has admin status",
            security = {@SecurityRequirement(
                            name = "ADMIN"),
                        @SecurityRequirement(
                            name = "USER")})
    public Response remove(@Parameter(description = "Id of the Bucketlist to delete the item from", required = true)
                            @PathParam("id") Integer blId, @Parameter(description = "Id of the Item to delete", required = true)
                            @PathParam("item_id") Integer itemId) {
        Bucketlists bucketlists = bucketlistsService.findById(blId).orElseThrow(NotFoundException::new);
        Items item = itemsService.findById(itemId).orElseThrow(NotFoundException::new);
        String itemName = item.getName();

        Principal principal = securityContext.getUserPrincipal();
        if (principal == null) {
            return Response.serverError().build();
        }

        if (verifyUser(principal, bucketlists) || securityContext.isUserInRole("ADMIN")) {

            itemsService.remove(item);

            return Response.ok("Bucketlist: " + itemName + " has been deleted").build();

        } else {
            return Response.ok("You did not create the item you want to delete").build();
        }
    }

    private Boolean verifyUser(Principal principal, Bucketlists bucketlists) {
        String createdBy = bucketlists.getCreatedBy().getUsername();
        String loggedUser = principal.getName();
        return createdBy.equals(loggedUser);
    }

    /**
     * Maps an {@link Items} instance to a {@link QueryItemsResult} instance.
     *
     * @param item the {@link Items} to be mapped to {@link QueryItemsResult}
     * @return an {@link Items} object
     */
    private QueryItemsResult toQueryItemsResult(Items item) {
        QueryItemsResult queryItemsResult = new QueryItemsResult();
        queryItemsResult.setId(item.getId());
        queryItemsResult.setName(item.getName());
        queryItemsResult.setDateCreated(item.getDateCreated());
        queryItemsResult.setDateModified(item.getDateModified());
        queryItemsResult.setDone(item.getDone());

        return queryItemsResult;
    }
}
