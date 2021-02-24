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

#### Pre Requisites for running integration tests

ONgDB server with bolt driver configured.

If authentication is required, it is assumed to be username `ongdb`
and password `ongdb`.

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
docker run --detach -p 7687:7687 -e ONGDB_AUTH=none graphfoundation/ongdb:1.0
make integration-test
```

## Licensing

Geequel Shell is an open source product licensed under GPLv3.

## Unaffiliated with Neo4j, Inc.
ONgDB is an independent fork of Neo4j® Enterprise Edition version 3.4.0.rc02 licensed under the AGPLv3 and/or Community Edition licensed under GPLv3. ONgDB and Graph Foundation, Inc. are not affiliated in any way with Neo4j, Inc. or Neo4j Sweden AB. Neo4j, Inc. and Neo4j Sweden AB do not sponsor or endorse ONgDB and Graph Foundation, Inc. Neo4j Sweden AB is the owner of the copyrights for Neo4j® software and commercial use of any source code from Neo4j® Enterprise Edition beyond Neo4j® Enterprise Edition version 3.2.14, Neo4j® Enterprise Edition version 3.3.10, and/or Neo4j® Enterprise Edition version 3.4.0.rc02 is prohibited and could subject the user to claims of copyright infringement.