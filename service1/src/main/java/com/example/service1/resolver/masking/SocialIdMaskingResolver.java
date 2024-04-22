package com.example.service1.resolver.masking;

import com.example.service1.resolver.Resolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/*
	1)주민등록번호 		/ 뒤 6자리 '*' 처리
 */
@Slf4j
@Component("SocialIdMaskingResolver")
public class SocialIdMaskingResolver extends Resolver {

	public static final char	MASKING_CHAR		= '*';
	public static final int		MASKING_LAST_LEN	= 6;

	@Override
	public Object resolve(Object obj) {
		StringBuffer strBuf = new StringBuffer();
		if (obj != null && obj instanceof String) {
			String str = (String) obj;

			int size = str.length();
			for (int i = 0; i < (size - MASKING_LAST_LEN); i++) {
				strBuf.append(str.charAt(i));
			}
			for (int i = (size - MASKING_LAST_LEN); i < size; i++) {
				strBuf.append(MASKING_CHAR);
			}
		}
		String str = strBuf.toString();
		log.debug("$$$$[MaskingResolverSocialId-resolve] obj : " + obj + "--> " + str);

		return str;
	}

	public static void main(String[] args) {
		try {
			SocialIdMaskingResolver socialIdMaskingResolver = new SocialIdMaskingResolver();
			String socialId = "123456-1234567";
			System.out.println("socialId : " + socialId);
			System.out.println("socialId : " + socialIdMaskingResolver.resolve("123456-1234567"));
			System.out.println("socialId : " + socialIdMaskingResolver.resolve("123456-123456"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
