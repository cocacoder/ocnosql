package com.ailk.oci.ocnosql.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class ParseUtil {

	public static List<String> parse(String str){
		Pattern pattern = Pattern.compile("(\\$(\\{.*?\\}))");
		Matcher matcher = pattern.matcher(str);
		List<String> v = new ArrayList<String>();
		while(matcher.find()){
			v.add(matcher.group(1));
		}
		return v;
	}
	
	
	public static String executeExp(String exp, String[] record, Map<String, Integer> columnIndexMap){
		List<String> tokens = parse(exp);
		for(int i = 0; i < tokens.size(); i++){
			Integer columnIndex = columnIndexMap.get(tokens.get(i));
			if(columnIndex == null){
				throw new RuntimeException("parse expression: '" + exp + "' exception, token: '" + tokens.get(i) + "' is not exsited in columnIndexMap.");
			}
			exp = exp.replace(tokens.get(i), record[columnIndex]);
		}
		return exp;
	}
	
	public static String executeExp(String exp, List<String> tokens, String[] record, Map<String, Integer> columnIndexMap){
		for(int i = 0; i < tokens.size(); i++){
			Integer columnIndex = columnIndexMap.get(tokens.get(i));
			if(columnIndex == null){
				throw new RuntimeException("parse expression: '" + exp + "' exception, token: '" + tokens.get(i) + "' is not exsited in columnIndexMap.");
			}
			exp = exp.replace(tokens.get(i), record[columnIndex]);
		}
		return exp;
	}
	
	
	
	public static void main(String[] args){
		parse("select * from ${ table } where name='${name}'");
	}
}
