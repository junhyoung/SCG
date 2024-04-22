package com.example.service1.resolver.masking;

import com.example.service1.resolver.Resolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/*
	13)카드번호			/ 카드번호 7번째 자리부터 6자리 '*' 처리 (예, 1234-56**-****-7890)
 */
@Slf4j
@Component("CardMaskingResolver")
public class CardMaskingResolver extends Resolver {

	public static final char	MASKING_CHAR		= '*';
	public static final int		MASKING_FIRST_LEN	= 6;
	public static final int		MASKING_MASKING_LEN	= 6;

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
		log.debug("$$$$[MaskingResolverCard-resolve] obj : " + obj + "--> " + str);

		return str;
	}

	public static void main(String[] args) {
		try {
			CardMaskingResolver cardMaskingResolver = new CardMaskingResolver();
			String card = "1234-1234-1234-1234";
			System.out.println("card : " + card);
			System.out.println("card : " + cardMaskingResolver.resolve("1234-1234-1234-1234"));
			System.out.println("card : " + cardMaskingResolver.resolve("1234-1234-1234-123"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
