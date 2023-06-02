package com.chromatic.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.Math.*;

/************************************************************************
 * @author: wg
 * @description:
 * @createTime: 11:13 2022/3/11
 * @updateTime: 11:13 2022/3/11
 ************************************************************************/
public class MathUtils {

    /**
     * 把角秒换算成弧度
     *
     * @param seconds 角秒
     * @return 对应的弧度值
     */
    public static double secondsToRadians(double seconds) {
        return toRadians(secondsToDegrees(seconds));
    }

    /**
     * 把角度限制在[0, 2π]之间
     *
     * @param r 原角度(rad)
     * @return 转换后的角度(rad)
     */
    public static double mod2Pi(double r) {
        while (r < 0) {
            r += PI * 2;
        }
        while (r > 2 * PI) {
            r -= PI * 2;
        }
        return r;
    }

    /**
     * 把角度限制在[-π, π]之间
     *
     * @param r 原角度(rad)
     * @return 转换后的角度(rad)
     */
    public static double modPi(double r) {
        while (r < -PI) {
            r += PI * 2;
        }
        while (r > PI) {
            r -= PI * 2;
        }
        return r;
    }

    /**
     * 把角秒换算成角度
     *
     * @param seconds 角秒
     * @return 对应的弧度值
     */
    public static double secondsToDegrees(double seconds) {
        return seconds / 3600;
    }

    /**
     * 把度分秒表示的角度换算成度(deg)
     *
     * @param d 度
     * @param m 分
     * @param s 秒
     * @return 换算成度的值
     */
    public static double dmsToDegrees(int d, int m, double s) {
        return d + m / 60.0 + s / 3600;
    }

    /**
     * 把度分秒表示的角度换算成秒(arcsecond)
     *
     * @param d 度
     * @param m 分
     * @param s 秒
     * @return 换算成秒的值
     */
    public static double dmsToSeconds(int d, int m, double s) {
        return d * 3600 + m * 60 + s;
    }

    /**
     * 把度分秒表示的角度换算成弧度(rad)
     *
     * @param d 度
     * @param m 分
     * @param s 秒
     * @return 换算成弧度的值
     */
    public static double dmsToRadians(int d, int m, double s) {
        return toRadians(dmsToDegrees(d, m, s));
    }

    /**
     * 牛顿迭代求解方程的根
     *
     * @param f  方程表达式
     * @param x0 对根的估值
     * @return 在x0附近的一个根
     */
    public static double newtonIteration(Function f, double x0) {
        final double EPSILON = 1e-7;
        final double DELTA = 5e-6;
        double x;
        do {
            x = x0;
            double fx = f.f(x);
            double fpx = (f.f(x + DELTA) - f.f(x - DELTA)) / DELTA / 2;
            x0 = x - fx / fpx;
        } while (abs(x0 - x) > EPSILON);
        return x;
    }

    /************************************************************************
     * @description: 判断是否是数字, 科学计数法的也判断
     * @author: wg
     * @date: 15:50  2021/12/14
     * @params:
     * @return:
     ************************************************************************/
    public static boolean isNumber(String val) {

        if (null == val || "".equals(val)) {
            return false;
        }

        String rex = "^[+-]?\\d*\\.?\\d*$";
        boolean numbMatch = Pattern.matches(rex, val);
        if (numbMatch) {
            return numbMatch;
        }

        // 科学计数法验证
        rex = "^[+-]?\\d+\\.?\\d*[Ee]*[+-]*\\d+$";
        boolean compile = Pattern.matches(rex, val);
        if (compile) {
            return compile;
        }
        return false;
    }

    /************************************************************************
     * @description: 是否是整数
     * "3." 也是整数
     * @author: wg
     * @date: 17:02  2021/12/14
     * @params:
     * @return:
     ************************************************************************/
    public static boolean isInteger(String val) {
        if (null == val || "".equals(val)) {
            return false;
        }

        String rex = "^[+-]?\\d*\\.?0*$";
        boolean numbMatch = Pattern.matches(rex, val);
        if (numbMatch) {
            return numbMatch;
        }

        // 科学计数法验证
        rex = "^[+-]?\\d*[Ee]*[+-]*\\d+$";
        boolean science = Pattern.matches(rex, val);
        if (science) {
            return science;
        }

        return false;
    }

