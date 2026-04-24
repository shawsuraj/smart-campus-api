# Smart Campus API (5COSC022W)

Name - Suraj Shaw

## API Design Overview

This is a REST API for managing campus rooms and sensors, built using JAX-RS with the Jersey implementation running on embedded Jetty. No database is used and all data lives in static HashMaps in memory for the duration the server is running (as mentioned in coursework).

There are three resources: rooms, sensors, and sensor readings. Readings are nested under sensors as a sub-resource, which reflects the real relationship, a reading belongs to a specific sensor so it made sense to model it that way.

**All endpoints:**
```
GET    /api/v1                              discovery / root endpoint
GET    /api/v1/rooms                        list all rooms
POST   /api/v1/rooms                        create a room
GET    /api/v1/rooms/{roomId}               get one room by id
DELETE /api/v1/rooms/{roomId}               delete a room 
GET    /api/v1/sensors                      list all sensors (optional ?type= filter)
POST   /api/v1/sensors                      create a sensor
GET    /api/v1/sensors/{sensorId}           get one sensor
GET    /api/v1/sensors/{sensorId}/readings  get reading history for a sensor
POST   /api/v1/sensors/{sensorId}/readings  add a new reading
```


**Key design choices :**

The root endpoint at `/api/v1` returns links to `/rooms` and `/sensors` so a client can navigate the whole API from just the base URL, this is the HATEOAS approach.

Data is stored using static fields in a `DataStore` class.

Error handling uses custom exceptions and `@Provider` ExceptionMappers so every error comes back as consistent JSON with `error`, `status`, and `message` fields. A catch-all `GeneralExceptionMapper` handles anything unexpected so stack traces never reach the client.

---

## How to Build and Run

**Requirements :**
- JDK 11 or higher
- Apache Maven 3.6 +
- Postman or curl for testing

**Step 1 : clone the code**

Clone the repo and go into the project folder:
```bash
git clone https://github.com/shawsuraj/smart-campus-api
cd smart-campus-api
```

**Step 2 : build it**

This downloads all dependencies and compiles everything:
```bash
mvn clean package
```

First time will take a minute while Maven downloads Jersey and other dependencies. You should see `BUILD SUCCESS` at the end.

**Step 3 : start the server**

```bash
mvn jetty:run
```

 The server is now running at `http://localhost:8080` and all endpoints are under `/api/v1`.

**Step 4 : test it**

Open a new terminal and run:
```bash
curl http://localhost:8080/api/v1
```

You should get back a JSON object with the API name, version, and links to rooms and sensors.

**Step 5 — stop the server**

Press `Ctrl+C` in the terminal where Jetty is running.


---

## Sample curl Commands

Run these in order as some depend on data created earlier.

**1. Discovery — check the API is up**
```bash
curl http://localhost:8080/api/v1
```
Expected: `200 OK` with API name, version, adminContact, and links to rooms and sensors.

---

**2. Create a room**
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "LIB-301", "name": "Library Study Room", "capacity": 30}'
```
Expected: `201 Created` with the room object and an empty `sensorIds` array.

---

**3. Create a sensor linked to that room**
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "TEMP-001", "type": "Temperature", "status": "ACTIVE", "currentValue": 21.0, "roomId": "LIB-301"}'
```
Expected: `201 Created`. The room's sensorIds will now include TEMP-001.

---

**4. Filter sensors by type**
```bash
curl "http://localhost:8080/api/v1/sensors?type=Temperature"
```
Expected: `200 OK` with only Temperature sensors. Without `?type=` you get all of them.

---

**5. Post a reading to a sensor**
```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 23.5}'
```
Expected: `201 Created`. Server fills in the `id` (UUID) and `timestamp` automatically. The sensor's `currentValue` also updates to 23.5.

---

**6. Get reading history**
```bash
curl http://localhost:8080/api/v1/sensors/TEMP-001/readings
```
Expected: `200 OK` with an array of all readings posted to TEMP-001.

---

**7. Try to delete a room that still has sensors — shows 409**
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```
Expected: `409 Conflict` — blocked because TEMP-001 is still assigned to the room.

---

**8. Try to create a sensor with a bad roomId — shows 422**
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "CO2-999", "type": "CO2", "status": "ACTIVE", "currentValue": 400.0, "roomId": "DOESNT-EXIST"}'
```
Expected: `422 Unprocessable Entity` — JSON is valid but the roomId doesn't point to anything.

---

