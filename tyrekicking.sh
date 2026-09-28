#!/bin/bash -eu
# Should be run in the out folder, containing geequel-shell.zip

function prepare {
  unzip geequel-shell.zip
}

function prepare-bundle {
  mkdir -p geequel-shell/tools
  mv geequel-shell/*.jar geequel-shell/tools
}

function testscript {
  # first try the default address (bolt://, unencrypted), if that fails with encryption and the server's
  # self-signed certificate (a server that requires Bolt TLS)
  if geequel-shell/geequel-shell -u ongdb -p owengee "RETURN 1;"; then
    echo "$1 Success!"
  elif geequel-shell/geequel-shell -a "bolt+ssc://localhost:7687" -u ongdb -p owengee "RETURN 1;"; then
    echo "$1 Success!"
  else
    echo "$1 Failure!"
    exit 1
  fi
}

prepare
## Standalone test
testscript "Standalone"
## Fake bundling test
prepare-bundle
testscript "Bundling"
