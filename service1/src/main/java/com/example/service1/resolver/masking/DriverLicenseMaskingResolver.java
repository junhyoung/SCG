package com.example.service1.resolver.masking;

import java.util.ArrayList;
import java.util.List;

import com.example.service1.resolver.Resolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/*
	4)운전면허번호		/ 운전면허번호 5번째 숫자부터 6자리 '*' 처리
 */

@Slf4j
@Component("DriverLicenseMaskingResolver")
public class DriverLicenseMaskingResolver extends Resolver {


	public static final char	MASKING_CHAR		= '*';
	private static final String HYPHEN = "-";
	private static final String BLANK = "";
	public static final char	LENGTH_WITH_HYPHEN	= 15;
	public static final char	LENGTH_WITHOUT_HYPHEN = 12;
	
	public static final int		MASKING_LEN	= 6;
	public static final int		START_LEN	= 5;
	
	

	@Override
	public Object resolve(Object obj) {
		StringBuffer strBuf = new StringBuffer();
		String str = "";
		
		try {
			
			if (obj != null && obj instanceof String) {
				str = (String) obj;
				int checker = 0;
				List<Integer> list = new ArrayList<Integer>();
				
				
				if(str.length() == LENGTH_WITH_HYPHEN ){
					
					do{
						checker = str.indexOf(HYPHEN, checker + 1);
						list.add(checker);
					}while(checker+1 < str.length() && checker!=-1);
					
					str = str.replaceAll(HYPHEN, BLANK);
					strBuf.append(str.substring(0, START_LEN));
					
					for (int i = 0; i < MASKING_LEN; i++) {
						strBuf.append(MASKING_CHAR);
					}
					
					strBuf.append(str.substring(START_LEN + MASKING_LEN));
					str = strBuf.toString();
					int before = 0;
					
					for (int i = 0; i < list.size(); i++) {
						if(list.get(i) != -1){
							str = str.substring(before, list.get(i)) + HYPHEN + str.substring(list.get(i) , str.length());
						}
					}
					
				}else if(str.length() == LENGTH_WITHOUT_HYPHEN){
					strBuf.append(str.substring(0, START_LEN));
					for (int i = 0; i < MASKING_LEN; i++) {
						strBuf.append(MASKING_CHAR);
					}
					strBuf.append(str.substring(START_LEN + MASKING_LEN));
					str = strBuf.toString();
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
			DriverLicenseMaskingResolver maskingResolver = new DriverLicenseMaskingResolver();
			System.out.println("socialId : " + maskingResolver.resolve("11-11-210146-90"));
			System.out.println("socialId : " + maskingResolver.resolve("111121014690"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
