package com.example.service1.resolver.masking;

import com.example.service1.resolver.Resolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/*
	7)휴대전화번호		/ 휴대전화번호 국번과 뒤 4자리 외에 가운데 '*' (예, 010-****-2345)
 */
@Slf4j
@Component("HPhoneMaskingResolver")
public class HPhoneMaskingResolver extends Resolver {

	public static final char	MASKING_CHAR		= '*';
	public static final int		MASKING_FIRST_LEN	= 3;
	public static final int		MASKING_LAST_LEN	= 4;

	@Override
	public Object resolve(Object obj) {
		StringBuffer strBuf = new StringBuffer();
		if (obj != null && obj instanceof String) {
			String str = (String) obj;

			if (str.length() > (MASKING_FIRST_LEN + MASKING_LAST_LEN)) {
				for (int i = 0; i < MASKING_FIRST_LEN; i++) {
					strBuf.append(str.charAt(i));
				}
				int size = str.length();
				for (int i = MASKING_FIRST_LEN; i < size - MASKING_LAST_LEN; i++) {
					if (str.charAt(i) == '-') {
						strBuf.append(str.charAt(i));
					} else {
						strBuf.append(MASKING_CHAR);
					}
				}
				for (int i = size - MASKING_LAST_LEN; i < size; i++) {
					strBuf.append(str.charAt(i));
				}
			}
		}
		String str = strBuf.toString();
		log.debug("$$$$[MaskingResolverHPhone-resolve] obj : " + obj + "--> " + str);

		return str;
	}

	public static void main(String[] args) {
		try {
			HPhoneMaskingResolver HPhoneMaskingResolver = new HPhoneMaskingResolver();
			String phone = "010-1234-1234";
			System.out.println("phone : " + phone);
			System.out.println("phone : " + HPhoneMaskingResolver.resolve(phone));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
