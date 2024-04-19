package com.example.service1.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/*
	14)가입증서번호(증권번호)	/	고객에게서 제시되는 고유번호는 적용 미정 ( 이라고 적혀있음 ) 12자리, 6자리 이후 4자리 *마스킹
 */
@Slf4j
@Component("maskingResolverSecurityNum")
public class MaskingResolverSecurityNum extends Resolver {

	public static final char	MASKING_CHAR		= '*';
	public static final int		MASKING_START_OFF	= 5;
	public static final int		MASKING_LEN			= 4;

	@Override
	public Object resolve(Object obj) {
		StringBuffer strBuf = new StringBuffer();
		if (obj != null && obj instanceof String) {
			String str = (String) obj;

			int i = 0;
			int size = str.length();
			for (; i < MASKING_START_OFF && i < size; i++) {
				strBuf.append(str.charAt(i));
			}
			for (; i < (MASKING_START_OFF + MASKING_LEN) && i < size; i++) {
				strBuf.append(MASKING_CHAR);
			}
			for (; i < size; i++) {
				strBuf.append(str.charAt(i));
			}
		}
		String str = strBuf.toString();
		log.debug("$$$$[MaskingResolverSecurityNum-resolve] obj : " + obj + "--> " + str);

		return str;
	}

	public static void main(String[] args) {
		try {
			MaskingResolverSecurityNum maskingResolverSecurityNum = new MaskingResolverSecurityNum();
			// --------------- // S
			String securityNum = "123456789012";
			System.out.println("securityNum : " + securityNum);
			System.out.println("securityNum : " + maskingResolverSecurityNum.resolve(securityNum));
			// --------------- // E
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
