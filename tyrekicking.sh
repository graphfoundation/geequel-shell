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
  # first try with encryption on (3.X series), if that fails with encryption of (4.X series)
  if geequel-shell/geequel-shell -u ongdb -p owengee --encryption true "RETURN 1;"; then
    echo "$1 Success!"
  elif geequel-shell/geequel-shell -u ongdb -p owengee --encryption false "RETURN 1;"; then
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
