*Organization*

* use format and transport agnostic services and injected datastores to make the queries and logic distinct from the (in this case) HTTP
* would be easy to add protobufs even though I didn't get there
* starting in Main, starts a service with the provided json blobs (defaults to sample.json)
* business logic is implemented in `src/scala/com/pricehero/domain/RatesPipe.scala`
* business logic is based on `src/scala/com/pricehero/serde/ActionPipe.scala`
* data-handling lives in `src/scala/com/pricehero/rates/RatesService.scala`

*API documentation*

running: `sbt run`, or `sbt "run <json file path>"`


HTTP(S):

* Send a `GET` to `localhost:8080/?start=<ISO8601>&stop=<ISO8601>&format=<json|xml>` and you'll get either a json blob like `{"rate":925}` or an xml blob like `<rate>925</rate>`
* For instance, running on localhost: `http://localhost:8080/?start=2015-04-14T11:07:36.639Z&stop=2015-04-14T11:37:36.639Z&format=xml` will get you either an xml blob.
* If you send non-ISO8601 compatible datetimes, you'll get back a 400 explaining what went wrong.
* If you send non-implemented formats, you'll get back a json response
* If you hit any other endpoint, you'll get back a 404 response

2. Protobufs: did not make the cut, but there's a .proto file in resources, and the architecture is laid out to handle them, but still made it into the design process. Frankly, it's a weekend and I'm not being paid to do this, so I'm not messing around with installing protoc and implementing a finagle codec for it.


*Further Work*

* I didn't get to protobufs but the architectural work is done; there's simply not an implementation of a protobuf service running on :8081 yet.
* Add a codec to finagle for handling protobufs via tcp connection. Nothing awful in theory, but I'm not doing it this weekend. Namely, it requires conversions from the generated protobuf case classes to `RateQuery` and `RateResponse` and an error type in the proto file. Also needs a service implementation, but that's easy given the `Read[In, A]`, and `Write[B, Out]` and `ActionPipe[A, B, In, Out]` traits, which are explicitly designed for that. Frankly I just ran out of inclination to mess with protobufs at the end, and finagle prefers thrift drastically.
* Add more tests to the storage code, because it's pretty much untested now.
