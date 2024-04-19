package com.example.service1.resolver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/*
	10)주소				/ 주소 읍/면/동 이후 '*' 처리 (예, 서울시 서초구 서초동 **-**)
 */

@Slf4j
@Component("maskingResolverAddress")
public class MaskingResolverAddress extends Resolver {

	public static final char	MASKING_CHAR		= '*';
	public static final int		MASKING_LEN	= 5;	

	@Override
	public Object resolve(Object obj) {
		String str = "";
		try {
			if (obj != null && obj instanceof String) {
				str = (String) obj;
				
				Pattern pattern = Pattern.compile("((([가-힣]+({1,5}|{1,5}(,|.){1,5}|)+(읍|면|동|가|리))(^구|)(({1,5}(~|-){1,5})(가|리|)|))([ ](산({1,5}(~|-)(1,5}|\\d{1,5}))|)|(([가-힣]|({1,5}(~|-){1,5})|{1,5})+(로|길))))");
				Matcher matcher = pattern.matcher(str);
				
				if(matcher.find(0)){
					str = str.substring(0, matcher.end()) + str.substring(matcher.end(), str.length()).replaceAll("[(가-힣)(0-9)]", "*"); 
				}
			}		
			return str;
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			return "";
		}
		
	}

	public static void main(String[] args) {
		try {
			MaskingResolverAddress maskingResolver = new MaskingResolverAddress();
			System.out.println("socialId : " + maskingResolver.resolve("경기도 고양시 일산동구 백석동 흰돌마을 405-1002호"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
