# **Architecture Goals**

------------------------

Overriding goals:
1. Handle exceptions in a central location
2. Treat HTTP(S)/Protobufs as transport layer implementations for the same interface
3. Treat JSON/XML as different implementations of the same SERDE interface
4. All services have in-memory implementations

# ***RatesService***

`rateForTime(dateTime: DateTime): Rate`


actor organization:
                    PriceHeroSupervisor
                   /                   \
             RequestsPool         RatesService
             /     |     \             |
           [JSON][XML][Protobuf]  JSONBackedRatesDAO


RequestsPool:
  -> Queue-backed request handler pool, spins up actors to handle more requests
  -> At max-backup, drop requests on the floor or return 500s/errors
  -> MaxActors: (Int, Int, Int) defines the maximum number of request handlers per handler-type
  -> MaxBackup: maximum queue size before we just do 500s

JSON/XML/ProtobufRequestHandler:
  -> SerDe for requests
  -> All extend RequestHandler
  -> Ping RatesService and wait for successsful response, send response, exit
  -> Handlers have access to the RatesService by internal requests, which it resolves asynchronously as well.

RatesService:
  -> Queue of pending requests
  -> At max-backup, return requests to sender
