package cn.wekyjay.test.utils;

import java.io.*;

/**
 * @Description: 静态Buffer工具类
 * @Title: Buffer工具
 * @author WekyJay
 * @Github: <a href="https://github.com/WekyJay">https://github.com/WekyJay</a>
 * @Date: 2026/1/18 22:36
 */
public class BufferUtils {

    /**
     * 写入XML字节数组到文件
     * @param xmlBytes
     * @return
     * @throws IOException
     */
    public static BufferedOutputStream getBufferedOutputStream(byte[] xmlBytes) throws IOException {
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream("target/test.bpmn20.xml"));
        BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(xmlBytes));
        byte[] buffer = new byte[8096];
        while (true) {
            int count = in.read(buffer);
            if (count == -1) {
                break;
            }
            outputStream.write(buffer, 0, count);
        }

        // 刷新并关闭流
        outputStream.flush();
        return outputStream;
    }
}
