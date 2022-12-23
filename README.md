# SEM Group22a Working Prototype

This template contains three microservices:
- authentication-member-microservice
- hoa-microservice
- voting-microservice

The `authentication-member-microservice` is responsible for registering new users, authenticating them, and keeping 
track of the membership pairs (member and hoa).

The `hoa-microservice` is the central and most extensive one of the bunch. It is responsible for keeping track of the
HOAs, which also includes the activities, requirements, notifications, board members, and election histories.

Finally, the `voting-microservice` is responsible for handling anything pertaining to the elections, including
creating new ones, updating votes, calculating final outcomes, and voting validation.

## Running the microservices

You can simply run the three microservices individually through Spring's default app bootloader. Two things to note:
- In proper deployment only the HOA and auth-member microservices are supposed to be visible to a potential client,
hence why the authenticating code is somewhat overlapping between the two services, but it must *always* start from 
auth-member in order to function.

- Because of this, the two microservices **must have the same jwtSecret** in order for the authentication to carry over 
properly