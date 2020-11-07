package program1;

import java.util.Scanner;

//维吉尼亚密码加密解密
public class VigenereCryptology {
    public static final char[] KEY = "security".toCharArray();
    public static final int KEY_LENGTH = KEY.length;
    public static final int WORD_NUMBER = 26;

    /**
     * 解密给定的字符
     * @param plaintext 需要解密的字符数组
     * @return 解密结果
     */
    public static char[] decryption(char[] plaintext){
        for(int i = 0;i < plaintext.length; i++) {
            if(plaintext[i] >= 'a' && plaintext[i] <= 'z'){
                plaintext[i] = (char)(((plaintext[i] - 'a') - (KEY[i % KEY_LENGTH] - 'a') + WORD_NUMBER) % WORD_NUMBER + 'a');
            }
        }
        return plaintext;
    }

    /**
     * 加密给定的字符
     * @param plaintext 需要解密的字符数组
     * @return 加密结果
     */
    public static char[] encryption(char[] plaintext){
        for(int i = 0;i < plaintext.length; i++) {
            if(plaintext[i] >= 'a' && plaintext[i] <= 'z'){
                plaintext[i] = (char) (((plaintext[i] - 'a') + (KEY[i % KEY_LENGTH] - 'a')) % WORD_NUMBER + 'a');
            }
        }
        return plaintext;
    }

    /**
     * 加密解密过程
     */
    public static void encryptionAndDecryption(){
        System.out.println("加密密钥:");
        System.out.println(KEY);
        char[] plaintext = null;
        //输入明文
        System.out.println("请输入需要加密的明文:");
        Scanner sc = new Scanner(System.in);
        if(sc.hasNextLine()){
            plaintext = sc.nextLine().toCharArray();
        }
        //加密
        plaintext = encryption(plaintext);
        System.out.println("加密后结果为:");
        System.out.println(plaintext);
        //解密
        plaintext = decryption(plaintext);
        System.out.println("解密后结果为:");
        System.out.println(plaintext);
    }

    public static void main(String[] args) {
        encryptionAndDecryption();
    }
}
