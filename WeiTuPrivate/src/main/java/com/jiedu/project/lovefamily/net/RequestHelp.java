package com.jiedu.project.lovefamily.net;

import android.util.Config;
import android.util.Log;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;


public class RequestHelp {

    PrintWriter printWriter = null;
    BufferedReader bufferedReader = null;
    StringBuffer resultBuff = null;
    String url = "http://api.map.baidu.com/telematics/v3/weather";
    HashMap<String, Object> map = new HashMap<String, Object>();

//    public static final String HTTP_HEAD = "http://";
//    public static final String HTTP_ACTION="180.97.28.81:8089";

    public static final String HTTP_HEAD = "http://";
   public static final String HTTP_ACTION="ajzx.wttskb.com";//正式库
 //   public static final String HTTP_ACTION="202.102.41.60";


    //石金彪
//    public static final String HTTP_ACTION="192.168.0.134:8080";
//    public static final String HTTP_ACTION="192.168.1.180:8080";
//    public static String home="192.168.1.180:8080";

    //获取验证码请求，需要把手机号码拼接到后面使用get请求
    public static final String VERIFICaTION_CODE = HTTP_HEAD + HTTP_ACTION + "/wttp/customer/getverifycode.do?phone=";
    //登陆请求，需要拼接?phone=&verifycode=使用get请求
    public static final String LOGIN_IN = HTTP_HEAD + HTTP_ACTION + "/wttp/customer/login.do";

    public static final String GET_MEMBER = HTTP_HEAD + HTTP_ACTION + "/wttp/customer/getCustomerList.do?customerId=";

//    192.168.1.12:8080/wttp/customer/saveCustomer.do?myphone=&phone=&nickName=&type=1

    public static final String ADD_MEMBER = HTTP_HEAD + HTTP_ACTION + "/wttp/customer/saveCustomer.do";
//添加成员没有图片
    public static final String ADD_MEMBER_NO_PIC=HTTP_HEAD + HTTP_ACTION +"/wttp/customer/saveCustomerNotPic.do";
    //定时上传位置
    public static final String UP_LOCATION = HTTP_HEAD + HTTP_ACTION + "/wttp/customerpostion/save.do";

    public static final String GET_LOCATION = HTTP_HEAD + HTTP_ACTION + "/wttp/customerpostion/getUserPostion.do";
    //编辑个人或者成员信息  ?monitorId=&monitoredId=&nickName=&birthday=1991-1-1&sex=&address=&type=
    public static final String EDIT_USER_INFO = HTTP_HEAD + HTTP_ACTION + "/wttp/customer/updateCustomer.do";
//编辑个人信息没有图片
    public static final String EDIT_USER_INFO_NO_PIC = HTTP_HEAD + HTTP_ACTION + "/wttp/customer/updateCustomerNotPic.do";
//提交新的邀请码?inviteCode=&monitoredId=
    public static final  String SUBMIT_INVITATION_CODE=HTTP_HEAD + HTTP_ACTION + "/wttp/customer/testInviteCode.do";
//获取监控人列表，?monitoredId=   如果对方能监控自己，那么可以从此列表中获取到对方
    public static final String GET_MONITOR_LIST=HTTP_HEAD + HTTP_ACTION + "/wttp/customerref/queryMonitorList.do";

    //删除监控人?monitorId=&monitoredId=
    public static final String DELETE_MONITOR=HTTP_HEAD + HTTP_ACTION + "/wttp/customerref/delete.do";
//查看安全范围list         ?pageNo=&monitorId=&monitoredId=
    public static final String GET_SAFE_RANGE_LIST=HTTP_HEAD + HTTP_ACTION +"/wttp/safetyregionsetting/list.do";

    //添加安全范围  ?id=&refId=&title=&latitude=&longitude=&radius=&address=
    public static final String ADD_SAFE_RANGE=HTTP_HEAD + HTTP_ACTION +"/wttp/safetyregionsetting/save.do";
    //删除安全范围?id=
    public static final  String DELETE_SAFE_RANGE=HTTP_HEAD + HTTP_ACTION +"/wttp/safetyregionsetting/delete.do";
    //获取监控请求
    public static final String GET_MONITOR_REQUEST=HTTP_HEAD + HTTP_ACTION +"/wttp/customer/getCustomerOtherList.do?monitoredId=";

