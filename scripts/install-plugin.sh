#!/usr/bin/env bash

set -e

rm -f ../server/plugins/SimpleCommandCooldown-*.jar
cp ../target/SimpleCommandCooldown-*.jar ../server/plugins
