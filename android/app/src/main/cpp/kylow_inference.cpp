#include <jni.h>
#include <cstdint>
#include <mutex>
#include <string>

namespace {
struct Engine {
    std::string model_path;
    int context_size;
    std::mutex mutex;
};

Engine* fromHandle(jlong handle) {
    return reinterpret_cast<Engine*>(static_cast<intptr_t>(handle));
}

std::string toString(JNIEnv* env, jstring value) {
    if (!value) return {};
    const char* chars = env->GetStringUTFChars(value, nullptr);
    if (!chars) return {};
    std::string out(chars);
    env->ReleaseStringUTFChars(value, chars);
    return out;
}

void throwState(JNIEnv* env, const char* message) {
    jclass cls = env->FindClass("java/lang/IllegalStateException");
    if (cls) env->ThrowNew(cls, message);
}
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_kylow_mobile_NativeInferenceBridge_create(
    JNIEnv* env, jobject, jstring modelPath, jint contextSize) {
    const std::string path = toString(env, modelPath);
    if (path.empty() || contextSize <= 0) {
        throwState(env, "Invalid native model configuration.");
        return 0;
    }
    auto* engine = new Engine{path, static_cast<int>(contextSize), {}};
    return static_cast<jlong>(reinterpret_cast<intptr_t>(engine));
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_kylow_mobile_NativeInferenceBridge_generate(
    JNIEnv* env, jobject, jlong handle, jstring, jstring, jint) {
    Engine* engine = fromHandle(handle);
    if (!engine) {
        throwState(env, "Native inference engine is not initialized.");
        return nullptr;
    }
    std::lock_guard<std::mutex> lock(engine->mutex);
    // This bridge intentionally refuses to fabricate inference. A verified
    // model backend must replace this error before RuntimeState may be Ready.
    throwState(env, "Native bridge loaded, but model inference backend is not linked.");
    return nullptr;
}

extern "C" JNIEXPORT void JNICALL
Java_com_kylow_mobile_NativeInferenceBridge_destroy(
    JNIEnv*, jobject, jlong handle) {
    delete fromHandle(handle);
}
