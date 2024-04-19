package com.example.service1.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/*
	6)성명				/ 성명중 일부 (예, 홀*찬) // 건별고개기본사항/계약사항 조회화면에서는 '*'표 미처리, 명단에서는 가운데 자리 '*' 처리
 */
@Slf4j
@Component("maskingResolverName")
public class MaskingResolverName extends Resolver {

	public static final char	MASKING_CHAR	= '*';

	@Override
	public Object resolve(Object obj) {
		StringBuffer strBuf = new StringBuffer();
		if (obj != null && obj instanceof String) {
			String str = (String) obj;

			if (str.length() == 1) {
				strBuf.append(MASKING_CHAR);
			} else if (str.length() == 2) {
				strBuf.append(str.charAt(0));
				strBuf.append(MASKING_CHAR);
			} else if (str.length() >= 3) {
				strBuf.append(str.charAt(0));
				for (int i = 1; i < str.length() - 1; i++) {
					strBuf.append(MASKING_CHAR);
				}
				strBuf.append(str.charAt(str.length() - 1));
			}

		}
		String str = strBuf.toString();
		log.debug("$$$$[MaskingResolverName-resolve] obj : " + obj + "--> " + str);

		return str;
	}

	public static void main(String[] args) {
		try {
			MaskingResolverName maskingResolverName = new MaskingResolverName();
			System.out.println("name : " + maskingResolverName.resolve("홍길동"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
