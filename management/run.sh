#!/bin/bash
root=$(pwd)
workdir="$root/src/main/proto"
javaPackageName='org.thethingsnetwork.management.proto'
branch='master'

echo "Cleaning workspace...";
rm -rf "$workdir/ttn"
rm -rf "$workdir/github.com"
rm -rf "$workdir/google"
mkdir -p "$workdir/github.com/gogo/protobuf/gogoproto/"
mkdir -p "$workdir/google/api"
echo "done."

echo "Fetching gogo.proto";
cd "$workdir/github.com/gogo/protobuf/gogoproto/"
wget https://raw.githubusercontent.com/gogo/protobuf/master/gogoproto/gogo.proto
echo "done."

echo "Fetching annotations.proto";
cd "$workdir/google/api"
wget https://raw.githubusercontent.com/grpc-ecosystem/grpc-gateway/acebe0f9ff5993e130b141ee60e83e592839ca22/third_party/googleapis/google/api/annotations.proto
echo "done."

echo "Fetching http.proto";
cd "$workdir/google/api"
wget https://raw.githubusercontent.com/grpc-ecosystem/grpc-gateway/acebe0f9ff5993e130b141ee60e83e592839ca22/third_party/googleapis/google/api/http.proto
echo "done."

echo "Fetching ttn repo...";
cd "$workdir"
git clone -b $branch https://github.com/TheThingsNetwork/ttn.git
echo "done."

find $workdir -type f ! -name "*.proto" -delete
find $workdir -type d -empty -delete

echo "Patching files...";
folders=`find "$workdir/ttn/api" -type f -name "*.proto"`
for i in $folders
do
    sed -n -i "H;\${x;s/^\n//;s/option go_package .*\n/option java_package = \"$javaPackageName\";\n\n&/;p;}" $i
done
echo "done."

echo "over!"
