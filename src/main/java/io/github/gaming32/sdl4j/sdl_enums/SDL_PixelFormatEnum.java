package io.github.gaming32.sdl4j.sdl_enums;

import static io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_DEFINE_PIXELFORMAT;
import static io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_DEFINE_PIXELFOURCC;

import java.nio.ByteOrder;

public final class SDL_PixelFormatEnum {
    public static final int

    UNKNOWN = 0,
    INDEX1LSB =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.INDEX1, SDL_BitmapOrder.SDL_BITMAPORDER_4321, 0,
                               1, 0),
    INDEX1MSB =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.INDEX1, SDL_BitmapOrder.SDL_BITMAPORDER_1234, 0,
                               1, 0),
    INDEX4LSB =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.INDEX4, SDL_BitmapOrder.SDL_BITMAPORDER_4321, 0,
                               4, 0),
    INDEX4MSB =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.INDEX4, SDL_BitmapOrder.SDL_BITMAPORDER_1234, 0,
                               4, 0),
    INDEX8 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.INDEX8, 0, 0, 8, 1),
    RGB332 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED8, SDL_PackedOrder.XRGB,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_332, 8, 1),
    XRGB4444 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED16, SDL_PackedOrder.XRGB,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_4444, 12, 2),
    RGB444 = XRGB4444,
    XBGR4444 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED16, SDL_PackedOrder.XBGR,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_4444, 12, 2),
    BGR444 = XBGR4444,
    XRGB1555 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED16, SDL_PackedOrder.XRGB,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_1555, 15, 2),
    RGB555 = XRGB1555,
    XBGR1555 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED16, SDL_PackedOrder.XBGR,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_1555, 15, 2),
    BGR555 = XBGR1555,
    ARGB4444 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED16, SDL_PackedOrder.ARGB,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_4444, 16, 2),
    RGBA4444 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED16, SDL_PackedOrder.RGBA,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_4444, 16, 2),
    ABGR4444 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED16, SDL_PackedOrder.ABGR,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_4444, 16, 2),
    BGRA4444 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED16, SDL_PackedOrder.BGRA,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_4444, 16, 2),
    ARGB1555 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED16, SDL_PackedOrder.ARGB,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_1555, 16, 2),
    RGBA5551 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED16, SDL_PackedOrder.RGBA,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_5551, 16, 2),
    ABGR1555 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED16, SDL_PackedOrder.ABGR,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_1555, 16, 2),
    BGRA5551 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED16, SDL_PackedOrder.BGRA,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_5551, 16, 2),
    RGB565 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED16, SDL_PackedOrder.XRGB,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_565, 16, 2),
    BGR565 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED16, SDL_PackedOrder.XBGR,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_565, 16, 2),
    RGB24 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.ARRAYU8, SDL_ArrayOrder.RGB, 0,
                               24, 3),
    BGR24 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.ARRAYU8, SDL_ArrayOrder.BGR, 0,
                               24, 3),
    XRGB8888 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED32, SDL_PackedOrder.XRGB,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_8888, 24, 4),
    RGB888 = XRGB8888,
    RGBX8888 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED32, SDL_PackedOrder.RGBX,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_8888, 24, 4),
    XBGR8888 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED32, SDL_PackedOrder.XBGR,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_8888, 24, 4),
    BGR888 = XBGR8888,
    BGRX8888 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED32, SDL_PackedOrder.BGRX,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_8888, 24, 4),
    ARGB8888 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED32, SDL_PackedOrder.ARGB,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_8888, 32, 4),
    RGBA8888 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED32, SDL_PackedOrder.RGBA,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_8888, 32, 4),
    ABGR8888 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED32, SDL_PackedOrder.ABGR,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_8888, 32, 4),
    BGRA8888 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED32, SDL_PackedOrder.BGRA,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_8888, 32, 4),
    ARGB2101010 =
        SDL_DEFINE_PIXELFORMAT(SDL_PixelType.PACKED32, SDL_PackedOrder.ARGB,
                               SDL_PackedLayout.SDL_PACKEDLAYOUT_2101010, 32, 4),

    /* Aliases for RGBA byte arrays of color data, for the current platform */
    RGBA32,
    ARGB32,
    BGRA32,
    ABGR32,

    YV12 = SDL_DEFINE_PIXELFOURCC('Y', 'V', '1', '2'),
    IYUV = SDL_DEFINE_PIXELFOURCC('I', 'Y', 'U', 'V'),
    YUY2 = SDL_DEFINE_PIXELFOURCC('Y', 'U', 'Y', '2'),
    UYVY = SDL_DEFINE_PIXELFOURCC('U', 'Y', 'V', 'Y'),
    YVYU = SDL_DEFINE_PIXELFOURCC('Y', 'V', 'Y', 'U'),
    NV12 = SDL_DEFINE_PIXELFOURCC('N', 'V', '1', '2'),
    NV21 = SDL_DEFINE_PIXELFOURCC('N', 'V', '2', '1'),
    EXTERNAL_OES = SDL_DEFINE_PIXELFOURCC('O', 'E', 'S', ' ');

    static {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            RGBA32 = RGBA8888;
            ARGB32 = ARGB8888;
            BGRA32 = BGRA8888;
            ABGR32 = ABGR8888;
        } else {
            RGBA32 = ABGR8888;
            ARGB32 = BGRA8888;
            BGRA32 = ARGB8888;
            ABGR32 = RGBA8888;
        }
    }
}
