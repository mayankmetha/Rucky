openssl sha512 ./arm64-v8a/rucky-hid | cut -d"=" -f2 | cut -d" " -f2 > ./arm64-v8a/rucky-hid.sha512
openssl sha512 ./armeabi-v7a/rucky-hid | cut -d"=" -f2 | cut -d" " -f2 > ./armeabi-v7a/rucky-hid.sha512
openssl sha512 ./x86/rucky-hid | cut -d"=" -f2 | cut -d" " -f2 > ./x86/rucky-hid.sha512
openssl sha512 ./x86_64/rucky-hid | cut -d"=" -f2 | cut -d" " -f2 > ./x86_64/rucky-hid.sha512
