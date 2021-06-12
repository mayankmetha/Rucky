while getopts b:a: flag
do
    case "${flag}" in
        a) minSdkVersion=${OPTARG};;
        b) buildType=${OPTARG};;
    esac
done
if [ "$buildType" = "release" ]; then
    openssl sha512 ./release/rucky.apk | cut -d"=" -f2 | cut -d" " -f2 > ./release/rucky.sha512
    echo "$minSdkVersion" > ./release/rucky.cfg
    echo "$(($(git rev-list HEAD --count master)+1))" >> ./nightly/rucky.cfg
elif [ "$buildType" = "nightly" ]; then
    openssl sha512 ./nightly/rucky-nightly.apk | cut -d"=" -f2 | cut -d" " -f2 > ./nightly/rucky.sha512
    echo "$minSdkVersion" > ./nightly/rucky.cfg
    echo "$(($(git rev-list HEAD --count master)+1))" >> ./nightly/rucky.cfg
fi
