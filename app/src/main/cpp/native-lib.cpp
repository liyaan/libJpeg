#include <jni.h>
#include <string>

#include <malloc.h>
#include <android/bitmap.h>
#include <jpeglib.h>
extern "C" JNIEXPORT jstring JNICALL
Java_com_liyaan_libjpeg_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

void write_JPEG_file(uint8_t *data, int w, int h, jint q, const char *path){
    jpeg_compress_struct jcs;
    jpeg_error_mgr error;
    jcs.err = jpeg_std_error(&error);
    jpeg_create_compress(&jcs);
    FILE *f = fopen(path, "wb");
    jpeg_stdio_dest(&jcs,f);
    jcs.image_width = w;
    jcs.image_height = h;
    jcs.input_components = 3;
    jcs.in_color_space = JCS_RGB;
    jpeg_set_defaults(&jcs);
    jcs.optimize_coding = true;
    jpeg_set_quality(&jcs,q,1);
    jpeg_start_compress(&jcs,1);
    int row_stride = w*3;
    JSAMPROW row[1];
    while (jcs.next_scanline<jcs.image_height){
        uint8_t *pixels = data+jcs.next_scanline*row_stride;
        row[0] = pixels;
        jpeg_write_scanlines(&jcs,row,1);
    }
    jpeg_finish_compress(&jcs);
    fclose(f);
    jpeg_destroy_compress(&jcs);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_liyaan_libjpeg_MainActivity_nativeCompress(JNIEnv *env, jobject thiz, jobject bitmap,
                                                    jint q, jstring path_) {
    const char *path = env->GetStringUTFChars(path_,0);
    AndroidBitmapInfo info;
    AndroidBitmap_getInfo(env,bitmap,&info);
    uint8_t *pixels;
    AndroidBitmap_lockPixels(env,bitmap,(void **)&pixels);
    int w = info.width;
    int h = info.height;
    int color;

    uint8_t *data = (uint8_t *)malloc(w*h*3);
    uint8_t *temp = data;
    uint8_t r,g,b;
    for(int i=0;i<h;i++){
        for (int j=0;j<w;j++){
            color = *(int *)pixels;
            r = (color >> 16) & 0xFF;
            g = (color >> 8) & 0xFF;
            b = color & 0xFF;

            *data = b;
            *(data+1) = g;
            *(data+2) = r;
            data+=3;
            pixels += 4;
        }
    }
    write_JPEG_file(temp, w, h, q, path);
    free(temp);
    AndroidBitmap_unlockPixels(env,bitmap);
    env->ReleaseStringUTFChars(path_, path);
}