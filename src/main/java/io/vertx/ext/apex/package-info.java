/**
 * = Apex
 * :toc: left
 *
 * Apex is a set of building blocks for building web applications with Vert.x. Think of it as a Swiss Army Knife for building
 * modern, scalable, web apps.
 *
 * Vert.x core provides a fairly low level set of functionality for handling HTTP, and for some applications
 * that will be sufficient.
 *
 * Vert.x Apex builds on Vert.x core to provide a richer set of functionality for building real web applications, more
 * easily.
 *
 * It's the successor to http://pmlopes.github.io/yoke/[Yoke] in Vert.x 2.x, and takes inspiration from projects such
 * as http://expressjs.com/[Express] in the Node.js world and http://www.sinatrarb.com/[Sinatra] in the Ruby world.
 *
 * Apex is designed to be powerful, un-opionated and fully embeddable. You just use the parts you want and nothing more.
 *
 * Apex is not a container.
 *
 * You can use Apex to create classic server-side web applications, RESTful web applications, 'real-time' (server push)
 * web applications, or any other kind of web application you can think of. Apex doesn't care.
 *
 * It's up to you to chose the type of app you prefer, not Apex.
 *
 * Apex is a great fit for writing *RESTful HTTP micro-services*, but we don't *force* you to write apps like that.
 *
 * Some of the key features of Apex include:
 *
 * * Routing (based on method, path, etc)
 * * Regex pattern matching for paths
 * * Extraction of parameters from paths
 * * Content negotiation
 * * Request body handling
 * * Body size limits
 * * Cookie parsing and handling
 * * Multipart forms
 * * Multipart file uploads
 * * Sub routers
 *
 * Apex add-ons include:
 *
 * * Session support - both local (for sticky sessions) and clustered (for non sticky)
 * * CORS (Cross Origin Resource Sharing) support
 * * Error page template
 * * Basic Authentication
 * * Redirect based authentication
 * * User/role/permission authorisation
 * * Favicon handling
 * * Template support for server side rendering. Supports: Handlebars, Jade, MVEL and Thymeleaf out of the box
 * * Response time handler
 * * Static file serving, including caching logic and directory listing.
 * * Request timeout support
 *
 * Most features in Apex are implemented as handlers so you can always write your own. We envisage many more being written
 * over time.
 *
 * We'll discuss all these features in this manual.
 *
 * == Re-cap on Vert.x core HTTP servers
 *
 * Apex uses and exposes API from Vert.x core, so it's well worth getting familiar with the basic concepts of writing
 * HTTP servers using Vert.x core, if you're not already.
 *
 * The Vert.x core HTTP documentation goes into a lot of detail on this.
 *
 * Here's a hello world web server written using Vert.x core. At this point there is no Apex involved:
 *
 * [source,java]
 * ----
 * {@link examples.Examples#example1}
 * ----
 *
 * We create an HTTP server instance, and we set a request handler on it. The request handler will be called whenever
 * a request arrives on the server.
 *
 * When that happens we are just going to set the content type to `text/plain`, and write `Hello World!` and end the
 * response.
 *
 * We then tell the server to listen at port `8080` (default host is `localhost`).
 *
 * You can run this, and point your browser at `http://localhost:8080` to verify that it works as expected.
 *
 * == Basic Apex concepts
 *
 * Here's the 10000 foot view:
 *
 * A {@link io.vertx.ext.apex.core.Router} is one of the core concepts of Apex.
 *
 * A router is an object which maintains zero or more {@link io.vertx.ext.apex.core.Route}s.
 *
 * A router handles an HTTP request and finds the first matching route for that request, and passes the request to that route.
 *
 * The route can have a *handler* associated with it, which then receives the request.
 *
 * You then *do something* with the request, and then, either end it or pass it to the next matching handler.
 *
 * Here's the simplest router example:
 *
 * [source,java]
 * ----
 * {@link examples.Examples#example2}
 * ----
 *
 * It's basically does the same thing as the Vert.x Core HTTP server hello world example from the previous section,
 * but this time using Apex.
 *
 * We create an HTTP server as before, then we create a router.
 *
 * Once we've done that we create a simple route with no matching criteria so it will match *all* requests that arrive on the server.
 *
 * We then specify a handler for that route. That handler will be called for all requests that arrive on the server.
 *
 * The object that gets passed into the handler is a {@link io.vertx.ext.apex.core.RoutingContext} - this contains
 * the standard Vert.x {@link io.vertx.core.http.HttpServerRequest} and {@link io.vertx.core.http.HttpServerResponse}
 * but also various other useful stuff that makes working with Apex simpler.
 *
 * Once we've set up the handler, we set the request handler of the HTTP server to pass all incoming requests
 * to {@link io.vertx.ext.apex.core.Router#accept}.
 *
 * So, that's the basics. Now we'll look at things in more detail:
 *
 * === Routing requests
 *
 * There are many ways routes can be set-up to route requests to handlers. We'll look at them all here.
 *
 * ==== Routing by path
 *
 * A route can be set-up to match the path from the request URI.
 *
 * In this case it will match any request which has a path that *starts with* the specified path.
 *
 * In the following example the handler will be called for all requests with a URI path that starts with
 * `/some/path/`.
 *
 * For example `/some/path/foo.html` and `/some/path/otherdir/blah.css` would both match.
 *
 * [source,java]
 * ----
 * {@link examples.Examples#example3}
 * ----
 *
 * Alternatively the path can be specified when creating the route:
 *
 * [source,java]
 * ----
 * {@link examples.Examples#example4}
 * ----
 *
 * ==== Routing with regular expressions
 *
 * Regular expressions can also be used to match URI paths in routes.
 *
 * As in straight path matching the regex is not an *exact match* for the path, but matches the start of the path.
 *
 * [source,java]
 * ----
 * {@link examples.Examples#example5}
 * ----
 *
 * Alternatively the regex can be specified when creating the route:
 *
 * [source,java]
 * ----
 * {@link examples.Examples#example6}
 * ----
 *
 * ==== Routing by HTTP method
 *
 * By default a route will match all HTTP methods.
 *
 * If you want a route to only match for a specific HTTP method you can use {@link io.vertx.ext.apex.core.Route#method}
 *
 * [source,java]
 * ----
 * {@link examples.Examples#example7}
 * ----
 *
 * Or you can specify this with a path when creating the route:
 *
 * [source,java]
 * ----
 * {@link examples.Examples#example8}
 * ----
 *
 * If you want to specify a route will match for more than HTTP method you can call {@link io.vertx.ext.apex.core.Route#method}
 * multiple times:
 *
 * [source,java]
 * ----
 * {@link examples.Examples#example9}
 * ----
 *
 * ==== Route order
 *
 * By default routes are matched in the order they are added to the router.
 *
 * When a request arrives the router will step through each route and check if it matches, if it matches then
 * the handler for that route will be called.
 *
 * If the handler subsequently calls {@link io.vertx.ext.apex.core.RoutingContext#next} the handler for the next
 * matching route (if any) will be called. And so on.
 *
 * Here's an example to illustrate this:
 *
 * [source,java]
 * ----
 * {@link examples.Examples#example10}
 * ----
 *
 * In the above example the response will contain:
 *
 * ----
 * route1
 * route2
 * route3
 * ----
 *
 * As the routes have been called in that order for any request that starts with `/some/path`.
 *
 * If you want to override the default ordering for routes, you can do so using {@link io.vertx.ext.apex.core.Route#order},
 * specifying an integer value.
 *
 * Default routes are assigned an implicit order corresponding to the order in which they were added to the router, with
 * the first route numbered `0`, the second route numbered `1`, and so on.
 *
 * By specifying an order for the route you can override the default ordering. Order can also be negative, e.g. if you
 * want to ensure a route is evaluated before route number `0`.
 *
 * Let's change the ordering of route2 so it runs before route1:
 *
 * [source,java]
 * ----
 * {@link examples.Examples#example11}
 * ----
 *
 * then the response will now contain:
 *
 * ----
 * route2
 * route1
 * route3
 * ----
 *
 * If two matching routes have the same value of order, then they will be called in the order they were added.
 *
 * You can also specify a route is handled last, with {@link io.vertx.ext.apex.core.Route#last}
 *
 * ==== Enabling and disabling routes
 *
 * You can disable a route with {@link io.vertx.ext.apex.core.Route#disable}.
 *
 * A disabled route will be ignored when matching.
 *
 * You can re-enable a disabled route with {@link io.vertx.ext.apex.core.Route#enable}
 *
 * ==== Content based routing
 *
 * Apex supports content based routing which allows request to be routed to specific handlers depending on the
 * MIME type of the request body as specified in the `content-type` header, and/or the set of MIME types the client
 * accepts as specified in the `accept` header.
 *
 * ===== Routing based on MIME type of request
 *
 * You can specify that a route will match against matching request MIME types using {@link io.vertx.ext.apex.core.Route#consumes}.
 *
 * In this case, the request will contain a `content-type` header specifying the MIME type of the request body.
 *
 * This will be matched against the value specified in {@link io.vertx.ext.apex.core.Route#consumes}.
 *
 * Basically, `consumes` is describing which MIME types the route will consume.
 *
 * Matching can be done on exact MIME type matches:
 *
 * [source,java]
 * ----
 * {@link examples.Examples#example12}
 * ----
 *
 * Multiple exact matches can also be specified:
 *
 * [source,java]
 * ----
 * {@link examples.Examples#example13}
 * ----
 *
 * Matching on wildcards for the sub-type is supported:
 *
 * [source,java]
 * ----
 * {@link examples.Examples#example14}
 * ----
 *
 * And you can also match on the top level type
 *
 * [source,java]
 * ----
 * {@link examples.Examples#example15}
 * ----
 *
 * If you don't specify a `/` in the consumers, it will assume you meant the sub-type.
 *
 * ===== Routing based on MIME types acceptable by the client
 *
 * The HTTP `accept` header is used to signify what MIME types of the response are acceptable to the client.
 *
 * An `accept` header can have multiple MIME types separated by `,`. MIME types can also have a q value appended to them
 * which signifies a weighting to apply if more than one response MIME type is available matching the accept header.
 *
 * By using {@link io.vertx.ext.apex.core.Route#produces} you define which MIME type(s) the route produces, e.g. the
 * following handler produces a response with MIME type `application/json`.
 *
 * This will then match against
 *
 * ==== Combining routing criteria
 *
 * You can combine routing criteria in many different ways
 *
 * (example of path, methods, content based)
 *
 * === Context data
 *
 * === Sub-routers
 *
 * === Handling requests and calling the next handler
 *
 * When a route matches the handler for the route will be called, passing in an instance of {@link io.vertx.ext.apex.core.RoutingContext}.
 *
 * If you don't end the request in your handler, you can call {@link io.vertx.ext.apex.core.RoutingContext#next} then the router
 * will call the next matching route handler (if any).
 *
 * You don't have to call {@link io.vertx.ext.apex.core.RoutingContext#next} before the handler has finished executing.
 * You can do this some time later, if you want:
 *
 * [source,java]
 * ----
 * {@link examples.Examples#example20}
 * ----
 *
 * In the above example `route1` is written to the response, then 5 seconds later `route2` is written to the response,
 * then 5 seconds later `route3` is written to the response and the response is ended.
 *
 * Note, all this happens without any thread blocking.
 *
 * === Error handling
 *
 *
 *
 *
 *
 */
@Document(fileName = "index.adoc")
package io.vertx.ext.apex;

import io.vertx.docgen.Document;
