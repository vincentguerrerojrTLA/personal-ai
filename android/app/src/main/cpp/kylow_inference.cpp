#include <jni.h>
#include <cstdint>
#include <mutex>
#include <string>
#include <vector>
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
    JNIEnv* env, jobject, jlong handle, jstring systemPrompt, jstring userText, jint maxTokens) {
    Engine* engine = fromHandle(handle);
#ifndef KYLOW_HAS_LLAMA
    (void) systemPrompt;
    (void) userText;
    (void) maxTokens;
#endif
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
    const std::string prompt = toString(env, systemPrompt) + "\nUser: " +
        toString(env, userText) + "\nAssistant:";
    if (prompt.empty() || maxTokens <= 0) {
        throwState(env, "Invalid inference request.");
        return nullptr;
    }

    const llama_vocab* vocab = llama_model_get_vocab(engine->model);
    const int32_t tokenCount = -llama_tokenize(
        vocab, prompt.c_str(), static_cast<int32_t>(prompt.size()),
        nullptr, 0, true, true);
    if (tokenCount <= 0) {
        throwState(env, "Unable to tokenize prompt.");
        return nullptr;
    }

    std::vector<llama_token> tokens(static_cast<size_t>(tokenCount));
    if (llama_tokenize(vocab, prompt.c_str(), static_cast<int32_t>(prompt.size()),
                       tokens.data(), tokenCount, true, true) < 0) {
        throwState(env, "Prompt tokenization failed.");
        return nullptr;
    }

    llama_batch batch = llama_batch_get_one(tokens.data(), tokenCount);
    if (llama_decode(engine->context, batch) != 0) {
        throwState(env, "Prompt decode failed.");
        return nullptr;
    }

    llama_sampler* sampler = llama_sampler_chain_init(llama_sampler_chain_default_params());
    llama_sampler_chain_add(sampler, llama_sampler_init_temp(0.7f));
    llama_sampler_chain_add(sampler, llama_sampler_init_dist(0));

    std::string output;
    for (int i = 0; i < maxTokens; ++i) {
        const llama_token token = llama_sampler_sample(sampler, engine->context, -1);
        if (llama_vocab_is_eog(vocab, token)) break;

        char piece[256];
        const int32_t n = llama_token_to_piece(vocab, token, piece, sizeof(piece), 0, true);
        if (n < 0) {
            llama_sampler_free(sampler);
            throwState(env, "Token conversion failed.");
            return nullptr;
        }
        output.append(piece, static_cast<size_t>(n));

        llama_token next = token;
        batch = llama_batch_get_one(&next, 1);
        if (llama_decode(engine->context, batch) != 0) {
            llama_sampler_free(sampler);
            throwState(env, "Generation decode failed.");
            return nullptr;
        }
    }
    llama_sampler_free(sampler);
    return env->NewStringUTF(output.c_str());
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
