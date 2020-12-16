#include "RSA.cpp"
#include "SHA1.c"

/**
 * 获取文章摘要
 */ 
int getFileAbstract(){
    unsigned char buf[64];      /*the input block of SHA-1*/
    FILE * file;
    int len;        /*used to save the length of one read from the file (byte)*/
    unsigned int floop1,floop2;     /*use floop1:floop2 to represent the length of the file in bit*/
    floop1 = floop2 = 0;        /*at 1st make them equal 0*/
    printf("计算出的文件摘要如下:\n");
    /*********************************Init the global var*/
    Init();

    /*********************************can not open file error*/
    if (!(file = fopen("test.txt", "rb")))
    {
        printf("can not open file!!!\n");
        return -1;
    }

    /*********************************read data from the file*/
    while(!feof(file))
    {
        /*********************************each time read 64 bits into buf at most*/
        len=fread(buf,1,64,file);/*len used to save the length of each read*/

        /*********************************read file error*/
        if(ferror(file))
        {
            printf("read file error!!!\n");
            return -1;
        }


        /*********************************the buf[64] is full*/
        if(len == 64)
        {
            /*use 2 unsigned int to represent 1 64bit number, floop1:floop2*/
            if((floop1 == 0xffffffff) && (floop2 == 0xfffffe00))
            {
                printf("file larger than 2exp(64)");
                return -1;
            }
            if(floop2 != 0xfffffe00)floop2+=512;
            else
            {
                floop1++;
                floop2 = 0;
            }

        /*call the function "shaTran" to do the loop*/
        shaTran(buf,state);
        }

        /*********************************less than 512 bits need to pad*/
        else
        {
            /*call the function "sha" to do the padding and other compute*/
            sha(buf,len,state,floop1,floop2);
        }
    }


    /*********************************print the digest*/
    printf("%08x%08x%08x%08x%08x\n",state[0],state[1],state[2],state[3],state[4]);
}

int main(void){
    getFileAbstract();
    //SHA1完成先产生公钥私钥
    printf("\n生成公钥私钥---------------\n");
    unsigned int plaintext[MAX_PLAINTEXT_LENGTH];
    //明文长度
    int length = 40;
    for(int i = 0; i < 5; i++){
        plaintext[i] = state[i];
    }
    //初始化随机数配置
    initRamdomConfig();
    //产生随机大素数
    mpz_t p,q;
    generatePrime(p);
    generatePrime(q);
    //计算大素数乘积n
    mpz_t n;
    mpz_init(n);
    mpz_mul(n,p,q);
    //利用欧拉函数计算
    mpz_t eulerNumber;
    mpz_init(eulerNumber);
    mpz_sub_ui(p,p,1);
    mpz_sub_ui(q,q,1);
    mpz_mul(eulerNumber,p,q);
    //选择随机E
    mpz_t e;
    getRamdomE(e,eulerNumber);
    gmp_printf("公钥(e,n) = (%Zx,%Zx)\n",e,n);
    //计算d
    mpz_t d;
    mpz_init(d);
    mpz_invert(d,e,eulerNumber);
    gmp_printf("私钥(d,n) = (%Zx,%Zx)\n",d,n);
    //签名
    printf("\n输入私钥开始签名---------------\n");
    cout << "请输入私钥d:" << endl;
    mpz_init(d);
    mpz_inp_str(d,NULL,16);
    cout << "请输入公钥n:" << endl;
    mpz_init(n);
    mpz_inp_str(n,NULL,16);
    RSA(plaintext,n,d,length);
    //验签
    printf("\n输入公钥开始验签---------------\n");
    cout << "请输入公钥e:" << endl;
    mpz_init(e);
    mpz_inp_str(e,NULL,16);
    cout << "请输入公钥n:" << endl;
    mpz_init(n);
    mpz_inp_str(n,NULL,16);
    RSADecryption(e,n);
    getFileAbstract();
    //清空内存
    mpz_clear(p);
    mpz_clear(q);
    mpz_clear(n);
    mpz_clear(eulerNumber);
    mpz_clear(e);
    return 0;
}