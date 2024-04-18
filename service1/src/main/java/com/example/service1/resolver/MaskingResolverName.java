package com.example.service1.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("maskingResolverName")
@Slf4j
public class MaskingResolverName implements Resolver {

    public static final char MASKING_CHAR =  '*' ;
    public MaskingResolverName() {
    }

    @Override
    public String resolve(String input) {
        return maskingName(input);
    }

    public String maskingName(Object name) {

            StringBuffer strBuf =  new StringBuffer();
            String str;
            if (name !=  null && name  instanceof String) {
                str = (String)name;
                if (str.length() ==  1 ) {
                    strBuf.append( '*' );
                }  else if (str.length() ==  2 ) {
                    strBuf.append(str.charAt( 0 ));
                    strBuf.append( '*' );
                }  else if (str.length() >=  3 ) {
                    strBuf.append(str.charAt( 0 ));
                    for ( int i =  1 ; i < str.length() -  1 ; ++i) {
                        strBuf.append( '*' );
                    }
                    strBuf.append(str.charAt(str.length() -  1 ));
                }
            }
            str = strBuf.toString();
            log.debug( "$$$$[KyoboV3MaskingResolverName-resolve] obj : " + name +  "--> " + str);
            return str;
    }
}
