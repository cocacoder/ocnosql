package com.ailk.oci.ocnosql.common.spring.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

public class OCNosqlWebArgumentResolver implements WebArgumentResolver {
	
	private static final Log log = LogFactory.getLog(OCNosqlWebArgumentResolver.class);
	
	public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest nativeWebRequest) throws Exception {
        // Get the annotation		
		WebAppContext webAppContextAnnotation = methodParameter.getParameterAnnotation(WebAppContext.class);
		Map<String, String[]> paramters = nativeWebRequest.getParameterMap();
        if(webAppContextAnnotation != null) {
            HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
            // Get user IP
            WebAppContextInfo info = new WebAppContextInfo();
            String userIp = getClientIpAddr(request);
            StringBuffer url = request.getRequestURL();
            int remotePort = request.getRemotePort();
            info.setClientIP(userIp);
            String host = request.getHeader("host");
            info.setServerIP(host);
            info.setClientport(remotePort);
            info.setRequestURL(url.toString());
            return info;
        }
		
        return UNRESOLVED;
    }

	private String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if(log.isDebugEnabled()){
			log.debug("the client ip is["+ip+"]");
		}
		return ip;
	}
}
