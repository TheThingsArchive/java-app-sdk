#!/bin/bash

if [[ $TRAVIS_PULL_REQUEST == "false" ]] && [[ $TRAVIS_BRANCH = "master" ]]; then
    openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in pubring.gpg.enc -out pubring.gpg -d
    openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in secring.gpg.enc -out secring.gpg -d
    mvn clean package install javadoc:aggregate javadoc:jar source:jar-no-fork deploy --settings settings.xml -DperformRelease=true
    exit $?
else
    mvn clean package javadoc:aggregate javadoc:jar source:jar-no-fork
    exit $?
fi