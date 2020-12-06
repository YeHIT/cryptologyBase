#include <stdio.h>
#include <stdlib.h>
#include "AESCryptography.c"
#define BLOCK_SIZE 16

void PadPlainText(char *p,int *plen){
    int pad;
    if(*plen == BLOCK_SIZE){
        pad = BLOCK_SIZE;
        for(int i = 0;i < BLOCK_SIZE;i++){
            p[i + (*plen)] = BLOCK_SIZE;
        }
        *plen += BLOCK_SIZE;
        p[*plen] = 0;
    }
    else{
        pad = BLOCK_SIZE - (*plen) % BLOCK_SIZE;
        for(int i = 0;i < pad;i++){
            p[i+(*plen)] = pad;
        }
        *plen += pad;
        p[*plen] = 0;
    }
    return;
}
void getKey(char k[17]){
    int klen;
    while(1){
        getString(k,17);
        klen = strlen(k);
        if(klen != 16){
            printf("请输入16个字符，当前长度为%d\n",klen);
        }
        else{
            printf("你的输入：%s\n",k);
            break;
        }
    }
    return;
}


int main(){
    //明文长度
    int plen;
    char p[MAXLEN],key[17],IV[17],tempText[17];
    int blockNum;
    //密文
    char ciphertext[MAXLEN];
    int clen,padNum,offset=0;
    printf("请输入你的明文\n");
    readPlainText(p,&plen);
    //pad后的长度    
    clen = plen;
    PadPlainText(p,&clen);
    printf("填充后的明文:\n");
    printASCCI(p,clen);
    printf("请输入密钥:\n");
    getKey(key);
    printf("请输入初始化向量:\n");
    getKey(IV);
    blockNum = clen / BLOCK_SIZE;
    printf("分组数量为:%d\n",blockNum);
    //异或
    for(int j = 0;j < 16;j++){
        tempText[j] = p[j] ^ IV[j];
    }
    tempText[16] = 0;
    //分组加密
    for(int i = 1;i <= blockNum;i++){
        printf("明文分组 %d..................\n",i);
        printASCCI(tempText,16);
        aes(tempText,16,key);
        offset = (i - 1) * BLOCK_SIZE;
        printf("密文分组 %d..................\n",i);
        printASCCI(tempText,16);
        for(int j = 0;j < 16;j++){
            ciphertext[j + offset] = tempText[j];
        }
        for(int j = 0;j < 16;j++){
            tempText[j] = p[j + offset + 16] ^ tempText[j];
        }
        tempText[16] = 0;
    }
    printf("得到的密文为..................\n");
    printASCCI(ciphertext,clen);
    printf("现在开始解密过程..................\n");
    //初始化明文
    for(int i = 0;i < clen;i++){
        p[i] = 0;
    }
    for(int i = 1;i < blockNum;i++){
        //偏移量
        offset = clen - i * 16;
        for(int j = 0;j < 16;j++){
            tempText[j] = ciphertext[j + offset];
        }
        printf("密文分组 %d..................\n",blockNum - i + 1);
        printASCCI(tempText,16);
        deAes(tempText,16,key);
        printf("明文分组 %d..................\n",blockNum - i + 1);
        printASCCI(tempText,16);
        for(int j = 0;j < 16;j++){
            p[j + offset] = tempText[j] ^ ciphertext[j + offset - 16];
        }
    }
    //获取填充数字
    padNum = p[clen - 1];
    plen = clen - padNum;
    for(int i = 0;i < 16;i++){
        tempText[i] = ciphertext[i];
    }
    printf("密文分组 1..................\n");
    printASCCI(tempText,16);
    deAes(tempText,16,key);
    printf("明文分组 1..................\n");
    printASCCI(tempText,16);
    for(int j = 0;j < 16;j++){
        p[j] = tempText[j] ^ IV[j];
    }
    printASCCI(p,plen);
    p[plen] = '\0';
    printf("解密的结果为：\n%s\n",p);
}
