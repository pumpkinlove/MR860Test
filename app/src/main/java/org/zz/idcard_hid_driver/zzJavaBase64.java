package org.zz.idcard_hid_driver;

public class zzJavaBase64 
{
	/*
	********************************************************************************
	* 函  数: unsigned int Base64Encode(unsigned char *pInput,unsigned int inputLen,unsigned char *pOutput,unsigned int outputbufsize)
	* 功  能: 对数据块进行base64编码
	* 输  入: 
	*  pInput - 输入，编码前数据块
	*  inputLen -  输入，输入数据块（pInput）长度
	*  pOutput - 输出，base64编码后数据块，大小为输入数据的4/3倍，
	*      输出数据块pInput 和输入数据块pOutput 起始地址可以相同
	*  outputbufsize- 输入,存放编码后数据（pOutput）的缓冲区大小
	* 返  回: 
	*     0:用于存放编码后数据的缓冲区不够，编码失败。
	*     大于0：编码后数据长度，值为(inputLen+2)/3*4
	********************************************************************************
	*/
	static int JavaBase64Encode(byte[] pInput, int inputLen, byte[] pOutput,int outputbufsize) 
	{
		int currentin = 0;
		int currentin2 = 0;
		int currentin3 = 0;
		String codebuffer = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
		byte[] encodingTable = new byte[65];//		
		encodingTable = codebuffer.getBytes();
		int outlen = (inputLen + 2) / 3 * 4; /* 编码后数据是4的整数倍 */
		int modulus = inputLen % 3;
		int datalen = inputLen - modulus;
		int encodedatalen = datalen * 4 / 3;
		int i, j;
		long ltmp;

		/* 缓冲区大小校验 */
		if (outputbufsize < outlen)
			return 0;
		
		switch (modulus) {
		case 0: /* nothing left to do */
			break;
		case 1:
			i = inputLen - 1;
			j = outlen - 4;
			currentin = pInput[i];
			if(currentin<0)
			{
				currentin = currentin+256;
			}
			ltmp = (long) currentin << 16; /* 剩余1个8位字节 */
			pOutput[j] = encodingTable[(int) ((ltmp >> 18) & 0x3f)];
			pOutput[j + 1] = encodingTable[(int) ((ltmp >> 12) & 0x3f)];
			pOutput[j + 2] = '=';
			pOutput[j + 3] = '=';
			break;
		case 2:
			i = inputLen - 2;
			j = outlen - 4;
			currentin = pInput[i];
			currentin2 = pInput[i + 1] ;			
			if(currentin<0)
			{
				currentin = currentin+256;
			}
			if(currentin2 <0)
			{
				currentin2 = currentin2+256;
			}
			ltmp = ((long) pInput[i] << 16) | ((long) currentin2 << 8); /* 剩余2个8位字节 */
			pOutput[j] = encodingTable[(int) ((ltmp >> 18) & 0x3f)];
			pOutput[j + 1] = encodingTable[(int) ((ltmp >> 12) & 0x3f)];
			pOutput[j + 2] = encodingTable[(int) ((ltmp >> 6) & 0x3f)];
			pOutput[j + 3] = '=';
			break;
		}

		for (i = datalen - 3, j = encodedatalen - 4; i >= 0; i -= 3, j -= 4) 
		{
			currentin = pInput[i];
			currentin2 = pInput[i + 1] ;
			currentin3 = pInput[i + 2];
			if(currentin<0)
			{
				currentin = currentin+256;
			}
			if(currentin2 <0)
			{
				currentin2 = currentin2+256;
			}
			if(currentin3<0)
			{
				currentin3 = currentin3+256;
			}
			/* 将3个8位字节转化为4个6位字节，并映射为可见字符 */
			ltmp = ((long) currentin << 16) | ((long) currentin2 << 8)
					| (long) currentin3;
			pOutput[j] = encodingTable[(int) ((ltmp >> 18) & 0x3f)];
			pOutput[j + 1] = encodingTable[(int) ((ltmp >> 12) & 0x3f)];
			pOutput[j + 2] = encodingTable[(int) ((ltmp >> 6) & 0x3f)];
			pOutput[j + 3] = encodingTable[(int) (ltmp & 0x3f)];
		}

		return outlen;
	}
	
