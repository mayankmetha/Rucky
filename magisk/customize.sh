#!/system/bin/sh

# Stable Android 12
if [[ "$(getprop ro.build.version.release)" -le "6" ]]; then
    ui_print "*******************************"
    ui_print " Use Android 6 or above        "
    ui_print "*******************************"

    # Abort install and clean up
    rm -fr $TMPDIR $MODPATH
    exit 1
else
    ui_print "*******************************"
    ui_print "             Rucky             "
    ui_print "         Mayank Metha D        "
    ui_print "*******************************"
    ui_print "- Hack at your own risk"
    ui_print "*******************************"
fi