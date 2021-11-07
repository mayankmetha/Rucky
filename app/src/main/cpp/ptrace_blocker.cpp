#include <jni.h>
#include <unistd.h>
#include <sys/ptrace.h>
#include <sys/wait.h>
#include <pthread.h>

static int child_pid;

void *monitor_pid() {
    int status;
    waitpid(child_pid, &status, 0);
    _exit(0);

}

void anti_debug() {
    child_pid = fork();
    if (child_pid == 0) {
        int ppid = getppid();
        int status;
        if (ptrace(PTRACE_ATTACH, ppid, NULL, NULL) == 0) {
            waitpid(ppid, &status, 0);
            ptrace(PTRACE_CONT, ppid, NULL, NULL);
            while (waitpid(ppid, &status, 0)) {
                if (WIFSTOPPED(status)) {
                    ptrace(PTRACE_CONT, ppid, NULL, NULL);
                } else {
                    _exit(0);
                }
            }
        }
    } else {
        pthread_t t;
        pthread_create(&t, nullptr, reinterpret_cast<void *(*)(void *)>(monitor_pid), (void *)nullptr);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_mayank_rucky_activity_EditorActivity_ptraceBlock(JNIEnv* env, jobject /* this */) {
    anti_debug();
}