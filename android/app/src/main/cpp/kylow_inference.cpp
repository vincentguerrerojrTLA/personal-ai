#include <jni.h>
#include <cstdint>
#include <mutex>
#include <string>
#ifdef KYLOW_HAS_LLAMA
#include "llama.h"
#endif

namespace {
struct Engine {
    std::string model_path;
    int context_size;
    std::mutex mutex;
#ifdef KYLOW_HAS_LLAMA
    llama_model* model = nullptr;
    llama_context* context = nullptr;
#endif
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
    auto* engine = new Engine();
    engine->model_path = path;
    engine->context_size = static_cast<int>(contextSize);
#ifdef KYLOW_HAS_LLAMA
    llama_backend_init();
    llama_model_params modelParams = llama_model_default_params();
    engine->model = llama_model_load_from_file(path.c_str(), modelParams);
    if (!engine->model) {
        delete engine;
        throwState(env, "Unable to load verified GGUF model.");
        return 0;
    }
    llama_context_params contextParams = llama_context_default_params();
    contextParams.n_ctx = static_cast<uint32_t>(contextSize);
    engine->context = llama_init_from_model(engine->model, contextParams);
    if (!engine->context) {
        llama_model_free(engine->model);
        delete engine;
        throwState(env, "Unable to create inference context.");
        return 0;
    }
#endif
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
#ifdef KYLOW_HAS_LLAMA
    if (!engine->model || !engine->context) {
        throwState(env, "llama.cpp backend is not initialized.");
        return nullptr;
    }
    // Model/context ownership is now real. Tokenization and decode/sampling are
    // implemented in the next slice rather than returning fabricated output.
    throwState(env, "llama.cpp model loaded; generation pipeline is not enabled yet.");
#else
    throwState(env, "Native bridge loaded, but model inference backend is not linked.");
#endif
    return nullptr;
}

extern "C" JNIEXPORT void JNICALL
Java_com_kylow_mobile_NativeInferenceBridge_destroy(
    JNIEnv*, jobject, jlong handle) {
    Engine* engine = fromHandle(handle);
    if (!engine) return;
#ifdef KYLOW_HAS_LLAMA
    if (engine->context) llama_free(engine->context);
    if (engine->model) llama_model_free(engine->model);
#endif
    delete engine;
}
