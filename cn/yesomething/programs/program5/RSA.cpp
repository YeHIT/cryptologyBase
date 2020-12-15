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
 * @param plaintext 明文数组
 * 获取输入
 */ 
void inputPlaintext(char plaintext[]){
    cout << "请输入要加密的明文:" << endl;
    cin.getline(plaintext,MAX_PLAINTEXT_LENGTH);
    cout << "输入的明文的ascii码为" << endl;
    for(int i = 0;i < strlen(plaintext); i++){
        printf("%x",plaintext[i]);
    }
    cout << endl;
}

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
void RSA(char plaintext[],mpz_t n,mpz_t e,mpz_t d){
    mpz_t tempPlaintext,tempCiphertext;
    mpz_init(tempPlaintext);
    mpz_init(tempCiphertext);
    int blockLength,offset,groupNumber;
    blockLength = ((mpz_sizeinbase(n,16) - 1) / 2) * 2;
    groupNumber = (strlen(plaintext) * 2) / blockLength + 1;
    cout << "加密后的密文为:" << endl;
    for(int i = 0;i < groupNumber;i++){
        offset = i * (blockLength / 2);
        mpz_init(tempPlaintext);
        mpz_init(tempCiphertext);
        for(int j = 0;j < (blockLength / 2);j++){
            if((offset + j) == strlen(plaintext)){
                break;
            }
            //移动值然后将新的字符移入
            mpz_mul_ui(tempPlaintext,tempPlaintext,256);
            mpz_add_ui(tempPlaintext,tempPlaintext,plaintext[offset + j]);
        }
        // c = m^e mod n
        mpz_powm(tempCiphertext,tempPlaintext,e,n);       
        cout << "第" << i << "组:" << endl;
        gmp_printf("%Zx\n",tempCiphertext);
    }
    RSADecryption(groupNumber,tempPlaintext,tempCiphertext,d,n);
}

/**
 * @param plaintext 明文
 * @param n 大素数乘积e
 * @param e 产生的随机数e
 */
void RSADecryption(int groupNumber,mpz_t tempPlaintext,mpz_t tempCiphertext,mpz_t d,mpz_t n){
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

int main(void){
    char plaintext[MAX_PLAINTEXT_LENGTH];
    //获取明文
    inputPlaintext(plaintext);
    //初始化随机数配置
    initRamdomConfig();
    //产生随机大素数
    mpz_t p,q;
    generatePrime(p);
    generatePrime(q);
    gmp_printf("p = %Zx ----- q = %Zx\n",p,q);
    //计算大素数乘积n
    mpz_t n;
    mpz_init(n);
    mpz_mul(n,p,q);
    gmp_printf("n = %Zx\n",n);
    //利用欧拉函数计算
    mpz_t eulerNumber;
    mpz_init(eulerNumber);
    mpz_sub_ui(p,p,1);
    mpz_sub_ui(q,q,1);
    mpz_mul(eulerNumber,p,q);
    gmp_printf("φ(n) = %Zx\n",eulerNumber);
    //选择随机E
    mpz_t e;
    getRamdomE(e,eulerNumber);
    gmp_printf("公钥(n,e) = (%Zx,%Zx)\n",n,e);
    //计算d
    mpz_t d;
    mpz_init(d);
    mpz_invert(d,e,eulerNumber);
    gmp_printf("私钥(d,n) = (%Zx,%Zx)\n",d,n);
    //加密解密过程
    RSA(plaintext,n,e,d);
    //清空内存
    mpz_clear(p);
    mpz_clear(q);
    mpz_clear(n);
    mpz_clear(eulerNumber);
    mpz_clear(e);
    return 0;
}