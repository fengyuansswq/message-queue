package com.sean.mq.utils;

import java.util.zip.CRC32;

/**
 * @author sean  ons文档上个copy来的
 * @version Id:,v0.1 2018/11/9 11:46 AM sean Exp $
 * @description
 */
public class HashUtil {
    public static long crc32Code(byte[] bytes) {
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return crc32.getValue();
    }
}
