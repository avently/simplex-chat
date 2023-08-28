#!/bin/bash

set -e

trap "rm apps/multiplatform/local.properties 2> /dev/null || true; rm local.properties 2> /dev/null || true; rm /tmp/simplex.keychain" EXIT
echo "desktop.mac.signing.identity=SimpleX Chat Ltd" >> apps/multiplatform/local.properties
echo "desktop.mac.signing.keychain=/tmp/simplex.keychain" >> apps/multiplatform/local.properties
echo "desktop.mac.notarization.apple_id=$APPLE_SIMPLEX_NOTARIZATION_APPLE_ID" >> apps/multiplatform/local.properties
echo "desktop.mac.notarization.password=$APPLE_SIMPLEX_NOTARIZATION_PASSWORD" >> apps/multiplatform/local.properties
echo "desktop.mac.notarization.team_id=5NN7GUYB6T" >> apps/multiplatform/local.properties
echo "$APPLE_SIMPLEX_SIGNING_KEYCHAIN" | base64 --decode - > /tmp/simplex.keychain

security unlock-keychain -p "" /tmp/simplex.keychain
# Adding keychain to the list of keychains. 
# Otherwise, it can find cert but exits while signing with "error: The specified item could not be found in the keychain."
security list-keychains -s `security list-keychains | xargs` /tmp/simplex.keychain
codesign -vvvv --timestamp --options runtime --force --prefix "chat.simplex." --sign "Developer ID Application: SimpleX Chat Ltd (5NN7GUYB6T)" --keychain /tmp/simplex.keychain /Users/runner/work/simplex-chat/simplex-chat/apps/multiplatform/local.properties
exit 1
scripts/desktop/build-lib-mac.sh
cd apps/multiplatform
./gradlew packageDmg
./gradlew notarizeDmg
 