    //同意监控请求
    public static final String CONFIRM_MONITOR_REQUEST=HTTP_HEAD + HTTP_ACTION +"/wttp/customer/confirmInvite.do?id=";
    //拒绝监控请求
    public static final String REFUSE_MONITOR_REQUEST=HTTP_HEAD + HTTP_ACTION +"/wttp/customer/cancelInvite.do?id=";

    public static final String WEB_HELP_URL=HTTP_HEAD + HTTP_ACTION +"/wttp/tohelp.do";

    public static final String GET_PAY_INFO=HTTP_HEAD + HTTP_ACTION +"/wttp/pay/queryAllBusiType.do";

    public static final String GET_MY_PAY=HTTP_HEAD + HTTP_ACTION+"/wttp/pay/subscribe.do";

    //删除消息
    public static final String Del_MESSAGE=HTTP_HEAD + HTTP_ACTION+"/wttp/messages/delete.do?ids=";

    //获取用户信息
    public static final String GET_USER_INFO=HTTP_HEAD + HTTP_ACTION+"/wttp/customer/getCustomerInfo.do";

    //退订
    public static final String CANCEL_PAY=HTTP_HEAD + HTTP_ACTION+"/wttp/pay/unsubscribe.do";

    //设置是否上传
    public static final String IS_UPLOAD_LOCATION=HTTP_HEAD + HTTP_ACTION+"/wttp/customer/uploadLocation.do";

    //设置上传频率
    public static final String UPLOAD_INTERVAL=HTTP_HEAD + HTTP_ACTION+"/wttp/customer/upInterval.do";

    //话费托管获取验证码
    public static final String GET_VERIY_CODE=HTTP_HEAD + HTTP_ACTION+"/wttp/pay/getverifycode.do";

    //话费托管支付
    public static final String CHECK=HTTP_HEAD + HTTP_ACTION+"/wttp/pay/check.do";


    //我的--》“消息中心” 和主页右上角的消息，请求地址：http://60ip/wttp/messages/query.do?page=起始页数&pagesize=每页显示数量&messageType=1

    public static final String GET_MESSAGE=HTTP_HEAD + HTTP_ACTION+"/wttp/messages/query.do";
    //http://202.102.41.60/60ip/wttp/messages/query.do?page=1&pagesize=10&&messageType=1

    public static final String QUERY_MESSAGE_COUNT=HTTP_HEAD + HTTP_ACTION+"/wttp/messages/queryMessgaesCount.do";

    public static final String READED=HTTP_HEAD + HTTP_ACTION+"/wttp/messages/readed.do";

    public static final String GET_LAST_VERSION=HTTP_HEAD + HTTP_ACTION+"/wttp/version/getLastVersion.do";

    public static final String TO_ABOUT=HTTP_HEAD + HTTP_ACTION+"/wttp/about.do";


    public String getLocation(String customerId) {
        return requestPost(GET_LOCATION + "?customerId=" + customerId, new HashMap<String, Object>());
    }

    ;

    public String sendVerification(String phone) {
        return requestPost(VERIFICaTION_CODE + phone, new HashMap<String, Object>());
    }
    // 生日格式1991-1-1  sex  0=男 1=女
    ;

