package program2;

import java.util.Scanner;

//DES密码加密解密
public class DESCryptography {
    //IP置换矩阵
    private static int[] IP_TABLE;
    //扩展矩阵E盒
    private static int[] E_TABLE;
    //P盒
    private static int[] P_TABLE;
    //逆IP置换矩阵
    private static int[] IPR_TABLE;
    //密钥第一次置换矩阵
    private static int[] PC1_TABLE;
    // 密钥第二次置换矩阵
    private static int[] PC2_TABLE;
    //8个S盒   三维数组
    private static int[][][] S_BOX;
    //循环左移位数表
    private static int[] MOVE_TABLE;

    /**
     * 参数初始化
     */
    private static void initParams(){
        //IP置换矩阵
        IP_TABLE = new int[]{
                58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4,
                62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8,
                57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3,
                61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7};
        //扩展矩阵E盒
        E_TABLE = new int[]{
                32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9,
                8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17,
                16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25,
                24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1};
        //P盒
        P_TABLE = new int[]{
                16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10,
                2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4, 25};
        //逆IP置换矩阵
        IPR_TABLE = new int[]{
                40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31,
                38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29,
                36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27,
                34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25};
        //密钥第一次置换矩阵
        PC1_TABLE = new int[]{
                57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18,
                10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36,
                63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22,
                14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4};
        // 密钥第二次置换矩阵
        PC2_TABLE = new int[]{
                14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10,
                23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2,
                41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48,
                44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32};
        //8个S盒   三维数组
        S_BOX = new int[][][]{
                // S1
                {
                        {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
                        {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
                        {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
                        {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}
                },
                // S2
                {
                        {15,  1,  8, 14,  6, 11,  3,  4,  9,  7,  2, 13, 12,  0,  5, 10},
                        {3, 13,  4,  7, 15,  2,  8, 14, 12,  0,  1, 10,  6,  9, 11,  5},
                        {0, 14,  7, 11, 10,  4, 13,  1,  5,  8, 12,  6,  9,  3,  2, 15},
                        {13,  8, 10,  1,  3, 15,  4,  2, 11,  6,  7, 12,  0,  5, 14,  9}
                },
                // S3
                {
                        {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
                        {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
                        {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
                        {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}
                },
                // S4
                {
                        {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
                        {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
                        {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
                        {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}
                },
                // S5
                {
                        {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
                        {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
                        {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
                        {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}
                },
                // S6
                {
                        {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
                        {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
                        {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
                        {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}
                },
                // S7
                {
                        {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
                        {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
                        {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
                        {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}
                },
                // S8
                {
                        {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
                        {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
                        {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
                        {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}
                }
        };
        //循环左移位数表
        MOVE_TABLE = new int[]{1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
    }

    /**
     * 生成子密钥
     * @param orientKey 64位初始密钥
     * @return 16个48位子密钥
     */
    private static byte[][] generateKeys(byte[] orientKey) {
        byte[] C = new byte[28];
        byte[] D = new byte[28];
        byte[][] keys = new byte[16][48];
        //置换PC_1
        for (int i = 0; i < 28; i++) {
            C[i] = orientKey[PC1_TABLE[i] - 1];
            D[i] = orientKey[PC1_TABLE[i + 28] - 1];
        }
        for (int i = 0; i < 16; i++) {
            //循环左移
            C = ringShiftLeft(C, MOVE_TABLE[i]);
            D = ringShiftLeft(D, MOVE_TABLE[i]);
            //置换PC_2
            for (int j = 0; j < 48; j++) {
                if (PC2_TABLE[j] <= 28) {
                    keys[i][j] = C[PC2_TABLE[j] - 1];
                }
                else {
                    keys[i][j] = D[PC2_TABLE[j] - 28 - 1];
                }
            }
        }
        return keys;
    }

    /**
     * 循环左移n位
     * @param b 需要移位的数组
     * @param n 移位位数
     * @return 移位后的结果
     */
    static byte[] ringShiftLeft(byte[] b, int n) {
        String s = new String(b);
        s = (s + s.substring(0, n)).substring(n);
        return s.getBytes();
    }

    /**
     * 将长度为8的字符数组变为64为字节数组
     * @param charArray 需要转换的字符数组
     * @return 字节数组
     */
    private static byte[] calBytes(char[] charArray){
        char[] tempCharArray = charArray.clone();
        byte[] byteArray = new byte[64];
        int index = 0;
        //共8个字符,且每个字符需要分为8位,注意放入数组时需要倒序且下标-1
        for (int i = 0; i < tempCharArray.length; i++) {
            index = 0;
            while (tempCharArray[i] != 0 || index % 8 != 0){
                if(tempCharArray[i] % 2 == 0){
                    byteArray[7 + 8 * i - index++] = 0;
                }
                else {
                    byteArray[7 + 8 * i -index++] = 1;
                }
                tempCharArray[i] /= 2;
            }
        }
        return byteArray;
    }

    /**
     * IP函数
     * @param plaintextBytes  64位数据
     * @return IP函数后的结果
     */
    private static byte[] IP(byte[] plaintextBytes) {
        byte[] ipTextBytes = new byte[64];
        for (int i = 0; i < 64; i++)
            ipTextBytes[i] = plaintextBytes[IP_TABLE[i] - 1];
        return ipTextBytes;
    }

    /**
     * 逆IP函数
     * @param plaintextBytes  64位数据
     * @return IP函数后的结果
     */
    private static byte[] reverseIP(byte[] plaintextBytes) {
        byte[] ripTextBytes = new byte[64];
        for (int i = 0; i < 64; i++){
            ripTextBytes[i] = plaintextBytes[IPR_TABLE[i] - 1];
        }
        return ripTextBytes;
    }

    /**
     * 轮函数
     * @param ipTextBytes 经过IP函数的64位数组
     * @param key 48位子密钥
     * @return 32位数组
     */
    private static byte[] feistel(byte[] ipTextBytes,byte[] key){
        byte[] textBytesAfterE = new byte[48];
        byte[] resultAfterS = new byte[32];
        int resultAfterSIndex = 0;
        byte[] result = new byte[32];
        //扩展置换E函数
        for (int i = 0; i < 48; i++) {
            textBytesAfterE[i] = ipTextBytes[E_TABLE[i] - 1];
        }
        //E(R0)与K1异或
        for (int i = 0; i < 48; i++) {
            textBytesAfterE[i] = (byte) (textBytesAfterE[i] ^ key[i]);
        }
        //S盒代换
        for (int i = 0; i < 48; i += 6) {
            //行为首尾两个数组成的二进制
            int row = textBytesAfterE[i] * 2 + textBytesAfterE[i + 5];
            //列为中间4个数组成的二进制
            int column = textBytesAfterE[i + 1] * 8 + textBytesAfterE[i + 2] * 4 + textBytesAfterE[i + 3] * 2 + textBytesAfterE[i + 4];
            //从S盒读出的结果
            int sResult = S_BOX[i / 6][row][column];
            resultAfterSIndex = 0;
            for (int n = 0; n < 4; n++) {
                if(sResult % 2 == 0){
                    resultAfterS[3 + 4 * (i / 6) - resultAfterSIndex++] = 0;
                }
                else {
                    resultAfterS[3 + 4 * (i/6) - resultAfterSIndex++] = 1;
                }
                sResult /= 2;
            }
        }
        // P盒置换
        for (int i = 0; i < 32; i++){
            result[i] = resultAfterS[P_TABLE[i] - 1];
        }
        return result;
    }

    /**
     * 以16进制打印byte数组
     * @param bytes 需要打印的数组
     */
    private static void displayBytesInHex(byte[] bytes){
        for (int i = 0; i < bytes.length; i = i + 4) {
            int sum = 0;
            for(int j = 0; j < 4; j++){
                sum += bytes[i + j] * Math.pow(2,4 - j - 1);
            }
            System.out.printf("%x",sum);
        }
    }

    /**
     * 将64位的byte数组以8位一字符的格式打印
     * @param bytes 需要打印的数组
     */
    private static void displayBytesToString(byte[] bytes){
        for (int i = 0; i < bytes.length; i = i + 8) {
            int sum = 0;
            for(int j = 0; j < 8; j++){
                sum += bytes[i + j] * Math.pow(2,8 - j - 1);
            }
            System.out.printf("%c",sum);
        }
    }

    /**
     * 加密过程
     */
    private static byte[] encryption(){
        initParams();
        char[] plaintext = null;
        char[] orientKey = null;
        byte[] plaintextBytes;
        byte[] orientKeyBytes;
        //L与R的值
        byte[][] tempL = new byte[17][32];
        byte[][] tempR = new byte[17][32];
        byte[] tempFeistelResult;
        //子秘钥数组
        byte[][] keys;
        //加密后的结果
        byte[] encryptionResult = new byte[64];
        //输入明文
        System.out.println("请输入需要加密的明文:");
        Scanner sc = new Scanner(System.in);
        if(sc.hasNextLine()){
            plaintext = sc.nextLine().toCharArray();
        }
        //长度只能为8
        if(plaintext.length != 8){
            System.out.println("字符串长度只能为8");
            return null;
        }
        //输入密钥
        System.out.println("请输入所用的密钥:");
        if(sc.hasNextLine()){
            orientKey = sc.nextLine().toCharArray();
        }
        //长度只能为8
        if(orientKey.length != 8){
            System.out.println("密钥长度只能为8");
            return null;
        }
        //将输入的字符变为64位byte数组
        plaintextBytes = calBytes(plaintext);
        orientKeyBytes = calBytes(orientKey);
        //根据原密钥生成子密钥
        keys = generateKeys(orientKeyBytes);
        //IP置换
        plaintextBytes = IP(plaintextBytes);
        //拆分为L0、R0
        for(int i = 0; i < tempL[0].length; i++){
            tempL[0][i] = plaintextBytes[i];
            tempR[0][i] = plaintextBytes[i + 32];
        }
        //16次轮函数
        for(int i = 1; i < 17; i++) {
            //执行轮函数
            tempFeistelResult = feistel(tempR[i-1],keys[i-1]);
            //更新R、L
            for(int j = 0; j < tempL[i].length; j++){
                tempR[i][j] = (byte) ( tempFeistelResult[j] ^ tempL[i-1][j] );
            }
            for(int j = 0; j < tempL[i].length; j++){
                tempL[i] = tempR[i-1];
            }
            System.out.printf("L%2d:",i);
            displayBytesInHex(tempL[i]);
            System.out.printf("-------R%2d:",i);
            displayBytesInHex(tempR[i]);
            System.out.printf("\n");
        }
        //产生加密结果
        for (int i = 0; i < tempL[16].length; i++) {
            encryptionResult[i] = tempR[16][i];
            encryptionResult[i + 32] = tempL[16][i];
        }
        //逆IP置换
        encryptionResult = reverseIP(encryptionResult);
        System.out.println("加密结果为:");
        displayBytesInHex(encryptionResult);
        System.out.printf("\n");
        return encryptionResult;
    }

    /**
     * 解密过程
     */
    private static void decryption(byte[] ciphertextBytes,char[] orientKey){
        initParams();
        byte[] orientKeyBytes;
        //L与R的值
        byte[][] tempL = new byte[17][32];
        byte[][] tempR = new byte[17][32];
        byte[] tempFeistelResult;
        //子秘钥数组
        byte[][] keys;
        //加密后的结果
        byte[] decryptionResult = new byte[64];
        //将输入的字符变为64位byte数组
        orientKeyBytes = calBytes(orientKey);
        //根据原密钥生成子密钥
        keys = generateKeys(orientKeyBytes);
        //IP置换
        ciphertextBytes = IP(ciphertextBytes);
        //拆分为L0、R0
        for(int i = 0; i < tempL[0].length; i++){
            tempL[0][i] = ciphertextBytes[i];
            tempR[0][i] = ciphertextBytes[i + 32];
        }
        //16次轮函数
        for(int i = 1; i < 17; i++) {
            //执行轮函数
            tempFeistelResult = feistel(tempR[i-1],keys[16-i]);
            //更新R、L
            for(int j = 0; j < tempL[i].length; j++){
                tempR[i][j] = (byte) ( tempFeistelResult[j] ^ tempL[i-1][j] );
            }
            for(int j = 0; j < tempL[i].length; j++){
                tempL[i] = tempR[i-1];
            }
        }
        //产生解密结果
        for (int i = 0; i < tempL[16].length; i++) {
            decryptionResult[i] = tempR[16][i];
            decryptionResult[i + 32] = tempL[16][i];
        }
        //逆IP置换
        decryptionResult = reverseIP(decryptionResult);
        System.out.println("解密结果为:");
        displayBytesToString(decryptionResult);
        System.out.printf("\n");
    }

    /**
     * 加密解密过程
     */
    private static void encryptionAndDecryption(){
        byte[] ciphertextBytes = encryption();
        //加密失败
        if(ciphertextBytes == null){
            return;
        }
        //输入密钥
        char[] orientKey = new char[8];
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入解密所用的密钥:");
        if(sc.hasNextLine()){
            orientKey = sc.nextLine().toCharArray();
        }
        //密钥长度只能为8
        if(orientKey.length != 8){
            System.out.println("密钥长度只能为8");
            return ;
        }
        decryption(ciphertextBytes,orientKey);
    }

    public static void main(String[] args) {
        encryptionAndDecryption();
    }
}
