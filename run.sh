#!/bin/bash

if [[ $TRAVIS_PULL_REQUEST == "false" ]] && [[ $TRAVIS_BRANCH = "fix-ci" ]]; then
    openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in $GPG_DIR/pubring.gpg.enc -out $GPG_DIR/pubring.gpg -d
    openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in $GPG_DIR/secring.gpg.enc -out $GPG_DIR/secring.gpg -d
    rm -rf samples
    mvn clean package install javadoc:aggregate javadoc:jar source:jar-no-fork deploy --settings settings.xml -DperformRelease=true
    #publish doc
    rm -rf out
    mkdir out
    cd out
    git init
    git config user.name "Travis-CI"
    git config user.email "travis@java-app-sdk"
    cp -r ../target/site/apidocs/* ./
    git add .
    git commit -m "Deployed to Github Pages"
    git push --force --quiet "https://${GH_TOKEN}@${GH_REF}" master:gh-pages > /dev/null 2>&1
    exit $?
else
    mvn clean package javadoc:aggregate javadoc:jar source:jar-no-fork
    exit $?
fi