    public String editUserInfo(String monitorId, String monitoredId, String nickName, String birthday, String sex, String address, String type) {

        try {
            address = URLEncoder.encode(address, "utf-8");
            nickName= URLEncoder.encode(nickName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return  requestPost(EDIT_USER_INFO_NO_PIC + "?monitorId=" + monitorId + "&monitoredId=" + monitoredId + "&nickName=" + nickName + "&birthday=" + birthday + "&sex=" + sex + "&address=" + address + "&type=" + type, new HashMap<String, Object>());
    }

    public String loginIn(String phone, String verifycode) {
        return requestPost(LOGIN_IN + "?phone=" + phone + "&verifycode=" + verifycode, new HashMap<String, Object>());

    }


    public  String sendPostMessage(Map<String,String> params,String encode,String path){//utf-8
        StringBuffer buffer=new StringBuffer();
        try {
            if(params!=null&&!params.isEmpty()){
                for (Map.Entry<String,String> entry:params.entrySet()){
                    buffer.append(entry.getKey()).append("=")
                            .append(URLEncoder.encode(entry.getValue(),encode))
                            .append("&");
                }

            }
            buffer.deleteCharAt(buffer.length()-1);
            URL url=new URL(path);
            HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();

            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);//表示从服务器读取数据
            httpURLConnection.setDoOutput(true);
            //获得上传信息字节大小以及长度
            byte[] mydata=buffer.toString().getBytes();
            httpURLConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("Content-Length",
                    String.valueOf(mydata.length));
            //获得输出流，像服务流输出数据
            OutputStream outputStream=httpURLConnection.getOutputStream();
            outputStream.write(mydata);
            //获得服务器响应的结果和状态吗
            int http=httpURLConnection.getResponseCode();
            if(httpURLConnection.getResponseCode()==200){
                return changheInputStream(httpURLConnection.getInputStream(),encode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }



     /*
     将一个输入流转换成制定编码的字符串
     */

    private static String changheInputStream(InputStream inputStream,String encode) {
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();//内存流
        byte[] data=new byte[1024];
        int len=0;
        String result="";
        if(inputStream!=null){
            try {
                while((len=inputStream.read(data))!=-1){
                    outputStream.write(data,0,len);

                }
                result=new String(outputStream.toByteArray(),encode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 获取成员
     *
     * @param id
     * @return
     */
    public String getMember(String id) {
        return requestPost(GET_MEMBER + id, new HashMap<String, Object>());
    }

    //?myphone=&phone=&nickName=&type=1
    public String addMember(String myPhone, String phone, String name, String type) {
        try {
            name= URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }
        return requestPost(ADD_MEMBER_NO_PIC + "?myphone=" + myPhone + "&phone=" + phone + "&nickName=" + name + "&type=" + type, new HashMap<String, Object>());
    }


    public String upLocation(String customerId, double latitude, double longitude) {
        return requestPost(UP_LOCATION + "?customerId=" + customerId + "&latitude=" + latitude + "&longitude=" + longitude, new HashMap<String, Object>());
    }

    public String requestPost(String url, HashMap<String, Object> parms) {

        resultBuff = new StringBuffer();
        try {
            URL url1 = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
            httpURLConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded;charset=UTF-8");
            Iterator iterator = parms.entrySet().iterator();
            StringBuffer parmBuffer = new StringBuffer();
            while (iterator.hasNext()) {
                Map.Entry parm = (Map.Entry) iterator.next();
                parmBuffer.append(parm.getKey());
                parmBuffer.append("=");
                parmBuffer.append(parm.getValue());
                parmBuffer.append("&");
            }
            if (parmBuffer.length() > 0) {
                parmBuffer.deleteCharAt(parmBuffer.length() - 1);
            }
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            printWriter.write(parmBuffer.toString());
            printWriter.flush();
            printWriter.close();
            int requestCode = httpURLConnection.getResponseCode();
            if (requestCode == 200) {
                Log.e("0011", "连接成功");
            }
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.e("0011", "====" + line);
                resultBuff.append(line).append("\n");
            }
            bufferedReader.close();
            httpURLConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("0011", "MalformedURLException异常", e);

        }
        return resultBuff.toString();
    }

    public static void readContentFromGet(String url) {
        try {
            URL getUrl = new URL(url);
            // 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型，  
            // 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection  
            HttpURLConnection connection = (HttpURLConnection) getUrl
                    .openConnection();
            connection.setRequestProperty("content-type", "application/x-www-form-urlencoded;charset=UTF-8");
            // 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到  
            // 服务器  
            connection.connect();
            int requestCode = connection.getResponseCode();
            if (requestCode == 200) {
                Log.e("0011", "链接成功");
            }
            // 取得输入流，并使用Reader读取  
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "utf-8"));
            Log.e("0011", "=============================");
            Log.e("0011", "Contents of get request");
            Log.e("0011", "=============================");
            String lines;
            while ((lines = reader.readLine()) != null) {
                System.out.println(lines);
            }
            reader.close();
            // 断开连接  
            connection.disconnect();
            Log.e("0011", "=============================");
            Log.e("0011", "Contents of get request ends");
            Log.e("0011", "=============================");
        } catch (Exception e) {
            Log.e("0011", "请求错误", e);
        }

        Log.e("0011", "请求完成");

    }
//提交验证码
    public String submitInvitationCode(String inviteCode,String monitoredId) {
        return requestPost(SUBMIT_INVITATION_CODE+"?inviteCode="+inviteCode+"&monitoredId="+monitoredId,new HashMap<String, Object>());
    }
    //获取监控列表
    public String getMonitorList(String monitoredId){
        return  requestPost(GET_MONITOR_LIST+"?monitoredId="+monitoredId,new HashMap<String, Object>());
    }

    /**
     *
     * @param monitorId 对方id
     * @param monitoredId 自己id
     * @return
     */
    public String deleteMonitor(String monitorId,String monitoredId){
        return  requestPost(DELETE_MONITOR+"?monitorId="+monitorId+"&monitoredId="+monitoredId,new HashMap<String, Object>());
    }

    /**
     *
     * @param monitorId 自己的id
     * @param monitoredId  对方id
     * @return
     */
    public String getSafeRangeList(String monitorId,String monitoredId){

        return requestPost(GET_SAFE_RANGE_LIST+"?&monitorId="+monitorId+"&monitoredId="+monitoredId,new HashMap<String, Object>());
    }

    /**
     * 添加或修改安全范围
     * @param id  如果是修改，此id为安全范围id，如果是添加，传空字符串
     * @param refId  成员的id
     * @param title  安全范围标题
     * @param lat
     * @param lon
     * @param radius  安全范围半径
     * @param address  中心点的位置
     * @return
     */
    public String addSafeRange(String id,String refId,String title,double lat,double lon,int radius,String address){
        try {
            title= URLEncoder.encode(title, "UTF-8");
            address= URLEncoder.encode(address, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();

        }
      return   requestPost(ADD_SAFE_RANGE+"?id="+id+"&refId="+refId+"&title="+title+"&latitude="+lat+"&longitude="+lon+"&radius="+radius+"&address="+address,new HashMap<String, Object>());
    }

    public String deleteSafeRange(String id){
        return  requestPost(DELETE_SAFE_RANGE+"?id="+id,new HashMap<String, Object>());
    }




    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10*1000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码
    /**
     * android上传文件到服务器
     * @param file  需要上传的文件
     * @return  返回响应的内容
     */
    public static String uploadFile(File file,String RequestURL)
    {


        String result = null;
        String  BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--" , LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型

        try {

//            Log.e("0011","请求地址"+RequestURL);
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
//           conn.setRequestProperty("content-type", "application/x-www-form-urlencoded;charset=UTF-8");

            if(file!=null)
            {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的   比如:abc.png
                 */

                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""+file.getName()+"\""+LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="+CHARSET+LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while((len=is.read(bytes))!=-1)
                {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                Log.e(TAG, "response code:"+res);
//                if(res==200)
//                {
                Log.e(TAG, "request success");
                BufferedReader bufferedReader2=null;
                StringBuffer resultBuff2=null;
                bufferedReader2 = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                resultBuff2 = new StringBuffer();


                String line;
                while ((line = bufferedReader2.readLine()) != null) {
                    Log.e("0011", "====" + line);
                    resultBuff2.append(line).append("\n");
                }
                bufferedReader2.close();
                result=resultBuff2.toString();

      /*          InputStream input =  conn.getInputStream();
                StringBuffer sb1= new StringBuffer();
                int ss ;
                while((ss=input.read())!=-1)
                {
                    sb1.append((char)ss);
                }
                result = sb1.toString();*/
                Log.e(TAG, "result : "+ result);
//                }
//                else{
//                    Log.e(TAG, "request error");
//                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("0011","MalformedURLException异常");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("0011", "IOException异常");
        }
        return result;
    }
}
