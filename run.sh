#!/bin/bash

if [[ $TRAVIS_PULL_REQUEST == "false" ]] && [[ $TRAVIS_BRANCH = "master" ]]; then
    openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in pubring.gpg.enc -out pubring.gpg -d
    openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in secring.gpg.enc -out secring.gpg -d
    mvn clean validate compile test package verify install deploy --settings settings.xml
    exit $?
else
    mvn clean validate compile test package verify
    exit $?
fi