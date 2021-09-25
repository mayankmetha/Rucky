#include <jni.h>
#include <cstring>
#include <random>

extern const char* __progname; //NOLINT

const char* originalName = "com.mayank.rucky";

const char* processName[] = {
        "com.google.vr.vrcore",
        "android.process.media",
        "com.google.android.gms",
        "com.google.android.apps.turbo",
        "com.android.systemui",
        "com.google.android.googlequicksearch",
        "com.google.process.gservices",
        "com.google.android.gm",
        "com.google.android.videos",
        "com.android.chrome:sandboxed_process0"
};

extern "C" JNIEXPORT jstring JNICALL
Java_com_mayank_rucky_activity_WelcomeActivity_changeProcessName(JNIEnv* env, jobject /* this */) {
    std::random_device r;
    std::seed_seq seed{r(), r(), r(), r(), r(), r(), r(), r()};
    std::mt19937 generator(seed);
    std::uniform_int_distribution<int> randInt(0,sizeof(processName)/sizeof(processName[0]));
    int value = randInt(generator);
    char *currentName = (char*)__progname;
    strcpy(currentName,processName[value]);
    currentName[strlen(processName[value])] = '\0';
    return env->NewStringUTF(currentName);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_mayank_rucky_activity_WelcomeActivity_restoreProcessName(JNIEnv* env, jobject /* this */) {
    char* currentName = (char*)__progname;
    strcpy(currentName,originalName);
    currentName[strlen(originalName)] = '\0';
    return env->NewStringUTF(currentName);
}