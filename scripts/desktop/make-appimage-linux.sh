#!/bin/bash

set -e

function readlink() {
  echo "$(cd "$(dirname "$1")"; pwd -P)"
}

root_dir="$(dirname "$(dirname "$(readlink "$0")")")"
multiplatform_dir=$root_dir/apps/multiplatform
release_app_dir=$root_dir/apps/multiplatform/release/main/app

cd $multiplatform_dir
libcrypto_path=$(ldd common/src/commonMain/cpp/desktop/libs/*/deps/libHSdirect-sqlcipher-*.so | grep libcrypto | cut -d'=' -f 2 | cut -d ' ' -f 2)

cp $libcrypto_path common/src/commonMain/cpp/desktop/libs/*/deps
./gradlew createDistributable
rm common/src/commonMain/cpp/desktop/libs/*/deps/`basename $libcrypto_path`
rm desktop/src/jvmMain/resources/libs/*/`basename $libcrypto_path`

rm -rf $release_app_dir/AppDir 2>/dev/null
mkdir -p $release_app_dir/AppDir/usr

cd $release_app_dir/AppDir
cp -r ../*imple*/{bin,lib} usr
cp usr/lib/simplex.png .
ln -s usr/bin/*imple* AppRun
cp $multiplatform_dir/desktop/src/jvmMain/resources/distribute/*imple*.desktop .
sed -i 's|Exec=.*|Exec=simplex|g' *imple*.desktop
sed -i 's|Icon=.*|Icon=simplex|g' *imple*.desktop

appimagetool-x86_64.AppImage .

mv *imple*.AppImage ../../
