## Geequel Shell

![Build Status](https://bamboo.graphfoundation.org/plugins/servlet/wittified/build-status/GEEQUEL-SHELL)

Geequel Shell is a powerful CLI for running Geequel queries against ONgDB. It comes bundled with each ONgDB release and can be installed independently on many systems.

## What is ONgDB?

[ONgDB](https://graphfoundation.org) _/ Owen-Gee-Dee-Bee /_ which stands for Open Native Graph Database, is an open source, high performance, native graph store with everything you would expect from an enterprise-ready database, including high availability clustering, ACID transactions, and an intuitive, pattern-centric graph query language.
Developers use graph theory-based structures that we call nodes and relationships instead of rows and columns.
For many use cases, ONgDB will provide orders of magnitude performance benefits compared to non-native graph, relational and NoSQL databases.

Learn more on the [Graph Foundation ONgDB site](https://graphfoundation.org/projects/ongdb).

## What is Geequel?

Geequel is ONgDB’s powerful Graph Query Language. It is a declarative, pattern matching language optimized for querying graph networks. Geequel is an implementation of openCypher®, the most widely adopted, fully-specified, and open query language for property graph databases. Geequel provides an intuitive way to work with ONgDB, a property graph, and is a great on-ramp to the Graph Query Language (GQL) standard being developed by ISO. Geequel is easy to learn and human-readable, making Geequel approachable, useful and unifying for business analysts, data scientists, software developers and operations professionals. The declarative nature of Geequel allows users to simply express the data they wish to retrieve while the underlying Geequel query runtime completes the task without burdening the user with Geequel implementation details.

## How to build

Use `make help` (`gradlew tasks`) to list possible tasks. But you
probably want either

-  `make build` (`gradlew installDist`) which will build an
   uber-jar and runnable script for you at
   `geequel-shell/build/install/geequel-shell`

- `make zip` which builds an uber-jar with runnable script and
   packages it up for you as: `out/geequel-shell.zip` Note that this
   will run a test on the script which requires a instance of ongdb
   (see Integration tests below).

- `make untested-zip` which builds the same zip file but doesn't test
  it. It will be placed in `tmp/geequel-shell.zip`.

## How to run, the fast way

This clears any previously known ongdb hosts, starts a throw-away
instance of ongdb, and connects to it.

```sh
rm -rf ~/.ongdb/known_hosts
docker run --detach -p 7687:7687 -e ONGDB_AUTH=none graphfoundation/ongdb:1.0
make run
```

## How to build packages

Packages require you to have `pandoc` available. It should be
available in your local package manager.

Then just do

```
make debian rpm
```

To test the packages you need to have Docker installed:

```
make debian-test rpm-test
```

To get the versions correct when building packages you can override
some variables, for example with:

```
make debian pkgversion=2
```

See `make info` for a list of variables and what the results will be.

## Development

### Integration tests

#### Prerequisites for running integration tests

1. ONgDB server with bolt driver configured
1. Authentication with username `ongdb` and password `owengee`

#### To run

Integration tests are usually skipped when you run `make test`
(`gradlew test`)

Use `make integration-test` (`gradlew integrationTest`) to
specifically run integration tests.

#### How to run the fast way

This clears any previously known ongdb hosts, starts a throw-away
instance of ongdb, and runs the integration tests against it.

```sh
rm -rf ~/.ongdb/known_hosts
docker run --detach -p 7687:7687 -e ONGDB_AUTH=ongdb/owengee graphfoundation/ongdb:1.0
make integration-test
```

## Licensing

Geequel Shell is an open source product licensed under GPLv3.

## Unaffiliated with Neo4j, Inc.
ONgDB is an independent fork of Neo4j® Enterprise Edition version 3.4.0.rc02 licensed under the AGPLv3 and/or Community Edition licensed under GPLv3. ONgDB and Graph Foundation, Inc. are not affiliated in any way with Neo4j, Inc. or Neo4j Sweden AB. Neo4j, Inc. and Neo4j Sweden AB do not sponsor or endorse ONgDB and Graph Foundation, Inc. Neo4j Sweden AB is the owner of the copyrights for Neo4j® software and commercial use of any source code from Neo4j® Enterprise Edition beyond Neo4j® Enterprise Edition version 3.2.14, Neo4j® Enterprise Edition version 3.3.10, and/or Neo4j® Enterprise Edition version 3.4.0.rc02 is prohibited and could subject the user to claims of copyright infringement.

Neo4j®, Cypher® and openCypher® are registered trademarks of Neo4j, Inc.