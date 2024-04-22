package com.example.service1.resolver.masking;

import com.example.service1.resolver.Resolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/*
	9)전화번호(자택)		/ 전화번호 가운데 '*' 처리 (예, 02-****-6545)
 */
@Slf4j
@Component("PhoneMaskingResolver")
public class PhoneMaskingResolver extends Resolver {

	public static final char	MASKING_CHAR		= '*';
	public static final int		MASKING_FIRST_LEN	= 3;
	public static final int		MASKING_LAST_LEN	= 4;

	@Override
	public Object resolve(Object obj) {
		StringBuffer strBuf = new StringBuffer();
		if (obj != null && obj instanceof String) {
			String str = (String) obj;

			boolean maskingFlag = false;
			int size = str.length();
			for (int i = 0; i < size; i++) {
				if (str.charAt(i) == '-') {
					maskingFlag = !maskingFlag;
					strBuf.append(str.charAt(i));
				} else {
					if (maskingFlag) {
						strBuf.append(MASKING_CHAR);
					} else {
						strBuf.append(str.charAt(i));
					}
				}
			}
		}
		String str = strBuf.toString();
		log.debug("$$$$[MaskingResolverPhone-resolve] obj : " + obj + "--> " + str);

		return str;
	}

	public static void main(String[] args) {
		try {
			PhoneMaskingResolver maskingResolverHPhone = new PhoneMaskingResolver();
			String phone = "02-1234-1234";
			System.out.println("phone : " + phone);
			System.out.println("phone : " + maskingResolverHPhone.resolve(phone));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
