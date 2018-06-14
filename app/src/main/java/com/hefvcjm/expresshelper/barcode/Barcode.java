package com.hefvcjm.expresshelper.barcode;

/**
 * Created by win10 on 2018/5/3.
 */

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * 产生条形码
 */
public class Barcode {

    /**
     * 转换条形码
     *
     * @param str 条形码的字符串
     * @return
     * @throws WriterException
     */
    public static Bitmap BarcodeFormatCode(String str) {
        int width = 800;
        int height = 350;
        BarcodeFormat barcodeFormat = BarcodeFormat.EAN_13;
        BitMatrix matrix = null;
        try {
//            String s = getFullCode(str);
            if (str == null) {
                return null;
            }
            matrix = new MultiFormatWriter().encode(str, barcodeFormat, width, height, null);
            return bitMatrix2Bitmap(matrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap bitMatrix2Bitmap(BitMatrix matrix) {
        int w = matrix.getWidth();
        int h = matrix.getHeight();
        int[] rawData = new int[w * h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int color = Color.WHITE;
                if (matrix.get(i, j)) {
                    // 有内容的部分，颜色设置为黑色，可以自己修改成其他颜色
                    color = Color.BLACK;
                }
                rawData[i + (j * w)] = color;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(rawData, 0, w, 0, 0, w, h);
        return bitmap;
    }

    //输入前12位数字，产生最后一位数字
    private static String getFullCode(String str) {
        CharSequence s = str;
        int length = s.length();
        if (length < 12) {
            System.out.println("输入字符串少于12位!");
            return null;
        } else if (length > 12) {
            System.out.println("输入字符串超过12位!");
            return null;
        } else {
            int sum = 0;

            int i;
            int digit;
            for (i = length - 1; i >= 0; i -= 2) {
                digit = s.charAt(i) - 48;
                if (digit < 0 || digit > 9) {
                    System.out.println("输入包括非数字字符!");
                    return null;
                }

                sum += digit;
            }

            sum *= 3;

            for (i = length - 2; i >= 0; i -= 2) {
                digit = s.charAt(i) - 48;
                if (digit < 0 || digit > 9) {
                    System.out.println("输入包括非数字字符!");
                    return null;
                }

                sum += digit;
            }
            String result = str + (10 - sum % 10) % 10;
            System.out.println(result);
            return result;
        }
    }

}


