#!/bin/bash

source iprc.sh
source tools.sh

ensureImageExist "${MYSQL_IMAGE}"

mkdir -p "${MYSQL_MOUNT_VOLUME}"
mkdir -p "${PASSWORD_DIR}"

echo "######################################################"
# shellcheck disable=SC2046
# shellcheck disable=SC2002
if [ ! -e "${MYSQL_PASS_FILE}" ] || [ $(cat "${MYSQL_PASS_FILE}" | wc -l) -eq 0 ]; then
    openssl rand -base64 10 >"${MYSQL_PASS_FILE}"
    echo "new password generated, find it in ${MYSQL_PASS_FILE}"
else
    echo "use password in ${MYSQL_PASS_FILE} generated before"
fi
echo "######################################################"

stopContainer "${MYSQL_CONTAINER_NAME}"

docker run --name "${MYSQL_CONTAINER_NAME}" -p 3306:3306 \
    -v "${MYSQL_MY_CONF_DIR}":/etc/mysql/conf.d \
    -v "${MYSQL_MOUNT_VOLUME}":/var/lib/mysql \
    -v "${MYSQL_INIT_DB_DIR}":/docker-entrypoint-initdb.d \
    -e MYSQL_ROOT_PASSWORD="$(cat "${MYSQL_PASS_FILE}")" \
    -d "${MYSQL_IMAGE}"

echo "#######################DB 配置#######################"
cat "${MYSQL_MY_CONF_DIR}"/*
echo "#######################DB 配置#######################"

echo "#######################DB 初始化#######################"
cat "${MYSQL_INIT_DB_DIR}"/*
echo "#######################DB 初始化#######################"
