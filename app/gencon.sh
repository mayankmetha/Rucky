while getopts b:a:n:m flag
do
    case "${flag}" in
        a) minSdkVersion=${OPTARG};;
        b) buildType=${OPTARG};;
        n) lastBuild=${OPTARG};;
        m) commitMsg=${OPTARG};;
    esac
done
if [ "$buildType" = "release" ]; then
    openssl sha512 ./release/rucky.apk | cut -d"=" -f2 | cut -d" " -f2 > ./release/rucky.sha512
    echo "$minSdkVersion" > ./release/rucky.cfg
    echo "$(($(git rev-list HEAD --count master)+1))" >> ./nightly/rucky.cfg
elif [ "$buildType" = "nightly" ]; then
    cp ./nightly/rucky-nightly.apk ../nightly/rucky-nightly.apk
    openssl sha512 ./nightly/rucky-nightly.apk | cut -d"=" -f2 | cut -d" " -f2 > ../nightly/rucky.cfg
    echo "$minSdkVersion" > ../nightly/rucky.cfg
    nextCommitCount=$(($(git rev-list HEAD --count master)+1))
    echo "$(($(git rev-list HEAD --count master)+1))" >> ../nightly/rucky.cfg
    diffCommitCount=$(($(git rev-list HEAD --count master)-$lastBuild))
    git log --oneline -$((diffCommitCount)) -s --format='• %s' | uniq -iu | tail -r > ../nightly/Changelog
    echo "• $((commitMsg))" >> ../nightly/Changelog
    git add ../nightly/rucky-nightly.apk ../nightly/rucky.cfg ../nightly/rucky.cfg ../nightly/Changelog
    git commit -m "$((commitMsg))"
    git push
fi
