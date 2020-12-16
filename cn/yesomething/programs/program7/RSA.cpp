#include <gmpxx.h>
#include <gmp.h>
#include <string>
#include <iostream>
#include <time.h>
using namespace std;

int MAX_PLAINTEXT_LENGTH = 1024; //最大明文长度
int KEY_LEN = 64;       //密钥长度
//生成随机数
gmp_randstate_t grt;
void RSADecryption(int groupNumber,mpz_t tempPlaintext,mpz_t tempCiphertext,mpz_t d,mpz_t n);

/**
 * @param number 需要生成的素数
 * 产生大素数
 */
void generatePrime(mpz_t number){
    mpz_init(number);
    mpz_urandomb(number,grt,KEY_LEN);
    mpz_nextprime(number,number);
}

/**
 * 初始化随机数
 */
void initRamdomConfig(){
    gmp_randinit_default(grt);
    gmp_randseed_ui(grt,time(NULL));
}

/**
 * @param e 产生的随机数e
 * @param eulerNumber 使用欧拉函数计算得的值
 * 产生随机数e
 */
void getRamdomE(mpz_t e,mpz_t eulerNumber){
    mpz_init(e);
    mpz_set_ui(e,65537);
    while(true){
        mpz_class gcd;
        mpz_gcd(gcd.get_mpz_t(),e,eulerNumber);
        if(gcd == 1){
            break;
        }
        mpz_add_ui(e,e,1);
    }
}

/**
 * @param plaintext 明文
 * @param n 大素数乘积e
 * @param e 公钥用到的e
 * @param d 私钥用到的d
 * RSA加密解密流程
 */
void RSA(unsigned int plaintext[],mpz_t n,mpz_t e,int length){
    mpz_t tempPlaintext,tempCiphertext;
    mpz_init(tempPlaintext);
    mpz_init(tempCiphertext);
    int blockLength,offset,groupNumber;
    blockLength = mpz_sizeinbase(n,16);
    groupNumber = length / blockLength + 1;
    cout << "加密后的密文为:" << endl;
    int bitNumber = 8;
    unsigned int moveBit = 256 * 256;
    for(int i = 0;i < groupNumber;i++){
        offset = i * ((blockLength - 1) / bitNumber);
        mpz_init(tempPlaintext);
        mpz_init(tempCiphertext);
        for(int j = 0;j < ((blockLength - 1) / bitNumber);j++){
            if((offset + j) == length / bitNumber){
                break;
            }
            //移动值然后将新的字符移入
            mpz_mul_ui(tempPlaintext,tempPlaintext,moveBit);
            mpz_mul_ui(tempPlaintext,tempPlaintext,moveBit);
            mpz_add_ui(tempPlaintext,tempPlaintext,plaintext[offset + j]);
        }
        // c = m^e mod n
        mpz_powm(tempCiphertext,tempPlaintext,e,n);       
        cout << "第" << i << "组:" << endl;
        gmp_printf("%Zx\n",tempCiphertext);
    }
}

/**
 * @param plaintext 明文
 * @param n 大素数乘积e
 * @param e 产生的随机数e
 */
void RSADecryption(mpz_t d,mpz_t n){
    mpz_t tempPlaintext;
    mpz_t tempCiphertext;
    int groupNumber = 2;
    //解密过程
    for(int i = 0;i < groupNumber;i++){
        mpz_init(tempPlaintext);
        mpz_init(tempCiphertext);
        cout << "请输入密文组" << i << ":" << endl;
        mpz_inp_str(tempCiphertext,NULL,16);
        mpz_powm(tempPlaintext,tempCiphertext,d,n);
        cout << "解密第" << i << "组密文得到的明文为:" << endl;
        gmp_printf("%Zx\n",tempPlaintext);
    }
}