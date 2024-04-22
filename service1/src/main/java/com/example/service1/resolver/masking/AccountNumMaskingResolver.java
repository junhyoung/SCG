package com.example.service1.resolver.masking;

import com.example.service1.resolver.Resolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/*
	12)계좌번호			/ 계좌번호 5번째 자리부터 4자리 '*' 처리
 */

@Slf4j
@Component("AccountNumMaskingResolver")
public class AccountNumMaskingResolver extends Resolver {

	public static final char	MASKING_CHAR		= '*';
	public static final int		MASKING_FIRST_LEN	= 4;
	public static final int		MASKING_MASKING_LEN	= 4;

	@Override
	public Object resolve(Object obj) {
		StringBuffer strBuf = new StringBuffer();
		if (obj != null && obj instanceof String) {
			String str = (String) obj;

			int cc = 0;
			boolean maskingFlag = false;
			int size = str.length();
			for (int i = 0; i < size; i++) {
				if (!maskingFlag) {
					if (cc < MASKING_FIRST_LEN) {
						strBuf.append(str.charAt(i));
						if (str.charAt(i) != '-') {
							cc++;
						}
					} else {
						strBuf.append(MASKING_CHAR);
						maskingFlag = !maskingFlag;
						cc = 0;
					}
				} else {
					if (cc < MASKING_MASKING_LEN) {
						if (str.charAt(i) != '-') {
							strBuf.append(MASKING_CHAR);
							cc++;
						} else {
							strBuf.append('-');
						}
					} else {
						strBuf.append(str.charAt(i));
						maskingFlag = !maskingFlag;
						cc = 0;
					}
				}
			}
		}
		String str = strBuf.toString();
		log.debug("$$$$[MaskingResolverAccountNum-resolve] obj : " + obj + "--> " + str);

		return str;
	}

	public static void main(String[] args) {
		try {
			AccountNumMaskingResolver accountNumMaskingResolver = new AccountNumMaskingResolver();
			// --------------- // S
			String accountNum = "010-2760-8446";
			System.out.println("accountNum : " + accountNum);
			System.out.println("accountNum : " + accountNumMaskingResolver.resolve("010-2760-8446"));
			System.out.println("accountNum : " + accountNumMaskingResolver.resolve("514-013941-04-011"));
			// --------------- // E
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
