#!/bin/bash

function stopContainer(){
    CONTAINER_NAME=$1
    if [ "$(docker container ls --filter=name="${CONTAINER_NAME}" -q | wc -l)" -gt 0 ]; then
        echo "stopping container [${CONTAINER_NAME}]"
        docker container stop "${CONTAINER_NAME}"
        echo "done"
    fi

    if [ "$(docker container ls -a --filter=name="${CONTAINER_NAME}" -q | wc -l)" -gt 0 ]; then
        echo "removing container [${CONTAINER_NAME}]"
        docker container rm "${CONTAINER_NAME}"
        echo "done"
    fi
}

function ensureImageExist() {
    image=$1
    if [ "$(docker images --filter=reference="${image}" -q | wc -l)" -eq 0 ]; then
        echo "Image [${image}] not found, try to pull"
        if [ "$(docker pull "${image}")" ]; then
            echo "Image [${image}] pull success";
        else
            echo "Image [${image}] pull error! "
            exit 1;
        fi
    fi
}

CONTAINER_NAME=mysql
MOUNT_VOLUME=/tmp/mysql
IMAGE=mysql:5.7
MY_CONF_DIR=$(pwd)/conf.d
INIT_DB_DIR=$(pwd)/docker-entrypoint-initdb.d

ensureImageExist ${IMAGE}

rm -rf ${MOUNT_VOLUME}
mkdir -p ${MOUNT_VOLUME}

mkdir -p ~/.password
MYSQL_PASS_FILE=~/.password/mysql
echo "######################################################"
if [ ! -e ${MYSQL_PASS_FILE} ] || [ $(cat ${MYSQL_PASS_FILE} | wc -l) -eq 0 ]; then
    openssl rand -base64 10 > ${MYSQL_PASS_FILE}
    echo "new password generated, find it in ${MYSQL_PASS_FILE}"
else
    echo "use password in ${MYSQL_PASS_FILE} generated before"
fi
echo "######################################################"

stopContainer ${CONTAINER_NAME}

docker run  --name ${CONTAINER_NAME} -p 3306:3306 \
  -v ${MY_CONF_DIR}:/etc/mysql/conf.d             \
  -v ${MOUNT_VOLUME}:/var/lib/mysql               \
  -v ${INIT_DB_DIR}:/docker-entrypoint-initdb.d   \
  -e MYSQL_ROOT_PASSWORD="$(cat "${MYSQL_PASS_FILE}")"  \
  -d ${IMAGE}


echo "#######################DB 配置#######################"
cat ${MY_CONF_DIR}/*
echo "#######################DB 配置#######################"

echo "#######################DB 初始化#######################"
cat ${INIT_DB_DIR}/*
echo "#######################DB 初始化#######################"
