#!/bin/bash
ENV=$1

if [ "$ENV" = 'us' ]; then
    SERVER=54.209.142.141
elif [ "$ENV" = 'cn' ]; then
    SERVER=115.28.44.110
else
    echo "deploy.sh env"
    exit 1
fi

PROJECT=acra
PROJECT_ROOT=/vol/$PROJECT
USER=armor
GROUP=admin

pushd $(dirname $0)
TMP_TAR=/tmp/$PROJECT.tar.gz
tar --exclude acralyzer --exclude src/node_modules --exclude src/logs --exclude src/uploaded -czf $TMP_TAR * > /dev/null

echo "Uploading source to "${SERVER}
ssh $USER@$SERVER "sudo mkdir -p $PROJECT_ROOT &>/dev/null;\
                   sudo chown $USER:$GROUP $PROJECT_ROOT;"

scp $TMP_TAR $USER@$SERVER:$TMP_TAR
version=$(date +%Y%m%d%H%M%S)
ssh $USER@$SERVER " cd $PROJECT_ROOT && tar -xf $TMP_TAR;\
                    mkdir -p $PROJECT_ROOT/.history/$version;\
                    cd $PROJECT_ROOT/.history/$version && tar -xf $TMP_TAR;\
                    rm $TMP_TAR;\
                    mkdir -p $PROJECT_ROOT/src/logs;\

                    cd $PROJECT_ROOT && bash ./npm-install.sh;\
                  "
rm $TMP_TAR
popd > /dev/null
