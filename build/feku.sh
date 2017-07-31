#!/usr/bin/env bash

ARCHIVE_NAME=feku
ARCHIVE_VERSION=0.1-SNAPSHOT

if [ -f "${ARCHIVE_NAME}-${ARCHIVE_VERSION}.jar" ]
then
    APP_LIB="."
elif [ -f "build/${ARCHIVE_NAME}-${ARCHIVE_VERSION}.jar" ]
then
    APP_LIB="build/"
fi

if [ ! -d "$APP_LIB" -o ! -f "${APP_LIB}/${ARCHIVE_NAME}-${ARCHIVE_VERSION}.jar" ]
then
    echo "ERROR: cannot find ${APP_LIB}/${ARCHIVE_NAME}-${ARCHIVE_VERSION}.jar file." 1>&2
    exit 2
fi

java -jar "${APP_LIB}/${ARCHIVE_NAME}-${ARCHIVE_VERSION}.jar" $@