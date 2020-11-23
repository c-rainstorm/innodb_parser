#!/bin/bash

source iprc
source tools.sh

ensureImageExist "${IMAGE}"

mkdir -p "${MOUNT_VOLUME}"
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

stopContainer "${CONTAINER_NAME}"

docker run --name "${CONTAINER_NAME}" -p 3306:3306 \
    -v "${MY_CONF_DIR}":/etc/mysql/conf.d \
    -v "${MOUNT_VOLUME}":/var/lib/mysql \
    -v "${INIT_DB_DIR}":/docker-entrypoint-initdb.d \
    -e MYSQL_ROOT_PASSWORD="$(cat "${MYSQL_PASS_FILE}")" \
    -d "${IMAGE}"

echo "#######################DB 配置#######################"
cat "${MY_CONF_DIR}"/*
echo "#######################DB 配置#######################"

echo "#######################DB 初始化#######################"
cat "${INIT_DB_DIR}"/*
echo "#######################DB 初始化#######################"
