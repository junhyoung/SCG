package com.example.service1.resolver.masking;

import com.example.service1.resolver.Resolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/*
	11)사업자등록번호		/ 사업자등록번호 뒤 5자리 '*'처리 (예, 123-45-***** )
 */
@Slf4j
@Component("BusinessNumMaskingResolver")
public class BusinessNumMaskingResolver extends Resolver {

	public static final char	MASKING_CHAR		= '*';
	public static final int		MASKING_LEN	= 5;	

	@Override
	public Object resolve(Object obj) {
		StringBuffer strBuf = new StringBuffer();
		String str = "";
		try {
			if (obj != null && obj instanceof String) {
				str = (String) obj;
				
				int size = str.length();
				for (int i = 0; i < size; i++) {
					System.out.println(i +":" + size);
					if(i < size - MASKING_LEN){
						strBuf.append(str.charAt(i));
					}else{
						strBuf.append(MASKING_CHAR);
					}
				}
				str = strBuf.toString();
			}		
			return str;
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			return "";
		}
		

	}

	public static void main(String[] args) {
		try {
			BusinessNumMaskingResolver maskingResolver = new BusinessNumMaskingResolver();
			System.out.println("socialId : " + maskingResolver.resolve("123-45-11111"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
