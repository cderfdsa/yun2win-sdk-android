package com.y2w.uikit.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import com.y2w.uikit.common.MD5;
import com.y2w.uikit.utils.pinyinutils.CharacterParser;
import com.y2w.uikit.utils.pinyinutils.HanziToPinyin;

/**
 * Created by maa2 on 2016/1/22.
 */
public class StringUtil {

    public final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater3 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater4 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MM-dd");
        }
    };

    /**
     * 判断字符串是否为空或null
     * @param input
     * @return
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input)
                || "null".equals(input.toLowerCase()))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }
    /**
     * 汉字转拼音,首字母
     * @param source
     * @return
     */
    public static String getFirstPinYin(String source){
        if (!Arrays.asList(Collator.getAvailableLocales()).contains(Locale.CHINA)) {
            return source;
        }
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(source);
        if (tokens == null || tokens.size() == 0) {
            return source;
        }
        StringBuffer result = new StringBuffer();
        for (HanziToPinyin.Token token : tokens) {
            if (token.type == HanziToPinyin.Token.PINYIN) {
                result.append(token.target.charAt(0));
            } else {
                result.append("#");
            }
        }
        return result.toString();

    }

    /**
     * 获取拼音第一个字母
     * @param 
     * @return
     */
    public static String getPinYinSortLetters(CharacterParser characterParser,String name) {
        String py = "#";
        try {
            // 汉字转换成拼音
            if (StringUtil.isEmpty(name)) {
                py="#";
            } else {
                String pinyin = characterParser.getSelling(name);
                String sortString = pinyin.substring(0, 1).toUpperCase();

                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    py=sortString;
                } else {
                    py="#";
                }
            }
        } catch (Exception ex) {
             py = "#";
        }
        return py;
    }

    /**
     * MD5加密
     * @param s
     * @return
     */
    public static String get32MD5(String s) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            byte[] strTemp = s.getBytes();
            // 使用MD5创建MessageDigest对象
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte b = md[i];
                str[k++] = hexDigits[b >> 4 & 0xf];
                str[k++] = hexDigits[b & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取随机字符串 指定某个位置是a-z、A-Z或是0-9
     * @param length 字符串长度
     * @return
     */
    public static String getRandomString(int length){
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(3);
            long result=0;
            switch(number){
                case 0:
                    result=Math.round(Math.random()*25+65);
                    sb.append(String.valueOf((char)result));
                    break;
                case 1:
                    result=Math.round(Math.random()*25+97);
                    sb.append(String.valueOf((char)result));
                    break;
                case 2:
                    sb.append(String.valueOf(new Random().nextInt(10)));
                    break;
            }

        }
        return sb.toString();
    }

    /**
     * 友好显示时间
     * @param sDate 时间字符串 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getFriendlyTime(String sDate) {
        if(sDate != null && sDate.contains("T")){
            sDate = sDate.replace("T"," ");
            sDate = sDate.replace(".000Z","");
        }
        Date time = toDate(sDate);
        if (time == null) {
            return "";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天 " + dateFormater3.get().format(time);
        }else if (curDate.substring(0, 4).equals(paramDate.substring(0, 4))) {
            ftime = dateFormater4.get().format(time);
        } else {
            ftime = dateFormater2.get().format(time);
        }

        return ftime;
    }

    /**
     * 讲时间字符串转化可操作的时间字符串 yyyy-MM-dd HH:mm:ss
     * @param sDate
     * @return
     */
    public static String getOPerTime(String sDate) {
        if(StringUtil.isEmpty(sDate)){
            return "";
        }
        if(sDate.length()==19){
            return sDate;
        }
        sDate = sDate.replace("Z", " UTC");//注意是空格+UTC
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");//注意格式化的表达式
        SimpleDateFormat target = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//目标格式
        try {
            Date date = format.parse(sDate);
            sDate = target.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sDate;
    }

    /**
     * 将字符串转位日期类型
     *
     * @param sDate
     * @return
     */
    public static Date toDate(String sDate) {
        try {
            return dateFormater.get().parse(sDate);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 聊天记录时间戳显示
     * 每一条消息与上一条消息比较时间，超过2分钟，即可显示时间
     * @param str1 上一条消息时间
     * @param str2 当前显示消息时间
     * @return
     */

    public static boolean isTimeDisplay(String str1,String str2){
        try {
            SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date begin = dfs.parse(str1);
            java.util.Date end = dfs.parse(str2);
            long between = (end.getTime() - begin.getTime()) / 1000;//除以1000是为了转换成秒

            long day = between / (24 * 3600);
            long hour = between % (24 * 3600) / 3600;
            long minute = between % 3600 / 60;
            long second = between % 60 / 60;
            if(day > 1 || hour > 1 || minute >= 2){
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    /**
     * 时间字符串大小比较，str2 > str1，返回1,str2 < str1 返回-1，str2 = str1 返回0
     * @param str1
     * @param str2
     * @return
     */
    public static int timeCompare(String str1,String str2){
        try {
            SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date begin = dfs.parse(str1);
            java.util.Date end = dfs.parse(str2);
            long between = (end.getTime() - begin.getTime()) / 1000;//除以1000是为了转换成秒

            long day = between / (24 * 3600);
            long hour = between % (24 * 3600) / 3600;
            long minute = between % 3600 / 60;
            long second = between % 60;
            if(day > 1 || hour > 1 || minute >= 1 || second >= 1){
                return 1;
            }else if(day == 0 && hour == 0 && minute == 0 && second == 0){
                return 0;
            }else{
                return -1;
            }
        }catch (Exception e){
            return 1;
        }
    }

    /**
     * 获取单个文件的MD5值！
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }
    /**
     * 获取尾字符的ASCII值
     * @param str
     * @return
     */
    public static int parseAscii(String str){

        char[] cs=str.toCharArray();
        if(cs.length==0){
            return  0;
        }
        return cs[cs.length-1];
    }


    public static String getTimeStamp(String str){
        long stamp = 0;
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dfs.parse(str);
            stamp = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stamp+"";
    }
    public static String getPercentString(float percent) {
        return String.format(Locale.US, "%d%%", (int) (percent * 100));
    }
    /**
     * 删除字符串中的空白符
     *
     * @param content
     * @return String
     */
    public static String removeBlanks(String content) {
        if (content == null) {
            return null;
        }
        StringBuilder buff = new StringBuilder();
        buff.append(content);
        for (int i = buff.length() - 1; i >= 0; i--) {
            if (' ' == buff.charAt(i) || ('\n' == buff.charAt(i)) || ('\t' == buff.charAt(i))
                    || ('\r' == buff.charAt(i))) {
                buff.deleteCharAt(i);
            }
        }
        return buff.toString();
    }
    /**
     * 获取32位uuid
     *
     * @return
     */
    public static String get32UUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 生成唯一号
     *
     * @return
     */
    public static String get36UUID() {
        UUID uuid = UUID.randomUUID();
        String uniqueId = uuid.toString();
        return uniqueId;
    }

    public static String makeMd5(String source) {
        return MD5.getStringMD5(source);
    }

    public static final String filterUCS4(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }

        if (str.codePointCount(0, str.length()) == str.length()) {
            return str;
        }

        StringBuilder sb = new StringBuilder();

        int index = 0;
        while (index < str.length()) {
            int codePoint = str.codePointAt(index);
            index += Character.charCount(codePoint);
            if (Character.isSupplementaryCodePoint(codePoint)) {
                continue;
            }

            sb.appendCodePoint(codePoint);
        }

        return sb.toString();
    }

    /**
     * counter ASCII character as one, otherwise two
     *
     * @param str
     * @return count
     */
    public static int counterChars(String str) {
        // return
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            int tmp = (int) str.charAt(i);
            if (tmp > 0 && tmp < 127) {
                count += 1;
            } else {
                count += 2;
            }
        }
        return count;
    }
    /*
    **判断数字是否大于99
     */
    public static String getMessageNum(int num){
        if(num>99)
            num =99;
        return num+"";
    }

    /**
     * 获取手机当前应用包名
     * @param context
     * @return
     */
    public static String getTopPackageName(Context context) {
        String topPackageName = null;
        try {
            ActivityManager activityManager = (ActivityManager) (context
                    .getSystemService(android.content.Context.ACTIVITY_SERVICE));
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager
                    .getRunningTasks(1);
            if (runningTaskInfos != null) {
                ComponentName f = runningTaskInfos.get(0).topActivity;
                topPackageName = f.getPackageName();
            }
        } catch (Exception e) {
        }
        return topPackageName;
    }

    /**
     * 获取手机当前时间，格式"yyyy-MM-dd hh:mm:ss"
     * @return
     */
    public static String getNowTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    /**
     * 是否是Apk文件(Apk)
     *
     * @param suffixName
     *            后缀
     * @return
     */
    public static boolean isApkFileWithSuffixName(String suffixName) {
        if (isEmpty(suffixName)) {
            return false;
        }
        suffixName = suffixName.toLowerCase();
        if ("apk".equals(suffixName)) {
            return true;
        }
        return false;
    }
    /**
     * 是否是Txt文件(Txt)
     *
     * @param suffixName
     *            后缀
     * @return
     */
    public static boolean isTxtFileWithSuffixName(String suffixName) {
        if (isEmpty(suffixName)) {
            return false;
        }
        suffixName = suffixName.toLowerCase();
        if ("txt".equals(suffixName)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是Pdf文件(Pdf)
     *
     * @param suffixName
     *            后缀
     * @return
     */
    public static boolean isPdfFileWithSuffixName(String suffixName) {
        if (isEmpty(suffixName)) {
            return false;
        }
        suffixName = suffixName.toLowerCase();
        if ("pdf".equals(suffixName)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是PPT文件(ppt,pptx,pps,dps)
     *
     * @param suffixName
     *            后缀
     * @return
     */
    public static boolean isPPTFileWithSuffixName(String suffixName) {
        if (isEmpty(suffixName)) {
            return false;
        }
        suffixName = suffixName.toLowerCase();
        if ("ppt".equals(suffixName) || "pptx".equals(suffixName)
                || "pps".equals(suffixName) || "dps".equals(suffixName)) {
            return true;
        }
        return false;
    }
    /**
     * 是否是xls文件(xls,xlsx)
     *
     * @param suffixName
     *            后缀
     * @return
     */
    public static boolean isXlsFileWithSuffixName(String suffixName) {
        if (isEmpty(suffixName)) {
            return false;
        }
        suffixName = suffixName.toLowerCase();
        if ("xls".equals(suffixName) || "xlsx".equals(suffixName)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是doc文件(doc,docx,wps)
     * @param suffixName
     *            后缀
     * @return
     */
    public static boolean isDocFileWithSuffixName(String suffixName) {
        if (isEmpty(suffixName)) {
            return false;
        }
        suffixName = suffixName.toLowerCase();
        if ("doc".equals(suffixName) || "docx".equals(suffixName)
                || "wps".equals(suffixName)) {
            return true;
        }
        return false;
    }
    /**
     * 是否是压缩文件(zip,rar,7z)
     * @param suffixName
     *            后缀
     * @return
     */
    public static boolean isZIPFileWithSuffixName(String suffixName) {
        if (isEmpty(suffixName)) {
            return false;
        }
        suffixName = suffixName.toLowerCase();
        if ("zip".equals(suffixName) || "rar".equals(suffixName)
                || "7z".equals(suffixName)) {
            return true;
        }
        return false;
    }
      /**
     * 是否是图片(jpg,jpeg,png,bmp,gif)
     * @param suffixName
     *            后缀
     * @return
     */
    public static boolean isImageWithSuffixName(String suffixName) {
        if (isEmpty(suffixName)) {
            return false;
        }
        suffixName = suffixName.toLowerCase();
        if ("jpg".equals(suffixName) || "jpeg".equals(suffixName)
                || "png".equals(suffixName) || "bmp".equals(suffixName)
                || "gif".equals(suffixName) || "tif".equals(suffixName)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是音频(audio,wma,wav,ogg,mp3,amr,aac)
     * @param suffixName
     *            后缀
     * @return
     */
    public static boolean isAudioWithSuffixName(String suffixName) {
        if (isEmpty(suffixName)) {
            return false;
        }
        suffixName = suffixName.toLowerCase();
        if ("audio".equals(suffixName) || "mp3".equals(suffixName)
                || "wma".equals(suffixName) || "wav".equals(suffixName)
                || "ogg".equals(suffixName) || "m4a".equals(suffixName)
                || "mid".equals(suffixName) || "xmf".equals(suffixName)
                || "amr".equals(suffixName)|| "aac".equals(suffixName)) {
            return true;
        }
        return false;
    }
    /**
     * 是否是视频(video,wmv,3gp,mp4,rmvb,avi)
     * @param suffixName
     *            后缀
     * @return
     */
    public static boolean isVideoWithSuffixName(String suffixName) {
        if (isEmpty(suffixName)) {
            return false;
        }
        suffixName = suffixName.toLowerCase();
        if ("video".equals(suffixName) || "wmv".equals(suffixName)
                || "3gp".equals(suffixName) || "mp4".equals(suffixName)
                || "rmvb".equals(suffixName) || "avi".equals(suffixName)) {
            return true;
        }
        return false;
    }

    /**
     * 根据文件名获取文件类型
     *
     * @param fileName
     * @return
     */
    public static String getFileExtensionName(String fileName) {
        String extensionName = "";
        if (!isEmpty(fileName)
                && fileName.lastIndexOf(".") >= 0) {
            extensionName = fileName.substring(fileName.lastIndexOf(".") + 1)
                    .toLowerCase();
        }
        return extensionName;
    }

    /**
     * 获取文件大小友好显示
     * @param fileSize
     * @return
     */
    public static String getFriendlyFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public static String getTopActivityName(Context context) {
        if(context == null){
            return "";
        }
        String topActivityClassName = null;
        try {
            ActivityManager activityManager = (ActivityManager) (context
                    .getSystemService(android.content.Context.ACTIVITY_SERVICE));
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager
                    .getRunningTasks(1);
            if (runningTaskInfos != null) {
                ComponentName f = runningTaskInfos.get(0).topActivity;
                topActivityClassName = f.getClassName();
            }
        } catch (Exception e) {
        }
        return topActivityClassName;

    }

}
