if [ "$1" = "release" ]; then
    openssl sha512 ./release/rucky.apk | cut -d"=" -f2 | cut -d" " -f2 > ./release/rucky.sha512
    echo "$2" > ./release/rucky.cfg
    echo "$(($(git rev-list HEAD --count master)+1))" >> ./nightly/rucky.cfg
elif [ "$1" = "nightly" ]; then
    read -p "Commit Msg:" commitMsg
    cp ./nightly/rucky-nightly.apk ../nightly/rucky-nightly.apk
    openssl sha512 ../nightly/rucky-nightly.apk | cut -d"=" -f2 | cut -d" " -f2 > ../nightly/rucky.sha512
    echo "$2" > ../nightly/rucky.cfg
    nextCommitCount=$(($(git rev-list HEAD --count master)+1))
    echo "$(($(git rev-list HEAD --count master)+1))" >> ../nightly/rucky.cfg
    diffCommitCount=$(($(git rev-list HEAD --count master)-$3))
    git log --oneline -$((diffCommitCount)) -s --format='• %s' | uniq -iu | tail -r > ../nightly/Changelog
    echo "• $commitMsg" >> ../nightly/Changelog
    git add -f ../nightly/rucky-nightly.apk ../nightly/rucky.cfg ../nightly/rucky.sha512 ../nightly/Changelog
    git commit -m "$commitMsg"
    git push
fi
