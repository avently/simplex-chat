#!/bin/bash

set -e

trap "rm apps/multiplatform/local.properties || true; rm local.properties || true; rm /tmp/simplex.keychain || true" EXIT
echo "desktop.mac.signing.identity=SimpleX Chat Ltd" >> apps/multiplatform/local.properties
echo "desktop.mac.signing.keychain=/tmp/simplex.keychain" >> apps/multiplatform/local.properties
echo "desktop.mac.notarization.apple_id=$APPLE_SIMPLEX_NOTARIZATION_APPLE_ID" >> apps/multiplatform/local.properties
echo "desktop.mac.notarization.password=$APPLE_SIMPLEX_NOTARIZATION_PASSWORD" >> apps/multiplatform/local.properties
echo "desktop.mac.notarization.team_id=5NN7GUYB6T" >> apps/multiplatform/local.properties
echo "$APPLE_SIMPLEX_SIGNING_KEYCHAIN" | base64 --decode - > /tmp/simplex.keychain

date
echo 1
security find-certificate -a -c "Developer ID Application: SimpleX Chat Ltd" /tmp/simplex.keychain
echo 2
open -a "Keychain Access" /tmp/simplex.keychain
security unlock-keychain -p "" /tmp/simplex.keychain
echo 3
security find-certificate -a -c "Developer ID Application: SimpleX Chat Ltd" /tmp/simplex.keychain
/usr/bin/codesign -vvvv --timestamp --options runtime --force --prefix "chat.simplex." --sign "Developer ID Application: SimpleX Chat Ltd (5NN7GUYB6T)" --keychain /tmp/simplex.keychain /Users/runner/work/simplex-chat/simplex-chat/apps/multiplatform/local.properties

scripts/desktop/build-lib-mac.sh
cd apps/multiplatform
./gradlew packageDmg
./gradlew notarizeDmg
 