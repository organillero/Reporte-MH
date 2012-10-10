package mx.ferreyra.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;



public class Utils {

	

	public static void CopyStream(InputStream is, OutputStream os){
		final int buffer_size=1024;
		try
		{
			byte[] bytes=new byte[buffer_size];
			for(;;)
			{
				int count=is.read(bytes, 0, buffer_size);
				if(count==-1)
					break;
				os.write(bytes, 0, count);
			}
		}
		catch(Exception ex){/**/}
	}
	
	public static String formatNumberToMoney(String number){
		return new String("$"+number);
	}
	
	public static String formatNumberToMoney(Double number){
		
		return formatNumberToMoney(number,"$0.00");
	}

	public static String formatNumberToMoney(Double number,String pattern){

		DecimalFormat f = (DecimalFormat) NumberFormat.getInstance();
		f.setDecimalSeparatorAlwaysShown(true);
		f.applyPattern(pattern);

		return f.format(number ) ;
	}

	private  final static Pattern rfc2822 = Pattern.compile(
			"^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$" );

	public static boolean isValidEmailAddress(String email){

		if (rfc2822.matcher(email).matches())
			return false;
		else 
			return true;

	}



	public static boolean compareDatetoToday (int dia, int mes, int anio){

		boolean ans = false;
		


		String date = anio + "/" + (mes) + "/" + dia;
		if (dia == 0 && mes == 0 && anio == 0)
			return true;
		
		
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		try {
			Date today = Calendar.getInstance().getTime();
			Date birthDay = df.parse(date);

			if (today.before(birthDay)){
				ans = true;
			}

		} catch (ParseException e) {
			ans =  false;
		}
		return ans;
	}

	private static String bytesToHex(byte[] b) {
		char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
				'8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		StringBuffer buf = new StringBuffer();
		for (int j=0; j<b.length; j++) {
			buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
			buf.append(hexDigit[b[j] & 0x0f]);
		}
		return buf.toString();
	}

	public static String getSHA1(String msg){
		String ans = null;

		try {
			MessageDigest md;
			md = MessageDigest.getInstance("SHA1");
			md.update(msg.getBytes());
			ans = Utils.bytesToHex( md.digest() );
		} catch (NoSuchAlgorithmException e) {
			ans = String.valueOf( msg.hashCode() );
		}

		return ans;
	} 
	public static boolean paro(char car){
		boolean eq=true;
		if(car==','||car==';'||car>64&&car<91||car>96&&car<123||car=='/')
			eq=false;
		return eq;
	}
	
	public static boolean isNumber(char car){
		boolean res=false;
		if(car>47&&car<58)
			res=true;
		return res;
	}
	
	public static String parsePhoneNumber(String tel){
		String res=null;
		int count=0,index=0;
		char[] numeros=new char[10];
		char[] aux=new char[10];
		while(count<10&&paro(tel.charAt(index))&&index<tel.length()){
			if(isNumber(tel.charAt(index))){
				numeros[count]=tel.charAt(index);
				count++;
			}
			index++;
		}
		if(count<10){
			if(count>7){
				int j=0;
				if(count==8){
					aux[0]='5';
					aux[1]='5';
					j=2;
				}
				if(count==9){
					aux[0]='5';
					j=1;
				}
				for(int i=0;i<8;i++){
					aux[i+j]=numeros[i];
				}
				res=new String(String.copyValueOf(aux));
			}
		}
		else{
			res=new String(String.copyValueOf(numeros));
		}
		return res;
	}
	

}
