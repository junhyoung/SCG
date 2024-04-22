package com.example.service1.resolver.masking;

import com.example.service1.resolver.Resolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/*
	8)이메일				/ ID 뒤 두글자 이상 '*' 처리 (예, a**@naver.com)
 */
@Slf4j
@Component("EmailMaskingResolver")
public class EmailMaskingResolver extends Resolver {

	public static final char	MASKING_CHAR		= '*';
	public static final int		MASKING_AFTER_POS	= 2;

	@Override
	public Object resolve(Object obj) {
		StringBuffer strBuf = new StringBuffer();
		if (obj != null && obj instanceof String) {
			String str = (String) obj;

			String tokens[] = str.split("@");
			if (tokens[0] != null) {
				String id = tokens[0];

				if (id.length() == 1) {
					strBuf.append(MASKING_CHAR);
				} else if (id.length() == 2) {
					strBuf.append(MASKING_CHAR);
					strBuf.append(MASKING_CHAR);
				} else if (id.length() >= 3) {
					for (int i = 0; i < MASKING_AFTER_POS; i++) {
						strBuf.append(tokens[0].charAt(i));
					}
					for (int i = MASKING_AFTER_POS; i < tokens[0].length(); i++) {
						strBuf.append(MASKING_CHAR);
					}
				}
			}
			strBuf.append("@").append(tokens[1]);
		}
		String str = strBuf.toString();
		log.debug("$$$$[MaskingResolverEmail-resolve] obj : " + obj + "--> " + str);

		return str;
	}

	public static void main(String[] args) {
		try {
			EmailMaskingResolver EMailMaskingResolver = new EmailMaskingResolver();
			String email = "test@naver.com";
			System.out.println("email : " + email);
			System.out.println("email : " + EMailMaskingResolver.resolve(email));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