	/*
	********************************************************************************
	* 函  数: unsigned int Base64Decode(unsigned char *pInput,unsigned int inputLen,unsigned char *pOutput)
	* 功  能: 对输入的base64编码数据块进行base64解码
	* 输  入: 
	*  pInput - 输入，base64编码数据块
	*  inputLen -  输入，base64编码数据块长度
	*  pOutput - 输出，base64解码后的数据块
	*      输出数据块pInput和输入数据块pOutput起始地址可以相同
	* 返  回:
	*  0: 无效数据，解码失败  
	*       大于0：base64解码后数据长度
	********************************************************************************
	*/
	static int JavaBase64Decode(byte[] pInput,int inputLen,byte[] pOutput)
	{
	  char np = 255;
	  int i,j,m;
	  int outlen;
	  int padnum;
	  int datalen;
	  long ltmp;
	  char ctmp;
		 /* 构造解码表 */
	  char[] decodingTable = new char[256];
	
	  for(i=0; i<256; i++)
		  decodingTable[i] = np;
		 for(i='A'; i<='Z'; i++)
		  decodingTable[i] = (char)(i - 'A');
		 for(i='a'; i<='z'; i++)
		  decodingTable[i] = (char)(i - 'a' + 26);
		 for(i='0'; i<='9'; i++)
		  decodingTable[i] = (char)(i - '0' + 52);
		 decodingTable['+'] = 62;
		 decodingTable['/'] = 63;
		 
		 /* 数据长度有效校验 */
		 if(inputLen % 4 != 0)
		  return 0; 
		 
		 /* 根据'='的个数判断编码前数据的长度 */
		 if(pInput[inputLen-2] == '=')
		  padnum = 2;
		 else if(pInput[inputLen-1] == '=')
		  padnum = 1;
		 else
		  padnum = 0;
		 outlen = inputLen/4*3 - padnum;
		 datalen = (inputLen-padnum)/4*3;
		 
		 /* 开始解码 */
		 for(i = 0, j = 0; i < datalen; i += 3, j += 4)
		 {
		  /* 将4个6位字节转化为3个8位字节 */
		  /*  ltmp = (pInput[j] << 18) | (pInput[j+1] << 12) | (pInput[j+2] << 6) | pInput[j+3]; */
		  ltmp = 0;
		  for(m=j; m<j+4; m++)
		  {
		   ctmp = decodingTable[pInput[m]];
		   if(ctmp == np)
		    return 0; /* 无效数据，失败返回 */
		   ltmp = (ltmp << 6) | ctmp;
		  }
		  pOutput[i] = (byte)( (ltmp >> 16) & 0xff );
		  pOutput[i+1] = (byte)( (ltmp >> 8) & 0xff );
		  pOutput[i+2] = (byte)( (ltmp) & 0xff );
		 }
		 switch (padnum) {
		 case 0: /* nothing left to do */
		  break;
		 case 1:
		  ltmp = 0;
		  for(m=inputLen-4; m<inputLen-1; m++)
		  {
		   ctmp = decodingTable[ pInput[m] ];
		   if(ctmp == np)
		    return 0; /* 无效数据，失败返回 */
		   ltmp = (ltmp << 6) | ctmp;
		  }
		  ltmp <<= 6; /* 1个填充的'=' */
		  pOutput[outlen-2] = ( byte)( (ltmp >> 16) & 0xff );
		  pOutput[outlen-1] = ( byte)( (ltmp >> 8) & 0xff );
		  break;
		 case 2:
		  ltmp = 0;
		  for(m=inputLen-4; m<inputLen-2; m++)
		  {
		   ctmp = decodingTable[pInput[m]];
		   if(ctmp == np)
		    return 0; /* 无效数据，失败返回 */
		   ltmp = (ltmp << 6) | ctmp;
		  } 
		  ltmp <<= 12; /* 两个填充的'=' */
		  pOutput[outlen-1] = (byte)((ltmp >> 16 ) & 0xff );
		  break;
		 } 
		 return outlen;
	}
}