    /************************************************************************
     * @description: byte -> bit (-128-127)
     * 字节 转 比特
     * 数组长度值为8，每个值代表bit，即8个bit。bit7 -> bit0
     * bit数组，bit7 -> bit0
     * @author: wg
     * @date: 15:38  2021/12/20
     * @params:
     * @return:
     ************************************************************************/
    public static byte[] byteToBit(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    /************************************************************************
     * @author: wg
     * @description: char -> int
     * @params:
     * @return:
     * @createTime: 10:37  2022/3/4
     * @updateTime: 10:37  2022/3/4
     ************************************************************************/
    public static int byteToInt(byte ch) {
        int val = 0;
        if (ch >= 0x30 && ch <= 0x39) {
            val = ch - 0x30;
        } else if (ch >= 0x41 && ch <= 0x46) {
            val = ch - 0x41 + 10;
        }
        return val;
    }

    /************************************************************************
     * @description: 字符串 转 bit
     * @author: wg
     * @date: 16:04  2021/12/20
     * @params:
     * @return:
     ************************************************************************/
    public static byte[][] stringToBit(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        byte[][] bits = new byte[bytes.length][8];
        for (int i = 0; i < bytes.length; i++) {
            byte[] bit = byteToBit(bytes[i]);
            bits[i] = bit;
        }
        return bits;
    }

    /************************************************************************
     * @author: wg
     * @description: 字节转16进制字符串
     * @params:
     * @return:
     * @createTime: 10:06  2022/3/3
     * @updateTime: 10:06  2022/3/3
     ************************************************************************/
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }

    /************************************************************************
     * @author: wg
     * @description: 2的n次方
     * @params:
     * @return:
     * @createTime: 15:52  2022/4/8
     * @updateTime: 15:52  2022/4/8
     ************************************************************************/
    public static String pow2(int n) {
        StringBuilder res = new StringBuilder("1");
        // 重复N次
        for (int i = 0; i < n; i++) {
            // 进位标志，每轮清零
            int temp = 0;
            // result中的字符，从前往后逐位*2
            for (int j = res.length() - 1; j >= 0; j--) {
                // 乘法运算,需要加上进位
                temp = ((res.charAt(j) - '0') << 1) + temp / 10;
                // 替换此位结果
                res.setCharAt(j, (char) (temp % 10 + '0'));
            }
            // 产生进位则需添加新的数字
            if (temp / 10 >= 1) {
                res.insert(0, '1');
            }
        }

        return res.toString();
    }

    /************************************************************************
     * @author: wg
     * @description: a 的 b 次方
     * @params:
     * @return:
     * @createTime: 16:54  2022/5/17
     * @updateTime: 16:54  2022/5/17
     ************************************************************************/
    public static long pow(int a, int b) {
        long p = 1;
        for (int i = 1; i <= b; i++) {
            p *= a;
        }
        return p;
    }

    /************************************************************************
     * @author: wg
     * @description: 求对数
     * @params:
     * @return:
     * @createTime: 16:32  2022/4/8
     * @updateTime: 16:32  2022/4/8
     ************************************************************************/
    public static List<Integer> log2(Long val) {
        List<Integer> list = new ArrayList<>();
        int ind = 0;
        do {
            if ((val & 1) == 1) {
                list.add(ind);
            }
            ind++;
            val = val >> 1;
        } while (val > 0);
        return list;
    }

    /************************************************************************
     * @description: 转成科学计数法
     * @author: wg
     * @date: 16:04  2021/11/11
     * @params:
     * @return:
     ************************************************************************/
    public static String double2ScientificNotation(double num) {
        if (isInteger(String.valueOf(num))) {
            if (num > -9999 || num <= 9999) {
                return String.valueOf(((int) num));
            }
        }
        if (num < 0.001 || num > 1000) {
            String str = String.format("%E", num);//获取直接格式化结果
            str = str.replace("E-0", "E-");//将E-0N处理为E-N
            // 处理结果
            String temp = str.substring(0, str.indexOf("E"));
            // 精确到小数点后3位
            String f = String.format("%.3f", Double.parseDouble(temp));
            str = f + str.substring(str.indexOf("E"));
            return str;
        } else {
            return String.valueOf(new BigDecimal(String.valueOf(num)).setScale(3, RoundingMode.HALF_UP).doubleValue());
        }
    }
}
