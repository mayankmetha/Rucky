openssl sha512 ./release/rucky.apk | cut -d"=" -f2 | cut -d" " -f2 > ./release/rucky.sha512
