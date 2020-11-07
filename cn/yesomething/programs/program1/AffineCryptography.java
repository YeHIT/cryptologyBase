package program1;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Scanner;

//仿射密码加密解密及暴力破解
public class AffineCryptography {
    private static final int WORD_NUMBER = 26;
    //字母出现频率
    private static final double wordFrequency[] = {
                0.082,0.015,0.028,0.043,
                0.127,0.022,0.020,0.061,
                0.070,0.002,0.008,0.040,
                0.024,0.067,0.075,0.019,
                0.001,0.060,0.063,0.090,
                0.028,0.010,0.023,0.001,
                0.020,0.001};
    //keyA可选的值共12个数
    private static int[] TEMP_CHOICES = new int[]{1,3,5,7,9,11,15,17,19,21,23,25};

    /**
     * 求某个数的逆元
     * @param a 需要求逆元的数
     * @param m 需要模的值
     * @return 逆元
     * @exception RuntimeException 无法求逆元时提示错误信息
     */
    public static int getInverse(int a,int m){
        for(int i = 1; i < m ; i++){
            int result = a * i;
            if(result % m == 1){
                return i;
            }
        }
        throw new RuntimeException("无法求逆元,需要求逆元的数为" + a + "模值为" + m);
    }

    /**
     * 计算当前数组词频和已知词频的相似度
     * @param charArray 当前数组
     * @return 相似度
     */
    public static double calDistance(char[] charArray) {
        double[] tempFrequency = calFrequency(charArray);
        double sum = 0;
        for (int i = 0; i < 26; ++i) {
            sum += (tempFrequency[i] - wordFrequency[i]) * (tempFrequency[i] - wordFrequency[i]);
        }
        return sum;
    }

    /**
     * 计算数组内各字母的出现频次
     * @param charArray 输入的数字
     * @return 词频数组
     */
    public static double[] calFrequency(char[] charArray){
        double[] tempFrequency = new double[WORD_NUMBER];
        int totalNumber = 0;
        for (int i = 0; i < charArray.length; i++) {
            if(charArray[i] >= 'a' && charArray[i] <= 'z'){
                int index = charArray[i] - 'a';
                tempFrequency[index]++;
                totalNumber++;
            }
        }
        for (int i = 0; i < tempFrequency.length; i++) {
            tempFrequency[i] /= totalNumber;
        }
        return tempFrequency;
    }

    /**
     * 暴力破解密文
     */
    public static void bruteForce(){
        char[] plaintext = null;
        //存放解密后明文及同标准值的相似度
        ArrayList<Pair<char[],Double>> pairArrayList = new ArrayList<>();
        //输入密文
        System.out.println("请输入需要解密的密文:");
        Scanner sc = new Scanner(System.in);
        if(sc.hasNextLine()){
            plaintext = sc.nextLine().toCharArray();
        }
        char[] tempMessage = new char[plaintext.length];
        //遍历秘钥可能值
        for(int keyIndex = 0; keyIndex < TEMP_CHOICES.length; keyIndex++){
            for(int keyB = 0;keyB < WORD_NUMBER; keyB++){
                int inverseA = getInverse(TEMP_CHOICES[keyIndex],WORD_NUMBER);
                for (int k = 0; k < plaintext.length; k++) {
                    tempMessage[k] = decryption(plaintext[k],inverseA,keyB);
                }
                //计算同标准值的欧几里得距离即相似度
                double distance = calDistance(tempMessage);
                pairArrayList.add(new Pair<>(tempMessage,distance));
                tempMessage = new char[plaintext.length];
            }
        }
        //按相似度排序
        pairArrayList.sort((pairA,pairB) ->{
            return pairA.getValue().compareTo(new Double(pairB.getValue()));
        });
        //打印结果
        for (int i = 0; i < pairArrayList.size(); i++) {
            System.out.println(pairArrayList.get(i).getKey());
            System.out.println(pairArrayList.get(i).getValue());
        }
    }

    /**
     * 加密给定的单个字符
     * @param plainChar 需要加密的字符
     * @param keyA 秘钥A
     * @param keyB 秘钥B
     * @return 加密结果
     */
    public static char encryption(char plainChar,int keyA,int keyB){
        if(plainChar < 'a' || plainChar > 'z'){
            return plainChar;
        }
        int plainInt = ((plainChar - 'a') * keyA + keyB ) % WORD_NUMBER + 'a';
        return (char)plainInt;
    }

    /**
     * 解密给定的单个字符
     * @param plainChar 需要解密的字符
     * @param inverseA 秘钥A的逆元
     * @param keyB 秘钥B
     * @return 解密结果
     */
    public static char decryption(char plainChar,int inverseA,int keyB){
        if(plainChar < 'a' || plainChar > 'z'){
            return plainChar;
        }
        int plainInt = ((( plainChar - 'a') - keyB + WORD_NUMBER ) * inverseA ) % WORD_NUMBER + 'a';
        return (char)plainInt;
    }

    /**
     * 加密解密流程
     */
    public static void encryptionAndDecryption(){
        //随机生成的秘钥A、B
        int tempKeyA;
        int tempKeyB;
        char[] plaintext = null;
        int tempIndex = (int) (Math.random() * TEMP_CHOICES.length ) ;
        tempKeyA = TEMP_CHOICES[tempIndex];
        tempKeyB = (int) (Math.random() * WORD_NUMBER);
        System.out.println("加密密钥a:" + tempKeyA + "---" + "b:" + tempKeyB);
        //输入明文
        System.out.println("请输入需要加密的明文:");
        Scanner sc=new Scanner(System.in);
        if(sc.hasNextLine()){
            plaintext = sc.nextLine().toCharArray();
        }
        //加密
        for (int i = 0; i < plaintext.length; i++) {
            plaintext[i] = encryption(plaintext[i],tempKeyA,tempKeyB);
        }
        System.out.println("加密后结果为:");
        System.out.println(plaintext);
        //求A的逆元
        int inverseA = getInverse(tempKeyA,WORD_NUMBER);
        System.out.println("解密密钥a:" +inverseA + "----" + "b:" + tempKeyB);
        //解密
        for (int i = 0; i < plaintext.length; i++) {
            plaintext[i] = decryption(plaintext[i],inverseA,tempKeyB);
        }
        System.out.println("解密后结果为:");
        System.out.println(plaintext);
    }

    public static void main(String[] args) {
        //加密解密
        encryptionAndDecryption();
        //暴力破解
        bruteForce();
    }
}