## Report

### Part 1 : Application Setup

**Q: In your report, explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race con-ditions.**

By default JAX-RS creates a new instance of the resource class for every request that comes in. I didn't know this at first and had my data stored in normal instance fields which meant everything disappeared between requests. Once I realised this I moved everything into a DataStore class with static fields. Static fields belong to the class not any instance so they stay in memory as long as the server is running. The only downside is two requests arriving at the same time could both write to the same map, but for this coursework thats fine.

**Q: Why is the provision of ”Hypermedia” (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?**

HATEOAS means your responses include links so a client can navigate the API without already knowing every URL. My root endpoint at GET /api/v1 returns links to /rooms and /sensors so a client only needs the base URL to discover everything else. Its better than just having documentation because if we change a path, the client follows the updated link rather than breaking. It also makes it easier to explore the API, we can start at the root and just follow links.

---

### Part 2 : Room Management

**Q: When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.**

Returning just IDs keeps the response small but then the client has to make a separate request for each one to get any useful data. With 100 rooms thats 101 requests total which is inefficient, this is what they call the N+1 problem. Returning full objects is a bigger payload but the client gets everything in one go. Room objects in this API are quite small so I just return the full thing. For a real system with thousands of rooms and lots of data per room we might want to think about pagination or a summary view.

**Q: Is the DELETE operation idempotent inyour implementation? Provideadetailed justification bydescribing whathappens if a client mistakenly sends the exact same DELETE requestfor a room multiple times.**

The first DELETE on a room that exists returns 204 and removes it. The second call finds nothing there and returns 404. In terms of the server state it is idempotent because the room is gone either way and sending it more times doesnt cause any extra side effects. The response code is different (204 then 404) but the actual data state is the same, which is really what idempotent means.

---

### Part 3 : Sensor Operations

**Q: We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?**

The @Consumes(MediaType.APPLICATION_JSON) annotation on the method tells JAX-RS this endpoint only accepts JSON. If someone sends text/plain or XML, JAX-RS rejects it before my code even runs and sends back a 415 Unsupported Media Type response. I dont have to write any code to handle that case, JAX-RS does it automatically based on the Content-Type header in the request.

**Q: You implemented this filtering using@QueryParam. Contrast this with an alterna-tive design where the type is part of the URL path (e.g., /api/vl/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?**

I tried the path approach first with /sensors/type/{type} as its own method but it immediately clashed with /sensors/{sensorId} and JAX-RS gave errors. Query params dont have this problem. The filter is also optional, without ?type= you get everything, with it we get filtered results. We cant really do that cleanly with path variables. Adding more filters later is also much easier, just add another @QueryParam without changing the URL structure.

---

### Part 4 : Sub-Resource Locator Pattern

**Q: Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity inlarge APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?**

Without sub-resources, SensorResource would need to handle GET /sensors, POST /sensors, GET /sensors/{id}, plus GET and POST for readings and it would get big quickly. The locator method has @Path but no HTTP verb annotation, so JAX-RS knows to call it to get an object and then dispatch the actual request to that object. This keeps SensorResource focused on sensors and SensorReadingResource focused on readings, each class does one thing. It also matches how we would naturally think about the data readings belong to a sensor, so it makes sense for them to be handled by a separate class that knows which sensor its working with.

---

**Q: Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?**

404 means the URL they requested doesnt exist. But /api/v1/sensors is a real valid endpoint that works fine, so 404 would be confusing. The actual problem is that the roomId inside the request body points to something that doesnt exist. 422 Unprocessable Entity means the request arrived fine and the JSON is valid, but something inside the content doesnt make sense. That fits this case much better because the issue is the broken reference in the body, not the URL.

**Q: From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?**

A stack trace shows an attacker the full package and class names of your code which tells them what framework and libraries you're using. From that they can look up known vulnerabilities for those specific versions. Method names and line numbers can give hints about the internal logic. If a database error is included it might leak query details or connection info. My GeneralExceptionMapper catches Throwable and returns a plain generic 500 message so none of that internal detail ever gets sent to the client.

**Q: Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?**

If I put logging in every method I would have to remember to add it each time and they'd probably end up inconsistent, different formats or missing fields in some places. A ContainerRequestFilter runs automatically on every request without me having to do anything in the resource classes. The log format is defined once and works everywhere. It also keeps the resource methods cleaner and logging is not really part of what a room or sensor endpoint does, mixing it in just makes the code harder to read.
